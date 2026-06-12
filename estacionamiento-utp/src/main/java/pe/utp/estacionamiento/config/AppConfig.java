package pe.utp.estacionamiento.config;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Properties;

public class AppConfig {
    private final Properties properties = new Properties();

    public AppConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IllegalStateException("No se encontro application.properties");
            }
            properties.load(input);
        } catch (IOException error) {
            throw new IllegalStateException("No se pudo cargar la configuracion del sistema", error);
        }
    }

    public String dbUrl() {
        return properties.getProperty("db.url");
    }

    public String dbUser() {
        return properties.getProperty("db.user");
    }

    public String dbPassword() {
        return properties.getProperty("db.password", "");
    }

    public Path reportPath() {
        return Path.of(
                properties.getProperty("report.output-dir", "reportes"),
                properties.getProperty("report.file-name", "reporte_estacionamiento.xlsx")
        );
    }

    public BigDecimal hourlyRate() {
        return new BigDecimal(properties.getProperty("parking.hourly-rate", "3.00"));
    }
}
