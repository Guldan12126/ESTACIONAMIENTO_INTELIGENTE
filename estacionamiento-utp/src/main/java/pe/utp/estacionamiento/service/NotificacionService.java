package pe.utp.estacionamiento.service;

import pe.utp.estacionamiento.model.Reserva;

public class NotificacionService {

    public boolean notificarReservaVencida(Reserva reserva) {
        if (reserva != null && reserva.isVencida()) {
            // Simulamos el envio de un mensaje / notificacion
            System.out.println("Notificacion enviada: La reserva del vehiculo " + reserva.getVehiculoPlaca() + " ha vencido.");
            return true;
        }
        return false;
    }
}
