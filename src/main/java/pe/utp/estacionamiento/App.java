package pe.utp.estacionamiento;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pe.utp.estacionamiento.config.AppConfig;
import pe.utp.estacionamiento.config.DatabaseConfig;
import pe.utp.estacionamiento.model.ReporteVehiculo;
import pe.utp.estacionamiento.repository.VehiculoRepository;
import pe.utp.estacionamiento.service.EventoSistemaService;
import pe.utp.estacionamiento.service.ReporteExcelService;

import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    CommandLineRunner runner(EventoSistemaService eventos) {
        return args -> {
            eventos.registrarInicioSistema();

            String comando = StringUtils.defaultIfBlank(args.length == 0 ? null : args[0], "");
            if (!"exportar-reporte".equalsIgnoreCase(comando)) {
                return;
            }

            AppConfig config = new AppConfig();
            DatabaseConfig databaseConfig = new DatabaseConfig(config);
            VehiculoRepository repository = new VehiculoRepository(databaseConfig, config.hourlyRate());
            ReporteExcelService reporteExcelService = new ReporteExcelService();

            try {
                List<ReporteVehiculo> vehiculos = repository.listarVehiculosParaReporte();
                Path archivo = reporteExcelService.exportar(vehiculos, config.reportPath());
                eventos.registrarReporteGenerado(archivo, vehiculos.size());
                System.out.println("Reporte generado correctamente: " + archivo.toAbsolutePath());
                System.out.println("Registros exportados: " + vehiculos.size());
            } catch (Exception error) {
                eventos.registrarErrorDatos("No se pudo generar el reporte Excel", error);
                System.err.println("No se pudo generar el reporte Excel: " + error.getMessage());
            }
        };
    }
}
