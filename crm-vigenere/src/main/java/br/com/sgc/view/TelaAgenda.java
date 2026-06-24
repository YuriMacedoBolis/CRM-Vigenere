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
import javax.swing.JOptionPane;

public class TelaAgenda extends JFrame {

    private JPanel contentPane;
    private JTable tabelaAgenda;

    public TelaAgenda() {
        setTitle("CRM Vigenere - Agenda Médica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(1144, 612);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(EstilosGerais.AZUL_FUNDO);
        contentPane.setBorder(new EmptyBorder(30, 40, 30, 40));
        contentPane.setLayout(new BorderLayout(0, 20)); 
        setContentPane(contentPane);

        // Cabeçalho e botão de retorno
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(EstilosGerais.AZUL_FUNDO);

        JLabel lblTitulo = new JLabel("Agenda Médica");
        lblTitulo.setForeground(EstilosGerais.DOURADO);
        lblTitulo.setFont(EstilosGerais.FONTE_TITULO);
        pnlHeader.add(lblTitulo, BorderLayout.WEST);

        JButton btnVoltar = criarBotaoAcao("⬅ Voltar ao Menu", EstilosGerais.AZUL_BOTAO);
        btnVoltar.addActionListener(e -> {
            new TelaMenuPrincipal().setVisible(true);
            dispose();
        });
        pnlHeader.add(btnVoltar, BorderLayout.EAST);
        contentPane.add(pnlHeader, BorderLayout.NORTH);

        // Tabela de listagem
        String[] colunas = {"ID", "Data/Hora", "Paciente", "Responsável", "Valor Total (R$)"};
        DefaultTableModel modeloTabela = new DefaultTableModel(null, colunas);
        tabelaAgenda = new JTable(modeloTabela);
        contentPane.add(new JScrollPane(tabelaAgenda), BorderLayout.CENTER);

        // Painel de ações (Rodapé)
        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0)); 
        pnlAcoes.setBackground(EstilosGerais.AZUL_FUNDO);

        JButton btnNovo = criarBotaoAcao(" Novo Agendamento", EstilosGerais.DOURADO);
        btnNovo.addActionListener(e -> {
            new TelaCadastroAgendamento().setVisible(true);
            dispose();
        });
        
        JButton btnEditar = criarBotaoAcao(" Editar", EstilosGerais.AZUL_BOTAO);
        btnEditar.addActionListener(e -> editarAgendamento());

        JButton btnCancelar = criarBotaoAcao(" Cancelar Consulta", new java.awt.Color(180, 50, 50));
        btnCancelar.addActionListener(e -> confirmarCancelamento(modeloTabela));

        pnlAcoes.add(btnNovo);
        pnlAcoes.add(btnEditar);
        pnlAcoes.add(btnCancelar);
        contentPane.add(pnlAcoes, BorderLayout.SOUTH);

        // Inicializa os dados da tabela
        carregarAgendaDaAPI(modeloTabela);
    }

    // Configuração padronizada de botões
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

    // Valida a seleção e redireciona para a tela de edição
    private void editarAgendamento() {
        int linha = tabelaAgenda.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um agendamento para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = tabelaAgenda.getValueAt(linha, 0).toString();
        String dataHora = tabelaAgenda.getValueAt(linha, 1).toString();
        String valor = tabelaAgenda.getValueAt(linha, 4).toString();

        new TelaEditarAgendamento(id, dataHora, valor).setVisible(true);
        dispose();
    }

    // Valida a seleção e exibe modal de confirmação antes de deletar
    private void confirmarCancelamento(DefaultTableModel modeloTabela) {
        int linha = tabelaAgenda.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma consulta para cancelar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = tabelaAgenda.getValueAt(linha, 0).toString();
        String paciente = tabelaAgenda.getValueAt(linha, 2).toString();

        int confirmacao = JOptionPane.showConfirmDialog(this, 
            "Deseja realmente cancelar a consulta de " + paciente + "?", 
            "Confirmar Cancelamento", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            cancelarAgendamentoNaAPI(id, modeloTabela);
        }
    }

    // Busca os agendamentos via GET e preenche a JTable
    private void carregarAgendaDaAPI(DefaultTableModel modeloTabela) {
        try {
            var client = java.net.http.HttpClient.newHttpClient();
            var request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/vendas"))
                    .GET()
                    .build();

            var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                modeloTabela.setRowCount(0);

                var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                var rootNode = mapper.readTree(response.body());

                for (var node : rootNode) {
                    // Limpeza do timestamp da data
                    String dataFormatada = node.hasNonNull("data") ? node.get("data").asText().replace("T", " ").substring(0, 16) : "";
                    
                    // Extração segura de campos aninhados (Paciente e Usuário)
                    String nomePaciente = (node.hasNonNull("paciente") && node.get("paciente").hasNonNull("nome")) 
                                          ? node.get("paciente").get("nome").asText() : "Não informado";
                    
                    String nomeUsuario = (node.hasNonNull("usuarioResponsavel") && node.get("usuarioResponsavel").hasNonNull("username")) 
                                         ? node.get("usuarioResponsavel").get("username").asText() : "Não informado";
                    
                    String valorTotal = node.hasNonNull("valorTotal") ? node.get("valorTotal").asText() : "0.00";

                    modeloTabela.addRow(new Object[]{
                        node.hasNonNull("id") ? node.get("id").asText() : "",
                        dataFormatada, nomePaciente, nomeUsuario, "R$ " + valorTotal
                    });
                }
            }
        } catch (Exception ex) {
            System.out.println("Erro ao carregar a agenda: " + ex.getMessage());
        }
    }
    
    // Dispara a requisição DELETE para a API e atualiza a interface
    private void cancelarAgendamentoNaAPI(String id, DefaultTableModel modeloTabela) {
        try {
            var client = java.net.http.HttpClient.newHttpClient();
            var request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/vendas/" + id))
                    .DELETE()
                    .build();

            var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204 || response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Consulta cancelada com sucesso!");
                carregarAgendaDaAPI(modeloTabela);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar. Existem vínculos financeiros?", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro de conexão com a API.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}