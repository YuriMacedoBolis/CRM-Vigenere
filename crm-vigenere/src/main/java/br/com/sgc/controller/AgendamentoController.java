package br.com.sgc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import br.com.sgc.domain.model.Agendamento;
import br.com.sgc.service.AgendamentoService;
import br.com.sgc.repository.AgendamentoRepository;

@RestController
@RequestMapping("/vendas") // Rota exigida no trabalho
@CrossOrigin(origins = "*")
public class AgendamentoController {

    private final AgendamentoService service;
    private final AgendamentoRepository repository;

    public AgendamentoController(AgendamentoService service, AgendamentoRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    // POST /vendas - Exigido no trabalho
    @PostMapping
    public ResponseEntity<Agendamento> registrarVenda(@RequestBody Agendamento agendamento) {
        Agendamento salvo = service.registrarAgendamento(agendamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // GET /vendas/{id} - Exigido no trabalho
    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Rota extra para apoiar a funcionalidade "Relatórios de Vendas"
    @GetMapping
    public List<Agendamento> listarTodas() {
        return repository.findAll();
    }
    
 // DELETE /vendas/{id} - Rota para cancelar uma consulta/venda
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarVenda(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
 // PUT /vendas/{id} - Rota para Atualizar um agendamento existente
    @PutMapping("/{id}")
    public ResponseEntity<Agendamento> atualizarVenda(@PathVariable Long id, @RequestBody Agendamento dadosAtualizados) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Colocamos o ID na requisição para o banco de dados saber que é uma atualização e não um novo cadastro
        dadosAtualizados.setId(id);
        
        // Passamos pelo service do seu professor para garantir que as regras de negócio sejam respeitadas!
        Agendamento salvo = service.registrarAgendamento(dadosAtualizados);
        return ResponseEntity.ok(salvo);
    }
}