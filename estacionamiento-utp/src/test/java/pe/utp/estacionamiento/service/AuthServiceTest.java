package pe.utp.estacionamiento.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pe.utp.estacionamiento.model.Usuario;
import pe.utp.estacionamiento.repository.UsuarioRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UsuarioRepository usuarioRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        authService = new AuthService(usuarioRepository);
    }

    // CP01: Login correcto
    @Test
    void testCP01_LoginCorrecto() throws Exception {
        Usuario mockUser = new Usuario("u22210840", "123456", "Estudiante");
        when(usuarioRepository.findByCodigo("u22210840")).thenReturn(mockUser);

        Usuario user = authService.login("u22210840", "123456");

        assertNotNull(user);
        assertEquals("u22210840", user.getCodigo());
        verify(usuarioRepository, times(1)).findByCodigo("u22210840");
        System.out.println("✅ CP01 - Login Correcto: ¡Hola, Bienvenido al sistema " + user.getCodigo() + "!");
    }

    // CP02: Login incorrecto
    @Test
    void testCP02_LoginIncorrecto() {
        Usuario mockUser = new Usuario("u22210840", "123456", "Estudiante");
        when(usuarioRepository.findByCodigo("u22210840")).thenReturn(mockUser);

        Exception exception = assertThrows(Exception.class, () -> {
            authService.login("u22210840", "wrongpass");
        });

        assertEquals("Credenciales invalidas", exception.getMessage());
        System.out.println("✅ CP02 - Login Incorrecto: Acceso denegado por contraseña errónea.");
    }

    // CP08: Validacion de roles
    @Test
    void testCP08_ValidacionRoles() {
        Usuario admin = new Usuario("admin01", "pass", "Administrador");
        Usuario estudiante = new Usuario("est01", "pass", "Estudiante");

        assertTrue(authService.esAdministrador(admin), "Deberia ser Administrador");
        assertFalse(authService.esAdministrador(estudiante), "No deberia ser Administrador");
        System.out.println("✅ CP08 - Validación de Roles: Administrador tiene acceso, estudiante no.");
    }
}
