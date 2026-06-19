package pe.utp.estacionamiento.repository;

import pe.utp.estacionamiento.model.Usuario;

public interface UsuarioRepository {
    Usuario findByCodigo(String codigo);
}
