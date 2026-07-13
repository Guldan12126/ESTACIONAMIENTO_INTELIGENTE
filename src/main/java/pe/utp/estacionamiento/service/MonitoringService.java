package pe.utp.estacionamiento.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import pe.utp.estacionamiento.model.MonitoringSnapshot;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MonitoringService {
    private final JdbcTemplate jdbcTemplate;
    private final Path logFile;
    private final boolean simulateMissingData;

    public MonitoringService(
            JdbcTemplate jdbcTemplate,
            @Value("${app.monitoring.log-file:logs/estacionamiento.log}") String logFile,
            @Value("${app.monitoring.simulate-missing-data:true}") boolean simulateMissingData
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.logFile = Path.of(logFile);
        this.simulateMissingData = simulateMissingData;
    }

    public MonitoringSnapshot snapshot() {
        long started = System.nanoTime();
        boolean databaseAvailable = isDatabaseAvailable();
        long errores = countLogLevel("ERROR");
        long warns = countLogLevel("WARN");
        double responseMs = (System.nanoTime() - started) / 1_000_000.0;

        return new MonitoringSnapshot(
                databaseAvailable ? "UP" : "DEGRADED",
                databaseAvailable,
                cpuUsage(),
                usedMemoryMb(),
                totalMemoryMb(),
                round(responseMs),
                activeUsers(),
                countOrSimulate("SELECT COUNT(*) FROM vehiculos", 12),
                countOrSimulate("SELECT COUNT(*) FROM espacios WHERE estado = 'disponible'", 8),
                countOrSimulate("SELECT COUNT(*) FROM espacios WHERE estado = 'reservado'", 3),
                countOrSimulate("SELECT COUNT(*) FROM historial_movimientos WHERE tipo = 'salida'", 5),
                errores,
                warns,
                LocalDateTime.now(),
                lastLogLines(10)
        );
    }

    public long activeUsers() {
        long logins = countLogContains("Login exitoso");
        if (logins > 0) {
            return logins;
        }
        return simulateMissingData ? 4 : 0;
    }

    public long erroresRegistrados() {
        return countLogLevel("ERROR");
    }

    public long vehiculosRegistrados() {
        return countOrSimulate("SELECT COUNT(*) FROM vehiculos", 12);
    }

    public long espaciosDisponibles() {
        return countOrSimulate("SELECT COUNT(*) FROM espacios WHERE estado = 'disponible'", 8);
    }

    public long reservasActivas() {
        return countOrSimulate("SELECT COUNT(*) FROM espacios WHERE estado = 'reservado'", 3);
    }

    public long pagosRealizados() {
        return countOrSimulate("SELECT COUNT(*) FROM historial_movimientos WHERE tipo = 'salida'", 5);
    }

    public boolean isDatabaseAvailable() {
        try {
            Integer value = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return value != null && value == 1;
        } catch (Exception error) {
            return false;
        }
    }

    private long countOrSimulate(String sql, long simulatedValue) {
        try {
            Long value = jdbcTemplate.queryForObject(sql, Long.class);
            return value == null ? 0 : value;
        } catch (Exception error) {
            return simulateMissingData ? simulatedValue : 0;
        }
    }

    private double cpuUsage() {
        java.lang.management.OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
        if (bean instanceof com.sun.management.OperatingSystemMXBean osBean) {
            double load = osBean.getProcessCpuLoad();
            if (load >= 0) {
                return round(load * 100);
            }
        }
        double fallback = bean.getSystemLoadAverage();
        return fallback < 0 ? 0 : round(Math.min(100, fallback * 10));
    }

    private long usedMemoryMb() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
    }

    private long totalMemoryMb() {
        return Runtime.getRuntime().maxMemory() / (1024 * 1024);
    }

    private long countLogLevel(String level) {
        return readLogLines().stream().filter(line -> line.contains(" " + level + " ")).count();
    }

    private long countLogContains(String text) {
        return readLogLines().stream().filter(line -> line.contains(text)).count();
    }

    private List<String> lastLogLines(int limit) {
        List<String> lines = new ArrayList<>(readLogLines());
        if (lines.isEmpty()) {
            return List.of("INFO  Modulo de monitoreo listo para registrar eventos.");
        }
        int from = Math.max(0, lines.size() - limit);
        return lines.subList(from, lines.size());
    }

    private List<String> readLogLines() {
        try {
            if (!Files.exists(logFile)) {
                Files.createDirectories(logFile.getParent());
                return Collections.emptyList();
            }
            return Files.readAllLines(logFile);
        } catch (IOException error) {
            return Collections.emptyList();
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
