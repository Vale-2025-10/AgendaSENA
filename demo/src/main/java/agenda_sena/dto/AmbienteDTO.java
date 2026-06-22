package agenda_sena.dto;


import agenda_sena.model.TipoAmbiente;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AmbienteDTO {
    private Long id;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotNull(message = "El tipo de ambiente es obligatorio")
    private TipoAmbiente tipo;
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacidad;
    @NotNull(message = "El estado activo/inactivo es obligatorio")
    private Boolean activo;
}