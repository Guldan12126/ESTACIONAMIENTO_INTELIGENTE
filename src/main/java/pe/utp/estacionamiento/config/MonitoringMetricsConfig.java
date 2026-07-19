package pe.utp.estacionamiento.config;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;
import pe.utp.estacionamiento.service.MonitoringService;

@Configuration
public class MonitoringMetricsConfig {
    public MonitoringMetricsConfig(MeterRegistry registry, MonitoringService monitoringService) {
        Gauge.builder("estacionamiento.usuarios.activos", monitoringService, MonitoringService::activeUsers)
                .description("Usuarios activos calculados desde eventos de login")
                .register(registry);

        Gauge.builder("estacionamiento.vehiculos.registrados", monitoringService, MonitoringService::vehiculosRegistrados)
                .description("Vehiculos registrados en el sistema")
                .register(registry);

        Gauge.builder("estacionamiento.espacios.disponibles", monitoringService, MonitoringService::espaciosDisponibles)
                .description("Espacios disponibles")
                .register(registry);

        Gauge.builder("estacionamiento.reservas.activas", monitoringService, MonitoringService::reservasActivas)
                .description("Espacios actualmente reservados")
                .register(registry);

        Gauge.builder("estacionamiento.pagos.realizados", monitoringService, MonitoringService::pagosRealizados)
                .description("Pagos realizados o salidas registradas")
                .register(registry);

        Gauge.builder("estacionamiento.errores.registrados", monitoringService, MonitoringService::erroresRegistrados)
                .description("Errores registrados en logs")
                .register(registry);
    }
}
