package pe.utp.estacionamiento.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servicio centralizado para registrar eventos del sistema.
 * Proporciona logs estructurados para auditoría y monitoreo.
 */
@Service
public class EventoSistemaService {
    private static final Logger log = LoggerFactory.getLogger(EventoSistemaService.class);
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ============ INICIO Y CONFIGURACION ============
    public void registrarInicioSistema() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP);
        log.info("============================================================");
        log.info("INICIO DEL SISTEMA DE ESTACIONAMIENTO - [{}]", timestamp);
        log.info("Modulo de Monitoreo: ACTIVO");
        log.info("Actuator: DISPONIBLE en http://localhost:8080/actuator");
        log.info("Dashboard: DISPONIBLE en http://localhost:8080/monitoring");
        log.info("============================================================");
    }

    // ============ AUTENTICACION Y USUARIOS ============
    public void registrarLogin(String codigoUsuario) {
        log.info("[LOGIN] Acceso exitoso - usuario: {} - timestamp: {}", 
                codigoUsuario, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarLoginFallido(String codigoUsuario, String motivo) {
        log.warn("[LOGIN-FALLIDO] Intento fallido - usuario: {} - motivo: {} - timestamp: {}", 
                codigoUsuario, motivo, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarLogout(String codigoUsuario) {
        log.info("[LOGOUT] Cierre de sesion - usuario: {} - timestamp: {}", 
                codigoUsuario, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarUsuario(String codigoUsuario, String accion) {
        log.info("[USUARIO] Gestion de usuario - accion: {} - usuario: {} - timestamp: {}", 
                accion, codigoUsuario, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarUsuarioCreado(String codigoUsuario, String nombre, String rol) {
        log.info("[USUARIO-CREADO] Nuevo usuario registrado - codigo: {} - nombre: {} - rol: {} - timestamp: {}", 
                codigoUsuario, nombre, rol, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarUsuarioEliminado(String codigoUsuario) {
        log.warn("[USUARIO-ELIMINADO] Usuario removido del sistema - codigo: {} - timestamp: {}", 
                codigoUsuario, LocalDateTime.now().format(TIMESTAMP));
    }

    // ============ VEHICULOS ============
    public void registrarVehiculo(String placa) {
        log.info("[VEHICULO-REGISTRO] Nuevo vehiculo registrado - placa: {} - timestamp: {}", 
                placa, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarVehiculoActualizado(String placa, String cambios) {
        log.info("[VEHICULO-ACTUALIZADO] Datos de vehiculo actualizados - placa: {} - cambios: {} - timestamp: {}", 
                placa, cambios, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarVehiculoEliminado(String placa) {
        log.info("[VEHICULO-ELIMINADO] Vehiculo removido - placa: {} - timestamp: {}", 
                placa, LocalDateTime.now().format(TIMESTAMP));
    }

    // ============ ENTRADA Y SALIDA ============
    public void registrarEntradaVehiculo(String placa, String espacio) {
        log.info("[ENTRADA-VEHICULO] Acceso al estacionamiento - placa: {} - espacio: {} - timestamp: {}", 
                placa, espacio, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarEntradaRechazada(String placa, String motivo) {
        log.warn("[ENTRADA-RECHAZADA] Acceso denegado - placa: {} - motivo: {} - timestamp: {}", 
                placa, motivo, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarSalidaVehiculo(String placa) {
        log.info("[SALIDA-VEHICULO] Vehiculo saliendo del estacionamiento - placa: {} - timestamp: {}", 
                placa, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarSalidaNoAutorizada(String placa) {
        log.warn("[SALIDA-NO-AUTORIZADA] Intento de salida sin autorizacion - placa: {} - timestamp: {}", 
                placa, LocalDateTime.now().format(TIMESTAMP));
    }

    // ============ RESERVAS Y ESPACIOS ============
    public void registrarReserva(String espacio, String estado) {
        log.info("[RESERVA] Operacion de reserva - espacio: {} - estado: {} - timestamp: {}", 
                espacio, estado, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarReservaCreada(String espacio, String usuario, String duracion) {
        log.info("[RESERVA-CREADA] Nueva reserva confirmada - espacio: {} - usuario: {} - duracion: {} - timestamp: {}", 
                espacio, usuario, duracion, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarReservaCancelada(String espacio, String razon) {
        log.info("[RESERVA-CANCELADA] Reserva cancelada - espacio: {} - razon: {} - timestamp: {}", 
                espacio, razon, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarEspacioNoDisponible(String espacio) {
        log.warn("[ESPACIO-NO-DISPONIBLE] Espacio ocupado o reservado - espacio: {} - timestamp: {}", 
                espacio, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarEstacionamientoLleno() {
        log.warn("[ESTACIONAMIENTO-LLENO] Advertencia: No hay espacios disponibles - timestamp: {}", 
                LocalDateTime.now().format(TIMESTAMP));
    }

    // ============ PAGOS ============
    public void registrarPago(String placa, String monto) {
        log.info("[PAGO] Pago procesado - placa: {} - monto: ${} - timestamp: {}", 
                placa, monto, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarPagoExitoso(String placa, String monto, String metodo) {
        log.info("[PAGO-EXITOSO] Transaccion completada - placa: {} - monto: ${} - metodo: {} - timestamp: {}", 
                placa, monto, metodo, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarPagoFallido(String placa, String motivo) {
        log.error("[PAGO-FALLIDO] Error en transaccion - placa: {} - motivo: {} - timestamp: {}", 
                placa, motivo, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarRefund(String placa, String monto) {
        log.info("[REFUND] Reembolso procesado - placa: {} - monto: ${} - timestamp: {}", 
                placa, monto, LocalDateTime.now().format(TIMESTAMP));
    }

    // ============ PANEL ADMINISTRADOR ============
    public void registrarPanelAdministrador(String accion) {
        log.info("[ADMIN-PANEL] Accion de administrador - accion: {} - timestamp: {}", 
                accion, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarGeneracionReporte() {
        log.info("[ADMIN-REPORTE] Generacion de reporte solicitada - timestamp: {}", 
                LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarBackupBaseDatos() {
        log.info("[ADMIN-BACKUP] Respaldo de base de datos iniciado - timestamp: {}", 
                LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarConfiguracionModificada(String parametro, String valorAnterior, String valorNuevo) {
        log.info("[ADMIN-CONFIG] Configuracion modificada - parametro: {} - antes: {} - despues: {} - timestamp: {}", 
                parametro, valorAnterior, valorNuevo, LocalDateTime.now().format(TIMESTAMP));
    }

    // ============ ADVERTENCIAS ============
    public void advertir(String mensaje) {
        log.warn("[ADVERTENCIA] {} - timestamp: {}", mensaje, LocalDateTime.now().format(TIMESTAMP));
    }

    public void advertirEstacionamientoLleno() {
        log.warn("[ADVERTENCIA-CAPACIDAD] Estacionamiento lleno o sin espacios disponibles - timestamp: {}", 
                LocalDateTime.now().format(TIMESTAMP));
    }

    public void advertirMantenimiento(String componente) {
        log.warn("[ADVERTENCIA-MANTENIMIENTO] Mantenimiento requerido en: {} - timestamp: {}", 
                componente, LocalDateTime.now().format(TIMESTAMP));
    }

    public void advertirConexionLenta() {
        log.warn("[ADVERTENCIA-RENDIMIENTO] Conexion lenta detectada - timestamp: {}", 
                LocalDateTime.now().format(TIMESTAMP));
    }

    // ============ ERRORES ============
    public void registrarErrorConexion(Exception error) {
        log.error("[ERROR-DB] Fallo de conexion con base de datos - timestamp: {} - detalle: {}", 
                LocalDateTime.now().format(TIMESTAMP), error.getMessage(), error);
    }

    public void registrarErrorDatos(String mensaje, Exception error) {
        log.error("[ERROR-DATOS] Error en procesamiento de datos - mensaje: {} - timestamp: {} - detalle: {}", 
                mensaje, LocalDateTime.now().format(TIMESTAMP), error.getMessage(), error);
    }

    public void registrarExcepcion(String modulo, Exception error) {
        log.error("[EXCEPCION] Excepcion no controlada - modulo: {} - timestamp: {} - mensaje: {} - clase: {}", 
                modulo, LocalDateTime.now().format(TIMESTAMP), error.getMessage(), error.getClass().getSimpleName(), error);
    }

    public void registrarErrorValidacion(String campo, String valor, String regla) {
        log.error("[ERROR-VALIDACION] Validacion fallida - campo: {} - valor: {} - regla: {} - timestamp: {}", 
                campo, valor, regla, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarErrorApiExterna(String servicio, String codigo) {
        log.error("[ERROR-API] Fallo en integracion externa - servicio: {} - codigo: {} - timestamp: {}", 
                servicio, codigo, LocalDateTime.now().format(TIMESTAMP));
    }

    // ============ REPORTES ============
    public void registrarReporteGenerado(Path archivo, int registros) {
        log.info("[REPORTE] Excel generado exitosamente - archivo: {} - registros: {} - timestamp: {}", 
                archivo.toAbsolutePath(), registros, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarReporteError(String motivo) {
        log.error("[REPORTE-ERROR] Fallo en generacion de reporte - motivo: {} - timestamp: {}", 
                motivo, LocalDateTime.now().format(TIMESTAMP));
    }

    // ============ SEGURIDAD ============
    public void registrarAccesoRestringido(String usuario, String recurso) {
        log.warn("[SEGURIDAD-ACCESO] Intento de acceso denegado - usuario: {} - recurso: {} - timestamp: {}", 
                usuario, recurso, LocalDateTime.now().format(TIMESTAMP));
    }

    public void registrarActividadSospechosa(String tipo, String descripcion) {
        log.warn("[SEGURIDAD-SOSPECHA] Actividad anormal detectada - tipo: {} - descripcion: {} - timestamp: {}", 
                tipo, descripcion, LocalDateTime.now().format(TIMESTAMP));
    }
}
