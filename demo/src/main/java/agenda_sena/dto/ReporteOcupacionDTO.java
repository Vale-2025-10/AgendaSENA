package agenda_sena.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReporteOcupacionDTO {
    private Long ambienteId;
    private String nombreAmbiente;
    private Double horasReservadas;
    private Double porcentajeOcupacion;
}
