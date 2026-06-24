package br.com.sgc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.sgc.domain.model.ItemAgendamento;

@Repository
public interface ItemAgendamentoRepository extends JpaRepository<ItemAgendamento, Long> {
}