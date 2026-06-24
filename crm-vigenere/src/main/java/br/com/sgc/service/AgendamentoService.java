package br.com.sgc.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.sgc.domain.model.Agendamento;
import br.com.sgc.domain.model.ItemAgendamento;
import br.com.sgc.repository.AgendamentoRepository;
import br.com.sgc.exception.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ConsultaService consultaService;

    public AgendamentoService(AgendamentoRepository agendamentoRepository, ConsultaService consultaService) {
        this.agendamentoRepository = agendamentoRepository;
        this.consultaService = consultaService;
    }

    @Transactional
    public Agendamento registrarAgendamento(Agendamento agendamento) {
        // Regra: Não permitir venda sem itens
        if (agendamento.getItens() == null || agendamento.getItens().isEmpty()) {
            throw new BusinessException("O agendamento deve conter pelo menos um procedimento/consulta.");
        }

        // SOLUÇÃO DA DATA: Só usa o 'now()' se o front-end não tiver enviado nenhuma data
        if (agendamento.getData() == null) {
            agendamento.setData(LocalDateTime.now());
        }
        
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItemAgendamento item : agendamento.getItens()) {
            // Busca a consulta no banco para garantir a ligação e conferir o estoque
            var consultaBanco = consultaService.buscarPorId(item.getConsulta().getId());
            
            // SOLUÇÃO DO PREÇO: Se o front-end mandou um preçoUnitario, nós respeitamos. 
            // Só pega o valor tabelado do banco se o front-end mandar vazio (null).
            if (item.getPrecoUnitario() == null) {
                item.setPrecoUnitario(consultaBanco.getPreco());
            }
            
            BigDecimal subtotal = item.getPrecoUnitario().multiply(new BigDecimal(item.getQuantidade()));
            valorTotal = valorTotal.add(subtotal);

            // Regra: Atualizar estoque após venda
            consultaService.abaterEstoque(consultaBanco.getId(), item.getQuantidade());
            
            // Faz a ligação bidirecional
            item.setConsulta(consultaBanco);
        }

        // Mantém a regra de calcular o total dinamicamente somando os itens
        agendamento.setValorTotal(valorTotal);
        return agendamentoRepository.save(agendamento);
    }
}