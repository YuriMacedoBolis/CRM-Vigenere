package br.com.sgc.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.sgc.domain.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método mágico do Spring que busca um usuário pelo nome de login
    Optional<Usuario> findByUsername(String username);
}