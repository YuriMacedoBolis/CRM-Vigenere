package br.com.sgc.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class TelaAgenda extends JFrame {

    private JPanel contentPane;
    private JTable tabelaAgenda;

    public TelaAgenda() {
        setTitle("CRM Vigenere - Agenda Médica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(1144, 612); // Padrão exato das outras telas
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(EstilosGerais.AZUL_FUNDO);
        contentPane.setBorder(new EmptyBorder(30, 40, 30, 40));
        contentPane.setLayout(new BorderLayout(0, 20)); 
        setContentPane(contentPane);

        // --- Cabeçalho ---
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(EstilosGerais.AZUL_FUNDO);
        pnlHeader.setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("Agenda Médica");
        lblTitulo.setForeground(EstilosGerais.DOURADO);
        lblTitulo.setFont(EstilosGerais.FONTE_TITULO);
        pnlHeader.add(lblTitulo, BorderLayout.WEST);

        JButton btnVoltar = new JButton("⬅ Voltar ao Menu");
        btnVoltar.setBackground(EstilosGerais.AZUL_BOTAO);
        btnVoltar.setForeground(EstilosGerais.TEXTO_BRANCO);
        btnVoltar.setFont(EstilosGerais.FONTE_BOTAO);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Ação de voltar para o Dashboard
        btnVoltar.addActionListener(e -> {
            TelaMenuPrincipal menu = new TelaMenuPrincipal();
            menu.setVisible(true);
            dispose();
        });
        pnlHeader.add(btnVoltar, BorderLayout.EAST);
        contentPane.add(pnlHeader, BorderLayout.NORTH);

        // --- Área Central (Tabela de Consultas) ---
        // Colunas pensadas para a rotina da clínica
     // Colunas atualizadas para refletir o modelo Agendamento
        String[] colunas = {"ID", "Data/Hora", "Paciente", "Responsável", "Valor Total (R$)"};
        DefaultTableModel modeloTabela = new DefaultTableModel(null, colunas);
        tabelaAgenda = new JTable(modeloTabela);
        
        JScrollPane scrollPane = new JScrollPane(tabelaAgenda);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // --- Rodapé (Botões de Ação) ---
        JPanel pnlAcoes = new JPanel();
        pnlAcoes.setBackground(EstilosGerais.AZUL_FUNDO);
        pnlAcoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0)); 

        JButton btnNovo = criarBotaoAcao(" Novo Agendamento", EstilosGerais.DOURADO);
        
        btnNovo.addActionListener(e -> {
            TelaCadastroAgendamento telaNova = new TelaCadastroAgendamento();
            telaNova.setVisible(true);
            dispose();
        });
        
        
        JButton btnEditar = criarBotaoAcao(" Editar", EstilosGerais.AZUL_BOTAO);
        
        
     // Evento do botão Editar
        btnEditar.addActionListener(e -> {
            int linhaSelecionada = tabelaAgenda.getSelectedRow();
            
            if (linhaSelecionada == -1) {
                javax.swing.JOptionPane.showMessageDialog(this, "Por favor, selecione um agendamento na tabela para editar.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Pega os dados da linha para mandar para a tela de edição
            String id = tabelaAgenda.getValueAt(linhaSelecionada, 0).toString();
            String dataHora = tabelaAgenda.getValueAt(linhaSelecionada, 1).toString();
            String valor = tabelaAgenda.getValueAt(linhaSelecionada, 4).toString();

            TelaEditarAgendamento telaEdit = new TelaEditarAgendamento(id, dataHora, valor);
            telaEdit.setVisible(true);
            dispose();
        });
        JButton btnCancelar = criarBotaoAcao(" Cancelar Consulta", new java.awt.Color(180, 50, 50));
        
     // Evento do botão Cancelar Consulta
        btnCancelar.addActionListener(e -> {
            int linhaSelecionada = tabelaAgenda.getSelectedRow();
            
            if (linhaSelecionada == -1) {
                javax.swing.JOptionPane.showMessageDialog(this, "Por favor, selecione uma consulta na tabela para cancelar.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Pega o ID (coluna 0) e o nome do Paciente (coluna 2) para a mensagem
            String idAgendamento = tabelaAgenda.getValueAt(linhaSelecionada, 0).toString();
            String paciente = tabelaAgenda.getValueAt(linhaSelecionada, 2).toString();

            int confirmacao = javax.swing.JOptionPane.showConfirmDialog(
                this, 
                "Tem certeza que deseja cancelar a consulta de " + paciente + "?", 
                "Confirmar Cancelamento", 
                javax.swing.JOptionPane.YES_NO_OPTION
            );

            if (confirmacao == javax.swing.JOptionPane.YES_OPTION) {
                cancelarAgendamentoNaAPI(idAgendamento, modeloTabela);
            }
        });

        pnlAcoes.add(btnNovo);
        pnlAcoes.add(btnEditar);
        pnlAcoes.add(btnCancelar);
        contentPane.add(pnlAcoes, BorderLayout.SOUTH);

        // Preparando o terreno para a API no futuro
        carregarAgendaDaAPI(modeloTabela);
    }

    private JButton criarBotaoAcao(String texto, java.awt.Color corFundo) {
        JButton btn = new JButton(texto);
        btn.setBackground(corFundo);
        btn.setForeground(EstilosGerais.TEXTO_BRANCO);
        btn.setFont(EstilosGerais.FONTE_BOTAO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Método reservado para consumir o GET da API (Spring Boot) no futuro.
     * Por enquanto, apenas adicionamos uma linha estática para você visualizar o design.
     */
    /**
     * Faz a requisição HTTP GET para a rota /vendas e preenche a JTable
     */
    private void carregarAgendaDaAPI(DefaultTableModel modeloTabela) {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/vendas"))
                    .GET()
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                modeloTabela.setRowCount(0); // Limpa a tabela

                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(response.body());

                for (com.fasterxml.jackson.databind.JsonNode node : rootNode) {
                    
                    // Tratamento da Data (Vem como "2026-10-25T14:30:00" do LocalDateTime)
                    String dataCrua = node.hasNonNull("data") ? node.get("data").asText() : "";
                    String dataFormatada = dataCrua.replace("T", " ").substring(0, 16); // Fica "2026-10-25 14:30"

                    // Extraindo o nome do Paciente (Objeto aninhado)
                    String nomePaciente = "Não informado";
                    if (node.hasNonNull("paciente") && node.get("paciente").hasNonNull("nome")) {
                        nomePaciente = node.get("paciente").get("nome").asText();
                    }

                    // Extraindo o nome do Usuário Responsável (Objeto aninhado)
                    String nomeUsuario = "Não informado";
                    if (node.hasNonNull("usuarioResponsavel") && node.get("usuarioResponsavel").hasNonNull("username")) {
                        nomeUsuario = node.get("usuarioResponsavel").get("username").asText();
                    }
                    
                    // Extraindo o Valor Total
                    String valorTotal = node.hasNonNull("valorTotal") ? node.get("valorTotal").asText() : "0.00";

                    // Adiciona a linha na interface
                    modeloTabela.addRow(new Object[]{
                        node.hasNonNull("id") ? node.get("id").asText() : "",
                        dataFormatada,
                        nomePaciente,
                        nomeUsuario,
                        "R$ " + valorTotal
                    });
                }
            }
        } catch (Exception ex) {
            System.out.println("Erro ao carregar a agenda: " + ex.getMessage());
        }
    }
    
    /**
     * Envia um HTTP DELETE para a rota /vendas e atualiza a tabela
     */
    private void cancelarAgendamentoNaAPI(String id, DefaultTableModel modeloTabela) {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/vendas/" + id))
                    .DELETE()
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204 || response.statusCode() == 200) {
                javax.swing.JOptionPane.showMessageDialog(this, "Consulta cancelada com sucesso!");
                carregarAgendaDaAPI(modeloTabela); // Recarrega a tabela automaticamente
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Erro ao cancelar. O agendamento possui vínculos financeiros?", "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Erro de conexão com a API.", "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}