package pe.utp.estacionamiento.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.utp.estacionamiento.model.MonitoringSnapshot;
import pe.utp.estacionamiento.service.EventoSistemaService;
import pe.utp.estacionamiento.service.MonitoringService;

@Controller
public class MonitoringController {
    private final MonitoringService monitoringService;
    private final EventoSistemaService eventos;

    public MonitoringController(MonitoringService monitoringService, EventoSistemaService eventos) {
        this.monitoringService = monitoringService;
        this.eventos = eventos;
    }

    @GetMapping({"/", "/monitoring"})
    public String dashboard(Model model) {
        eventos.registrarPanelAdministrador("consulta_dashboard_monitoreo");
        model.addAttribute("snapshot", monitoringService.snapshot());
        return "monitoring";
    }

    @GetMapping("/api/monitoring")
    @ResponseBody
    public MonitoringSnapshot api() {
        return monitoringService.snapshot();
    }

    @GetMapping("/monitoring/demo-logs")
    @ResponseBody
    public String demoLogs() {
        eventos.registrarLogin("u22210840");
        eventos.registrarUsuario("u22210840", "registro_demo");
        eventos.registrarVehiculo("ABC123");
        eventos.registrarEntradaVehiculo("ABC123", "A1");
        eventos.registrarSalidaVehiculo("ABC123");
        eventos.registrarReserva("A8", "reservado");
        eventos.registrarPago("ABC123", "3.00");
        eventos.advertir("WARN demo: espacio reservado consultado desde monitoreo");
        eventos.registrarExcepcion("demo", new IllegalStateException("ERROR demo para captura de pantalla"));
        return "Logs demo INFO, WARN y ERROR generados. Revisa consola y logs/estacionamiento.log";
    }
}
