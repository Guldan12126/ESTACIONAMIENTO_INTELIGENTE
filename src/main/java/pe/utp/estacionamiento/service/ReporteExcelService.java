package pe.utp.estacionamiento.service;

import com.google.common.collect.ImmutableList;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.utp.estacionamiento.model.ReporteVehiculo;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class ReporteExcelService {
    private static final Logger logger = LoggerFactory.getLogger(ReporteExcelService.class);
    private static final List<String> COLUMNAS = ImmutableList.of("Placa", "Hora Entrada", "Hora Salida", "Tiempo", "Pago");

    public Path exportar(List<ReporteVehiculo> vehiculos, Path rutaArchivo) throws IOException {
        Files.createDirectories(rutaArchivo.getParent());

        try (Workbook workbook = new XSSFWorkbook();
             OutputStream outputStream = Files.newOutputStream(rutaArchivo)) {

            Sheet sheet = workbook.createSheet("Reporte Estacionamiento");
            CellStyle estiloCabecera = crearEstiloCabecera(workbook);
            CellStyle estiloFecha = crearEstiloFecha(workbook);
            CellStyle estiloPago = crearEstiloPago(workbook);

            crearCabecera(sheet, estiloCabecera);
            llenarDatos(sheet, vehiculos, estiloFecha, estiloPago);

            for (int columna = 0; columna < COLUMNAS.size(); columna++) {
                sheet.autoSizeColumn(columna);
            }

            workbook.write(outputStream);
            logger.info("Reporte Excel generado correctamente en {}", rutaArchivo.toAbsolutePath());
            return rutaArchivo;
        } catch (IOException error) {
            logger.error("Error al generar el reporte Excel", error);
            throw error;
        }
    }

    private CellStyle crearEstiloCabecera(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private CellStyle crearEstiloFecha(Workbook workbook) {
        CreationHelper helper = workbook.getCreationHelper();
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(helper.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));
        return style;
    }

    private CellStyle crearEstiloPago(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("\"S/\" #,##0.00"));
        return style;
    }

    private void crearCabecera(Sheet sheet, CellStyle estiloCabecera) {
        Row header = sheet.createRow(0);
        for (int columna = 0; columna < COLUMNAS.size(); columna++) {
            header.createCell(columna).setCellValue(COLUMNAS.get(columna));
            header.getCell(columna).setCellStyle(estiloCabecera);
        }
    }

    private void llenarDatos(Sheet sheet, List<ReporteVehiculo> vehiculos, CellStyle estiloFecha, CellStyle estiloPago) {
        int fila = 1;
        for (ReporteVehiculo vehiculo : vehiculos) {
            Row row = sheet.createRow(fila++);
            row.createCell(0).setCellValue(vehiculo.placa());
            escribirFecha(row, 1, vehiculo.horaEntrada(), estiloFecha);
            escribirFecha(row, 2, vehiculo.horaSalida(), estiloFecha);
            row.createCell(3).setCellValue(formatearTiempo(vehiculo.tiempo()));
            escribirPago(row, 4, vehiculo.pago(), estiloPago);
        }
    }

    private void escribirFecha(Row row, int columna, LocalDateTime fecha, CellStyle estiloFecha) {
        if (fecha == null) {
            row.createCell(columna).setCellValue("En estacionamiento");
            return;
        }

        row.createCell(columna).setCellValue(fecha);
        row.getCell(columna).setCellStyle(estiloFecha);
    }

    private void escribirPago(Row row, int columna, BigDecimal pago, CellStyle estiloPago) {
        row.createCell(columna).setCellValue(pago.doubleValue());
        row.getCell(columna).setCellStyle(estiloPago);
    }

    private String formatearTiempo(Duration tiempo) {
        long minutos = Math.max(0, tiempo.toMinutes());
        long horas = minutos / 60;
        long minutosRestantes = minutos % 60;
        return horas + " h " + minutosRestantes + " min";
    }
}
