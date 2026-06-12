package pe.utp.estacionamiento;

import org.apache.commons.lang3.StringUtils;
import pe.utp.estacionamiento.config.AppConfig;
import pe.utp.estacionamiento.config.DatabaseConfig;
import pe.utp.estacionamiento.model.ReporteVehiculo;
import pe.utp.estacionamiento.repository.VehiculoRepository;
import pe.utp.estacionamiento.service.EventoSistemaService;
import pe.utp.estacionamiento.service.ReporteExcelService;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

public class App {
    public static void main(String[] args) {
        EventoSistemaService eventos = new EventoSistemaService();
        eventos.registrarInicioSistema();

        String comando = StringUtils.defaultIfBlank(args.length == 0 ? null : args[0], "exportar-reporte");
        AppConfig config = new AppConfig();

        if ("exportar-reporte".equalsIgnoreCase(comando)) {
            exportarReporte(config, eventos);
            return;
        }

        eventos.advertir("Comando no reconocido: " + comando);
        System.out.println("Comando no reconocido. Usa: mvn exec:java -Dexec.args=\"exportar-reporte\"");
    }

    private static void exportarReporte(AppConfig config, EventoSistemaService eventos) {
        DatabaseConfig databaseConfig = new DatabaseConfig(config);
        VehiculoRepository repository = new VehiculoRepository(databaseConfig, config.hourlyRate());
        ReporteExcelService reporteExcelService = new ReporteExcelService();

        try {
            List<ReporteVehiculo> vehiculos = repository.listarVehiculosParaReporte();
            Path archivo = reporteExcelService.exportar(vehiculos, config.reportPath());
            System.out.println("Reporte generado correctamente: " + archivo.toAbsolutePath());
            System.out.println("Registros exportados: " + vehiculos.size());
        } catch (SQLException error) {
            eventos.registrarErrorConexion(error);
            System.err.println("Error de conexion. Revisa que MySQL/XAMPP este activo y que la base de datos exista.");
        } catch (Exception error) {
            eventos.registrarErrorDatos("No se pudo generar el reporte Excel", error);
            System.err.println("No se pudo generar el reporte Excel: " + error.getMessage());
        }
    }
}
