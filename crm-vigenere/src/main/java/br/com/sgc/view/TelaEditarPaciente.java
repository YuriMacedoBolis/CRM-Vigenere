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

public class TelaEditarPaciente extends JFrame {

    private JPanel contentPane;
    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private String idPacienteSelecionado; // Guarda o ID para sabermos quem atualizar

    // O Construtor agora exige os dados do paciente para preencher a tela!
    public TelaEditarPaciente(String id, String nome, String cpf, String telefone, String email) {
        this.idPacienteSelecionado = id;

        setTitle("CRM Vigenere - Editar Paciente");
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
        JLabel lblTitulo = new JLabel("Editar Dados do Paciente");
        lblTitulo.setForeground(EstilosGerais.DOURADO);
        lblTitulo.setFont(EstilosGerais.FONTE_TITULO);
        lblTitulo.setHorizontalAlignment(JLabel.CENTER);
        contentPane.add(lblTitulo, BorderLayout.NORTH);

        // --- Formulário ---
        JPanel pnlForm = new JPanel();
        pnlForm.setBackground(EstilosGerais.TEXTO_BRANCO);
        pnlForm.setLayout(null); 
        contentPane.add(pnlForm, BorderLayout.CENTER);

        int startX = 200, startY = 40, width = 500, height = 30, gap = 70;

        // Monta os campos e JÁ PREENCHE com os dados que vieram da tabela
        JLabel lblNome = criarLabel("Nome Completo:", startX, startY);
        pnlForm.add(lblNome);
        txtNome = new JTextField(nome);
        txtNome.setBounds(startX, startY + 25, width, height);
        pnlForm.add(txtNome);

        JLabel lblCpf = criarLabel("CPF (Somente números):", startX, startY + gap);
        pnlForm.add(lblCpf);
        txtCpf = new JTextField(cpf);
        txtCpf.setBounds(startX, startY + gap + 25, width, height);
        // Opcional: Impedir que o CPF seja alterado, se for uma regra de negócio sua
        // txtCpf.setEditable(false); 
        pnlForm.add(txtCpf);

        JLabel lblTelefone = criarLabel("Telefone:", startX, startY + (gap * 2));
        pnlForm.add(lblTelefone);
        txtTelefone = new JTextField(telefone);
        txtTelefone.setBounds(startX, startY + (gap * 2) + 25, width, height);
        pnlForm.add(txtTelefone);

        JLabel lblEmail = criarLabel("E-mail:", startX, startY + (gap * 3));
        pnlForm.add(lblEmail);
        txtEmail = new JTextField(email);
        txtEmail.setBounds(startX, startY + (gap * 3) + 25, width, height);
        pnlForm.add(txtEmail);

        // --- Rodapé ---
        JPanel pnlBotoes = new JPanel();
        pnlBotoes.setBackground(EstilosGerais.AZUL_FUNDO);
        
        JButton btnCancelar = new JButton("Cancelar");
        estilizarBotao(btnCancelar, new java.awt.Color(180, 50, 50));
        btnCancelar.addActionListener(e -> voltarParaTabela());
        
        JButton btnSalvar = new JButton("Salvar Alterações");
        estilizarBotao(btnSalvar, EstilosGerais.DOURADO);
        btnSalvar.addActionListener(e -> atualizarPacienteNaAPI());

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

    // A MÁGICA DA ATUALIZAÇÃO (PUT)
    private void atualizarPacienteNaAPI() {
        try {
            String nomeAtualizado = txtNome.getText();
            String cpfAtualizado = txtCpf.getText();
            String telefoneAtualizado = txtTelefone.getText();
            String emailAtualizado = txtEmail.getText();

            if (nomeAtualizado.isEmpty() || cpfAtualizado.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e CPF não podem ficar vazios!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String jsonRequestBody = String.format(
                "{\"nome\":\"%s\", \"cpf\":\"%s\", \"telefone\":\"%s\", \"email\":\"%s\", \"endereco\":\"\"}",
                nomeAtualizado, cpfAtualizado, telefoneAtualizado, emailAtualizado
            );

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    // ATENÇÃO: Dispara para /clientes/{id} com o verbo PUT
                    .uri(java.net.URI.create("http://localhost:8080/clientes/" + idPacienteSelecionado))
                    .header("Content-Type", "application/json")
                    .PUT(java.net.http.HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) { 
                JOptionPane.showMessageDialog(this, "Dados atualizados com sucesso!");
                voltarParaTabela(); 
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar. Verifique os dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro de conexão com a API.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}