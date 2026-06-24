package br.com.sgc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.sgc.domain.model.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    boolean existsByCpf(String cpf);
}