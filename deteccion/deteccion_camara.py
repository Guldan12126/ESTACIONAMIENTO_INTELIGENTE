"""
Deteccion de estacionamiento con YOLO + envio de estado y video a la API del proyecto
================================================================================
Este script es el mismo sistema de deteccion (ESP32-CAM + YOLO + poligonos),
pero cada cierto tiempo envia:
  1) el estado de los espacios (libre/ocupado) a la API PHP del proyecto
     (api/index.php, accion "camera_status") para que la tabla `espacios`
     de la base de datos y el mapa web se actualicen solos.
  2) el frame anotado (con los rectangulos y poligonos dibujados) a la
     accion "camera_frame", para que el panel de administrador pueda
     mostrar en vivo lo que ve la camara.

Requisitos:
    pip install ultralytics opencv-python requests numpy

Antes de ejecutar, revisa la seccion "CONFIGURACION" mas abajo.
"""

import cv2
import numpy as np
import urllib.request
import threading
import time
from collections import deque

import requests
from ultralytics import YOLO

print("=" * 60)
print("SISTEMA DE DETECCION DE ESTACIONAMIENTO SGEI - UTP")
print("=" * 60)
print("Usando poligonos adaptados a las lineas blancas")
print("Cada auto ocupa un solo espacio")
print("Envia el estado y el video a la API PHP del proyecto")

# ============================================================
# CONFIGURACION
# ============================================================

# URL para capturar imagenes de la ESP32-CAM
URL_CAPTURE = "http://10.117.114.53/capture"

# URL de la API del proyecto (ajusta host/puerto/carpeta segun tu XAMPP).
# IMPORTANTE: debe apuntar al nombre REAL de tu carpeta dentro de
# htdocs y al puerto real en el que esta escuchando Apache.
API_URL = "http://localhost:8012/estacionamiento-utp/api/index.php"

# Debe ser EXACTAMENTE la misma clave que CAMERA_API_KEY en api/config.php
CAMERA_API_KEY = "utp-sgei-camara-2026"

# Cada cuantos segundos se envia el estado a la API (no en cada frame,
# para no saturar el servidor ni la base de datos)
INTERVALO_ENVIO_SEGUNDOS = 3

# Cada cuantos segundos se envia el frame de video para el panel de admin.
# Puede ser mas frecuente que el estado, pero no tan frecuente como el
# framerate real (para no saturar Apache con imagenes).
INTERVALO_ENVIO_FRAME_SEGUNDOS = 1

# Calidad JPEG del frame que se envia (1-100). Mas bajo = mas liviano.
CALIDAD_JPEG_FRAME = 70

# Mapeo entre el nombre del espacio en este script ("1".."7", segun los
# poligonos definidos abajo) y el codigo_espacio real en la tabla `espacios`
# de la base de datos (por ejemplo A1..A8 segun database.sql). Ajusta esto
# a como esten ubicados fisicamente los espacios que ve tu camara.
MAPEO_ESPACIOS = {
    "1": "A1",
    "2": "A2",
    "3": "A3",
    "4": "A4",
    "5": "A5",
    "6": "A6",
    "7": "A7",
}

# 1. Cargamos el modelo YOLO
print("Cargando modelo YOLO...")
modelo = YOLO('yolov8s.pt')

# 3. ESPACIOS USANDO POLIGONOS (4 puntos cada uno)
espacios = {
    "1": [(1060, 639), (764, 447), (895, 368), (1173, 510)],
    "2": [(1176, 513), (906, 370), (1016, 301), (1265, 423)],
    "3": [(199, 719), (537, 552), (435, 453), (129, 576)],
    "4": [(120, 573), (426, 438), (346, 357), (82, 459)],
    "5": [(79, 458), (349, 356), (279, 287), (41, 365)],
    "6": [(48, 372), (267, 290), (229, 232), (13, 299)],
    "7": [(1259, 416), (1025, 304), (1116, 249), (1273, 330)],
}

# Historial de detecciones (para estabilizar el estado, evita parpadeos)
historial_ocupados = {nombre: deque(maxlen=5) for nombre in espacios.keys()}


def punto_en_poligono(punto, poligono):
    """Verifica si un punto esta dentro de un poligono"""
    x, y = punto
    n = len(poligono)
    inside = False
    p1x, p1y = poligono[0]
    for i in range(n + 1):
        p2x, p2y = poligono[i % n]
        if y > min(p1y, p2y):
            if y <= max(p1y, p2y):
                if x <= max(p1x, p2x):
                    if p1y != p2y:
                        xinters = (y - p1y) * (p2x - p1x) / (p2y - p1y) + p1x
                    if p1x == p2x or x <= xinters:
                        inside = not inside
        p1x, p1y = p2x, p2y
    return inside


def porcentaje_rectangulo_en_poligono(rect, poligono):
    """Calcula que porcentaje del rectangulo esta dentro del poligono"""
    x1, y1, x2, y2 = rect
    ancho = x2 - x1
    alto = y2 - y1

    if ancho < 10 or alto < 10:
        return 0.0

    puntos_a_verificar = []
    for i in range(5):
        for j in range(5):
            px = x1 + (ancho * i / 4)
            py = y1 + (alto * j / 4)
            puntos_a_verificar.append((px, py))

    puntos_dentro = sum(1 for p in puntos_a_verificar if punto_en_poligono(p, poligono))
    return puntos_dentro / len(puntos_a_verificar)


def capturar_imagen():
    """Captura una imagen de la ESP32-CAM"""
    try:
        response = urllib.request.urlopen(URL_CAPTURE, timeout=2)
        img_array = np.asarray(bytearray(response.read()), dtype=np.uint8)
        frame = cv2.imdecode(img_array, cv2.IMREAD_COLOR)
        return frame
    except Exception as e:
        print(f"Error al capturar: {e}")
        return None


def dibujar_texto_con_fondo(img, texto, posicion, color_texto, color_fondo):
    """Dibuja texto con fondo"""
    font = cv2.FONT_HERSHEY_SIMPLEX
    font_scale = 0.5
    thickness = 2
    (texto_ancho, texto_alto), baseline = cv2.getTextSize(texto, font, font_scale, thickness)
    x, y = posicion
    cv2.rectangle(img, (x, y - texto_alto - 5), (x + texto_ancho, y + 5), color_fondo, -1)
    cv2.putText(img, texto, (x, y), font, font_scale, color_texto, thickness)


def enviar_estado_api(estado_por_espacio):
    """
    Envia el estado detectado (libre/ocupado) a la API PHP.
    Se ejecuta en un hilo aparte para no frenar la captura de video.
    """
    payload = {"espacios": estado_por_espacio}
    headers = {
        "Content-Type": "application/json",
        "X-Camera-Key": CAMERA_API_KEY,
    }

    def _enviar():
        try:
            resp = requests.post(
                f"{API_URL}?action=camera_status",
                json=payload,
                headers=headers,
                timeout=4,
            )
            data = resp.json()
            if data.get("ok"):
                actualizados = data.get("actualizados", [])
                if actualizados:
                    print(f"API actualizada -> {actualizados}")
            else:
                print(f"La API respondio con error: {data.get('message')}")
        except Exception as e:
            print(f"No se pudo enviar el estado a la API: {e}")

    threading.Thread(target=_enviar, daemon=True).start()


def enviar_frame_api(frame):
    """
    Envia el frame anotado (JPEG) a la API PHP para que el panel de
    administrador lo muestre en vivo. Se ejecuta en un hilo aparte.
    """
    ok, buffer = cv2.imencode(".jpg", frame, [cv2.IMWRITE_JPEG_QUALITY, CALIDAD_JPEG_FRAME])
    if not ok:
        return

    headers = {
        "Content-Type": "image/jpeg",
        "X-Camera-Key": CAMERA_API_KEY,
    }

    def _enviar():
        try:
            requests.post(
                f"{API_URL}?action=camera_frame",
                data=buffer.tobytes(),
                headers=headers,
                timeout=4,
            )
        except Exception as e:
            print(f"No se pudo enviar el frame a la API: {e}")

    threading.Thread(target=_enviar, daemon=True).start()


print("Conectando a la ESP32-CAM...")

# Variables
frame_count = 0
fps_counter = 0
fps_time = time.time()
min_frame_interval = 0.1
ultimo_envio = 0.0
ultimo_envio_frame = 0.0

while True:
    inicio_ciclo = time.time()

    frame = capturar_imagen()

    if frame is None:
        print("Reintentando en 1 segundo...")
        time.sleep(1)
        continue

    # Se fuerza siempre a 1280x720 (sin importar la resolucion real de la
    # camara) para que las coordenadas de los poligonos de "espacios"
    # coincidan siempre con el mismo tamaño de lienzo.
    frame = cv2.resize(frame, (1280, 720))

    frame_count += 1
    fps_counter += 1

    # ============================================
    # DETECCION DE VEHICULOS
    # ============================================

    cajas_carros = []

    resultados = modelo.predict(frame, classes=[2, 3, 5, 7], conf=0.12, verbose=False, imgsz=640)
    cajas_temp = resultados[0].boxes.xyxy.cpu().numpy()

    for caja in cajas_temp:
        x1, y1, x2, y2 = caja
        ancho = x2 - x1
        alto = y2 - y1
        if ancho > 25 and alto > 15:
            cajas_carros.append(caja)

    cajas_carros = np.array(cajas_carros) if cajas_carros else []

    if len(cajas_carros) == 0 and frame_count % 3 == 0:
        resultados = modelo.predict(frame, classes=[2], conf=0.05, verbose=False, imgsz=640)
        cajas_temp = resultados[0].boxes.xyxy.cpu().numpy()
        for caja in cajas_temp:
            x1, y1, x2, y2 = caja
            ancho = x2 - x1
            alto = y2 - y1
            if ancho > 20 and alto > 10:
                cajas_carros.append(caja)
        cajas_carros = np.array(cajas_carros) if cajas_carros else []

    # ============================================
    # ASIGNAR CADA AUTO A UN SOLO ESPACIO
    # ============================================

    asignaciones = {}
    porcentajes_auto_espacio = {}

    for idx_auto, caja in enumerate(cajas_carros):
        x1_a, y1_a, x2_a, y2_a = caja
        rect = (x1_a, y1_a, x2_a, y2_a)

        mejor_espacio = None
        mejor_porcentaje = 0

        for nombre, poligono in espacios.items():
            porcentaje = porcentaje_rectangulo_en_poligono(rect, poligono)
            if porcentaje > mejor_porcentaje:
                mejor_porcentaje = porcentaje
                mejor_espacio = nombre

        if mejor_espacio is not None and mejor_porcentaje > 0.1:
            asignaciones[idx_auto] = mejor_espacio
            porcentajes_auto_espacio[idx_auto] = mejor_porcentaje

    # Resolver conflictos: un auto por espacio
    conteo_por_espacio = {}
    for idx_auto, espacio in asignaciones.items():
        conteo_por_espacio.setdefault(espacio, []).append(idx_auto)

    asignaciones_finales = {}
    for espacio, autos in conteo_por_espacio.items():
        if len(autos) == 1:
            asignaciones_finales[espacio] = autos[0]
        else:
            mejor_auto = max(autos, key=lambda idx: porcentajes_auto_espacio[idx])
            asignaciones_finales[espacio] = mejor_auto
            print(f"Espacio {espacio} tenia {len(autos)} autos, se quedo con el auto {mejor_auto}")

    # ============================================
    # DIBUJAR RESULTADOS
    # ============================================

    for idx_auto, caja in enumerate(cajas_carros):
        x1_a, y1_a, x2_a, y2_a = caja

        espacio_asignado = None
        for espacio, idx in asignaciones_finales.items():
            if idx == idx_auto:
                espacio_asignado = espacio
                break

        if espacio_asignado is not None:
            color_auto = (0, 255, 0)
            cv2.putText(frame, f"Auto {idx_auto}->E{espacio_asignado}",
                        (int(x1_a), int(y1_a) - 5),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.4, (0, 255, 0), 1)
        else:
            color_auto = (0, 0, 255)
            cv2.putText(frame, f"Auto {idx_auto}",
                        (int(x1_a), int(y1_a) - 5),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.4, (0, 0, 255), 1)

        cv2.rectangle(frame, (int(x1_a), int(y1_a)), (int(x2_a), int(y2_a)), color_auto, 2)

    # ============================================
    # VERIFICAR CADA ESPACIO (con estabilizacion por historial)
    # ============================================

    espacios_ocupados = 0
    espacios_libres = 0
    estado_por_espacio = {}

    for nombre, poligono in espacios.items():
        pts = np.array(poligono, dtype=np.int32)

        deteccion_actual = nombre in asignaciones_finales
        historial_ocupados[nombre].append(deteccion_actual)
        ocupado_estable = sum(historial_ocupados[nombre]) >= 3

        if ocupado_estable:
            estado = "OCUPADO"
            color = (0, 0, 255)
            espacios_ocupados += 1
        else:
            estado = "LIBRE"
            color = (0, 255, 0)
            espacios_libres += 1

        estado_por_espacio[nombre] = "ocupado" if ocupado_estable else "disponible"

        cv2.polylines(frame, [pts], True, color, 2)
        cx = int(sum(p[0] for p in poligono) / 4)
        cy = int(sum(p[1] for p in poligono) / 4)
        dibujar_texto_con_fondo(frame, f"{nombre}:{estado}", (cx - 30, cy), color, (0, 0, 0))

    # ============================================
    # ENVIAR ESTADO A LA API (cada INTERVALO_ENVIO_SEGUNDOS)
    # ============================================

    ahora = time.time()
    if ahora - ultimo_envio >= INTERVALO_ENVIO_SEGUNDOS:
        estado_para_api = {
            MAPEO_ESPACIOS[nombre]: estado
            for nombre, estado in estado_por_espacio.items()
            if nombre in MAPEO_ESPACIOS
        }
        if estado_para_api:
            enviar_estado_api(estado_para_api)
        ultimo_envio = ahora

    # ============================================
    # ENVIAR FRAME DE VIDEO A LA API (para el panel de admin)
    # ============================================

    if ahora - ultimo_envio_frame >= INTERVALO_ENVIO_FRAME_SEGUNDOS:
        enviar_frame_api(frame)
        ultimo_envio_frame = ahora

    # ============================================
    # MOSTRAR INFORMACION
    # ============================================

    total_espacios = len(espacios)
    info_y = 30
    cv2.putText(frame, f"FPS: {fps_counter}", (10, info_y),
                cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 255, 255), 2)
    cv2.putText(frame, f"Frame: {frame_count}", (10, info_y + 30),
                cv2.FONT_HERSHEY_SIMPLEX, 0.5, (200, 200, 200), 2)
    cv2.putText(frame, f"Libres: {espacios_libres}/{total_espacios}", (10, info_y + 60),
                cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 0), 2)
    cv2.putText(frame, f"Ocupados: {espacios_ocupados}/{total_espacios}", (10, info_y + 85),
                cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 255), 2)
    cv2.putText(frame, f"Autos detectados: {len(cajas_carros)}", (10, info_y + 110),
                cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 0), 2)
    cv2.putText(frame, f"Asignaciones: {len(asignaciones_finales)}", (10, info_y + 135),
                cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 255), 2)

    cv2.imshow("SGEI - Estacionamiento", frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

    tiempo_ejecucion = time.time() - inicio_ciclo
    if tiempo_ejecucion < min_frame_interval:
        time.sleep(min_frame_interval - tiempo_ejecucion)

    if time.time() - fps_time >= 1:
        fps_counter = 0
        fps_time = time.time()

cv2.destroyAllWindows()
print("Programa finalizado")