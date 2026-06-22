package agenda_sena.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import agenda_sena.model.Ambiente;

import java.util.List;

public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {
    List<Ambiente> findByActivoTrue();
}