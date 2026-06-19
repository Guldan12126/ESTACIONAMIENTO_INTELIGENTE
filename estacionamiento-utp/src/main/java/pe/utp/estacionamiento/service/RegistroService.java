package pe.utp.estacionamiento.service;

import pe.utp.estacionamiento.model.Vehiculo;
import pe.utp.estacionamiento.repository.RegistroVehiculoRepository;

public class RegistroService {

    private final RegistroVehiculoRepository repository;

    public RegistroService(RegistroVehiculoRepository repository) {
        this.repository = repository;
    }

    public void registrarVehiculo(String placa, String propietarioCodigo) throws Exception {
        if (placa == null || placa.trim().isEmpty() || placa.length() < 6) {
            throw new Exception("Placa invalida");
        }
        Vehiculo v = new Vehiculo(placa, propietarioCodigo);
        repository.save(v);
    }
}
