# Sistema de Gestión de Estacionamientos Inteligentes (SGEI)
## Universidad Tecnológica del Perú – Sede Piura

---

# Descripción del Proyecto

El Sistema de Gestión de Estacionamientos Inteligentes (SGEI) es una solución tecnológica desarrollada para optimizar la administración de los espacios de estacionamiento dentro de la Universidad Tecnológica del Perú (UTP) – sede Piura.

Actualmente, el control de estacionamientos se realiza de forma manual, generando demoras, desorganización y conflictos entre estudiantes, docentes y personal administrativo. Este proyecto busca automatizar dichos procesos mediante una plataforma digital que permita visualizar espacios disponibles en tiempo real, registrar vehículos, realizar reservas y generar reportes estadísticos.

El sistema está orientado a mejorar la experiencia de la comunidad universitaria, reducir el tiempo de búsqueda de estacionamientos y optimizar el uso de los recursos disponibles dentro del campus.

---

# Objetivos del Sistema

## Objetivo General

Desarrollar un sistema inteligente que automatice la gestión de estacionamientos en la UTP Piura mediante herramientas tecnológicas modernas.

## Objetivos Específicos

- Permitir el registro y autenticación de usuarios.
- Mostrar espacios disponibles en tiempo real.
- Gestionar reservas de estacionamientos.
- Generar reportes administrativos.
- Reducir tiempos de espera y congestión vehicular.
- Mejorar la organización y distribución de espacios.

---

# Problemática

Actualmente la UTP Piura presenta los siguientes problemas:

- Registro manual de ingresos y salidas.
- Pérdida de tiempo buscando estacionamiento.
- Uso ineficiente de espacios disponibles.
- Congestión vehicular en horas punta.
- Ausencia de estadísticas de ocupación.
- Conflictos entre usuarios por espacios reservados.

El SGEI busca solucionar estas dificultades mediante automatización y monitoreo digital.

---

# Alcance del Proyecto

El sistema permitirá:

- Registro de estudiantes, docentes y administradores.
- Registro de vehículos institucionales y personales.
- Visualización de disponibilidad de espacios.
- Reserva temporal de estacionamientos.
- Gestión de espacios ocupados y libres.
- Generación de reportes diarios y semanales.
- Administración de usuarios y configuraciones.

---

# Arquitectura del Sistema

El sistema seguirá una arquitectura cliente-servidor dividida en tres capas principales.

## Frontend

Interfaz gráfica desarrollada para que los usuarios interactúen con el sistema desde computadoras o dispositivos móviles.

Funciones:
- Inicio de sesión
- Registro de vehículos
- Visualización de estacionamientos
- Reservas
- Panel administrativo

## Backend

Servidor encargado de procesar la lógica de negocio y comunicación con la base de datos.

Funciones:
- Validación de usuarios
- Gestión de reservas
- Control de disponibilidad
- Generación de reportes
- Seguridad y autenticación

## Base de Datos

Almacenamiento centralizado de toda la información del sistema.

Información almacenada:
- Usuarios
- Vehículos
- Espacios
- Reservas
- Reportes
- Historial de ocupación

---

# Tecnologías Utilizadas

| Tecnología | Uso |
|---|---|
| Java | Backend |
| Spring Boot | API REST |
| PostgreSQL | Base de Datos |
| HTML5 | Frontend |
| CSS3 | Diseño Visual |
| JavaScript | Interactividad |
| GitHub | Control de versiones |
| Figma | Diseño de interfaces |
| Draw.io / Bizagi | Diagramas BPMN y UML |

---

# Requerimientos Funcionales

| Código | Descripción |
|---|---|
| RF01 | Registro e inicio de sesión |
| RF02 | Visualización de espacios disponibles |
| RF03 | Reserva de estacionamientos |
| RF04 | Registro de vehículos |
| RF05 | Generación de reportes |
| RF06 | Administración de usuarios |
| RF07 | Actualización automática del estado de espacios |

---

# Requerimientos No Funcionales

| Código | Descripción |
|---|---|
| RNF01 | Tiempo de respuesta menor a 2 segundos |
| RNF02 | Disponibilidad 24/7 |
| RNF03 | Seguridad mediante cifrado SHA-256 |
| RNF04 | Compatibilidad con dispositivos móviles |
| RNF05 | Escalabilidad para futuras ampliaciones |

---

# Roles del Sistema

## Estudiante

Funciones:
- Registrarse
- Registrar vehículos
- Consultar disponibilidad
- Reservar espacios

## Docente

Funciones:
- Reservas prioritarias
- Consultar espacios
- Gestionar reservas

## Administrador

Funciones:
- Gestionar usuarios
- Supervisar ocupación
- Generar reportes
- Configurar parámetros

---

# Flujo General del Sistema

1. El usuario inicia sesión.
2. El sistema valida credenciales.
3. El usuario visualiza espacios disponibles.
4. El usuario selecciona un espacio.
5. El sistema registra la reserva.
6. El sistema actualiza el estado del espacio.
7. El administrador puede generar reportes.

---

# Modelos de Base de Datos

## Modelo Entidad Relación (ER)

Relaciones principales:
- Un usuario puede registrar varios vehículos.
- Un usuario puede realizar muchas reservas.
- Un espacio puede ser reservado muchas veces.
- Un administrador puede generar reportes.

---

# Modelo Lógico

## Tabla USUARIO

```sql
CREATE TABLE USUARIO(
    id_usuario INT PRIMARY KEY,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    correo VARCHAR(150),
    password VARCHAR(255),
    rol VARCHAR(50),
    telefono VARCHAR(20)
);
```

## Tabla VEHICULO

```sql
CREATE TABLE VEHICULO(
    id_vehiculo INT PRIMARY KEY,
    placa VARCHAR(20),
    marca VARCHAR(50),
    modelo VARCHAR(50),
    color VARCHAR(30),
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario)
);
```

## Tabla ESPACIO

```sql
CREATE TABLE ESPACIO(
    id_espacio INT PRIMARY KEY,
    codigo VARCHAR(20),
    ubicacion VARCHAR(100),
    estado VARCHAR(30),
    tipo VARCHAR(30)
);
```

## Tabla RESERVA

```sql
CREATE TABLE RESERVA(
    id_reserva INT PRIMARY KEY,
    fecha DATE,
    hora_inicio TIME,
    hora_fin TIME,
    estado VARCHAR(30),
    id_usuario INT,
    id_espacio INT,
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario),
    FOREIGN KEY (id_espacio) REFERENCES ESPACIO(id_espacio)
);
```

## Tabla REPORTE

```sql
CREATE TABLE REPORTE(
    id_reporte INT PRIMARY KEY,
    tipo VARCHAR(50),
    fecha_generacion DATE,
    descripcion TEXT,
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario)
);
```

---

# Claves Primarias y Foráneas

## Primary Key (PK)

Las claves primarias identifican de manera única cada registro.

Ejemplos:
- id_usuario
- id_vehiculo
- id_espacio
- id_reserva

## Foreign Key (FK)

Las claves foráneas permiten relacionar tablas.

Ejemplos:
- id_usuario en VEHICULO
- id_usuario en RESERVA
- id_espacio en RESERVA

---

# Seguridad del Sistema

El sistema implementará medidas de seguridad para proteger la información.

Funciones de seguridad:
- Contraseñas cifradas con SHA-256
- Validación de sesiones
- Control de acceso por roles
- Restricción de permisos administrativos
- Protección contra accesos no autorizados

---

# Beneficios Esperados

- Reducción del tiempo de búsqueda de estacionamientos
- Mejor organización vehicular
- Optimización de espacios disponibles
- Menor congestión dentro del campus
- Mayor satisfacción de estudiantes y docentes
- Obtención de métricas para toma de decisiones

---

# Instalación del Proyecto

## Requisitos Previos

- Java JDK 17 o superior
- PostgreSQL
- Maven
- Git
- IntelliJ IDEA o VS Code

---

# Módulos del Sistema

## 1. Gestión de Usuarios

Funciones:
- Registrar usuarios
- Editar información
- Eliminar usuarios
- Gestionar roles

## 2. Gestión de Vehículos

Funciones:
- Registrar vehículos
- Consultar vehículos
- Actualizar información

## 3. Gestión de Reservas

Proceso:
1. Iniciar sesión
2. Visualizar espacios disponibles
3. Seleccionar espacio
4. Confirmar reserva
5. Registrar reserva

## 4. Gestión de Reportes

Funciones:
- Generar estadísticas
- Visualizar ocupación
- Consultar historial
- Exportar reportes

---

# Metodología de Trabajo

El proyecto seguirá una metodología ágil basada en Scrum.

Etapas:
- Análisis
- Diseño
- Desarrollo
- Pruebas
- Implementación
- Documentación

---

# Integrantes del Proyecto

- Darío Milton Arroyo Núñez
- Victor Hugo La Madrid Pacherres
- Yeremy Yandel Castro Loayza
- Brayan Jhosset Meza Ruiz
- Jose Carlos Guarderas Nuñez
- Manuel David Tineo Esquerre

---
