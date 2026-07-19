# Implementacion Java: Apache POI y Logback

Este proyecto web existente funciona con PHP, JavaScript y MySQL en XAMPP. Para cumplir el requisito Java/Maven sin romper la aplicacion actual, se agrego un modulo Java en la raiz del repositorio.

## Librerias agregadas

Las dependencias estan en `pom.xml`:

- `org.apache.poi:poi-ooxml`: genera archivos Excel `.xlsx`.
- `ch.qos.logback:logback-classic`: escribe logs en consola y archivo.
- `org.slf4j:slf4j-api`: API estandar para usar Logback.
- `com.mysql:mysql-connector-j`: permite leer los datos reales desde MySQL.
- `com.google.guava:guava`: usa colecciones inmutables para valores fijos del reporte.
- `org.apache.commons:commons-lang3`: simplifica el manejo seguro de textos recibidos por consola.

## Apache POI

Apache POI se usa porque permite crear documentos Excel desde Java sin depender de Microsoft Excel instalado. En este sistema sirve para exportar los vehiculos registrados desde la tabla `vehiculos` hacia un archivo profesional con columnas:

- Placa
- Hora Entrada
- Hora Salida
- Tiempo
- Pago

La clase principal es `ReporteExcelService`. Esta clase crea la hoja `Reporte Estacionamiento`, aplica estilos basicos, escribe los datos y guarda el archivo en `reportes/reporte_estacionamiento.xlsx`.

## Logback

Logback se usa porque es una implementacion robusta de logging para Java. Permite registrar eventos con niveles como `INFO`, `WARN` y `ERROR`, tanto en consola como en archivo.

La configuracion esta en `src/main/resources/logback.xml` y guarda los archivos en:

```text
logs/estacionamiento.log
```

Eventos soportados por el servicio `EventoSistemaService`:

- Inicio del sistema
- Registro de vehiculo
- Salida de vehiculo
- Error de conexion
- Error al registrar datos
- Advertencias del sistema

## Configuracion

La conexion a MySQL y la ruta del reporte estan en:

```text
src/main/resources/application.properties
```

Valores por defecto para XAMPP:

```properties
db.url=jdbc:mysql://localhost:3306/estacionamiento_utp?useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true
db.user=root
db.password=
report.output-dir=reportes
report.file-name=reporte_estacionamiento.xlsx
parking.hourly-rate=3.00
```

## Como ejecutar

1. Inicia Apache y MySQL desde XAMPP.
2. Importa `database.sql` en phpMyAdmin si aun no existe la base de datos.
3. Instala Maven si no esta disponible en la terminal.
4. Ejecuta:

```bash
mvn clean compile
mvn exec:java -Dexec.args="exportar-reporte"
```

Resultado esperado:

```text
Reporte generado correctamente: .../reportes/reporte_estacionamiento.xlsx
Registros exportados: N
```

Tambien se generara o actualizara:

```text
logs/estacionamiento.log
```

## Explicacion sencilla para exposicion

Apache POI convierte los datos del estacionamiento en un archivo Excel para que el administrador pueda revisar o entregar reportes sin copiar informacion manualmente.

Logback registra lo que ocurre dentro del sistema. Esto ayuda a saber cuando inicio la aplicacion, cuando se genero un reporte o si hubo errores de base de datos. En sistemas reales, los logs son importantes porque permiten diagnosticar fallas y auditar operaciones.

## Guava y Apache Commons

Google Guava se usa en `ReporteExcelService` para declarar como inmutable la lista de columnas del Excel. Esto evita cambios accidentales en la estructura del reporte durante la ejecucion.

Apache Commons Lang se usa en `App` para normalizar el comando recibido por consola y aplicar el comando por defecto cuando el valor llega vacio.

## Beneficios para el sistema

- Automatiza reportes administrativos.
- Evita errores de copia manual.
- Genera archivos compatibles con Excel.
- Registra eventos importantes del sistema.
- Ayuda a detectar errores de conexion o de datos.
- Mantiene el codigo separado por responsabilidades.
