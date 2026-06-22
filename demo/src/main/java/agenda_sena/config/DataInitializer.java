package agenda_sena.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import agenda_sena.model.Ambiente;
import agenda_sena.model.TipoAmbiente;
import agenda_sena.repository.AmbienteRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AmbienteRepository ambienteRepository;

    public DataInitializer(AmbienteRepository ambienteRepository) {
        this.ambienteRepository = ambienteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (ambienteRepository.count() == 0) {
            Ambiente a1 = new Ambiente();
            a1.setNombre("Sistemas 101");
            a1.setTipo(TipoAmbiente.LABORATORIO);
            a1.setCapacidad(25);
            a1.setActivo(true);

            Ambiente a2 = new Ambiente();
            a2.setNombre("Auditorio Central");
            a2.setTipo(TipoAmbiente.AUDITORIO);
            a2.setCapacidad(100);
            a2.setActivo(true);

            Ambiente a3 = new Ambiente();
            a3.setNombre("Sala de Juntas");
            a3.setTipo(TipoAmbiente.SALA);
            a3.setCapacidad(10);
            a3.setActivo(true);

            Ambiente a4 = new Ambiente();
            a4.setNombre("Laboratorio de Electrónica");
            a4.setTipo(TipoAmbiente.LABORATORIO);
            a4.setCapacidad(15);
            a4.setActivo(false); // Uno inactivo para pruebas de fallos

            ambienteRepository.save(a1);
            ambienteRepository.save(a2);
            ambienteRepository.save(a3);
            ambienteRepository.save(a4);
        }
    }
}