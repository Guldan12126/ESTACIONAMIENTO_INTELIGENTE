package pe.utp.estacionamiento.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.utp.estacionamiento.model.Espacio;
import pe.utp.estacionamiento.model.Reserva;
import pe.utp.estacionamiento.repository.EspacioRepository;
import pe.utp.estacionamiento.repository.ReservaRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaServiceTest {

    private ReservaRepository reservaRepository;
    private EspacioRepository espacioRepository;
    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        reservaRepository = mock(ReservaRepository.class);
        espacioRepository = mock(EspacioRepository.class);
        reservaService = new ReservaService(reservaRepository, espacioRepository);
    }

    // CP04: Reservar espacio
    @Test
    void testCP04_ReservarEspacio() throws Exception {
        Espacio espacioLibre = new Espacio("A1", false);
        
        when(reservaRepository.findActivasByUsuario("u22210840")).thenReturn(Collections.emptyList());
        when(espacioRepository.findById("A1")).thenReturn(espacioLibre);

        Reserva reserva = reservaService.reservarEspacio("u22210840", "ABC123", "A1");

        assertNotNull(reserva);
        assertTrue(espacioLibre.isOcupado());
        verify(espacioRepository, times(1)).update(espacioLibre);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
        System.out.println("✅ CP04 - Reservar Espacio: Reserva creada con éxito en el espacio A1.");
    }

    // CP05: Reserva duplicada
    @Test
    void testCP05_ReservaDuplicada() {
        Reserva reservaActiva = new Reserva("1", "ABC123", "A1", true, false);
        when(reservaRepository.findActivasByUsuario("u22210840")).thenReturn(List.of(reservaActiva));

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.reservarEspacio("u22210840", "XYZ789", "A2");
        });

        assertEquals("El usuario ya tiene una reserva activa", exception.getMessage());
        System.out.println("✅ CP05 - Reserva Duplicada: Bloqueo exitoso. El usuario ya tenía una reserva activa.");
    }

    // CP06: Consultar espacios
    @Test
    void testCP06_ConsultarEspacios() {
        List<Espacio> espaciosMock = List.of(new Espacio("A1", false), new Espacio("A2", false));
        when(espacioRepository.findAllLibres()).thenReturn(espaciosMock);

        List<Espacio> disponibles = reservaService.consultarEspaciosDisponibles();

        assertEquals(2, disponibles.size());
        verify(espacioRepository, times(1)).findAllLibres();
        System.out.println("✅ CP06 - Consultar Espacios: Listado de espacios libres mostrado (Total: " + disponibles.size() + ").");
    }
}
