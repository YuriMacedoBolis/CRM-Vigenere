package br.com.sgc.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class TelaFinanceiro extends JFrame {

    private JPanel contentPane;
    private JLabel lblFaturamentoTotal;
    private JLabel lblTotalConsultas;

    public TelaFinanceiro() {
        setTitle("CRM Vigenere - Relatório Financeiro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(1144, 612);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(EstilosGerais.AZUL_FUNDO);
        contentPane.setBorder(new EmptyBorder(40, 60, 40, 60));
        contentPane.setLayout(new BorderLayout(0, 40));
        setContentPane(contentPane);

        // --- Cabeçalho ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(EstilosGerais.AZUL_FUNDO);

        JLabel lblTitulo = new JLabel("Dashboard Financeiro");
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
        btnVoltar.addActionListener(e -> {
            TelaMenuPrincipal menu = new TelaMenuPrincipal();
            menu.setVisible(true);
            dispose();
        });
        pnlHeader.add(btnVoltar, BorderLayout.EAST);
        contentPane.add(pnlHeader, BorderLayout.NORTH);

        // --- Área Central (Cards de Resumo) ---
        JPanel pnlCards = new JPanel(new GridLayout(1, 2, 40, 0));
        pnlCards.setBackground(EstilosGerais.AZUL_FUNDO);
        
        // Card 1: Faturamento Total
        JPanel cardFaturamento = criarCardResumo("Faturamento Total Bruto");
        lblFaturamentoTotal = new JLabel("R$ 0,00");
        lblFaturamentoTotal.setFont(new Font("Arial", Font.BOLD, 48));
        lblFaturamentoTotal.setForeground(new java.awt.Color(40, 167, 69)); // Verde financeiro
        lblFaturamentoTotal.setHorizontalAlignment(JLabel.CENTER);
        cardFaturamento.add(lblFaturamentoTotal, BorderLayout.CENTER);
        
        // Card 2: Total de Consultas
        JPanel cardConsultas = criarCardResumo("Consultas Realizadas");
        lblTotalConsultas = new JLabel("0");
        lblTotalConsultas.setFont(new Font("Arial", Font.BOLD, 48));
        lblTotalConsultas.setForeground(EstilosGerais.AZUL_BOTAO);
        lblTotalConsultas.setHorizontalAlignment(JLabel.CENTER);
        cardConsultas.add(lblTotalConsultas, BorderLayout.CENTER);

        pnlCards.add(cardFaturamento);
        pnlCards.add(cardConsultas);
        contentPane.add(pnlCards, BorderLayout.CENTER);

        // --- Rodapé ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlFooter.setBackground(EstilosGerais.AZUL_FUNDO);
        JLabel lblAviso = new JLabel("Os dados são calculados em tempo real com base no histórico de agendamentos.");
        lblAviso.setForeground(EstilosGerais.TEXTO_BRANCO);
        lblAviso.setFont(EstilosGerais.FONTE_LABEL);
        pnlFooter.add(lblAviso);
        contentPane.add(pnlFooter, BorderLayout.SOUTH);

        // Dispara o cálculo assim que a tela abre
        calcularFinanceiroDaAPI();
    }

    private JPanel criarCardResumo(String titulo) {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(EstilosGerais.TEXTO_BRANCO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EstilosGerais.DOURADO, 2),
            new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(EstilosGerais.AZUL_FUNDO);
        lblTitulo.setHorizontalAlignment(JLabel.CENTER);
        panel.add(lblTitulo, BorderLayout.NORTH);

        return panel;
    }

    // --- O MOTOR MATEMÁTICO QUE LÊ A API ---
    private void calcularFinanceiroDaAPI() {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/vendas"))
                    .GET().build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(response.body());

                double somaTotal = 0.0;
                int contagemConsultas = 0;

                // Percorre todas as vendas e soma os valores
                for (com.fasterxml.jackson.databind.JsonNode node : rootNode) {
                    contagemConsultas++;
                    if (node.hasNonNull("valorTotal")) {
                        somaTotal += node.get("valorTotal").asDouble();
                    }
                }

                // Atualiza a interface com os números reais
                lblTotalConsultas.setText(String.valueOf(contagemConsultas));
                lblFaturamentoTotal.setText(String.format("R$ %.2f", somaTotal));
            }
        } catch (Exception ex) {
            System.out.println("Erro ao calcular financeiro: " + ex.getMessage());
        }
    }
}