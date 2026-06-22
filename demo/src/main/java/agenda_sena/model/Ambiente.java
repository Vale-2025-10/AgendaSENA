package agenda_sena.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Ambiente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    
    @Enumerated(EnumType.STRING)
    private TipoAmbiente tipo;
    
    private Integer capacidad;
    private Boolean activo;
}
