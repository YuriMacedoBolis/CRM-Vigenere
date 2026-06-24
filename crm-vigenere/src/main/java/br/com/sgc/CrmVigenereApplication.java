package br.com.sgc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import br.com.sgc.view.TelaLogin;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class CrmVigenereApplication {

    public static void main(String[] args) {
        
        // 1. Configura o Spring Boot para permitir a abertura de telas do Java Swing
        SpringApplicationBuilder builder = new SpringApplicationBuilder(CrmVigenereApplication.class);
        builder.headless(false); 
        
        // 2. Liga o Servidor da API (O motor do backend)
        builder.run(args);
        
        // 3. Assim que o servidor ligar com sucesso, ele abre a Tela de Login automaticamente!
        java.awt.EventQueue.invokeLater(() -> {
            TelaLogin tela = new TelaLogin();
            tela.setVisible(true);
        });
    }

}
