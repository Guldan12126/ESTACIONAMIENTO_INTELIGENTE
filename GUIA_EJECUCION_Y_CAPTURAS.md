# 📋 GUÍA DE EJECUCIÓN - SISTEMA DE ESTACIONAMIENTO UTP

## 🚀 PASO 1: REQUISITOS PREVIOS

Asegúrate de tener:
- **Java 17** instalado y configurado en PATH
- **Maven** instalado y configurado en PATH  
- **MySQL** en ejecución con base de datos `estacionamiento_utp`
- **Terminal/PowerShell** con acceso al directorio del proyecto

### Verificar Instalación
```powershell
java -version
mvn -version
mysql --version
```

---

## 🔧 PASO 2: COMPILAR EL PROYECTO

Abre una terminal **PowerShell** en el directorio raíz del proyecto y ejecuta:

```powershell
cd C:\xampp\htdocs\estacionamiento-utp
mvn clean install -DskipTests
```

**Tiempo esperado:** 2-5 minutos (depende de las descargas de Maven)

**Resultado esperado:** 
```
[INFO] BUILD SUCCESS
[INFO] Total time: X.XXX s
```

---

## 🎯 PASO 3: EJECUTAR EL PROYECTO

### Opción 1: Desde Maven (RECOMENDADO)
```powershell
mvn spring-boot:run
```

### Opción 2: Desde JAR compilado
```powershell
java -jar target/estacionamiento-utp-1.0.0.jar
```

---

## 📊 PASO 4: VERIFICAR QUE TODO FUNCIONA

Cuando veas esto en la consola:
```
============================================================
INICIO DEL SISTEMA DE ESTACIONAMIENTO - [YYYY-MM-DD HH:MM:SS]
Modulo de Monitoreo: ACTIVO
Actuator: DISPONIBLE en http://localhost:8080/actuator
Dashboard: DISPONIBLE en http://localhost:8080/monitoring
============================================================
```

**El sistema está listo para usar.**

---

## 🖥️ PASO 5: ABRIR NAVEGADOR Y ACCEDER A LOS ENDPOINTS

Abre un navegador web y accede a los siguientes URLs:

### 📈 Dashboard de Monitoreo (PRINCIPAL)
```
http://localhost:8080/monitoring
```
**Qué verás:** Dashboard interactivo con todas las métricas del sistema en tiempo real.

---

### 🔍 ENDPOINTS DE ACTUATOR

#### 1. Raíz de Actuator
```
http://localhost:8080/actuator
```
**Qué verás:** Lista de todos los endpoints disponibles.

---

#### 2. Estado del Sistema
```
http://localhost:8080/actuator/health
```
**Qué verás:** Estado de la aplicación y componentes:
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

---

#### 3. Información de la Aplicación
```
http://localhost:8080/actuator/info
```
**Qué verás:** Versión, descripción y detalles de la aplicación.

---

#### 4. Métricas del Sistema
```
http://localhost:8080/actuator/metrics
```
**Qué verás:** Lista completa de métricas disponibles.

Para ver una métrica específica:
```
http://localhost:8080/actuator/metrics/jvm.memory.used
```

---

#### 5. Variables de Entorno
```
http://localhost:8080/actuator/env
```
**Qué verás:** Todas las variables de entorno y propiedades configuradas.

---

#### 6. Beans de Spring
```
http://localhost:8080/actuator/beans
```
**Qué verás:** Todos los beans registrados en el contexto de Spring.

---

#### 7. Métricas de Prometheus (IMPORTANTE para Grafana)
```
http://localhost:8080/actuator/prometheus
```
**Qué verás:** Métricas en formato Prometheus (texto plano).

---

## 🎬 PASO 6: GENERAR LOGS DE DEMOSTRACIÓN

En el Dashboard de Monitoreo, haz clic en el botón:
```
"Generar logs demo"
```

Esto ejecutará automáticamente:
- ✅ Login exitoso
- ✅ Registro de usuario
- ✅ Registro de vehículo
- ✅ Entrada de vehículo
- ✅ Salida de vehículo
- ✅ Reserva de espacio
- ✅ Pago registrado
- ✅ Advertencia
- ✅ Error de demostración

**Resultado:** Verás logs en la consola y en `logs/estacionamiento.log`

---

## 👁️ PASO 7: REVISAR LOS LOGS EN ARCHIVOS

Los logs se guardan en:

### Archivo de logs principal:
```
C:\xampp\htdocs\estacionamiento-utp\logs\estacionamiento.log
```

### Archivo solo de errores:
```
C:\xampp\htdocs\estacionamiento-utp\logs\errores.log
```

### Ver en tiempo real desde PowerShell:
```powershell
Get-Content -Path logs/estacionamiento.log -Tail 20 -Wait
```

---

## 📸 LISTA COMPLETA DE CAPTURAS A TOMAR (INFORME)

### **SECCIÓN 1: DASHBOARD PRINCIPAL**
1. ✅ **Dashboard de Monitoreo Completo** (`http://localhost:8080/monitoring`)
   - Mostrar todas las métricas visibles

2. ✅ **Consola con logs INFO** (después de generar logs demo)
   - Mostrar los [INFO] logs en la consola de ejecución

3. ✅ **Consola con logs WARN** 
   - Filtrar o mostrar los [WARN] logs

4. ✅ **Consola con logs ERROR**
   - Mostrar los [ERROR] logs generados

---

### **SECCIÓN 2: ENDPOINTS DE ACTUATOR**
5. ✅ **Página /actuator** (`http://localhost:8080/actuator`)
   - Mostrar lista de endpoints disponibles

6. ✅ **Página /actuator/health** 
   - Mostrar estado UP y componentes

7. ✅ **Página /actuator/info**
   - Mostrar información de la aplicación

8. ✅ **Página /actuator/metrics**
   - Mostrar lista de métricas disponibles

9. ✅ **Página /actuator/metrics/jvm.memory.used** (ejemplo de métrica específica)
   - Mostrar datos de memoria JVM

---

### **SECCIÓN 3: DATOS DETALLADOS**
10. ✅ **Página /actuator/env**
    - Mostrar configuración y variables de entorno

11. ✅ **Página /actuator/beans**
    - Mostrar lista de beans registrados

12. ✅ **Página /actuator/prometheus**
    - Mostrar métricas en formato Prometheus

---

### **SECCIÓN 4: ARCHIVOS DE LOG**
13. ✅ **Archivo logs/estacionamiento.log abierto en editor**
    - Mostrar contenido con logs INFO, WARN y ERROR

14. ✅ **Archivo logs/errores.log abierto en editor**
    - Mostrar contenido solo con errores

---

### **SECCIÓN 5: CONFIGURACIÓN**
15. ✅ **Archivo application.properties** 
    - Mostrar configuración de la aplicación

16. ✅ **Archivo logback-spring.xml**
    - Mostrar configuración de Logback

17. ✅ **Archivo pom.xml**
    - Mostrar dependencias (especialmente Spring Boot Actuator, Prometheus, Logback)

---

### **SECCIÓN 6: ESTRUCTURA DEL PROYECTO**
18. ✅ **Estructura de carpetas** (en IDE o explorador)
    - Mostrar la estructura completa del proyecto

19. ✅ **Archivo EventoSistemaService.java**
    - Mostrar métodos de logging implementados

20. ✅ **Archivo MonitoringService.java**
    - Mostrar métodos de recolección de métricas

---

## 🎯 RESUMEN DE PANTALLAS PARA EL INFORME

```
TABLA RESUMEN:
┌────┬──────────────────────────────────────────────────────┬──────────────────────────┐
│ #  │ PANTALLA / ELEMENTO                                  │ URL o ARCHIVO            │
├────┼──────────────────────────────────────────────────────┼──────────────────────────┤
│ 1  │ Dashboard de Monitoreo Completo                      │ /monitoring              │
│ 2  │ Logs INFO en Consola                                 │ PowerShell/Terminal      │
│ 3  │ Logs WARN en Consola                                 │ PowerShell/Terminal      │
│ 4  │ Logs ERROR en Consola                                │ PowerShell/Terminal      │
│ 5  │ Página /actuator                                     │ /actuator                │
│ 6  │ Página /actuator/health                              │ /actuator/health         │
│ 7  │ Página /actuator/info                                │ /actuator/info           │
│ 8  │ Página /actuator/metrics                             │ /actuator/metrics        │
│ 9  │ Página /actuator/metrics/jvm.memory.used             │ /actuator/metrics/jvm... │
│ 10 │ Página /actuator/env                                 │ /actuator/env            │
│ 11 │ Página /actuator/beans                               │ /actuator/beans          │
│ 12 │ Página /actuator/prometheus                          │ /actuator/prometheus     │
│ 13 │ Archivo estacionamiento.log                          │ logs/estacionamiento.log │
│ 14 │ Archivo errores.log                                  │ logs/errores.log         │
│ 15 │ Archivo application.properties                       │ src/main/resources/...   │
│ 16 │ Archivo logback-spring.xml                           │ src/main/resources/...   │
│ 17 │ Archivo pom.xml (dependencias)                       │ pom.xml                  │
│ 18 │ Estructura de carpetas del proyecto                  │ Explorador de archivos   │
│ 19 │ Código: EventoSistemaService.java                    │ src/main/java/pe/utp/... │
│ 20 │ Código: MonitoringService.java                       │ src/main/java/pe/utp/... │
└────┴──────────────────────────────────────────────────────┴──────────────────────────┘
```

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### **Error: Puerto 8080 en uso**
```powershell
# Cambiar puerto en application.properties:
server.port=8080
```

### **Error: No se conecta a MySQL**
```
- Verificar que MySQL esté corriendo
- Verificar credenciales en application.properties
- Verificar que la base de datos exista: estacionamiento_utp
```

### **Error: No se generan logs**
```
- Verificar permisos de escritura en carpeta /logs
- Revisar logback-spring.xml
- Verificar que logging.level esté en INFO
```

### **Maven no se encuentra**
```powershell
# Agregar Maven al PATH o usar el wrapper:
./mvnw clean install
./mvnw spring-boot:run
```

---

## ✅ CHECKLIST FINAL

- [ ] Java 17 instalado y verificado
- [ ] MySQL corriendo con base de datos correcta
- [ ] Proyecto compilado sin errores
- [ ] Aplicación iniciada correctamente
- [ ] Dashboard accesible en /monitoring
- [ ] Logs de demostración generados
- [ ] Todos los endpoints de Actuator funcionando
- [ ] Archivos de log creados en carpeta /logs
- [ ] Capturas tomadas y guardadas

---

## 📞 PREGUNTAS FRECUENTES

**P: ¿Cuánto tarda en iniciar?**
R: Normalmente 5-10 segundos después de compilar.

**P: ¿Dónde se guardan los logs?**
R: En la carpeta `/logs` (se crea automáticamente).

**P: ¿Puedo cambiar el puerto?**
R: Sí, en `application.properties`, propiedad `server.port`.

**P: ¿Cómo reinicio el servidor?**
R: Presiona `Ctrl+C` en la terminal y ejecuta `mvn spring-boot:run` nuevamente.

---

## 📖 ESTRUCTURA DE CARPETAS DEL PROYECTO

```
estacionamiento-utp/
├── src/
│   ├── main/
│   │   ├── java/pe/utp/estacionamiento/
│   │   │   ├── App.java (Clase principal)
│   │   │   ├── config/
│   │   │   │   ├── AppConfig.java
│   │   │   │   ├── DatabaseConfig.java
│   │   │   │   ├── MonitoringMetricsConfig.java
│   │   │   │   └── RequestLoggingFilter.java
│   │   │   ├── controller/
│   │   │   │   └── MonitoringController.java
│   │   │   ├── model/
│   │   │   │   ├── MonitoringSnapshot.java
│   │   │   │   └── ReporteVehiculo.java
│   │   │   ├── repository/
│   │   │   │   └── VehiculoRepository.java
│   │   │   └── service/
│   │   │       ├── EventoSistemaService.java (LOGS)
│   │   │       ├── MonitoringService.java (MÉTRICAS)
│   │   │       └── ReporteExcelService.java
│   │   └── resources/
│   │       ├── application.properties (CONFIGURACIÓN)
│   │       ├── logback-spring.xml (CONFIGURACIÓN DE LOGS)
│   │       └── templates/
│   │           └── monitoring.html (DASHBOARD)
│   └── test/
├── pom.xml (DEPENDENCIAS)
├── logs/
│   ├── estacionamiento.log
│   └── errores.log
├── reportes/
├── GUIA_EJECUCION_Y_CAPTURAS.md (Este archivo)
└── README.md
```

---

**¡Tu sistema de estacionamiento está listo para demostrarse!** 🎉

