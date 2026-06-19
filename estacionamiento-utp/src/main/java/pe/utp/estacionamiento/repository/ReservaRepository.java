package pe.utp.estacionamiento.repository;

import pe.utp.estacionamiento.model.Reserva;
import java.util.List;

public interface ReservaRepository {
    void save(Reserva reserva);
    List<Reserva> findActivasByUsuario(String codigoUsuario);
}
