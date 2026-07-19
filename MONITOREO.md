# Monitoreo del Sistema de Estacionamiento UTP

## Ejecutar el proyecto Spring Boot

1. Inicia XAMPP y activa MySQL.
2. Importa `database.sql` si aun no tienes la base `estacionamiento_utp`.
3. Ejecuta el modulo Spring Boot:

```bash
mvn spring-boot:run
```

El dashboard interno queda disponible en:

```text
http://localhost:8080/monitoring
```

## Endpoints Actuator

Abre estas pantallas para capturas:

```text
http://localhost:8080/actuator
http://localhost:8080/actuator/health
http://localhost:8080/actuator/info
http://localhost:8080/actuator/metrics
http://localhost:8080/actuator/env
http://localhost:8080/actuator/beans
http://localhost:8080/actuator/prometheus
```

## Generar logs INFO, WARN y ERROR para capturas

Con la aplicacion levantada, abre:

```text
http://localhost:8080/monitoring/demo-logs
```

Luego revisa:

```text
logs/estacionamiento.log
logs/errores.log
```

## Ejecutar Prometheus y Grafana

Instala Docker Desktop y ejecuta:

```bash
docker compose -f docker-compose.monitoring.yml up -d
```

Prometheus:

```text
http://localhost:9090
```

Grafana:

```text
http://localhost:3000
```

Credenciales:

```text
Usuario: admin
Password: admin
```

En Grafana abre:

```text
Dashboards > Estacionamiento > Estacionamiento UTP - Monitoreo
```

## Consultas Prometheus principales

```promql
up{job="estacionamiento-utp"}
process_cpu_usage
jvm_memory_used_bytes
rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])
estacionamiento_usuarios_activos
estacionamiento_vehiculos_registrados
estacionamiento_espacios_disponibles
estacionamiento_reservas_activas
estacionamiento_pagos_realizados
estacionamiento_errores_registrados
```

## Capturas recomendadas para el informe

- Dashboard principal del sistema web en `http://localhost/estacionamiento-utp/index.html`.
- Dashboard de monitoreo Spring Boot en `http://localhost:8080/monitoring`.
- Endpoint `http://localhost:8080/actuator`.
- Endpoint `http://localhost:8080/actuator/health`.
- Endpoint `http://localhost:8080/actuator/info`.
- Endpoint `http://localhost:8080/actuator/metrics`.
- Endpoint `http://localhost:8080/actuator/env`.
- Endpoint `http://localhost:8080/actuator/beans`.
- Endpoint `http://localhost:8080/actuator/prometheus`.
- Prometheus en `http://localhost:9090`.
- Grafana en `http://localhost:3000`.
- Dashboard de Grafana `Estacionamiento UTP - Monitoreo`.
- Consola mostrando logs `INFO`.
- Consola mostrando logs `WARN`.
- Consola mostrando logs `ERROR`.
- Archivo `logs/estacionamiento.log`.
- Archivo `logs/errores.log`.
- Pantalla del dashboard con CPU, RAM, disponibilidad, usuarios activos, vehiculos, espacios, reservas, pagos y errores.
