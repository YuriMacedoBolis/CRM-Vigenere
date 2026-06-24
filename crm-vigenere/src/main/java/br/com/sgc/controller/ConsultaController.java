package br.com.sgc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

import br.com.sgc.domain.model.Consulta;
import br.com.sgc.dto.ConsultaDTO;
import br.com.sgc.repository.ConsultaRepository;

@RestController
@RequestMapping("/produtos") // Rota exigida no trabalho
@CrossOrigin(origins = "*")
public class ConsultaController {

    private final ConsultaRepository repository;

    public ConsultaController(ConsultaRepository repository) {
        this.repository = repository;
    }

    // GET /produtos - Exigido no trabalho
    @GetMapping
    public List<Consulta> listarTodos() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<Consulta> cadastrar(@RequestBody @Valid ConsultaDTO dto) {
        Consulta consulta = new Consulta();
        consulta.setNome(dto.getNome());
        consulta.setDescricao(dto.getDescricao());
        consulta.setPreco(dto.getPreco());
        consulta.setQuantidadeDisponivel(dto.getQuantidadeDisponivel());

        Consulta salvo = repository.save(consulta);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }
}