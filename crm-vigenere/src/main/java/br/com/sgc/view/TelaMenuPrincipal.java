package br.com.sgc.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class TelaMenuPrincipal extends JFrame {

    private JPanel contentPane;

    public TelaMenuPrincipal() {
        // 1. Configurações Básicas da Janela
        setTitle("CRM Vigenere - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // TRAVANDO O TAMANHO E IGUALANDO À TELA DE LOGIN
        setResizable(false); 
        setSize(1144, 612); 
        setLocationRelativeTo(null); // Centraliza no meio do seu monitor

        // 2. Fundo Principal (O Esqueleto BorderLayout)
        contentPane = new JPanel();
        contentPane.setBackground(EstilosGerais.AZUL_FUNDO);
        contentPane.setBorder(new EmptyBorder(40, 50, 50, 50)); 
        contentPane.setLayout(new BorderLayout(0, 40)); 
        setContentPane(contentPane);

        // 3. Header (O Cabeçalho no Topo)
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(EstilosGerais.AZUL_FUNDO);
        pnlHeader.setLayout(new BorderLayout(0, 10)); 

        JLabel lblTitulo = new JLabel("Painel de Controle");
        lblTitulo.setForeground(EstilosGerais.DOURADO); 
        lblTitulo.setFont(EstilosGerais.FONTE_TITULO);

        JLabel lblSubtitulo = new JLabel("Bem-vindo(a) ao CRM Vigenere! Escolha um módulo abaixo para iniciar.");
        lblSubtitulo.setForeground(EstilosGerais.TEXTO_BRANCO);
        lblSubtitulo.setFont(EstilosGerais.FONTE_LABEL);

        pnlHeader.add(lblTitulo, BorderLayout.NORTH);
        pnlHeader.add(lblSubtitulo, BorderLayout.CENTER);
        contentPane.add(pnlHeader, BorderLayout.NORTH);

        // 4. Área de Navegação (Os Cards em Grid no Centro)
        JPanel pnlCards = new JPanel();
        pnlCards.setBackground(EstilosGerais.AZUL_FUNDO);
        pnlCards.setLayout(new GridLayout(2, 2, 30, 30));

        // Injetando os Cards no Grid
        JButton btnPacientes = criarBotaoCard("Gestão de Pacientes");
        JButton btnAgenda = criarBotaoCard("Agenda Médica");
        JButton btnFinanceiro = criarBotaoCard("Módulo Financeiro");
        JButton btnSair = criarBotaoCard("Sair do Sistema");
        
        // Mantemos apenas o botão Sair vermelho para indicar ação de saída
        btnSair.setBackground(new java.awt.Color(180, 50, 50)); 

        pnlCards.add(btnPacientes);
        pnlCards.add(btnAgenda);
        pnlCards.add(btnFinanceiro);
        pnlCards.add(btnSair);

        contentPane.add(pnlCards, BorderLayout.CENTER);
        
        // 5. Interação de UX: O botão "Sair" faz o logout, fecha o dashboard e reabre o Login
        btnSair.addActionListener(e -> {
            TelaLogin telaLogin = new TelaLogin();
            telaLogin.setVisible(true);
            dispose(); 
        });
        
     // Interação de UX: Abrir a tela de pacientes
        btnPacientes.addActionListener(e -> {
            TelaPacientes telaPacientes = new TelaPacientes();
            telaPacientes.setVisible(true);
            dispose(); // Fecha o menu
        });
        
        
     // Interação de UX: Abrir a tela de Agenda
        btnAgenda.addActionListener(e -> {
            TelaAgenda telaAgenda = new TelaAgenda();
            telaAgenda.setVisible(true);
            dispose(); // Fecha o menu
        });
        
        btnFinanceiro.addActionListener(e -> {
            TelaFinanceiro telaFinanceiro = new TelaFinanceiro();
            telaFinanceiro.setVisible(true);
            dispose();
        });
        
        
    }

    /**
     * Componente Reutilizável de Estilo
     */
    private JButton criarBotaoCard(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(EstilosGerais.AZUL_BOTAO);
        btn.setForeground(EstilosGerais.TEXTO_BRANCO);
        btn.setFont(EstilosGerais.FONTE_TITULO); 
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        return btn;
    }
}