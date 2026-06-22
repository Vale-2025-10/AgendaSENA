package agenda_sena.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import agenda_sena.dto.ReservaDTO;
import agenda_sena.service.agendaService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final agendaService agendaService;

    public ReservaController(agendaService agendaService) {
        this.agendaService = agendaService;
    }

    @PostMapping
    public ResponseEntity<ReservaDTO> crear(@Valid @RequestBody ReservaDTO dto) {
        return new ResponseEntity<>(agendaService.crearReserva(dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        agendaService.cancelarReserva(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ambiente/{id}")
    public ResponseEntity<List<ReservaDTO>> verPorAmbienteYFecha(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(agendaService.verReservasActivasAmbienteFecha(id, fecha));
    }
}