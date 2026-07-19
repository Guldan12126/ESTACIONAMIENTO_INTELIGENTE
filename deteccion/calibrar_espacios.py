"""
CALIBRADOR DE ESPACIOS DE ESTACIONAMIENTO
================================================================================
Esta herramienta te ayuda a recalcular las coordenadas de los poligonos de
"espacios" para el nuevo tamano de lienzo (1280x720), sin tener que adivinar
numeros a mano.

Como funciona:
  1. Captura UNA foto de tu camara (la misma URL_CAPTURE que usas en el
     script principal) y la estira a 1280x720, igual que hace
     deteccion_camara.py.
  2. Se abre una ventana con esa foto. Haces CLIC IZQUIERDO en las 4
     esquinas del primer espacio, en cualquier orden pero siguiendo el
     contorno (no cruzado), por ejemplo: arriba-izq, arriba-der,
     abajo-der, abajo-izq.
  3. Al completar 4 clics, ese espacio se dibuja en pantalla y
     automaticamente empieza el siguiente espacio.
  4. Si te equivocas en un clic, presiona la tecla "z" para deshacer el
     ultimo punto.
  5. Cuando termines TODOS los espacios que necesites, presiona la tecla
     "g" para que el script imprima en la terminal el diccionario
     "espacios" completo, listo para copiar y pegar en tu script
     principal (deteccion_camara.py).
  6. Presiona "q" en cualquier momento para salir sin guardar.

Requisitos: los mismos que el script principal (opencv-python, numpy,
requests solo si usas ESP32-CAM).
"""

import cv2
import numpy as np
import urllib.request

# ============================================================
# CONFIGURACION - debe ser la MISMA URL que usas en deteccion_camara.py
# ============================================================
URL_CAPTURE = "http://10.117.114.53/capture"

# Nombres de los espacios que vas a calibrar, en el orden en que los vas
# a clickear. Ajusta esta lista a los que realmente ves en tu maqueta.
NOMBRES_ESPACIOS = ["1", "2", "3", "4", "5", "6", "7"]


def capturar_imagen():
    """Captura una imagen de la ESP32-CAM (o camara configurada)."""
    try:
        response = urllib.request.urlopen(URL_CAPTURE, timeout=3)
        img_array = np.asarray(bytearray(response.read()), dtype=np.uint8)
        frame = cv2.imdecode(img_array, cv2.IMREAD_COLOR)
        return frame
    except Exception as e:
        print(f"Error al capturar: {e}")
        return None


print("=" * 60)
print("CALIBRADOR DE ESPACIOS - SGEI UTP")
print("=" * 60)
print("Capturando una foto de la camara...")

frame_original = capturar_imagen()

if frame_original is None:
    print("No se pudo capturar ninguna imagen. Revisa URL_CAPTURE y que la camara este encendida.")
    raise SystemExit(1)

print(f"Resolucion real capturada: {frame_original.shape[1]}x{frame_original.shape[0]}")

# Se estira siempre a 1280x720, igual que el script principal.
frame_base = cv2.resize(frame_original, (1280, 720))

espacios_calibrados = {}
puntos_actuales = []
indice_espacio = 0

COLORES = [
    (0, 255, 0), (255, 0, 0), (0, 0, 255), (0, 255, 255),
    (255, 0, 255), (255, 255, 0), (128, 255, 0), (0, 128, 255),
]


def redibujar():
    """Redibuja el frame base con todos los poligonos ya calibrados y
    los puntos que se han marcado del espacio actual."""
    img = frame_base.copy()

    for i, (nombre, poligono) in enumerate(espacios_calibrados.items()):
        color = COLORES[i % len(COLORES)]
        pts = np.array(poligono, dtype=np.int32)
        cv2.polylines(img, [pts], True, color, 2)
        cx = int(sum(p[0] for p in poligono) / len(poligono))
        cy = int(sum(p[1] for p in poligono) / len(poligono))
        cv2.putText(img, nombre, (cx - 10, cy), cv2.FONT_HERSHEY_SIMPLEX, 0.7, color, 2)

    for punto in puntos_actuales:
        cv2.circle(img, punto, 5, (0, 0, 255), -1)

    if len(puntos_actuales) > 1:
        for i in range(len(puntos_actuales) - 1):
            cv2.line(img, puntos_actuales[i], puntos_actuales[i + 1], (0, 0, 255), 1)

    if indice_espacio < len(NOMBRES_ESPACIOS):
        texto = f"Marcando espacio '{NOMBRES_ESPACIOS[indice_espacio]}' - punto {len(puntos_actuales) + 1}/4"
    else:
        texto = "Todos los espacios listos. Presiona 'g' para generar el codigo."

    cv2.rectangle(img, (0, 0), (1280, 30), (0, 0, 0), -1)
    cv2.putText(img, texto, (10, 20), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 1)
    cv2.putText(img, "Clic=punto | z=deshacer | g=generar codigo | q=salir",
                (10, 700), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1)

    cv2.imshow("Calibrador de espacios", img)


def click_event(event, x, y, flags, param):
    global puntos_actuales, indice_espacio

    if event == cv2.EVENT_LBUTTONDOWN:
        if indice_espacio >= len(NOMBRES_ESPACIOS):
            return

        puntos_actuales.append((x, y))

        if len(puntos_actuales) == 4:
            nombre = NOMBRES_ESPACIOS[indice_espacio]
            espacios_calibrados[nombre] = puntos_actuales.copy()
            print(f"Espacio '{nombre}' guardado: {puntos_actuales}")
            puntos_actuales = []
            indice_espacio += 1

        redibujar()


cv2.namedWindow("Calibrador de espacios", cv2.WINDOW_NORMAL)
cv2.resizeWindow("Calibrador de espacios", 1280, 720)
cv2.setMouseCallback("Calibrador de espacios", click_event)
redibujar()

while True:
    key = cv2.waitKey(20) & 0xFF

    if key == ord('q'):
        print("Saliendo sin generar el codigo final.")
        break

    if key == ord('z'):
        if puntos_actuales:
            puntos_actuales.pop()
        elif indice_espacio > 0:
            indice_espacio -= 1
            nombre = NOMBRES_ESPACIOS[indice_espacio]
            puntos_actuales = espacios_calibrados.pop(nombre)
            puntos_actuales.pop()
        redibujar()

    if key == ord('g'):
        if not espacios_calibrados:
            print("Todavia no calibraste ningun espacio.")
            continue

        print()
        print("=" * 60)
        print("COPIA ESTE BLOQUE Y REEMPLAZA 'espacios' EN deteccion_camara.py")
        print("=" * 60)
        print("espacios = {")
        for nombre, poligono in espacios_calibrados.items():
            puntos_texto = ", ".join(f"({x}, {y})" for x, y in poligono)
            print(f'    "{nombre}": [{puntos_texto}],')
        print("}")
        print("=" * 60)

cv2.destroyAllWindows()