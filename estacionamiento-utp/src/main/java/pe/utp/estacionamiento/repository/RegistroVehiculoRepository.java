package pe.utp.estacionamiento.repository;

import pe.utp.estacionamiento.model.Vehiculo;

public interface RegistroVehiculoRepository {
    void save(Vehiculo vehiculo);
}
