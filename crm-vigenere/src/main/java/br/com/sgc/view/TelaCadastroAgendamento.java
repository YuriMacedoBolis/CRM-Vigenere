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

        JLabel lblTitulo = new JLabel("Marcar Nova Consulta");
        lblTitulo.setForeground(EstilosGerais.DOURADO);
        lblTitulo.setFont(EstilosGerais.FONTE_TITULO);
        lblTitulo.setHorizontalAlignment(JLabel.CENTER);
        contentPane.add(lblTitulo, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel();
        pnlForm.setBackground(EstilosGerais.TEXTO_BRANCO);
        pnlForm.setLayout(null);
        contentPane.add(pnlForm, BorderLayout.CENTER);

        int startX = 50, startY = 30, width = 400, height = 30, gap = 60;

        // Primeira coluna: Seleção de Paciente e Médico
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

        // Segunda coluna: Dados de tempo e valor
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

        carregarPacientesNoCombo();
        carregarUsuariosNoCombo();
    }

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
        new TelaAgenda().setVisible(true);
        dispose();
    }

    // Carrega a lista de clientes da API para o JComboBox
    private void carregarPacientesNoCombo() {
        try {
            var client = java.net.http.HttpClient.newHttpClient();
            var request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/clientes"))
                    .GET().build();
            
            var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                var rootNode = mapper.readTree(response.body());
                for (var node : rootNode) {
                    cbPacientes.addItem(new ComboItem(node.get("id").asLong(), node.get("nome").asText()));
                }
            }
        } catch (Exception ex) {
            System.out.println("Erro ao carregar pacientes: " + ex.getMessage());
        }
    }

    private void carregarUsuariosNoCombo() {
        cbUsuarios.addItem(new ComboItem(1L, "dr.vigenere"));
    }

    // Processa os dados do formulário e envia o POST para a API
    private void salvarAgendamentoNaAPI() {
        try {
            ComboItem pacienteSelecionado = (ComboItem) cbPacientes.getSelectedItem();
            ComboItem usuarioSelecionado = (ComboItem) cbUsuarios.getSelectedItem();

            if (pacienteSelecionado == null || txtData.getText().trim().isEmpty() || 
                txtHora.getText().trim().isEmpty() || txtValor.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String[] partesData = txtData.getText().trim().split("/");
            if(partesData.length != 3) {
                JOptionPane.showMessageDialog(this, "Data inválida. Utilize o formato DD/MM/YYYY.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String dataAmericana = partesData[2] + "-" + partesData[1] + "-" + partesData[0];
            String dataHoraFinal = dataAmericana + "T" + txtHora.getText().trim() + ":00";
            String valorFormatado = txtValor.getText().trim().replace(",", ".");

            // Monta o JSON da requisição incluindo um serviço padrão (ID 1) para satisfazer a regra de negócio do backend
            String jsonRequestBody = String.format(
                "{\"data\":\"%s\", " +
                "\"paciente\":{\"id\":%d}, " +
                "\"usuarioResponsavel\":{\"id\":%d}, " +
                "\"valorTotal\":%s, " +
                "\"itens\":[{\"consulta\":{\"id\":1}, \"precoUnitario\":%s, \"quantidade\":1}]}",
                dataHoraFinal, pacienteSelecionado.getId(), usuarioSelecionado.getId(), valorFormatado, valorFormatado
            );

            System.out.println("Payload: " + jsonRequestBody);

            var client = java.net.http.HttpClient.newHttpClient();
            var request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/vendas"))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) { 
                JOptionPane.showMessageDialog(this, "Agendamento registrado com sucesso!");
                voltarParaAgenda(); 
            } else {
                System.out.println("Erro na API: " + response.body());
                JOptionPane.showMessageDialog(this, "Erro ao registrar o agendamento (Status " + response.statusCode() + ").\nConsulte o log para mais detalhes.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro de comunicação com o servidor: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Classe auxiliar para manipular chave/valor no JComboBox
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