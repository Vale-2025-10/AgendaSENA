package agenda_sena.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import agenda_sena.dto.AmbienteDTO;
import agenda_sena.dto.ReporteOcupacionDTO;
import agenda_sena.dto.ReservaDTO;
import agenda_sena.exception.BusinessException;
import agenda_sena.model.Ambiente;
import agenda_sena.model.EstadoReserva;
import agenda_sena.model.Reserva;
import agenda_sena.repository.AmbienteRepository;
import agenda_sena.repository.ReservaRepository;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendaService {

    private final AmbienteRepository ambienteRepository;
    private final ReservaRepository reservaRepository;

    public AgendaService(AmbienteRepository ambienteRepository, ReservaRepository reservaRepository) {
        this.ambienteRepository = ambienteRepository;
        this.reservaRepository = reservaRepository;
    }

    public AmbienteDTO registrarAmbiente(AmbienteDTO dto) {
        Ambiente entity = new Ambiente();
        entity.setNombre(dto.getNombre());
        entity.setTipo(dto.getTipo());
        entity.setCapacidad(dto.getCapacidad());
        entity.setActivo(dto.getActivo());
        entity = ambienteRepository.save(entity);
        dto.setId(entity.getId());
        return dto;
    }

    public List<AmbienteDTO> listarAmbientes() {
        return ambienteRepository.findAll().stream().map(a -> {
            AmbienteDTO dto = new AmbienteDTO();
            dto.setId(a.getId());
            dto.setNombre(a.getNombre());
            dto.setTipo(a.getTipo());
            dto.setCapacidad(a.getCapacidad());
            dto.setActivo(a.getActivo());
            return dto;
        }).collect(Collectors.toList());
    }

    public ReservaDTO crearReserva(ReservaDTO dto) {
        Ambiente ambiente = ambienteRepository.findById(dto.getAmbienteId())
                .orElseThrow(() -> new BusinessException("Ambiente no encontrado", HttpStatus.BAD_REQUEST));

        // REGLA 4: Ambientes inactivos
        if (!ambiente.getActivo()) {
            throw new BusinessException("No se puede reservar un ambiente inactivo", HttpStatus.BAD_REQUEST);
        }

        // REGLA 7: No se reserva en el pasado
        if (dto.getFechaHoraInicio().isBefore(LocalDateTime.now())) {
            throw new BusinessException("La fecha de inicio debe ser en el futuro", HttpStatus.BAD_REQUEST);
        }

        // REGLA 3: Horario institucional (6:00 a 22:00) y duración (1 a 4 horas)
        LocalTime horaInicio = dto.getFechaHoraInicio().toLocalTime();
        LocalTime horaFin = dto.getFechaHoraFin().toLocalTime();
        if (horaInicio.isBefore(LocalTime.of(6, 0)) || horaFin.isAfter(LocalTime.of(22, 0))) {
            throw new BusinessException("Las reservas solo están permitidas entre las 06:00 y las 22:00", HttpStatus.BAD_REQUEST);
        }
        
        long horasDuracion = ChronoUnit.HOURS.between(dto.getFechaHoraInicio(), dto.getFechaHoraFin());
        if (horasDuracion < 1 || horasDuracion > 4) {
            throw new BusinessException("La reserva debe durar entre 1 y 4 horas", HttpStatus.BAD_REQUEST);
        }

        // REGLA 2: Capacidad
        if (dto.getNumeroAprendices() > ambiente.getCapacidad()) {
            throw new BusinessException("El número de aprendices supera la capacidad del ambiente", HttpStatus.BAD_REQUEST);
        }

        // REGLA 5: Límite por instructor (Max 3 activas por día)
        LocalDateTime inicioDia = dto.getFechaHoraInicio().toLocalDate().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);
        if (reservaRepository.countReservasInstructorDia(dto.getNombreInstructor(), inicioDia, finDia) >= 3) {
            throw new BusinessException("El instructor ya tiene 3 reservas activas en este día", HttpStatus.BAD_REQUEST);
        }

        // REGLA 1: Sin cruces de horario (Solapamiento)
        if (reservaRepository.existsOverlappingReservation(ambiente.getId(), dto.getFechaHoraInicio(), dto.getFechaHoraFin())) {
            throw new BusinessException("El ambiente ya se encuentra reservado en ese horario", HttpStatus.CONFLICT);
        }

        Reserva reserva = new Reserva();
        reserva.setAmbiente(ambiente);
        reserva.setNombreInstructor(dto.getNombreInstructor());
        reserva.setFechaHoraInicio(dto.getFechaHoraInicio());
        reserva.setFechaHoraFin(dto.getFechaHoraFin());
        reserva.setNumeroAprendices(dto.getNumeroAprendices());
        reserva.setEstado(EstadoReserva.ACTIVA);

        reserva = reservaRepository.save(reserva);
        dto.setId(reserva.getId());
        dto.setEstado(reserva.getEstado());
        return dto;
    }

    public void cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Reserva no encontrada", HttpStatus.BAD_REQUEST));

        // REGLA 6: Cancelación con anticipación (Mínimo 2 horas antes)
        if (LocalDateTime.now().plusHours(2).isAfter(reserva.getFechaHoraInicio())) {
            throw new BusinessException("Solo se puede cancelar con al menos 2 horas de anticipación", HttpStatus.BAD_REQUEST);
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    public List<ReservaDTO> verReservasActivasAmbienteFecha(Long ambienteId, LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = inicio.plusDays(1);
        return reservaRepository.findActivasPorAmbienteYDia(ambienteId, inicio, fin).stream().map(r -> {
            ReservaDTO d = new ReservaDTO();
            d.setId(r.getId());
            d.setAmbienteId(r.getAmbiente().getId());
            d.setNombreInstructor(r.getNombreInstructor());
            d.setFechaHoraInicio(r.getFechaHoraInicio());
            d.setFechaHoraFin(r.getFechaHoraFin());
            d.setNumeroAprendices(r.getNumeroAprendices());
            d.setEstado(r.getEstado());
            return d;
        }).collect(Collectors.toList());
    }

    public List<AmbienteDTO> listarAmbientesDisponibles(LocalDateTime inicio, LocalDateTime fin) {
        return ambienteRepository.findByActivoTrue().stream()
                .filter(a -> !reservaRepository.existsOverlappingReservation(a.getId(), inicio, fin))
                .map(a -> {
                    AmbienteDTO dto = new AmbienteDTO();
                    dto.setId(a.getId());
                    dto.setNombre(a.getNombre());
                    dto.setTipo(a.getTipo());
                    dto.setCapacidad(a.getCapacidad());
                    dto.setActivo(a.getActivo());
                    return dto;
                }).collect(Collectors.toList());
    }

    public List<ReporteOcupacionDTO> generarReporteOcupacion(LocalDate fecha) {
    LocalDateTime inicioDia = fecha.atStartOfDay();
    LocalDateTime finDia = inicioDia.plusDays(1);
    List<Ambiente> ambientes = ambienteRepository.findAll();
    List<ReporteOcupacionDTO> reporte = new ArrayList<>();

    for (Ambiente a : ambientes) {
        List<Reserva> activas = reservaRepository.findActivasPorAmbienteYDia(a.getId(), inicioDia, finDia);
        double horasOcupadas = 0;
        for (Reserva r : activas) {
            horasOcupadas += java.time.temporal.ChronoUnit.HOURS.between(r.getFechaHoraInicio(), r.getFechaHoraFin());
        }
        double porcentaje = (horasOcupadas / 16.0) * 100.0; // 16 horas institucionales
        reporte.add(new ReporteOcupacionDTO(a.getId(), a.getNombre(), horasOcupadas, porcentaje));
    }
    return reporte;

}
}
