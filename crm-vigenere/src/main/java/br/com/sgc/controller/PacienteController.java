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
@RequestMapping("/clientes")
@CrossOrigin(origins = "*")
public class PacienteController {

    private final PacienteRepository repository;

    public PacienteController(PacienteRepository repository) {
        this.repository = repository;
    }

    // Retorna a lista de todos os pacientes cadastrados
    @GetMapping
    public List<Paciente> listarTodos() {
        return repository.findAll();
    }

    // Cadastra um novo paciente
    @PostMapping
    public ResponseEntity<Paciente> cadastrar(@RequestBody @Valid PacienteDTO dto) {
        // Valida unicidade do CPF
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

    // Remove um paciente pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        //O banco de dados (Constraint Violation) bloqueia a exclusão caso o paciente possua agendamentos atrelados
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // Atualiza os dados cadastrais de um paciente
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