package agenda_sena.controller;

import agenda_sena.service.agendaService;
import agenda_sena.dto.ReporteOcupacionDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    // 1. CAMBIA AQUÍ: Tipo con 'A' mayúscula, el nombre de la variable se queda en minúscula
    private final agendaService agendaService; 

    // 2. CAMBIA AQUÍ: El parámetro del constructor también debe ser 'AgendaService'
    public ReporteController(agendaService agendaService) {
        this.agendaService = agendaService;
    }

    @GetMapping("/ocupacion")
    public ResponseEntity<List<ReporteOcupacionDTO>> obtenerOcupacion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(agendaService.generarReporteOcupacion(fecha));
    }
}