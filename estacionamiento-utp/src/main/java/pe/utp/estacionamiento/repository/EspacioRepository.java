package pe.utp.estacionamiento.repository;

import pe.utp.estacionamiento.model.Espacio;
import java.util.List;

public interface EspacioRepository {
    List<Espacio> findAllLibres();
    Espacio findById(String id);
    void update(Espacio espacio);
}
