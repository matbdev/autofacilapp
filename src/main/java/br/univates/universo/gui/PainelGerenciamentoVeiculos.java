package br.univates.universo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.PlainDocument;

import br.univates.universo.core.Veiculo;
import br.univates.universo.data.GerenciadorAlugueis;
import br.univates.universo.data.GerenciadorVeiculos;
import br.univates.universo.util.PlacaDocumentFilter;
import br.univates.universo.util.UIDesigner;
import br.univates.universo.util.WrapLayout;

/**
 * Painel para gerenciamento completo da frota de veículos.
 * Apresenta uma interface moderna com visualização em cards e um painel
 * de detalhes contextual para adição e edição.
 *
 * @version 3.0
 */
public class PainelGerenciamentoVeiculos extends JPanel {
    private List<Veiculo> listaCompletaVeiculos;
    private final JPanel painelCards;
    private final JPanel painelDetalhes;
    private Veiculo veiculoSelecionado;

    // Componentes do painel de detalhes
    private JLabel lblTituloDetalhes;
    private JTextField txtPlaca, txtMarca, txtModelo, txtAno, txtPrecoDiaria;
    private JComboBox<String> comboCor;
    private JButton btnSalvar, btnRemover;

    public PainelGerenciamentoVeiculos() {
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
     * Carrega a lista de veículos do arquivo e atualiza a exibição.
     */
    public void carregarVeiculos() {
        this.listaCompletaVeiculos = GerenciadorVeiculos.carregarVeiculos();
        filtrarVeiculos("");
    }

    /**
     * Filtra e exibe os veículos com base em um termo de busca.
     * 
     * @param termo O texto a ser buscado na descrição do veículo.
     */
    private void filtrarVeiculos(String termo) {
        painelCards.removeAll();
        termo = termo.toLowerCase();
        for (Veiculo veiculo : listaCompletaVeiculos) {
            if (veiculo.toString().toLowerCase().contains(termo)) {
                painelCards.add(createVeiculoCard(veiculo));
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

        JLabel titulo = new JLabel("Frota de Veículos");
        titulo.setFont(UIDesigner.FONT_TITLE);
        titulo.setForeground(UIDesigner.COLOR_FOREGROUND);
        painel.add(titulo, BorderLayout.WEST);

        JPanel acoesCentro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acoesCentro.setOpaque(false);
        JTextField txtBusca = new JTextField(25);
        txtBusca.putClientProperty("JTextField.placeholderText", "Buscar por placa, marca ou modelo...");
        txtBusca.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarVeiculos(txtBusca.getText());
            }
        });
        acoesCentro.add(txtBusca);
        painel.add(acoesCentro, BorderLayout.CENTER);

        JButton btnAdicionar = UIDesigner.createPrimaryButton("Adicionar Veículo", "icons/add.svg");
        btnAdicionar.addActionListener(e -> mostrarPainelDetalhes(null));
        painel.add(btnAdicionar, BorderLayout.EAST);

        return painel;
    }

    /**
     * Cria um card visual para representar um único veículo.
     * 
     * @param veiculo O objeto Veiculo a ser exibido.
     * @return Um JPanel estilizado como um card.
     */
    private JPanel createVeiculoCard(Veiculo veiculo) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(UIDesigner.BORDER_CARD);
        card.setBackground(UIDesigner.COLOR_CARD_BACKGROUND);
        card.setPreferredSize(new Dimension(320, 160));

        JLabel lblMarcaModelo = new JLabel(veiculo.getMarca() + " " + veiculo.getModelo());
        lblMarcaModelo.setFont(UIDesigner.FONT_SUBTITLE);
        lblMarcaModelo.setForeground(UIDesigner.COLOR_FOREGROUND);

        JLabel lblAnoCor = new JLabel(veiculo.getAno() + " - " + veiculo.getCor());
        lblAnoCor.setFont(UIDesigner.FONT_BODY);
        lblAnoCor.setForeground(UIDesigner.COLOR_TEXT_SECONDARY);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(lblMarcaModelo, BorderLayout.NORTH);
        northPanel.add(lblAnoCor, BorderLayout.CENTER);
        card.add(northPanel, BorderLayout.NORTH);

        JLabel lblPlaca = new JLabel(veiculo.getPlacaFormatada());
        lblPlaca.setFont(new Font("Consolas", Font.BOLD, 20));
        lblPlaca.setForeground(UIDesigner.COLOR_FOREGROUND);
        lblPlaca.setHorizontalAlignment(JLabel.CENTER);
        card.add(lblPlaca, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        JLabel lblPreco = new JLabel(String.format("R$ %.2f/dia", veiculo.getPrecoDiaria()));
        lblPreco.setFont(UIDesigner.FONT_BODY.deriveFont(Font.BOLD));
        lblPreco.setForeground(UIDesigner.COLOR_FOREGROUND);

        JLabel lblStatus = new JLabel(veiculo.getStatus());
        lblStatus.setFont(UIDesigner.FONT_BODY.deriveFont(Font.BOLD));
        lblStatus.setForeground(
                "Disponível".equals(veiculo.getStatus()) ? UIDesigner.COLOR_SUCCESS : UIDesigner.COLOR_DANGER);

        southPanel.add(lblPreco, BorderLayout.WEST);
        southPanel.add(lblStatus, BorderLayout.EAST);
        card.add(southPanel, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarPainelDetalhes(veiculo);
            }
        });
        UIDesigner.addHoverEffect(card);

        return card;
    }

    /**
     * Cria o painel lateral de detalhes/formulário para adicionar ou editar
     * veículos.
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

        formPanel.add(new JLabel("Placa"), gbc);
        gbc.gridy++;
        txtPlaca = new JTextField();
        ((PlainDocument) txtPlaca.getDocument()).setDocumentFilter(new PlacaDocumentFilter());
        formPanel.add(txtPlaca, gbc);
        gbc.gridy++;

        formPanel.add(new JLabel("Marca"), gbc);
        gbc.gridy++;
        txtMarca = new JTextField();
        formPanel.add(txtMarca, gbc);
        gbc.gridy++;

        formPanel.add(new JLabel("Modelo"), gbc);
        gbc.gridy++;
        txtModelo = new JTextField();
        formPanel.add(txtModelo, gbc);
        gbc.gridy++;

        formPanel.add(new JLabel("Ano"), gbc);
        gbc.gridy++;
        txtAno = new JTextField();
        formPanel.add(txtAno, gbc);
        gbc.gridy++;

        formPanel.add(new JLabel("Cor"), gbc);
        gbc.gridy++;
        comboCor = new JComboBox<>(new String[] { "Branco", "Preto", "Prata", "Cinza", "Grafite", "Vermelho", "Azul",
                "Verde", "Amarelo", "Dourado", "Marrom", "Laranja", "Vinho", "Bege", "Outra" });
        formPanel.add(comboCor, gbc);
        gbc.gridy++;

        formPanel.add(new JLabel("Preço da Diária (R$)"), gbc);
        gbc.gridy++;
        txtPrecoDiaria = new JTextField();
        formPanel.add(txtPrecoDiaria, gbc);
        gbc.gridy++;

        painel.add(formPanel, BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoesPanel.setOpaque(false);
        btnRemover = UIDesigner.createDangerButton("Remover", "icons/delete.svg");
        btnSalvar = UIDesigner.createPrimaryButton("Salvar", "icons/save.svg");
        botoesPanel.add(btnRemover);
        botoesPanel.add(btnSalvar);
        painel.add(botoesPanel, BorderLayout.SOUTH);

        btnSalvar.addActionListener(e -> salvarVeiculo());
        btnRemover.addActionListener(e -> removerVeiculo());

        return painel;
    }

    /**
     * Torna o painel de detalhes visível e preenche seus campos.
     * 
     * @param veiculo O veículo a ser editado, ou null para adicionar um novo.
     */
    private void mostrarPainelDetalhes(Veiculo veiculo) {
        this.veiculoSelecionado = veiculo;
        if (veiculo != null) { // Editando
            lblTituloDetalhes.setText("Detalhes do Veículo");
            txtPlaca.setText(veiculo.getPlacaFormatada());
            txtPlaca.setEditable(false);
            txtMarca.setText(veiculo.getMarca());
            txtMarca.setEditable(false);
            txtModelo.setText(veiculo.getModelo());
            txtModelo.setEditable(false);
            txtAno.setText(String.valueOf(veiculo.getAno()));
            txtAno.setEditable(false);
            comboCor.setSelectedItem(veiculo.getCor());
            txtPrecoDiaria.setText(String.format("%.2f", veiculo.getPrecoDiaria()).replace(",", "."));
            btnRemover.setVisible(true);
        } else { // Adicionando
            lblTituloDetalhes.setText("Adicionar Novo Veículo");
            txtPlaca.setText("");
            txtPlaca.setEditable(true);
            txtMarca.setText("");
            txtMarca.setEditable(true);
            txtModelo.setText("");
            txtModelo.setEditable(true);
            txtAno.setText("");
            txtAno.setEditable(true);
            comboCor.setSelectedIndex(0);
            txtPrecoDiaria.setText("");
            btnRemover.setVisible(false);
        }
        painelDetalhes.setVisible(true);
        revalidate();
        repaint();
    }

    /**
     * Valida os campos e salva um veículo novo ou atualiza um existente.
     */
    private void salvarVeiculo() {
        try {
            String placa = txtPlaca.getText().replaceAll("-", "").trim();
            String marca = txtMarca.getText().trim();
            String modelo = txtModelo.getText().trim();
            int ano = Integer.parseInt(txtAno.getText().trim());
            String cor = (String) comboCor.getSelectedItem();
            double precoDiaria = Double.parseDouble(txtPrecoDiaria.getText().replace(",", "."));

            if (placa.isEmpty() || marca.isEmpty() || modelo.isEmpty() || cor == null) {
                JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (veiculoSelecionado != null) { // Atualização
                veiculoSelecionado.setCor(cor);
                veiculoSelecionado.setPrecoDiaria(precoDiaria);
            } else { // Novo
                Optional<Veiculo> existente = GerenciadorVeiculos.carregarVeiculos().stream()
                        .filter(v -> v.getPlaca().equals(placa)).findFirst();
                if (existente.isPresent()) {
                    JOptionPane.showMessageDialog(this, "Já existe um veículo com esta placa.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                veiculoSelecionado = new Veiculo(placa, marca, modelo, ano, cor, precoDiaria);
                listaCompletaVeiculos.add(veiculoSelecionado);
            }

            GerenciadorVeiculos.salvarVeiculos(listaCompletaVeiculos);
            JOptionPane.showMessageDialog(this, "Veículo salvo com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);

            carregarVeiculos();
            painelDetalhes.setVisible(false);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ano e Preço devem ser números válidos.", "Erro de Formato",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Remove o veículo selecionado do sistema.
     */
    private void removerVeiculo() {
        if (veiculoSelecionado == null)
            return;

        boolean temAluguelAtivo = GerenciadorAlugueis.carregarAlugueis().stream()
                .anyMatch(a -> a.getPlacaVeiculo().equals(veiculoSelecionado.getPlaca())
                        && "Ativo".equals(a.getStatusAluguel()));

        if (temAluguelAtivo) {
            JOptionPane.showMessageDialog(this, "Veículo não pode ser removido pois possui um aluguel ativo.",
                    "Operação Negada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover o veículo " + veiculoSelecionado.getModelo() + "?", "Confirmar Remoção",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            listaCompletaVeiculos.remove(veiculoSelecionado);
            GerenciadorVeiculos.salvarVeiculos(listaCompletaVeiculos);
            JOptionPane.showMessageDialog(this, "Veículo removido com sucesso.", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            carregarVeiculos();
            painelDetalhes.setVisible(false);
        }
    }
}
