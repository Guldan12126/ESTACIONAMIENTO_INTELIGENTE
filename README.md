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

El sistema seguirá una arquitectura cliente-servidor dividida en tres capas principales:

## Frontend
Interfaz gráfica desarrollada para que los usuarios interactúen con el sistema desde computadoras o dispositivos móviles.

Funciones:
- Inicio de sesión.
- Registro de vehículos.
- Visualización de estacionamientos.
- Reservas.
- Panel administrativo.

## Backend
Servidor encargado de procesar la lógica de negocio y comunicación con la base de datos.

Funciones:
- Validación de usuarios.
- Gestión de reservas.
- Control de disponibilidad.
- Generación de reportes.
- Seguridad y autenticación.

## Base de Datos
Almacenamiento centralizado de toda la información del sistema.

Información almacenada:
- Usuarios.
- Vehículos.
- Espacios.
- Reservas.
- Reportes.
- Historial de ocupación.

---

# Tecnologías Utilizadas

| Tecnología | Descripción |
|---|---|
| Java Spring Boot | Desarrollo del backend |
| PostgreSQL | Base de datos relacional |
| HTML5 | Estructura del frontend |
| CSS3 | Diseño visual |
| JavaScript | Interactividad |
| GitHub | Control de versiones |
| Figma | Diseño de mockups |
| Draw.io / Lucidchart | Diagramas UML y BPMN |

---

# Requerimientos Funcionales

| Código | Descripción |
|---|---|
| RF01 | Registro e inicio de sesión de usuarios |
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
| RNF04 | Interfaz amigable e intuitiva |
| RNF05 | Compatibilidad con dispositivos móviles |
| RNF06 | Escalabilidad para futuras ampliaciones |

---

# Roles del Sistema

## Estudiante
- Registrarse.
- Registrar vehículo.
- Consultar disponibilidad.
- Reservar estacionamientos.

## Docente
- Acceder a reservas prioritarias.
- Consultar espacios disponibles.
- Gestionar sus reservas.

## Administrador
- Gestionar usuarios.
- Supervisar ocupación.
- Generar reportes.
- Configurar parámetros del sistema.

---

# Flujo General del Sistema

1. El usuario inicia sesión.
2. El sistema valida las credenciales.
3. El usuario visualiza espacios disponibles.
4. El usuario selecciona un espacio.
5. El sistema registra la reserva.
6. El estado del espacio cambia a “reservado”.
7. El administrador puede visualizar reportes de uso.

---

# Estructura de la Base de Datos

## Tablas Principales

### Usuario
Contiene la información de estudiantes, docentes y administradores.

### Vehículo
Almacena los vehículos registrados por cada usuario.

### Espacio
Representa los estacionamientos disponibles dentro del campus.

### Reserva
Relaciona usuarios con espacios reservados.

### Reporte
Guarda estadísticas y métricas de ocupación.

---

# Seguridad del Sistema

El sistema implementará medidas de seguridad para proteger la información:

- Contraseñas cifradas con SHA-256.
- Validación de sesiones.
- Control de acceso por roles.
- Restricción de permisos administrativos.
- Protección contra accesos no autorizados.

---

# Beneficios Esperados

- Reducción del tiempo de búsqueda de estacionamientos.
- Mejor organización vehicular.
- Optimización de espacios disponibles.
- Menor congestión dentro del campus.
- Mayor satisfacción de estudiantes y docentes.
- Obtención de métricas para toma de decisiones.

---

# Instalación del Proyecto

## Requisitos Previos

- Java JDK 17 o superior
- PostgreSQL
- Maven
- IDE IntelliJ IDEA o VS Code
- Git

---



