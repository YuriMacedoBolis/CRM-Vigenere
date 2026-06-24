package br.com.sgc.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TelaLogin extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtNome;
	private JPasswordField txtSenha;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TelaLogin frame = new TelaLogin();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public TelaLogin() {
		setResizable(false);
		setTitle("CRM - VIGENERE");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1144, 612);
        setLocationRelativeTo(null); // Centraliza a janela no monitor
		
		contentPane = new JPanel();
		contentPane.setBackground(EstilosGerais.AZUL_FUNDO);
		contentPane.setBorder(new EmptyBorder(60, 100, 60, 100));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
        panel.setBackground(EstilosGerais.TEXTO_BRANCO); // Fundo branco para o Card
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel lblNomeCRM = new JLabel("CRM - VIGENERE");
		lblNomeCRM.setFont(EstilosGerais.FONTE_TITULO);
		lblNomeCRM.setBounds(368, 40, 208, 28);
		panel.add(lblNomeCRM);
		
		JLabel lblNome = new JLabel("Usuário");
		lblNome.setFont(EstilosGerais.FONTE_LABEL);
		lblNome.setBounds(200, 150, 68, 22);
		panel.add(lblNome);
		
		JLabel lblNewLabel = new JLabel("Senha");
		lblNewLabel.setFont(EstilosGerais.FONTE_LABEL);
		lblNewLabel.setBounds(200, 240, 68, 28);
		panel.add(lblNewLabel);
		
		txtNome = new JTextField();
		txtNome.setBounds(200, 183, 510, 28);
		panel.add(txtNome);
		txtNome.setColumns(10);
		
		txtSenha = new JPasswordField();
		txtSenha.setBounds(200, 279, 510, 28);
		panel.add(txtSenha);
		
		JButton btnEntrar = new JButton("Entrar");
		btnEntrar.setBackground(EstilosGerais.AZUL_BOTAO);
        btnEntrar.setForeground(EstilosGerais.TEXTO_BRANCO);
		btnEntrar.setFont(EstilosGerais.FONTE_BOTAO);
        btnEntrar.setBorderPainted(false); // Estilo moderno
        btnEntrar.setFocusPainted(false);
        
		btnEntrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String usuario = txtNome.getText();
				String senha = new String(txtSenha.getPassword());

				try {
				    String jsonRequestBody = "{\"username\":\"" + usuario + "\", \"senha\":\"" + senha + "\"}";
				    java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
				    java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
				            .uri(java.net.URI.create("http://localhost:8080/auth/login"))
				            .header("Content-Type", "application/json")
				            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonRequestBody))
				            .build();

				    java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

				    if (response.statusCode() == 200) {
				        TelaMenuPrincipal menu = new TelaMenuPrincipal();
				        menu.setVisible(true);
				        dispose(); 
				    } else {
				        javax.swing.JOptionPane.showMessageDialog(null, "Usuário ou senha incorretos!", "Falha na Autenticação", javax.swing.JOptionPane.ERROR_MESSAGE);
				    }
				} catch (Exception ex) {
				    javax.swing.JOptionPane.showMessageDialog(null, "Servidor offline. Ligue a API primeiro!", "Erro de Conexão", javax.swing.JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnEntrar.setBounds(398, 360, 152, 49);
		panel.add(btnEntrar);
	}
}