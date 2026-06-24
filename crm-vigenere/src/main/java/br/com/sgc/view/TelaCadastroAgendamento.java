package br.com.sgc.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;

public class TelaCadastroAgendamento extends JFrame {

    private JPanel contentPane;
    private JTextField txtData;
    private JTextField txtHora;
    private JTextField txtValor;
    private JComboBox<ComboItem> cbPacientes;
    private JComboBox<ComboItem> cbUsuarios;

    public TelaCadastroAgendamento() {
        setTitle("CRM Vigenere - Novo Agendamento");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(1144, 612);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(EstilosGerais.AZUL_FUNDO);
        contentPane.setBorder(new EmptyBorder(40, 100, 40, 100));
        contentPane.setLayout(new BorderLayout(0, 30));
        setContentPane(contentPane);

        // --- Cabeçalho ---
        JLabel lblTitulo = new JLabel("Marcar Nova Consulta (Venda)");
        lblTitulo.setForeground(EstilosGerais.DOURADO);
        lblTitulo.setFont(EstilosGerais.FONTE_TITULO);
        lblTitulo.setHorizontalAlignment(JLabel.CENTER);
        contentPane.add(lblTitulo, BorderLayout.NORTH);

        // --- Formulário ---
        JPanel pnlForm = new JPanel();
        pnlForm.setBackground(EstilosGerais.TEXTO_BRANCO);
        pnlForm.setLayout(null);
        contentPane.add(pnlForm, BorderLayout.CENTER);

        int startX = 50, startY = 30, width = 400, height = 30, gap = 60;

        // Coluna 1: Paciente e Médico
        pnlForm.add(criarLabel("Selecione o Paciente:", startX, startY));
        cbPacientes = new JComboBox<>();
        cbPacientes.setBounds(startX, startY + 25, width, height);
        cbPacientes.setBackground(EstilosGerais.TEXTO_BRANCO);
        pnlForm.add(cbPacientes);

        pnlForm.add(criarLabel("Médico/Responsável:", startX, startY + gap));
        cbUsuarios = new JComboBox<>();
        cbUsuarios.setBounds(startX, startY + gap + 25, width, height);
        cbUsuarios.setBackground(EstilosGerais.TEXTO_BRANCO);
        pnlForm.add(cbUsuarios);

        // Coluna 2: Data, Hora e Valor (Financeiro)
        int col2X = 500;
        
        pnlForm.add(criarLabel("Data (DD/MM/YYYY):", col2X, startY));
        txtData = new JTextField();
        txtData.setBounds(col2X, startY + 25, 180, height);
        pnlForm.add(txtData);

        pnlForm.add(criarLabel("Hora (HH:MM):", col2X + 200, startY));
        txtHora = new JTextField();
        txtHora.setBounds(col2X + 200, startY + 25, 100, height);
        pnlForm.add(txtHora);

        pnlForm.add(criarLabel("Valor da Consulta (R$):", col2X, startY + gap));
        txtValor = new JTextField();
        txtValor.setBounds(col2X, startY + gap + 25, 280, height);
        pnlForm.add(txtValor);

        // --- Rodapé ---
        JPanel pnlBotoes = new JPanel();
        pnlBotoes.setBackground(EstilosGerais.AZUL_FUNDO);
        
        JButton btnCancelar = new JButton("Cancelar");
        estilizarBotao(btnCancelar, new java.awt.Color(180, 50, 50));
        btnCancelar.addActionListener(e -> voltarParaAgenda());
        
        JButton btnSalvar = new JButton("Confirmar Agendamento");
        estilizarBotao(btnSalvar, EstilosGerais.DOURADO);
        btnSalvar.addActionListener(e -> salvarAgendamentoNaAPI());

        pnlBotoes.add(btnCancelar);
        pnlBotoes.add(btnSalvar);
        contentPane.add(pnlBotoes, BorderLayout.SOUTH);

        // Carrega as listas suspensas ao abrir a tela
        carregarPacientesNoCombo();
        carregarUsuariosNoCombo();
    }

    // --- MÉTODOS DE APOIO ---

    private JLabel criarLabel(String texto, int x, int y) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(EstilosGerais.FONTE_LABEL);
        lbl.setBounds(x, y, 300, 20);
        return lbl;
    }

    private void estilizarBotao(JButton btn, java.awt.Color cor) {
        btn.setBackground(cor);
        btn.setForeground(EstilosGerais.TEXTO_BRANCO);
        btn.setFont(EstilosGerais.FONTE_BOTAO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void voltarParaAgenda() {
        TelaAgenda tela = new TelaAgenda();
        tela.setVisible(true);
        dispose();
    }

    // --- MÉTODOS DE INTEGRAÇÃO COM A API ---

    private void carregarPacientesNoCombo() {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/clientes"))
                    .GET().build();
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(response.body());
                for (com.fasterxml.jackson.databind.JsonNode node : rootNode) {
                    cbPacientes.addItem(new ComboItem(node.get("id").asLong(), node.get("nome").asText()));
                }
            }
        } catch (Exception ex) {
            System.out.println("Erro ao carregar pacientes: " + ex.getMessage());
        }
    }

    private void carregarUsuariosNoCombo() {
        // ID 1 é o padrão do seu banco para o dr.vigenere
        cbUsuarios.addItem(new ComboItem(1L, "dr.vigenere"));
    }

    // O MOTOR BLINDADO QUE AVISA O QUE DEU ERRADO
    private void salvarAgendamentoNaAPI() {
        try {
            ComboItem pacienteSelecionado = (ComboItem) cbPacientes.getSelectedItem();
            ComboItem usuarioSelecionado = (ComboItem) cbUsuarios.getSelectedItem();

            if (pacienteSelecionado == null || txtData.getText().trim().isEmpty() || txtHora.getText().trim().isEmpty() || txtValor.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos, incluindo o Valor da Consulta!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Tratamento da data
            String[] partesData = txtData.getText().trim().split("/");
            if(partesData.length != 3) {
                JOptionPane.showMessageDialog(this, "Data inválida! Use DD/MM/YYYY.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String dataAmericana = partesData[2] + "-" + partesData[1] + "-" + partesData[0];
            String dataHoraFinal = dataAmericana + "T" + txtHora.getText().trim() + ":00";
            
            // Tratamento do valor (troca vírgula por ponto)
            String valorFormatado = txtValor.getText().trim().replace(",", ".");

         // Criamos um item/procedimento padrão chamado "Consulta Médica" para satisfazer a regra de negócio da API.
            // O valor unitário do item recebe o mesmo valor total preenchido na tela.
         // O segredo está aqui: o item agora passa um objeto "consulta" apontando para o ID 1 do seu catálogo
            String jsonRequestBody = String.format(
                "{\"data\":\"%s\", " +
                "\"paciente\":{\"id\":%d}, " +
                "\"usuarioResponsavel\":{\"id\":%d}, " +
                "\"valorTotal\":%s, " +
                "\"itens\":[{\"consulta\":{\"id\":1}, \"precoUnitario\":%s, \"quantidade\":1}]}",
                dataHoraFinal, pacienteSelecionado.getId(), usuarioSelecionado.getId(), valorFormatado, valorFormatado
            );

            // Rastreador no Console
            System.out.println("JSON ENVIADO: " + jsonRequestBody);

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/vendas"))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) { 
                JOptionPane.showMessageDialog(this, "Agendamento registrado com sucesso!");
                voltarParaAgenda(); 
            } else {
                // Rastreador do Erro Real
                System.out.println("RESPOSTA DO SERVIDOR: " + response.body());
                JOptionPane.showMessageDialog(this, "Erro do Servidor (Código " + response.statusCode() + ")\n\nVerifique o console do Eclipse para detalhes.", "Falha no Cadastro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro de conexão: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- CLASSE AUXILIAR DO DROPDOWN ---
    class ComboItem {
        private Long id;
        private String nome;

        public ComboItem(Long id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        public Long getId() { return id; }

        @Override
        public String toString() { return nome; }
    }
}