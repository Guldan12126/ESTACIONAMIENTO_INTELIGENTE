package pe.utp.estacionamiento.model;

import java.time.LocalDateTime;
import java.util.List;

public record MonitoringSnapshot(
        String estadoSistema,
        boolean disponible,
        double cpuPorcentaje,
        long ramUsadaMb,
        long ramTotalMb,
        double tiempoRespuestaMs,
        long usuariosActivos,
        long vehiculosRegistrados,
        long espaciosDisponibles,
        long reservasActivas,
        long pagosRealizados,
        long erroresRegistrados,
        long advertenciasRegistradas,
        LocalDateTime actualizadoEn,
        List<String> ultimosLogs
) {
}
