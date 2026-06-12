package pe.utp.estacionamiento.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventoSistemaService {
    private static final Logger logger = LoggerFactory.getLogger(EventoSistemaService.class);

    public void registrarInicioSistema() {
        logger.info("Inicio del sistema de estacionamiento");
    }

    public void registrarVehiculo(String placa) {
        logger.info("Vehiculo registrado correctamente: {}", placa);
    }

    public void registrarSalidaVehiculo(String placa) {
        logger.info("Salida de vehiculo registrada correctamente: {}", placa);
    }

    public void advertir(String mensaje) {
        logger.warn(mensaje);
    }

    public void registrarErrorConexion(Exception error) {
        logger.error("Error de conexion con la base de datos", error);
    }

    public void registrarErrorDatos(String mensaje, Exception error) {
        logger.error("Error al registrar datos: {}", mensaje, error);
    }
}
