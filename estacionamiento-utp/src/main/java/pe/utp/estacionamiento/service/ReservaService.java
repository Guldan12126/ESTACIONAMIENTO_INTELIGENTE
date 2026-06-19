package pe.utp.estacionamiento.service;

import pe.utp.estacionamiento.model.Espacio;
import pe.utp.estacionamiento.model.Reserva;
import pe.utp.estacionamiento.repository.EspacioRepository;
import pe.utp.estacionamiento.repository.ReservaRepository;

import java.util.List;
import java.util.UUID;

public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final EspacioRepository espacioRepository;

    public ReservaService(ReservaRepository reservaRepository, EspacioRepository espacioRepository) {
        this.reservaRepository = reservaRepository;
        this.espacioRepository = espacioRepository;
    }

    public Reserva reservarEspacio(String codigoUsuario, String vehiculoPlaca, String espacioId) throws Exception {
        List<Reserva> activas = reservaRepository.findActivasByUsuario(codigoUsuario);
        if (activas != null && !activas.isEmpty()) {
            throw new Exception("El usuario ya tiene una reserva activa");
        }

        Espacio espacio = espacioRepository.findById(espacioId);
        if (espacio == null || espacio.isOcupado()) {
            throw new Exception("El espacio no esta disponible");
        }

        espacio.setOcupado(true);
        espacioRepository.update(espacio);

        Reserva reserva = new Reserva(UUID.randomUUID().toString(), vehiculoPlaca, espacioId, true, false);
        reservaRepository.save(reserva);
        
        return reserva;
    }

    public List<Espacio> consultarEspaciosDisponibles() {
        return espacioRepository.findAllLibres();
    }
}
