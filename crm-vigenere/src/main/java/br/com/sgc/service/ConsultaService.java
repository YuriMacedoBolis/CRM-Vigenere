package br.com.sgc.service;

import org.springframework.stereotype.Service;

import br.com.sgc.domain.model.Consulta;
import br.com.sgc.exception.BusinessException;
import br.com.sgc.repository.ConsultaRepository;

@Service
public class ConsultaService {

    private final ConsultaRepository repository;

    public ConsultaService(ConsultaRepository repository) {
        this.repository = repository;
    }

    public Consulta buscarPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new BusinessException("Consulta/Procedimento não encontrado."));
    }

    // Regra: Atualizar estoque e Não permitir venda se estoque insuficiente
    public void abaterEstoque(Long id, Integer quantidade) {
        Consulta consulta = buscarPorId(id);
        
        if (consulta.getQuantidadeDisponivel() < quantidade) {
            throw new BusinessException("Vagas/Estoque insuficientes para: " + consulta.getNome());
        }
        
        consulta.setQuantidadeDisponivel(consulta.getQuantidadeDisponivel() - quantidade);
        repository.save(consulta);
    }
}