package agenda_sena.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "ambiente_id")
    private Ambiente ambiente;
    
    private String nombreInstructor;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Integer numeroAprendices;
    
    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;
}