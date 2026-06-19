package pe.utp.estacionamiento.service;

import org.junit.jupiter.api.Test;
import pe.utp.estacionamiento.model.Reserva;

import static org.junit.jupiter.api.Assertions.*;

class NotificacionServiceTest {

    // CP07: Notificacion
    @Test
    void testCP07_Notificacion() {
        NotificacionService notificacionService = new NotificacionService();
        Reserva reservaVencida = new Reserva("1", "ABC123", "A1", true, true);
        Reserva reservaVigente = new Reserva("2", "XYZ789", "A2", true, false);

        assertTrue(notificacionService.notificarReservaVencida(reservaVencida));
        assertFalse(notificacionService.notificarReservaVencida(reservaVigente));
    }
}
