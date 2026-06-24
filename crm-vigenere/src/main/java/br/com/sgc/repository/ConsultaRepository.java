package br.com.sgc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.sgc.domain.model.Consulta;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
}