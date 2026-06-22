package agenda_sena.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import agenda_sena.dto.AmbienteDTO;
import agenda_sena.service.agendaService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ambientes")
public class AmbienteController {

    private final agendaService agendaService;

    public AmbienteController(agendaService agendaService) {
        this.agendaService = agendaService;
    }

    @PostMapping
    public ResponseEntity<AmbienteDTO> registrar(@Valid @RequestBody AmbienteDTO dto) {
        return new ResponseEntity<>(agendaService.registrarAmbiente(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AmbienteDTO>> listar() {
        return ResponseEntity.ok(agendaService.listarAmbientes());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<AmbienteDTO>> listarDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(agendaService.listarAmbientesDisponibles(inicio, fin));
    }
}