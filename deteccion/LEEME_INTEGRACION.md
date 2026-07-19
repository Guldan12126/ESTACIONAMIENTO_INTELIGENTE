# Integración de la cámara (YOLO) con el sistema web

## Qué se agregó

1. **`api/config.php`** — nueva constante `CAMERA_API_KEY` (clave secreta compartida con el script de Python).
2. **`api/index.php`** — nueva acción `camera_status`:
   - No requiere sesión de usuario, requiere el header `X-Camera-Key`.
   - Recibe `{"espacios": {"A1": "ocupado", "A2": "disponible", ...}}`.
   - Actualiza `espacios.estado` **solo** si el espacio no está `reservado` y no tiene una `placa` registrada (para no pisar un ingreso hecho por un alumno/admin). Esos espacios solo cambian con `register_vehicle` / `exit_vehicle`, como ya funcionaba.
3. **`app.js`** — la app ahora hace un `apiRequest("data")` automático cada 5 segundos (`startAutoRefresh`) mientras hay sesión activa, para que el mapa muestre en vivo lo que detecta la cámara. Se detiene al cerrar sesión.
4. **`deteccion/deteccion_camara.py`** — tu script original de YOLO, con dos cambios:
   - Usa el historial de 5 frames para decidir si un espacio está realmente ocupado (igual que antes, pero ahora ese estado estable es el que se envía).
   - Cada `INTERVALO_ENVIO_SEGUNDOS` (3s por defecto) envía por HTTP el estado de los 7 espacios a `api/index.php?action=camera_status`, en un hilo aparte para no bajar el FPS del video.

## Cómo probarlo

1. Ejecuta el `database.sql` si aún no lo hiciste (crea la tabla `espacios` con códigos `A1`..`C8`).
2. Corre tu proyecto con XAMPP como siempre (Apache + MySQL).
3. Revisa/edita en `deteccion/deteccion_camara.py`:
   - `URL_CAPTURE`: la IP de tu ESP32-CAM.
   - `API_URL`: la ruta real a tu `api/index.php` (por ejemplo `http://localhost/ESTACIONAMIENTO_INTELIGENTE-main/api/index.php`).
   - `CAMERA_API_KEY`: debe ser **idéntica** a la de `api/config.php`.
   - `MAPEO_ESPACIOS`: qué espacio de la cámara ("1".."7") corresponde a qué `codigo_espacio` real (por defecto A1..A7, dejando A8 como reservado, igual que en `database.sql`).
4. Instala dependencias si falta alguna: `pip install ultralytics opencv-python requests numpy`.
5. Ejecuta: `python deteccion/deteccion_camara.py`.
6. Abre el sistema web (`index.html`), inicia sesión y entra al Mapa: en unos segundos deberías ver los espacios A1–A7 cambiar de color solos cuando la cámara detecta o deja de detectar un auto.

## Notas importantes

- Si un alumno registra su vehículo (con placa) en un espacio, la cámara **no** lo va a marcar como libre aunque no lo detecte bien (evita romper el historial de ingresos/salidas). Para liberar ese espacio se debe usar "Registrar salida" como ya funcionaba.
- Los espacios `reservado` (A8, B8, C8) tampoco los toca la cámara.
- Si quieres cubrir más espacios (B y C), solo necesitas otra cámara/otro set de polígonos, o ampliar `espacios` y `MAPEO_ESPACIOS` en el script.
