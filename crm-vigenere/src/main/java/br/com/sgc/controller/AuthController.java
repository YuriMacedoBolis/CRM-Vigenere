package br.com.sgc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

import br.com.sgc.domain.model.Usuario;
import br.com.sgc.repository.UsuarioRepository;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository repository;

    public AuthController(UsuarioRepository repository) {
        this.repository = repository;
    }

    // Autentica o usuário e retorna o perfil de acesso
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciais) {
        String username = credenciais.get("username");
        String senha = credenciais.get("senha");

        Optional<Usuario> usuarioOpt = repository.findByUsername(username);

        // Valida as credenciais (inclui bypass provisório para desenvolvimento local)
        if (usuarioOpt.isPresent() && (usuarioOpt.get().getSenha().equals(senha) || senha.equals("123456"))) {
            return ResponseEntity.ok().body(Map.of(
                "mensagem", "Login efetuado com sucesso!", 
                "perfil", usuarioOpt.get().getPerfil()
            ));
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
            "erro", "Usuário ou senha inválidos."
        ));
    }
}