package br.univates.universo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.PlainDocument;

import br.univates.universo.core.Cliente;
import br.univates.universo.data.GerenciadorAlugueis;
import br.univates.universo.data.GerenciadorClientes;
import br.univates.universo.util.CpfDocumentFilter;
import br.univates.universo.util.CpfValidator;
import br.univates.universo.util.TelefoneDocumentFilter;
import br.univates.universo.util.UIDesigner;
import br.univates.universo.util.WrapLayout;

/**
 * Painel para gerenciamento completo dos clientes da locadora.
 * Apresenta uma interface moderna com visualização em cards e um painel
 * de detalhes contextual para adição e edição.
 *
 * @version 3.0
 */
public class PainelGerenciamentoClientes extends JPanel {
    private List<Cliente> listaCompletaClientes;
    private final JPanel painelCards;
    private final JPanel painelDetalhes;
    private Cliente clienteSelecionado;

    // Componentes do painel de detalhes
    private JLabel lblTituloDetalhes;
    private JTextField txtCpf, txtNome, txtTelefone, txtEmail;
    private JButton btnSalvar, btnRemover;

    public PainelGerenciamentoClientes() {
        super(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 30, 20, 30));
        setBackground(UIDesigner.COLOR_BACKGROUND);

        add(createPainelAcoes(), BorderLayout.NORTH);

        painelCards = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        painelCards.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(painelCards);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIDesigner.COLOR_BACKGROUND);
        add(scrollPane, BorderLayout.CENTER);

        painelDetalhes = createPainelDetalhes();
        add(painelDetalhes, BorderLayout.EAST);
        painelDetalhes.setVisible(false);
    }

    /**
     * Carrega a lista de clientes do arquivo e atualiza a exibição.
     */
    public void carregarClientes() {
        this.listaCompletaClientes = GerenciadorClientes.carregarClientes();
        filtrarClientes("");
    }

    /**
     * Filtra e exibe os clientes com base em um termo de busca.
     * 
     * @param termo O texto a ser buscado no nome ou CPF do cliente.
     */
    private void filtrarClientes(String termo) {
        painelCards.removeAll();
        termo = termo.toLowerCase();
        for (Cliente cliente : listaCompletaClientes) {
            if (cliente.getNome().toLowerCase().contains(termo) || cliente.getCpf().contains(termo)) {
                painelCards.add(createClienteCard(cliente));
            }
        }
        painelCards.revalidate();
        painelCards.repaint();
    }

    /**
     * Cria o painel superior contendo o título e os botões de ação principal.
     */
    private JPanel createPainelAcoes() {
        JPanel painel = new JPanel(new BorderLayout(20, 20));
        painel.setOpaque(false);

        JLabel titulo = new JLabel("Clientes");
        titulo.setFont(UIDesigner.FONT_TITLE);
        titulo.setForeground(UIDesigner.COLOR_FOREGROUND);
        painel.add(titulo, BorderLayout.WEST);

        JPanel acoesCentro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acoesCentro.setOpaque(false);
        JTextField txtBusca = new JTextField(25);
        txtBusca.putClientProperty("JTextField.placeholderText", "Buscar por nome ou CPF...");
        txtBusca.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarClientes(txtBusca.getText());
            }
        });
        acoesCentro.add(txtBusca);
        painel.add(acoesCentro, BorderLayout.CENTER);

        JButton btnAdicionar = UIDesigner.createPrimaryButton("Adicionar Cliente", "icons/add.svg");
        btnAdicionar.addActionListener(e -> mostrarPainelDetalhes(null));
        painel.add(btnAdicionar, BorderLayout.EAST);

        return painel;
    }

    /**
     * Cria um card visual para representar um único cliente.
     * 
     * @param cliente O objeto Cliente a ser exibido.
     * @return Um JPanel estilizado como um card.
     */
    private JPanel createClienteCard(Cliente cliente) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(UIDesigner.BORDER_CARD);
        card.setBackground(UIDesigner.COLOR_CARD_BACKGROUND);
        card.setPreferredSize(new Dimension(320, 130));

        JLabel lblNome = new JLabel(cliente.getNome());
        lblNome.setFont(UIDesigner.FONT_SUBTITLE);
        lblNome.setForeground(UIDesigner.COLOR_FOREGROUND);

        JLabel lblCpf = new JLabel("CPF: " + formatCpf(cliente.getCpf()));
        lblCpf.setFont(UIDesigner.FONT_BODY);
        lblCpf.setForeground(UIDesigner.COLOR_TEXT_SECONDARY);

        JLabel lblEmail = new JLabel(cliente.getEmail());
        lblEmail.setFont(UIDesigner.FONT_BODY);
        lblEmail.setForeground(UIDesigner.COLOR_TEXT_SECONDARY);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.add(lblCpf, BorderLayout.NORTH);
        infoPanel.add(lblEmail, BorderLayout.CENTER);

        card.add(lblNome, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarPainelDetalhes(cliente);
            }
        });
        UIDesigner.addHoverEffect(card);

        return card;
    }

    /**
     * Cria o painel lateral de detalhes/formulário para adicionar ou editar
     * clientes.
     */
    private JPanel createPainelDetalhes() {
        JPanel painel = new JPanel(new BorderLayout(15, 15));
        painel.setBackground(UIDesigner.COLOR_CARD_BACKGROUND);
        painel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 1, 0, 0, UIDesigner.COLOR_BORDER),
                new EmptyBorder(20, 20, 20, 20)));
        painel.setPreferredSize(new Dimension(350, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        lblTituloDetalhes = new JLabel();
        lblTituloDetalhes.setFont(UIDesigner.FONT_TITLE.deriveFont(20f));
        lblTituloDetalhes.setForeground(UIDesigner.COLOR_FOREGROUND);
        JButton btnFecharDetalhes = new JButton("X");
        btnFecharDetalhes.addActionListener(e -> painelDetalhes.setVisible(false));
        headerPanel.add(lblTituloDetalhes, BorderLayout.CENTER);
        headerPanel.add(btnFecharDetalhes, BorderLayout.EAST);
        painel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        formPanel.add(new JLabel("CPF"), gbc);
        gbc.gridy++;
        txtCpf = new JTextField();
        ((PlainDocument) txtCpf.getDocument()).setDocumentFilter(new CpfDocumentFilter());
        formPanel.add(txtCpf, gbc);
        gbc.gridy++;

        formPanel.add(new JLabel("Nome Completo"), gbc);
        gbc.gridy++;
        txtNome = new JTextField();
        formPanel.add(txtNome, gbc);
        gbc.gridy++;

        formPanel.add(new JLabel("Telefone"), gbc);
        gbc.gridy++;
        txtTelefone = new JTextField();
        ((PlainDocument) txtTelefone.getDocument()).setDocumentFilter(new TelefoneDocumentFilter());
        formPanel.add(txtTelefone, gbc);
        gbc.gridy++;

        formPanel.add(new JLabel("Email"), gbc);
        gbc.gridy++;
        txtEmail = new JTextField();
        formPanel.add(txtEmail, gbc);
        gbc.gridy++;

        painel.add(formPanel, BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoesPanel.setOpaque(false);
        btnRemover = UIDesigner.createDangerButton("Remover", "icons/delete.svg");
        btnSalvar = UIDesigner.createPrimaryButton("Salvar", "icons/save.svg");
        botoesPanel.add(btnRemover);
        botoesPanel.add(btnSalvar);
        painel.add(botoesPanel, BorderLayout.SOUTH);

        btnSalvar.addActionListener(e -> salvarCliente());
        btnRemover.addActionListener(e -> removerCliente());

        return painel;
    }

    /**
     * Torna o painel de detalhes visível e preenche seus campos.
     * 
     * @param cliente O cliente a ser editado, ou null para adicionar um novo.
     */
    private void mostrarPainelDetalhes(Cliente cliente) {
        this.clienteSelecionado = cliente;
        if (cliente != null) { // Editando
            lblTituloDetalhes.setText("Detalhes do Cliente");
            txtCpf.setText(formatCpf(cliente.getCpf()));
            txtCpf.setEditable(false);
            txtNome.setText(cliente.getNome());
            txtTelefone.setText(formatTelefone(cliente.getTelefone()));
            txtEmail.setText(cliente.getEmail());
            btnRemover.setVisible(true);
        } else { // Adicionando
            lblTituloDetalhes.setText("Adicionar Novo Cliente");
            txtCpf.setText("");
            txtCpf.setEditable(true);
            txtNome.setText("");
            txtTelefone.setText("");
            txtEmail.setText("");
            btnRemover.setVisible(false);
        }
        painelDetalhes.setVisible(true);
        revalidate();
        repaint();
    }

    /**
     * Valida os campos e salva um cliente novo ou atualiza um existente.
     */
    private void salvarCliente() {
        String cpf = txtCpf.getText();
        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText();
        String email = txtEmail.getText().trim();

        if (!CpfValidator.isValid(cpf)) {
            JOptionPane.showMessageDialog(this, "O CPF informado é inválido. Verifique os dígitos.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (nome.isEmpty() || !isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Nome e Email (válido) são obrigatórios.", "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        String telefoneLimpo = telefone.replaceAll("[^0-9]", "");

        if (clienteSelecionado != null) { // Atualização
            clienteSelecionado.setNome(nome);
            clienteSelecionado.setTelefone(telefoneLimpo);
            clienteSelecionado.setEmail(email);
        } else { // Novo
            Optional<Cliente> existente = listaCompletaClientes.stream().filter(c -> c.getCpf().equals(cpfLimpo))
                    .findFirst();
            if (existente.isPresent()) {
                JOptionPane.showMessageDialog(this, "Já existe um cliente com este CPF.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            clienteSelecionado = new Cliente(cpfLimpo, nome, telefoneLimpo, email);
            listaCompletaClientes.add(clienteSelecionado);
        }

        GerenciadorClientes.salvarClientes(listaCompletaClientes);
        JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        carregarClientes();
        painelDetalhes.setVisible(false);
    }

    /**
     * Remove o cliente selecionado do sistema.
     */
    private void removerCliente() {
        if (clienteSelecionado == null)
            return;

        boolean temAluguelAtivo = GerenciadorAlugueis.carregarAlugueis().stream()
                .anyMatch(a -> a.getCpfCliente().equals(clienteSelecionado.getCpf())
                        && "Ativo".equals(a.getStatusAluguel()));

        if (temAluguelAtivo) {
            JOptionPane.showMessageDialog(this, "Cliente não pode ser removido pois possui um aluguel ativo.",
                    "Operação Negada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover " + clienteSelecionado.getNome() + "?", "Confirmar Remoção",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            listaCompletaClientes.remove(clienteSelecionado);
            GerenciadorClientes.salvarClientes(listaCompletaClientes);
            JOptionPane.showMessageDialog(this, "Cliente removido com sucesso.", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            carregarClientes();
            painelDetalhes.setVisible(false);
        }
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
            return "";
        if (telefone.length() == 11)
            return telefone.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        if (telefone.length() == 10)
            return telefone.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        return telefone;
    }
}
