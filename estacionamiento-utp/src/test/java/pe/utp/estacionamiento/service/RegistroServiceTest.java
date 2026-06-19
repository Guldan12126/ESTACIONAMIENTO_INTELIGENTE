package pe.utp.estacionamiento.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.utp.estacionamiento.model.Vehiculo;
import pe.utp.estacionamiento.repository.RegistroVehiculoRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistroServiceTest {

    private RegistroVehiculoRepository repository;
    private RegistroService registroService;

    @BeforeEach
    void setUp() {
        repository = mock(RegistroVehiculoRepository.class);
        registroService = new RegistroService(repository);
    }

    // CP03: Registrar vehiculo
    @Test
    void testCP03_RegistrarVehiculo() throws Exception {
        registroService.registrarVehiculo("ABC123", "u22210840");

        verify(repository, times(1)).save(any(Vehiculo.class));
    }
    
    @Test
    void testCP03_RegistrarVehiculo_PlacaInvalida() {
        Exception exception = assertThrows(Exception.class, () -> {
            registroService.registrarVehiculo("ABC", "u22210840");
        });

        assertEquals("Placa invalida", exception.getMessage());
    }
}
