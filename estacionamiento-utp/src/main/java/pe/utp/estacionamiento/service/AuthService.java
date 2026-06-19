package pe.utp.estacionamiento.service;

import pe.utp.estacionamiento.model.Usuario;
import pe.utp.estacionamiento.repository.UsuarioRepository;

public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario login(String codigo, String password) throws Exception {
        Usuario usuario = usuarioRepository.findByCodigo(codigo);
        if (usuario != null && usuario.getPassword().equals(password)) {
            return usuario;
        }
        throw new Exception("Credenciales invalidas");
    }

    public boolean esAdministrador(Usuario usuario) {
        return usuario != null && "Administrador".equalsIgnoreCase(usuario.getRol());
    }
}
