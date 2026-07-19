package pe.utp.estacionamiento.model;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public class ReporteVehiculo {
    private final String placa;
    private final LocalDateTime horaEntrada;
    private final LocalDateTime horaSalida;
    private final Duration tiempo;
    private final BigDecimal pago;

    public ReporteVehiculo(String placa, LocalDateTime horaEntrada, LocalDateTime horaSalida, Duration tiempo, BigDecimal pago) {
        this.placa = placa;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.tiempo = tiempo;
        this.pago = pago;
    }

    public String placa() {
        return placa;
    }

    public LocalDateTime horaEntrada() {
        return horaEntrada;
    }

    public LocalDateTime horaSalida() {
        return horaSalida;
    }

    public Duration tiempo() {
        return tiempo;
    }

    public BigDecimal pago() {
        return pago;
    }
}
