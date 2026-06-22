package agenda_sena.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

import agenda_sena.model.EstadoReserva;

@Data
public class ReservaDTO {
    private Long id;
    @NotNull(message = "El ID del ambiente es obligatorio")
    private Long ambienteId;
    @NotBlank(message = "El nombre del instructor es obligatorio")
    private String nombreInstructor;
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaHoraInicio;
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaHoraFin;
    @Min(value = 1, message = "Debe haber al menos 1 aprendiz")
    private Integer numeroAprendices;
    private EstadoReserva estado;
}