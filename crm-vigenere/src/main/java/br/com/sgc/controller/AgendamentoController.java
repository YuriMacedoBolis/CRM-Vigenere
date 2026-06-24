package br.com.sgc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import br.com.sgc.domain.model.Agendamento;
import br.com.sgc.service.AgendamentoService;
import br.com.sgc.repository.AgendamentoRepository;

@RestController
@RequestMapping("/vendas")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    private final AgendamentoService service;
    private final AgendamentoRepository repository;

    public AgendamentoController(AgendamentoService service, AgendamentoRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    // Registra um novo agendamento
    @PostMapping
    public ResponseEntity<Agendamento> registrarVenda(@RequestBody Agendamento agendamento) {
        Agendamento salvo = service.registrarAgendamento(agendamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // Busca um agendamento específico pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Retorna o histórico de vendas para listagens e relatórios
    @GetMapping
    public List<Agendamento> listarTodas() {
        return repository.findAll();
    }
    
    // Cancela um agendamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarVenda(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // Atualiza os dados de um agendamento existente
    @PutMapping("/{id}")
    public ResponseEntity<Agendamento> atualizarVenda(@PathVariable Long id, @RequestBody Agendamento dadosAtualizados) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Garante que o ID da URL será usado para atualizar o registro correto
        dadosAtualizados.setId(id);
        
        // Passa pelo service para manter as regras de negócio e reprocessar valores
        Agendamento salvo = service.registrarAgendamento(dadosAtualizados);
        return ResponseEntity.ok(salvo);
    }
}