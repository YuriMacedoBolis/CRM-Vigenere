package br.com.sgc.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.sgc.domain.model.Agendamento;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    
    List<Agendamento> findByDataBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Agendamento> findByPacienteId(Long pacienteId);
}