# 🔧 INSTALACIÓN DE MAVEN - Guía Completa

## ¿Qué es Maven?

Maven es una herramienta de gestión de proyectos que:
- Descarga dependencias automáticamente
- Compila el código Java
- Ejecuta la aplicación
- Gestiona versiones

Sin Maven, necesitarías descargar manualmente cientos de librerías. Con Maven, todo es automático.

---

## 📋 REQUISITOS PREVIOS

1. **Java 17 o superior** (obligatorio)
2. **Conexión a internet** (para descargar Maven y dependencias)
3. **PowerShell o CMD** (terminal)
4. **Escritura en C:\ o la carpeta donde instales Maven**

### Verificar que tienes Java instalado:
```powershell
java -version
```

Deberías ver algo como:
```
java version "17.0.5" 2022-10-18 LTS
Java(TM) SE Runtime Environment (build 17.0.5+8-LTS-191)
```

**Si no ves esto, ve a: https://www.oracle.com/java/technologies/downloads/#java17**

---

## 🚀 OPCIÓN 1: Instalación Rápida (RECOMENDADO)

### Paso 1: Descargar Maven

Ve a: https://maven.apache.org/download.cgi

Descarga el archivo: **apache-maven-3.9.6-bin.zip** (o versión más reciente)

### Paso 2: Extraer el archivo

1. Crea una carpeta: `C:\apache-maven\`
2. Extrae el ZIP dentro de esa carpeta
3. Deberías tener: `C:\apache-maven\apache-maven-3.9.6\`

### Paso 3: Agregar a Variables de Entorno

#### En Windows 10/11:

**Opción A: Usando PowerShell (Automático)**

Abre **PowerShell como Administrador** y ejecuta:

```powershell
# Establecer variable MAVEN_HOME
[Environment]::SetEnvironmentVariable('MAVEN_HOME', 'C:\apache-maven\apache-maven-3.9.6', 'Machine')

# Agregar Maven al PATH
$env:Path += ';C:\apache-maven\apache-maven-3.9.6\bin'
[Environment]::SetEnvironmentVariable('Path', $env:Path, 'Machine')

# Verificar
java -version
mvn -version
```

**Opción B: Manual (Por interfaz gráfica)**

1. Presiona **Win + X**, selecciona **"Sistema"**
2. En la izquierda, haz clic en **"Configuración avanzada del sistema"**
3. Haz clic en **"Variables de entorno"**
4. En **"Variables del sistema"**, haz clic en **"Nueva"**
   - Nombre: `MAVEN_HOME`
   - Valor: `C:\apache-maven\apache-maven-3.9.6`
   - Haz clic en **"Aceptar"**

5. En **"Variables del sistema"**, busca `Path` y haz doble clic
6. Haz clic en **"Nuevo"**
7. Escribe: `C:\apache-maven\apache-maven-3.9.6\bin`
8. Haz clic en **"Aceptar"** dos veces

9. **Reinicia PowerShell** (ciérralo y abre uno nuevo)

### Paso 4: Verificar Instalación

Abre una **NUEVA ventana de PowerShell** y ejecuta:

```powershell
mvn -version
```

Deberías ver:
```
Apache Maven 3.9.6 (...)
Maven home: C:\apache-maven\apache-maven-3.9.6
Java version: 17.0.5
```

✅ **¡Maven está instalado!**

---

## 🚀 OPCIÓN 2: Usar el Wrapper de Maven (Sin instalar)

Si no quieres instalar Maven globalmente, Maven incluye un "wrapper" (mvnw) que descarga Maven automáticamente.

### En el directorio del proyecto:

```powershell
cd C:\xampp\htdocs\estacionamiento-utp

# En Windows:
.\mvnw.cmd clean install -DskipTests
.\mvnw.cmd spring-boot:run

# En Linux/Mac:
./mvnw clean install -DskipTests
./mvnw spring-boot:run
```

**Nota:** La primera ejecución tardará más porque descarga Maven.

---

## 🎯 COMPILAR EL PROYECTO

Una vez que Maven esté instalado:

### Paso 1: Abrir PowerShell en el directorio del proyecto

```powershell
cd C:\xampp\htdocs\estacionamiento-utp
```

### Paso 2: Compilar

```powershell
mvn clean install -DskipTests
```

**Salida esperada:**
```
[INFO] Scanning for projects...
[INFO] --< pe.utp:estacionamiento-utp >--
[INFO] Building Sistema de Estacionamiento UTP 1.0.0
...
[INFO] BUILD SUCCESS
[INFO] Total time: 2 m 45 s
[INFO] Finished at: 2024-01-12T14:30:45
```

⏱️ **Tiempo:** 2-5 minutos (depende de tu internet)

### Paso 3: Ejecutar la aplicación

```powershell
mvn spring-boot:run
```

O alternativamente:

```powershell
java -jar target/estacionamiento-utp-1.0.0.jar
```

---

## 🎯 VERIFICAR QUE FUNCIONA

Cuando veas esto en la consola:

```
============================================================
INICIO DEL SISTEMA DE ESTACIONAMIENTO - [2024-01-12 14:30:45]
Modulo de Monitoreo: ACTIVO
Actuator: DISPONIBLE en http://localhost:8080/actuator
Dashboard: DISPONIBLE en http://localhost:8080/monitoring
============================================================
```

✅ **La aplicación está corriendo.**

Abre tu navegador y ve a:

```
http://localhost:8080/monitoring
```

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### **Problema 1: "mvn: The term 'mvn' is not recognized"**

**Causa:** Maven no está en el PATH o necesitas reiniciar PowerShell.

**Solución:**
```powershell
# Opción 1: Reinicia PowerShell y intenta de nuevo

# Opción 2: Verifica la ruta
echo $env:Path | Select-String "apache-maven"

# Opción 3: Agrega manualmente la ruta
$env:Path += ';C:\apache-maven\apache-maven-3.9.6\bin'
mvn -version
```

### **Problema 2: "JAVA_HOME is not defined"**

**Causa:** Java no está configurado correctamente.

**Solución:**
```powershell
# Encuentra dónde está instalado Java:
Get-Command java

# Debería estar en algo como: C:\Program Files\Java\jdk-17.x.x\bin\java.exe

# Agrega la variable JAVA_HOME:
[Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Java\jdk-17.x.x', 'Machine')

# Reinicia PowerShell y verifica:
java -version
mvn -version
```

### **Problema 3: "Connection refused" o timeout**

**Causa:** Problemas de internet al descargar dependencias.

**Solución:**
```powershell
# Intenta de nuevo (Maven cachea las descargas):
mvn clean install -DskipTests -U

# O usa un proxy si estás detrás de un firewall
mvn -Dhttp.proxyHost=proxy.example.com -Dhttp.proxyPort=8080 clean install
```

### **Problema 4: Build falla con errores de compilación**

**Causa:** Problemas en el código Java.

**Solución:**
```powershell
# Reconstruir desde cero:
mvn clean install -DskipTests -X

# Busca la línea que empieza con [ERROR]
# Eso te dirá cuál es el problema
```

### **Problema 5: Puerto 8080 en uso**

**Causa:** Otra aplicación está usando el puerto.

**Solución:**
```powershell
# Opción 1: Matar el proceso que usa 8080
Get-Process | Where-Object {$_.ProcessName -eq "java"} | Stop-Process -Force

# Opción 2: Cambiar el puerto en application.properties
# Edita: src/main/resources/application.properties
# Cambia: server.port=8081

# Luego recompila:
mvn clean install
mvn spring-boot:run
```

---

## 📊 Estructura de carpetas después de instalar Maven

```
C:\apache-maven\
├── apache-maven-3.9.6\
│   ├── bin\
│   │   ├── mvn.cmd          ← Esto es lo que usarás
│   │   └── mvn
│   ├── lib\
│   └── ...
```

Después de descargar dependencias:
```
C:\Users\TuUsuario\.m2\repository\
├── org\
│   ├── springframework\
│   └── apache\
├── com\
│   └── mysql\
└── ...  (cientos de librerías)
```

---

## ⚡ Comandos Útiles de Maven

```powershell
# Compilar solo (sin ejecutar tests)
mvn clean compile

# Compilar y empaquetar en JAR
mvn clean package -DskipTests

# Ejecutar la aplicación
mvn spring-boot:run

# Ejecutar tests
mvn test

# Limpiar compilaciones anteriores
mvn clean

# Ver versiones instaladas
mvn -version

# Actualizar todas las dependencias
mvn clean install -U

# Compilar con más detalles (debug)
mvn clean install -X

# Instalar dependencia desde archivo local
mvn install:install-file -Dfile=mi-libreria.jar -DgroupId=com.ejemplo -DartifactId=mi-lib -Dversion=1.0 -Dpackaging=jar
```

---

## 🌐 Configurar Proxy (Si está detrás de firewall)

Si tu empresa usa proxy:

**Opción 1: Por línea de comandos**
```powershell
mvn -Dhttp.proxyHost=proxy.example.com -Dhttp.proxyPort=8080 -Dhttp.proxyUser=username -Dhttp.proxyPassword=password clean install
```

**Opción 2: En archivo settings.xml**

Edita: `C:\apache-maven\apache-maven-3.9.6\conf\settings.xml`

Busca la sección `<proxies>` y agrega:
```xml
<proxy>
  <active>true</active>
  <protocol>http</protocol>
  <host>proxy.example.com</host>
  <port>8080</port>
  <username>tusuario</username>
  <password>tucontraseña</password>
</proxy>
```

---

## ✅ CHECKLIST FINAL

- [ ] Java 17+ instalado y verificado (`java -version`)
- [ ] Maven descargado de https://maven.apache.org
- [ ] Maven extraído en C:\apache-maven\
- [ ] Variables de entorno configuradas (MAVEN_HOME y PATH)
- [ ] PowerShell reiniciado después de cambiar variables
- [ ] `mvn -version` funciona
- [ ] Proyecto compilado sin errores (`mvn clean install`)
- [ ] Aplicación ejecutándose (`mvn spring-boot:run`)
- [ ] Dashboard accesible en http://localhost:8080/monitoring

---

## 📞 Soporte

Si tienes problemas:

1. **Verifica que Java esté instalado:** `java -version`
2. **Verifica que Maven esté en PATH:** `mvn -version`
3. **Lee los mensajes de error** - suelen indicar el problema
4. **Intenta compilar de nuevo:** `mvn clean install`
5. **Consulta:** https://maven.apache.org/guides/

---

## 🎓 Próximos Pasos

Una vez que Maven esté funcionando:

1. Compila el proyecto: `mvn clean install -DskipTests`
2. Ejecuta la aplicación: `mvn spring-boot:run`
3. Abre: http://localhost:8080/monitoring
4. Sigue la [GUIA_EJECUCION_Y_CAPTURAS.md](./GUIA_EJECUCION_Y_CAPTURAS.md) para capturar pantallas

---

**¡Maven instalado correctamente! 🎉**

Ahora puedes compilar y ejecutar el proyecto sin problemas.

