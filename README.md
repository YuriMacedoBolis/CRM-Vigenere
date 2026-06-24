#  CRM Vigenere - Sistema de Gestão Clínica

Este projeto é um sistema completo (Client-Server) para gestão de clínicas médicas, construído como requisito de avaliação final (Entrega 3). O sistema separa as responsabilidades entre uma API RESTful no back-end e uma interface gráfica Desktop no front-end.

##  Funcionalidades Principais (Atendimento à Errata do Projeto)

O sistema foi desenvolvido cumprindo todos os requisitos estabelecidos na documentação da disciplina:

* **Integração com Swing:** A interface gráfica foi construída inteiramente com Java Swing, consumindo a API REST de forma assíncrona via requisições HTTP.
* **Registro de Vendas:** O motor de agendamentos (`/vendas`) funciona como um PDV, onde o usuário seleciona o paciente, o serviço médico, e o sistema registra a transação financeira completa.
* **Controle de Estoque:** Implementado de forma rígida na camada `ConsultaService`. Ao realizar um agendamento/venda, o sistema valida a disponibilidade e abate automaticamente a quantidade (vagas/estoque) daquele procedimento específico.
* **Relatórios de Vendas:** Dashboard financeiro gerencial que consulta o histórico de vendas na API, calcula e exibe em tempo real o volume de consultas realizadas e o faturamento total bruto.
* **API Protegida:** Acesso restrito via módulo de Autenticação (`AuthController`), exigindo login validado contra o banco de dados antes da liberação e navegação para o Menu Principal.

##  Detalhes Técnicos e Funcionalidades Avançadas

Além dos requisitos básicos, o sistema foi arquitetado com boas práticas de engenharia de software:

### Spring Boot & API RESTful
* **CRUD Completo e Semântica HTTP:** Mapeamento correto de rotas usando os verbos `GET`, `POST`, `PUT` e `DELETE`, retornando os status codes apropriados (ex: `201 Created`, `204 No Content`, `404 Not Found`).
* **Padrão DTO (Data Transfer Object):** Utilização de DTOs para separar a camada de visualização dos modelos de banco de dados, aumentando a segurança no tráfego de informações (JSON).
* **Tratamento de Exceções Customizadas:** Criação da classe `BusinessException` para blindar o *Service*. Se uma regra de negócio for violada (ex: agendamento sem serviço), a API não "quebra", mas devolve uma mensagem limpa para o front-end.
* **Transações Seguras (ACID):** Uso da anotação `@Transactional` nos serviços para garantir que vendas e baixas de estoque ocorram com segurança (se uma etapa falhar, o banco sofre *rollback*).
* **Cross-Origin Resource Sharing (CORS):** Rotas liberadas via `@CrossOrigin` para permitir a comunicação fluida entre a aplicação Desktop e a API.

### Java Swing (Interface Gráfica & UX)
* **Design System Centralizado:** Criação de uma classe utilitária (`EstilosGerais`) para gerenciar as cores, fontes e espaçamentos, mantendo a consistência visual em todas as telas sem duplicação de código.
* **Componentes Dinâmicos (Data Binding):** Elementos como `JComboBox` (listas suspensas) e `JTable` (tabelas) não usam dados fixos; eles são preenchidos e populados dinamicamente extraindo dados do JSON retornado pela API via *Jackson ObjectMapper*.
* **Tratamento de Falhas e Resiliência:** O front-end está programado para capturar falhas de requisição. Se o servidor cair ou uma validação falhar, o sistema exibe popups amigáveis (`JOptionPane`) orientando o usuário em vez de travar a tela.
* **Navegação Inteligente:** Controle eficiente do ciclo de vida das janelas utilizando o método `dispose()`, garantindo uma navegação leve entre os módulos sem sobrecarregar a memória da máquina.

##  Arquitetura
O projeto adota os princípios de alta coesão e baixo acoplamento:
* **Back-end:** Java 21, Spring Boot 3, MVC, Spring Data JPA, Hibernate.
* **Front-end:** Java Swing com `java.net.http.HttpClient`.
* **Banco de Dados:** MySQL com mapeamento relacional (Entidades com chaves estrangeiras e relacionamentos `@OneToMany` e `@ManyToOne`).

---

##  Instruções de Execução

Siga os passos abaixo para rodar o sistema localmente na sua máquina:

### 1. Pré-requisitos
* **Java Development Kit (JDK) 21** instalado.
* **MySQL Server** rodando na porta padrão (`3306`).
* IDE de sua preferência (Eclipse, IntelliJ) com suporte a projetos **Maven**.

### 2. Configuração do Banco de Dados
1. Abra o seu MySQL Workbench (ou linha de comando) e crie um banco de dados vazio chamado `vigenere_db`:
   ```sql
   CREATE DATABASE vigenere_db;
   ```
2. O sistema está configurado no arquivo `application.properties` para usar o usuário `root` e a senha `Escola21`. Se o seu banco local utilizar credenciais diferentes, atualize as seguintes linhas no arquivo:
   ```properties
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   ```

### 3. Inicialização da API (Back-end)
1. Importe o projeto na sua IDE como *Existing Maven Project*.
2. Aguarde o Maven realizar o download de todas as dependências do `pom.xml`.
3. Navegue até o pacote raiz `br.com.sgc` e execute a classe `CrmVigenereApplication.java` como uma *Java Application*.
4. O Spring Boot criará as tabelas no banco de dados automaticamente. Em seguida, o script de inicialização `data.sql` irá injetar os dados falsos (mock) para testes. Verifique no console se o servidor iniciou com sucesso na porta `8080`.

### 4. Inicialização da Interface Gráfica (Front-end)
1. Com o servidor Spring Boot rodando em segundo plano, navegue até o pacote `br.com.sgc.view`.
2. Execute a classe `TelaLogin.java` como uma *Java Application*.
3. **Acesso ao Sistema:** Utilize as credenciais seguras injetadas pelo banco de dados para entrar no painel:
   * **Usuário:** `dr.vigenere` (ou `recepcao01`)
   * **Senha:** `123456`

---

## 👥 Desenvolvedores
* **Yuri Macedo Bolis**
* **Victor Hugo Cândido**
* **Vinicius Coelho**
