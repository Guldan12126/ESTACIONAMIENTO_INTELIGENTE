# 📸 LISTA COMPLETA DE CAPTURAS PARA EL INFORME

## 🎯 Resumen Ejecutivo

Esta es la lista de **20 capturas de pantalla** que debes tomar para demostrar que el sistema de monitoreo está completamente funcional.

**Tiempo estimado:** 15-20 minutos  
**Herramientas necesarias:** Navegador web (Chrome/Firefox) + PowerShell  
**Archivo de salida:** Informe con todas las capturas

---

## 📋 SECCIÓN 1: DASHBOARD PRINCIPAL (2 capturas)

### Captura #1: Dashboard de Monitoreo Completo
```
URL: http://localhost:8080/monitoring
```
**Qué debe mostrar:**
- ✅ Barra superior azul con estado "ONLINE"
- ✅ 4 tarjetas principales: Disponibilidad, CPU, RAM, Tiempo de respuesta
- ✅ 6 tarjetas de negocio: Usuarios activos, Vehículos, Espacios, Reservas, Pagos, Errores
- ✅ Sección de accesos rápidos a endpoints
- ✅ Sección de Prometheus
- ✅ Últimos logs del sistema

**Cómo capturar:**
```powershell
# Abre el navegador
start http://localhost:8080/monitoring

# Presiona: Print Screen o Ctrl+Shift+S (Windows 11)
# O usa las herramientas del navegador (F12 → Screenshot)
```

---

### Captura #2: Dashboard Completo (Scroll hasta abajo)
```
URL: http://localhost:8080/monitoring (scroll down)
```
**Qué debe mostrar:**
- ✅ Sección "Últimos Logs del Sistema"
- ✅ Caja con logs en color negro y texto verde/amarillo/rojo
- ✅ Información de Prometheus y Grafana
- ✅ Footer con timestamp de actualización

**Cómo capturar:**
```
En la misma página de /monitoring, haz scroll hacia abajo
Presiona: Print Screen
```

---

## 📊 SECCIÓN 2: CONSOLA CON LOGS (3 capturas)

### Captura #3: Consola mostrando Logs INFO
```
Ubicación: PowerShell/Terminal ejecutando mvn spring-boot:run
```
**Qué debe mostrar:**
- ✅ Múltiples líneas con `[INFO]`
- ✅ Timestamp: `yyyy-MM-dd HH:mm:ss.SSS`
- ✅ Ejemplos:
  - `[INFO] [LOGIN] Acceso exitoso - usuario: ...`
  - `[INFO] [VEHICULO-REGISTRO] Nuevo vehiculo registrado - placa: ...`
  - `[INFO] [ENTRADA-VEHICULO] Acceso al estacionamiento ...`

**Cómo capturar:**
```powershell
# En la terminal donde corre mvn spring-boot:run
# Haz clic en "Generar logs demo" en el dashboard

# Selecciona el texto de logs INFO con el ratón
# Presiona: Print Screen o Ctrl+Shift+S
```

**Nota:** Los logs aparecerán después de hacer clic en "Generar Logs Demo" en el dashboard.

---

### Captura #4: Consola mostrando Logs WARN
```
Ubicación: PowerShell/Terminal - misma consola anterior
```
**Qué debe mostrar:**
- ✅ Líneas con `[WARN]` o `ADVERTENCIA`
- ✅ Ejemplos:
  - `[WARN] [LOGIN-FALLIDO] Intento fallido ...`
  - `[WARN] [ENTRADA-RECHAZADA] Acceso denegado ...`
  - `[WARN] [ADVERTENCIA-CAPACIDAD] Estacionamiento lleno ...`

**Cómo capturar:**
```powershell
# La misma consola que en captura #3
# Los WARN están mezclados con los INFO
# Selecciona las líneas que empiezan con [WARN]
# Presiona: Print Screen
```

---

### Captura #5: Consola mostrando Logs ERROR
```
Ubicación: PowerShell/Terminal - misma consola anterior
```
**Qué debe mostrar:**
- ✅ Líneas con `[ERROR]` en color rojo
- ✅ Ejemplos:
  - `[ERROR] [PAGO-FALLIDO] Error en transaccion ...`
  - `[EXCEPCION] Excepcion no controlada ...`
  - `[ERROR-DB] Fallo de conexion con base de datos ...`
  - El ERROR de demostración generado automáticamente

**Cómo capturar:**
```powershell
# Busca en la consola líneas con [ERROR]
# Selecciona varias líneas con errores
# Presiona: Print Screen
```

---

## 🔗 SECCIÓN 3: ENDPOINTS DE ACTUATOR (8 capturas)

### Captura #6: Página /actuator (Raíz de Actuator)
```
URL: http://localhost:8080/actuator
```
**Qué debe mostrar:**
```json
{
  "_links": {
    "self": { "href": "http://localhost:8080/actuator" },
    "health": { "href": "http://localhost:8080/actuator/health" },
    "info": { "href": "http://localhost:8080/actuator/info" },
    "metrics": { "href": "http://localhost:8080/actuator/metrics" },
    ...
  }
}
```

**Cómo capturar:**
```
Haz clic en el botón "/actuator" del dashboard
O ve a http://localhost:8080/actuator
Presiona: Print Screen o Ctrl+Shift+S
```

---

### Captura #7: Página /actuator/health
```
URL: http://localhost:8080/actuator/health
```
**Qué debe mostrar:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```
✅ Estado debe ser `"UP"` (verde)

---

### Captura #8: Página /actuator/info
```
URL: http://localhost:8080/actuator/info
```
**Qué debe mostrar:**
```json
{
  "app": {
    "name": "Sistema de Estacionamiento UTP",
    "description": "Plataforma profesional de gestión de estacionamiento...",
    "version": "1.0.0",
    "author": "Equipo de Desarrollo UTP"
  },
  "java": {
    "version": "17"
  },
  "organization": {
    "name": "Universidad Tecnológica del Perú"
  }
}
```

---

### Captura #9: Página /actuator/metrics (Lista de métricas)
```
URL: http://localhost:8080/actuator/metrics
```
**Qué debe mostrar:**
```json
{
  "names": [
    "application.ready.time",
    "application.started.time",
    "jvm.buffer.count",
    "jvm.buffer.memory.used",
    "jvm.gc.collection.seconds",
    "jvm.memory.committed",
    "jvm.memory.max",
    "jvm.memory.usage",
    "jvm.memory.used",
    "process.cpu.usage",
    "process.uptime",
    "system.cpu.usage",
    ...
  ]
}
```
Deberías ver cientos de métricas disponibles.

---

### Captura #10: Página /actuator/metrics/jvm.memory.used
```
URL: http://localhost:8080/actuator/metrics/jvm.memory.used
```
**Qué debe mostrar:**
```json
{
  "name": "jvm.memory.used",
  "description": "The amount of used memory",
  "baseUnit": "bytes",
  "measurements": [
    {
      "statistic": "VALUE",
      "value": 245000000
    }
  ],
  "availableTags": [...]
}
```
Muestra el uso actual de memoria JVM.

---

### Captura #11: Página /actuator/env (Variables de entorno)
```
URL: http://localhost:8080/actuator/env
```
**Qué debe mostrar:**
- ✅ `spring.application.name`: `estacionamiento-utp`
- ✅ `server.port`: `8080`
- ✅ `spring.datasource.url`: `jdbc:mysql://localhost:3306/...`
- ✅ `logging.level...`: `INFO`
- ✅ `management.endpoints.web.exposure.include`: `health,info,metrics,env,beans,prometheus`

Scroll hasta encontrar estas propiedades.

---

### Captura #12: Página /actuator/beans (Lista de Beans)
```
URL: http://localhost:8080/actuator/beans
```
**Qué debe mostrar:**
```json
{
  "contexts": {
    "application": {
      "beans": {
        "monitoringController": {...},
        "eventoSistemaService": {...},
        "monitoringService": {...},
        ...
      }
    }
  }
}
```
- ✅ `monitoringController`
- ✅ `eventoSistemaService`
- ✅ `monitoringService`
- ✅ Otros beans de Spring

---

### Captura #13: Página /actuator/prometheus (Métricas Prometheus)
```
URL: http://localhost:8080/actuator/prometheus
```
**Qué debe mostrar:**
```
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="G1 Old Gen",} 1.23456E8

# HELP jvm_memory_max_bytes The maximum amount of memory available
# TYPE jvm_memory_max_bytes gauge
jvm_memory_max_bytes{area="heap",id="G1 Old Gen",} 2.0555776E9

...
```
- ✅ Formato de métricas en texto plano
- ✅ Compatible con Prometheus
- ✅ Cientos de líneas de métricas

---

## 📄 SECCIÓN 4: ARCHIVOS DE LOG (3 capturas)

### Captura #14: Archivo estacionamiento.log abierto
```
Archivo: logs/estacionamiento.log
Editor: Visual Studio Code o Notepad++
```
**Qué debe mostrar:**
```
2024-01-12 14:30:45.123 INFO  [main] p.u.e.App - ============================================================
2024-01-12 14:30:45.124 INFO  [main] p.u.e.App - INICIO DEL SISTEMA DE ESTACIONAMIENTO - [2024-01-12 14:30:45]
2024-01-12 14:30:45.125 INFO  [main] p.u.e.App - Modulo de Monitoreo: ACTIVO
2024-01-12 14:30:50.456 INFO  [http-nio-8080-exec-1] p.u.e.s.EventoSistemaService - [LOGIN] Acceso exitoso - usuario: u22210840
2024-01-12 14:30:50.789 INFO  [http-nio-8080-exec-1] p.u.e.s.EventoSistemaService - [VEHICULO-REGISTRO] Nuevo vehiculo registrado - placa: ABC123
2024-01-12 14:30:51.234 WARN  [http-nio-8080-exec-2] p.u.e.s.EventoSistemaService - [ADVERTENCIA-CAPACIDAD] Estacionamiento lleno
2024-01-12 14:30:51.567 ERROR [http-nio-8080-exec-3] p.u.e.s.EventoSistemaService - [EXCEPCION] Excepcion no controlada
```

**Cómo capturar:**
```powershell
# Abre el archivo en el editor
code logs/estacionamiento.log

# O con Notepad:
notepad logs/estacionamiento.log

# Presiona: Print Screen
```

---

### Captura #15: Archivo errores.log abierto
```
Archivo: logs/errores.log
Editor: Visual Studio Code o Notepad++
```
**Qué debe mostrar:**
- ✅ Solo líneas con ERROR
- ✅ Menos líneas que estacionamiento.log
- ✅ Ejemplos:
  ```
  2024-01-12 14:30:51.567 ERROR [thread-1] p.u.e.s.EventoSistemaService - [EXCEPCION] Excepcion no controlada - modulo: demo
  ```

---

### Captura #16: Vista de carpeta logs en explorador
```
Carpeta: C:\xampp\htdocs\estacionamiento-utp\logs\
Visualización: Explorador de archivos o VS Code
```
**Qué debe mostrar:**
- ✅ Archivo: `estacionamiento.log` (con tamaño)
- ✅ Archivo: `errores.log` (con tamaño)
- ✅ Ambos deben tener contenido (tamaño > 0)

---

## 📝 SECCIÓN 5: CÓDIGO FUENTE (2 capturas)

### Captura #17: EventoSistemaService.java - Métodos de Logging
```
Archivo: src/main/java/pe/utp/estacionamiento/service/EventoSistemaService.java
Editor: Visual Studio Code
```
**Qué debe mostrar:**
```java
@Service
public class EventoSistemaService {
    private static final Logger log = LoggerFactory.getLogger(EventoSistemaService.class);

    public void registrarLogin(String codigoUsuario) {
        log.info("[LOGIN] Acceso exitoso - usuario: {} - timestamp: {}", 
                codigoUsuario, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarVehiculo(String placa) {
        log.info("[VEHICULO-REGISTRO] Nuevo vehiculo registrado - placa: {} - timestamp: {}", 
                placa, LocalDateTime.now().format(TIMESTAMP));
    }
    
    // ... más métodos
}
```

**Cómo capturar:**
```powershell
# Abre en VS Code
code src/main/java/pe/utp/estacionamiento/service/EventoSistemaService.java

# Presiona: Print Screen
```

---

### Captura #18: MonitoringService.java - Métodos de Métricas
```
Archivo: src/main/java/pe/utp/estacionamiento/service/MonitoringService.java
Editor: Visual Studio Code
```
**Qué debe mostrar:**
```java
@Service
public class MonitoringService {
    private final JdbcTemplate jdbcTemplate;

    public MonitoringSnapshot snapshot() {
        boolean databaseAvailable = isDatabaseAvailable();
        return new MonitoringSnapshot(
                databaseAvailable ? "UP" : "DEGRADED",
                databaseAvailable,
                cpuUsage(),
                usedMemoryMb(),
                // ... más parámetros
        );
    }

    public long activeUsers() { ... }
    public long vehiculosRegistrados() { ... }
    public long espaciosDisponibles() { ... }
}
```

---

## ⚙️ SECCIÓN 6: CONFIGURACIÓN (2 capturas)

### Captura #19: application.properties
```
Archivo: src/main/resources/application.properties
Editor: Visual Studio Code o Notepad
```
**Qué debe mostrar:**
```properties
spring.application.name=estacionamiento-utp
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/estacionamiento_utp
management.endpoints.web.exposure.include=health,info,metrics,env,beans,prometheus
management.endpoint.health.show-details=always

logging.config=classpath:logback-spring.xml
logging.level.pe.utp.estacionamiento=INFO

app.monitoring.log-file=logs/estacionamiento.log
app.monitoring.error-log-file=logs/errores.log
```

---

### Captura #20: pom.xml - Dependencias
```
Archivo: pom.xml
Editor: Visual Studio Code o Notepad
```
**Qué debe mostrar:**
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-logging</artifactId>
    </dependency>

    <!-- Más dependencias -->
</dependencies>
```

---

## 🎬 RESUMEN DE CAPTURAS - TABLA RÁPIDA

```
┌────┬─────────────────────────────────────┬────────────────────────────────┐
│ #  │ NOMBRE DE CAPTURA                   │ URL / UBICACIÓN                │
├────┼─────────────────────────────────────┼────────────────────────────────┤
│ 1  │ Dashboard Principal Completo        │ http://localhost:8080/monitoring
│ 2  │ Dashboard - Scroll Abajo            │ http://localhost:8080/monitoring
│ 3  │ Logs INFO en Consola                │ PowerShell (después de demo)   │
│ 4  │ Logs WARN en Consola                │ PowerShell (después de demo)   │
│ 5  │ Logs ERROR en Consola               │ PowerShell (después de demo)   │
│ 6  │ /actuator (Raíz)                    │ http://localhost:8080/actuator │
│ 7  │ /actuator/health                    │ http://localhost:8080/actuator/health
│ 8  │ /actuator/info                      │ http://localhost:8080/actuator/info
│ 9  │ /actuator/metrics (lista)           │ http://localhost:8080/actuator/metrics
│ 10 │ /actuator/metrics/jvm.memory.used   │ http://localhost:8080/actuator/metrics/jvm.memory.used
│ 11 │ /actuator/env                       │ http://localhost:8080/actuator/env
│ 12 │ /actuator/beans                     │ http://localhost:8080/actuator/beans
│ 13 │ /actuator/prometheus                │ http://localhost:8080/actuator/prometheus
│ 14 │ Archivo estacionamiento.log         │ VS Code / Notepad              │
│ 15 │ Archivo errores.log                 │ VS Code / Notepad              │
│ 16 │ Carpeta /logs en Explorador         │ Explorador de archivos         │
│ 17 │ EventoSistemaService.java           │ VS Code                        │
│ 18 │ MonitoringService.java              │ VS Code                        │
│ 19 │ application.properties              │ VS Code                        │
│ 20 │ pom.xml - Dependencias              │ VS Code                        │
└────┴─────────────────────────────────────┴────────────────────────────────┘
```

---

## 🎯 ORDEN RECOMENDADO PARA TOMAR CAPTURAS

### Antes de empezar:
1. Asegúrate de que la aplicación está corriendo: `mvn spring-boot:run`
2. Abre una segunda ventana de PowerShell para ver los logs
3. Haz clic en "Generar Logs Demo" en el dashboard

### Capturas (en orden):
```
Paso 1: Capturas del Dashboard (1-2)
Paso 2: Espera 5 segundos y luego toma logs (3-5)
Paso 3: Abre endpoints en navegador y captura (6-13)
Paso 4: Abre archivos de log en editor (14-16)
Paso 5: Abre código fuente en VS Code (17-20)
```

---

## 💾 Cómo Guardar las Capturas

### Opción 1: Print Screen
```powershell
# Presiona Print Screen
# Abre Paint (Ctrl+Alt+P) o Word
# Pega con Ctrl+V
# Guarda como imagen
```

### Opción 2: Usar Herramienta de Captura (Windows 11)
```powershell
# Presiona: Windows + Shift + S
# Arrastra para seleccionar
# Se copia automáticamente
# Pega en Word o PowerPoint
```

### Opción 3: Herramienta de Captura Nativa
```powershell
# Presiona: Windows + Shift + S
# Selecciona "Captura de pantalla completa"
# Guarda el archivo
```

### Opción 4: Usar el Navegador
```
En Chrome/Firefox:
1. Presiona: F12 (DevTools)
2. Ctrl + Shift + P (Command Palette)
3. Escribe: "screenshot"
4. Selecciona: "Capture full page screenshot"
```

---

## 📊 Documento Final para el Informe

Cuando tengas todas las 20 capturas:

1. **Crea un documento Word** con secciones:
   - Introducción
   - Sección 1: Dashboard (2 capturas)
   - Sección 2: Logs (3 capturas)
   - Sección 3: Actuator (8 capturas)
   - Sección 4: Archivos (3 capturas)
   - Sección 5: Código (2 capturas)
   - Sección 6: Configuración (2 capturas)
   - Conclusión

2. **Agrega explicaciones** debajo de cada captura

3. **Añade una tabla resumen** al inicio

4. **Incluye timestamps** de ejecución

---

## ✅ CHECKLIST ANTES DE ENTREGAR

- [ ] 20 capturas tomadas y guardadas
- [ ] Todas las capturas en alta calidad (no pixeladas)
- [ ] Cada captura está en su sección correcta
- [ ] Se ve claramente el contenido (no oscuro)
- [ ] Los logs muestran datos reales del sistema
- [ ] Los endpoints muestran JSON válido
- [ ] Los archivos de log tienen contenido
- [ ] El código fuente es legible
- [ ] Las configuraciones están visibles
- [ ] Documento final está bien estructurado

---

## 🎓 NOTAS IMPORTANTES

✅ **Importante:** Asegúrate de que:
- La aplicación está en ejecución cuando tomas capturas
- Hiciste clic en "Generar Logs Demo" antes de capturar logs
- Esperas 2-3 segundos después de generar logs para que aparezcan en archivos
- Todos los URLs son correctos (http://localhost:8080)
- Las capturas muestran datos reales (no vacíos)

---

**¡Listo para tomar 20 capturas profesionales de tu sistema!** 📸🚀

