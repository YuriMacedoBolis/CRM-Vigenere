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

public class TelaPacientes extends JFrame {

    private JPanel contentPane;
    private JTable tabelaPacientes;

    public TelaPacientes() {
        setTitle("CRM Vigenere - Gestão de Pacientes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(1144, 612); 
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

        JLabel lblTitulo = new JLabel("Gestão de Pacientes");
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

        // --- Área Central (Tabela) ---
        String[] colunas = {"ID", "Nome", "CPF", "Telefone", "Email"};
        DefaultTableModel modeloTabela = new DefaultTableModel(null, colunas);
        tabelaPacientes = new JTable(modeloTabela);
        
        JScrollPane scrollPane = new JScrollPane(tabelaPacientes);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // --- Rodapé (Botões de Ação) ---
        JPanel pnlAcoes = new JPanel();
        pnlAcoes.setBackground(EstilosGerais.AZUL_FUNDO);
        pnlAcoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0)); 

        JButton btnNovo = criarBotaoAcao(" Novo Paciente", EstilosGerais.DOURADO);
        
     // Evento que abre a tela de formulário
        btnNovo.addActionListener(e -> {
            TelaCadastroPaciente telaCadastro = new TelaCadastroPaciente();
            telaCadastro.setVisible(true);
            dispose(); // Fecha a tabela temporariamente
        });
        
        
        JButton btnEditar = criarBotaoAcao(" Editar", EstilosGerais.AZUL_BOTAO);
        
     // Evento do botão Editar
        btnEditar.addActionListener(e -> {
            int linhaSelecionada = tabelaPacientes.getSelectedRow();
            
            if (linhaSelecionada == -1) {
                javax.swing.JOptionPane.showMessageDialog(this, "Por favor, selecione um paciente na tabela para editar.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Captura todos os dados da linha selecionada
            String id = tabelaPacientes.getValueAt(linhaSelecionada, 0).toString();
            String nome = tabelaPacientes.getValueAt(linhaSelecionada, 1).toString();
            String cpf = tabelaPacientes.getValueAt(linhaSelecionada, 2).toString();
            String telefone = tabelaPacientes.getValueAt(linhaSelecionada, 3).toString();
            String email = tabelaPacientes.getValueAt(linhaSelecionada, 4).toString();

            // Abre a nova tela passando esses dados
            TelaEditarPaciente telaEdit = new TelaEditarPaciente(id, nome, cpf, telefone, email);
            telaEdit.setVisible(true);
            dispose(); // Fecha a tabela
        });
        JButton btnExcluir = criarBotaoAcao(" Excluir", new java.awt.Color(180, 50, 50));
        
     // Evento do botão Excluir
        btnExcluir.addActionListener(e -> {
            // 1. Descobre qual linha o usuário clicou
            int linhaSelecionada = tabelaPacientes.getSelectedRow();
            
            // Se for -1, significa que ele não clicou em nada
            if (linhaSelecionada == -1) {
                javax.swing.JOptionPane.showMessageDialog(this, "Por favor, selecione um paciente na tabela para excluir.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Pega o ID do paciente (que está invisível na coluna 0 da tabela)
            String idPaciente = tabelaPacientes.getValueAt(linhaSelecionada, 0).toString();
            String nomePaciente = tabelaPacientes.getValueAt(linhaSelecionada, 1).toString();

            // 3. Pede confirmação de segurança (Boa prática de UX)
            int confirmacao = javax.swing.JOptionPane.showConfirmDialog(
                this, 
                "Tem certeza que deseja excluir o paciente " + nomePaciente + "?", 
                "Confirmar Exclusão", 
                javax.swing.JOptionPane.YES_NO_OPTION
            );

            // 4. Se ele disser "Sim", disparamos para a API
            if (confirmacao == javax.swing.JOptionPane.YES_OPTION) {
                excluirPacienteDaAPI(idPaciente, modeloTabela);
            }
        });

        pnlAcoes.add(btnNovo);
        pnlAcoes.add(btnEditar);
        pnlAcoes.add(btnExcluir);
        contentPane.add(pnlAcoes, BorderLayout.SOUTH);

        // --- CHAMADA DA API AO ABRIR A TELA ---
        carregarPacientesDaAPI(modeloTabela);
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
     * Faz a requisição HTTP GET para a rota /clientes e preenche a JTable
     */
    private void carregarPacientesDaAPI(DefaultTableModel modeloTabela) {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    // ATENÇÃO: Rota atualizada para a do seu Controller!
                    .uri(java.net.URI.create("http://localhost:8080/clientes"))
                    .GET()
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                modeloTabela.setRowCount(0);

                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(response.body());

                for (com.fasterxml.jackson.databind.JsonNode node : rootNode) {
                    modeloTabela.addRow(new Object[]{
                        node.hasNonNull("id") ? node.get("id").asText() : "",
                        node.hasNonNull("nome") ? node.get("nome").asText() : "",
                        node.hasNonNull("cpf") ? node.get("cpf").asText() : "",
                        node.hasNonNull("telefone") ? node.get("telefone").asText() : "",
                        node.hasNonNull("email") ? node.get("email").asText() : ""
                    });
                }
            }
        } catch (Exception ex) {
            System.out.println("Erro ao carregar pacientes: " + ex.getMessage());
        }
    }
    
    /**
     * Envia um HTTP DELETE para a API e atualiza a tabela
     */
    private void excluirPacienteDaAPI(String id, DefaultTableModel modeloTabela) {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/clientes/" + id))
                    .DELETE() // Verbo correto para exclusão!
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            // 204 (No Content) é o padrão do Spring para DELETE com sucesso
            if (response.statusCode() == 204 || response.statusCode() == 200) {
                javax.swing.JOptionPane.showMessageDialog(this, "Paciente excluído com sucesso!");
                // Recarrega a tabela automaticamente para o paciente sumir da tela
                carregarPacientesDaAPI(modeloTabela); 
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Erro ao excluir. O paciente possui vínculos?", "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Erro de conexão com a API.", "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}