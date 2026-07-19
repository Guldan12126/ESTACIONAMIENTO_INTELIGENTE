package pe.utp.estacionamiento.repository;

import pe.utp.estacionamiento.config.DatabaseConfig;
import pe.utp.estacionamiento.model.ReporteVehiculo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VehiculoRepository {
    private static final String REPORTE_SQL = """
            SELECT placa, hora_ingreso, hora_salida
            FROM vehiculos
            ORDER BY hora_ingreso DESC
            """;

    private final DatabaseConfig databaseConfig;
    private final BigDecimal tarifaHora;

    public VehiculoRepository(DatabaseConfig databaseConfig, BigDecimal tarifaHora) {
        this.databaseConfig = databaseConfig;
        this.tarifaHora = tarifaHora;
    }

    public List<ReporteVehiculo> listarVehiculosParaReporte() throws SQLException {
        List<ReporteVehiculo> vehiculos = new ArrayList<>();

        try (Connection connection = databaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(REPORTE_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String placa = resultSet.getString("placa");
                LocalDateTime horaEntrada = toLocalDateTime(resultSet.getTimestamp("hora_ingreso"));
                LocalDateTime horaSalida = toLocalDateTime(resultSet.getTimestamp("hora_salida"));
                LocalDateTime finCalculo = horaSalida != null ? horaSalida : LocalDateTime.now();
                Duration tiempo = Duration.between(horaEntrada, finCalculo);

                vehiculos.add(new ReporteVehiculo(
                        placa,
                        horaEntrada,
                        horaSalida,
                        tiempo,
                        calcularPago(tiempo)
                ));
            }
        }

        return vehiculos;
    }

    private BigDecimal calcularPago(Duration tiempo) {
        long minutos = Math.max(0, tiempo.toMinutes());
        long horasCobradas = Math.max(1, (long) Math.ceil(minutos / 60.0));
        return tarifaHora.multiply(BigDecimal.valueOf(horasCobradas)).setScale(2, RoundingMode.HALF_UP);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
