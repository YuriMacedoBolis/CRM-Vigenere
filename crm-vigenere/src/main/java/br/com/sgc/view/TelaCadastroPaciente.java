package br.com.sgc.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;

public class TelaCadastroPaciente extends JFrame {

    private JPanel contentPane;
    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtTelefone;
    private JTextField txtEmail;

    public TelaCadastroPaciente() {
        setTitle("CRM Vigenere - Novo Paciente");
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
        JLabel lblTitulo = new JLabel("Cadastro de Novo Paciente");
        lblTitulo.setForeground(EstilosGerais.DOURADO);
        lblTitulo.setFont(EstilosGerais.FONTE_TITULO);
        lblTitulo.setHorizontalAlignment(JLabel.CENTER);
        contentPane.add(lblTitulo, BorderLayout.NORTH);

        // --- Formulário (Card Central) ---
        JPanel pnlForm = new JPanel();
        pnlForm.setBackground(EstilosGerais.TEXTO_BRANCO);
        pnlForm.setLayout(null); 
        contentPane.add(pnlForm, BorderLayout.CENTER);

        int startX = 200;
        int startY = 40;
        int width = 500;
        int height = 30;
        int gap = 70;

        // Campo Nome
        JLabel lblNome = criarLabel("Nome Completo:", startX, startY);
        pnlForm.add(lblNome);
        txtNome = new JTextField();
        txtNome.setBounds(startX, startY + 25, width, height);
        pnlForm.add(txtNome);

        // Campo CPF
        JLabel lblCpf = criarLabel("CPF (Somente números):", startX, startY + gap);
        pnlForm.add(lblCpf);
        txtCpf = new JTextField();
        txtCpf.setBounds(startX, startY + gap + 25, width, height);
        pnlForm.add(txtCpf);

        // Campo Telefone
        JLabel lblTelefone = criarLabel("Telefone:", startX, startY + (gap * 2));
        pnlForm.add(lblTelefone);
        txtTelefone = new JTextField();
        txtTelefone.setBounds(startX, startY + (gap * 2) + 25, width, height);
        pnlForm.add(txtTelefone);

        // Campo Email
        JLabel lblEmail = criarLabel("E-mail:", startX, startY + (gap * 3));
        pnlForm.add(lblEmail);
        txtEmail = new JTextField();
        txtEmail.setBounds(startX, startY + (gap * 3) + 25, width, height);
        pnlForm.add(txtEmail);

        // --- Rodapé (Botões) ---
        JPanel pnlBotoes = new JPanel();
        pnlBotoes.setBackground(EstilosGerais.AZUL_FUNDO);
        
        JButton btnCancelar = new JButton("Cancelar");
        estilizarBotao(btnCancelar, new java.awt.Color(180, 50, 50));
        btnCancelar.addActionListener(e -> voltarParaTabela());
        
        JButton btnSalvar = new JButton("Salvar Paciente");
        estilizarBotao(btnSalvar, EstilosGerais.DOURADO);
        btnSalvar.addActionListener(e -> salvarPacienteNaAPI());

        pnlBotoes.add(btnCancelar);
        pnlBotoes.add(btnSalvar);
        contentPane.add(pnlBotoes, BorderLayout.SOUTH);
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

    private void voltarParaTabela() {
        TelaPacientes tela = new TelaPacientes();
        tela.setVisible(true);
        dispose();
    }

    // AQUI ESTÁ A CHAMADA DA API PARA ADICIONAR NO BANCO
    private void salvarPacienteNaAPI() {
        try {
            String nome = txtNome.getText();
            String cpf = txtCpf.getText();
            String telefone = txtTelefone.getText();
            String email = txtEmail.getText();

            if (nome.isEmpty() || cpf.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e CPF são obrigatórios!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Monta o pacote JSON com os dados preenchidos
            String jsonRequestBody = String.format(
                "{\"nome\":\"%s\", \"cpf\":\"%s\", \"telefone\":\"%s\", \"email\":\"%s\", \"endereco\":\"\"}",
                nome, cpf, telefone, email
            );

            // Dispara para o backend
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/clientes"))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) { 
                JOptionPane.showMessageDialog(this, "Paciente cadastrado com sucesso!");
                voltarParaTabela(); 
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar. Verifique os dados ou se o CPF já existe.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro de conexão com a API.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}