package br.univates.universo.gui;

import br.univates.universo.core.Cliente;
import br.univates.universo.data.GerenciadorAlugueis;
import br.univates.universo.data.GerenciadorClientes;
import br.univates.universo.util.CpfDocumentFilter;
import br.univates.universo.util.CpfValidator;
import br.univates.universo.util.NomeDocumentFilter;
import br.univates.universo.util.TelefoneDocumentFilter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.PlainDocument;

public final class PainelGerenciamentoClientes extends JPanel {
    private JTextField txtCpf, txtNome, txtTelefone, txtEmail;
    private JButton btnSalvar, btnLimpar, btnRemover;
    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;

    public PainelGerenciamentoClientes() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        setupListeners();
        carregarClientesNaTabela();
    }

    private void initComponents() {
        // Formulário
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtCpf = createCpfField();
        painelFormulario.add(txtCpf, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        txtNome = createNomeField();
        painelFormulario.add(txtNome, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtTelefone = createTelefoneField();
        painelFormulario.add(txtTelefone, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        txtEmail = new JTextField();
        painelFormulario.add(txtEmail, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnSalvar = new JButton("Salvar / Atualizar");
        btnLimpar = new JButton("Limpar Campos");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnLimpar);

        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.add(painelFormulario, BorderLayout.CENTER);
        painelSuperior.add(painelBotoes, BorderLayout.SOUTH);

        // Tabela
        String[] colunas = { "CPF", "Nome Completo", "Telefone", "Email" };
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabelaClientes = new JTable(modeloTabela);
        tabelaClientes.setRowSorter(new TableRowSorter<>(modeloTabela));
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Clientes Cadastrados"));

        // Painel Inferior
        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRemover = new JButton("Remover Selecionado");
        painelInferior.add(btnRemover);

        add(painelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(painelInferior, BorderLayout.SOUTH);
    }

    private JTextField createCpfField() {
        JTextField field = new JTextField(15);
        ((PlainDocument) field.getDocument()).setDocumentFilter(new CpfDocumentFilter());
        return field;
    }

    private JTextField createNomeField() {
        JTextField field = new JTextField(30);
        ((PlainDocument) field.getDocument()).setDocumentFilter(new NomeDocumentFilter());
        return field;
    }

    private JTextField createTelefoneField() {
        JTextField field = new JTextField(15);
        ((PlainDocument) field.getDocument()).setDocumentFilter(new TelefoneDocumentFilter());
        return field;
    }

    private void setupListeners() {
        btnSalvar.addActionListener(e -> salvarCliente());
        btnLimpar.addActionListener(e -> limparCampos());
        btnRemover.addActionListener(e -> removerCliente());

        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaClientes.getSelectedRow() != -1) {
                preencherFormularioComLinhaSelecionada();
            }
        });
    }

    private void salvarCliente() {
        String cpf = txtCpf.getText();
        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText();
        String email = txtEmail.getText().trim();

        // --- NOVO: Validações explícitas ---
        if (!CpfValidator.isValid(cpf)) {
            JOptionPane.showMessageDialog(this, "O CPF informado é inválido. Verifique os dígitos.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O campo 'Nome' é obrigatório.", "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (telefone.replaceAll("[^0-9]", "").length() < 10) {
            JOptionPane.showMessageDialog(this, "O número de telefone parece incompleto.", "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "O formato do e-mail é inválido (ex: nome@dominio.com).",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        String telefoneLimpo = telefone.replaceAll("[^0-9]", "");

        List<Cliente> clientes = GerenciadorClientes.carregarClientes();
        Optional<Cliente> existente = clientes.stream().filter(c -> c.getCpf().equals(cpfLimpo)).findFirst();

        if (existente.isPresent()) {
            Cliente c = existente.get();
            c.setNome(nome);
            c.setTelefone(telefoneLimpo);
            c.setEmail(email);
            JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
        } else {
            clientes.add(new Cliente(cpfLimpo, nome, telefoneLimpo, email));
            JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
        }
        GerenciadorClientes.salvarClientes(clientes);
        carregarClientesNaTabela();
        limparCampos();
    }

    private void removerCliente() {
        int linha = tabelaClientes.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para remover.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cpf = ((String) modeloTabela.getValueAt(tabelaClientes.convertRowIndexToModel(linha), 0))
                .replaceAll("[^0-9]", "");

        boolean temAluguelAtivo = GerenciadorAlugueis.carregarAlugueis().stream()
                .anyMatch(a -> a.getCpfCliente().equals(cpf) && "Ativo".equals(a.getStatusAluguel()));

        if (temAluguelAtivo) {
            JOptionPane.showMessageDialog(this, "Cliente não pode ser removido pois possui um aluguel ativo.",
                    "Operação Negada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover o cliente?",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<Cliente> clientes = GerenciadorClientes.carregarClientes();
            clientes.removeIf(c -> c.getCpf().equals(cpf));
            GerenciadorClientes.salvarClientes(clientes);
            carregarClientesNaTabela();
            limparCampos();
        }
    }

    public void carregarClientesNaTabela() {
        modeloTabela.setRowCount(0);
        GerenciadorClientes.carregarClientes().forEach(c -> modeloTabela.addRow(new Object[] {
                formatCpf(c.getCpf()), c.getNome(), formatTelefone(c.getTelefone()), c.getEmail()
        }));
    }

    private void preencherFormularioComLinhaSelecionada() {
        int linha = tabelaClientes.getSelectedRow();
        if (linha == -1)
            return;

        int modelRow = tabelaClientes.convertRowIndexToModel(linha);
        txtCpf.setText((String) modeloTabela.getValueAt(modelRow, 0));
        txtNome.setText((String) modeloTabela.getValueAt(modelRow, 1));
        txtTelefone.setText((String) modeloTabela.getValueAt(modelRow, 2));
        txtEmail.setText((String) modeloTabela.getValueAt(modelRow, 3));
        txtCpf.setEditable(false);
    }

    private void limparCampos() {
        txtCpf.setText("");
        txtCpf.setEditable(true);
        txtNome.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        tabelaClientes.clearSelection();
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty())
            return false;
        return Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", email);
    }

    private String formatCpf(String cpf) {
        if (cpf == null || cpf.length() != 11)
            return cpf;
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    private String formatTelefone(String telefone) {
        if (telefone == null)
            return telefone;
        if (telefone.length() == 11) {
            return telefone.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }
        if (telefone.length() == 10) {
            return telefone.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
        return telefone;
    }
}
