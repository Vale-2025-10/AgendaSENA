package agenda_sena.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import agenda_sena.model.Reserva;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Regla de oro de solapamiento: (Inicio1 < Fin2) Y (Fin1 > Inicio2)
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.ambiente.id = :ambienteId " +
           "AND r.estado = 'ACTIVA' " +
           "AND r.fechaHoraInicio < :fin AND r.fechaHoraFin > :inicio")
    boolean existsOverlappingReservation(@Param("ambienteId") Long ambienteId, 
                                         @Param("inicio") LocalDateTime inicio, 
                                         @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.nombreInstructor = :instructor " +
           "AND r.estado = 'ACTIVA' " +
           "AND r.fechaHoraInicio >= :inicioYMD AND r.fechaHoraInicio < :finYMD")
    long countReservasInstructorDia(@Param("instructor") String instructor, 
                                    @Param("inicioYMD") LocalDateTime inicioYMD, 
                                    @Param("finYMD") LocalDateTime finYMD);

    @Query("SELECT r FROM Reserva r WHERE r.ambiente.id = :ambienteId " +
           "AND r.estado = 'ACTIVA' " +
           "AND r.fechaHoraInicio >= :inicioYMD AND r.fechaHoraInicio < :finYMD")
    List<Reserva> findActivasPorAmbienteYDia(@Param("ambienteId") Long ambienteId, 
                                             @Param("inicioYMD") LocalDateTime inicioYMD, 
                                             @Param("finYMD") LocalDateTime finYMD);
}