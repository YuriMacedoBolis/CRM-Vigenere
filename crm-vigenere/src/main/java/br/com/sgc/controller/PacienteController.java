package br.com.sgc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

import br.com.sgc.domain.model.Paciente;
import br.com.sgc.dto.PacienteDTO;
import br.com.sgc.repository.PacienteRepository;

@RestController
@RequestMapping("/clientes") // URL exigida pelo professor
@CrossOrigin(origins = "*") // Permite que a sua interface web converse com a API sem bloqueios de segurança do navegador
public class PacienteController {

    private final PacienteRepository repository;

    public PacienteController(PacienteRepository repository) {
        this.repository = repository;
    }

    // GET /clientes
    @GetMapping
    public List<Paciente> listarTodos() {
        return repository.findAll();
    }

    // POST /clientes
    @PostMapping
    public ResponseEntity<Paciente> cadastrar(@RequestBody @Valid PacienteDTO dto) {
        // Regra: CPF não pode ser duplicado
        if (repository.existsByCpf(dto.getCpf())) {
            throw new br.com.sgc.exception.BusinessException("CPF já cadastrado no sistema.");
        }

        Paciente paciente = new Paciente();
        paciente.setNome(dto.getNome());
        paciente.setCpf(dto.getCpf());
        paciente.setEmail(dto.getEmail());
        paciente.setTelefone(dto.getTelefone());
        paciente.setEndereco(dto.getEndereco());

        Paciente salvo = repository.save(paciente);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // DELETE /clientes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // A regra "Cliente não pode ser removido se possuir vendas" será garantida automaticamente
        // pelo banco de dados através da chave estrangeira (Constraint Violation)!
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // PUT /clientes/{id} - Rota para Atualizar um paciente existente
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizar(@PathVariable Long id, @RequestBody @Valid PacienteDTO dto) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Paciente paciente = repository.findById(id).get();
        
        paciente.setNome(dto.getNome());
        paciente.setCpf(dto.getCpf());
        paciente.setEmail(dto.getEmail());
        paciente.setTelefone(dto.getTelefone());
        paciente.setEndereco(dto.getEndereco());

        Paciente salvo = repository.save(paciente);
        return ResponseEntity.ok(salvo);
    }
}