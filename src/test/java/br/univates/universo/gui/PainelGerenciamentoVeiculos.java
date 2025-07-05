package br.univates.universo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.PlainDocument;

import br.univates.universo.core.Veiculo;
import br.univates.universo.data.GerenciadorAlugueis;
import br.univates.universo.data.GerenciadorVeiculos;
import br.univates.universo.util.FipeApiClient;
import br.univates.universo.util.FipeItem;
import br.univates.universo.util.PlacaDocumentFilter;
import br.univates.universo.util.UIDesigner;
import br.univates.universo.util.WrapLayout;

/**
 * Painel para gerenciamento completo da frota de veículos.
 * Apresenta uma interface moderna com visualização em cards e um painel
 * de detalhes contextual para adição e edição.
 *
 * @version 3.4 (Com parsing robusto de FIPE)
 */
public class PainelGerenciamentoVeiculos extends JPanel {
    private List<Veiculo> listaCompletaVeiculos;
    private final JPanel painelCards;
    private final JPanel painelDetalhes;
    private Veiculo veiculoSelecionado;
    private final FipeApiClient fipeApiClient;

    // Componentes do painel de detalhes
    private JLabel lblTituloDetalhes;
    private JTextField txtPlaca, txtPrecoDiaria, txtValorFipe;
    private JComboBox<String> comboCor;
    private JComboBox<FipeItem> comboMarca, comboModelo, comboAno;
    private JLabel lblInfoDiaria;
    private JButton btnSalvar, btnRemover;

    public PainelGerenciamentoVeiculos() {
        super(new BorderLayout(20, 20));
        this.fipeApiClient = new FipeApiClient();
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

    public void carregarVeiculos() {
        this.listaCompletaVeiculos = GerenciadorVeiculos.carregarVeiculos();
        filtrarVeiculos("");
    }

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

        // CORRIGIDO
        JButton btnAdicionar = UIDesigner.createPrimaryButton("Adicionar Veículo", "icons/add.png");
        btnAdicionar.addActionListener(e -> mostrarPainelDetalhes(null));
        painel.add(btnAdicionar, BorderLayout.EAST);

        return painel;
    }

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

    private JPanel createPainelDetalhes() {
        JPanel painel = new JPanel(new BorderLayout(15, 15));
        painel.setBackground(UIDesigner.COLOR_CARD_BACKGROUND);
        painel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 1, 0, 0, UIDesigner.COLOR_BORDER),
                new EmptyBorder(20, 20, 20, 20)));
        painel.setPreferredSize(new Dimension(380, 0));

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
        comboMarca = new JComboBox<>();
        formPanel.add(comboMarca, gbc);
        gbc.gridy++;

        formPanel.add(new JLabel("Modelo"), gbc);
        gbc.gridy++;
        comboModelo = new JComboBox<>();
        comboModelo.setEnabled(false);
        formPanel.add(comboModelo, gbc);
        gbc.gridy++;

        formPanel.add(new JLabel("Ano"), gbc);
        gbc.gridy++;
        comboAno = new JComboBox<>();
        comboAno.setEnabled(false);
        formPanel.add(comboAno, gbc);
        gbc.gridy++;

        formPanel.add(new JLabel("Valor (FIPE)"), gbc);
        gbc.gridy++;
        txtValorFipe = new JTextField("Selecione marca, modelo e ano");
        txtValorFipe.setEditable(false);
        txtValorFipe.setFont(txtValorFipe.getFont().deriveFont(Font.BOLD));
        formPanel.add(txtValorFipe, gbc);
        gbc.gridy++;

        formPanel.add(new JLabel("Cor"), gbc);
        gbc.gridy++;
        comboCor = new JComboBox<>(new String[] { "Branco", "Preto", "Prata", "Cinza", "Grafite", "Vermelho", "Azul",
                "Verde", "Amarelo", "Dourado", "Marrom", "Laranja", "Vinho", "Bege", "Outra" });
        formPanel.add(comboCor, gbc);
        gbc.gridy++;

        JPanel painelDiaria = new JPanel(new BorderLayout(5, 0));
        painelDiaria.setOpaque(false);
        txtPrecoDiaria = new JTextField();
        lblInfoDiaria = new JLabel("(1% do valor FIPE)");
        lblInfoDiaria.setFont(UIDesigner.FONT_BODY.deriveFont(11f));
        lblInfoDiaria.setForeground(UIDesigner.COLOR_TEXT_SECONDARY);
        painelDiaria.add(txtPrecoDiaria, BorderLayout.CENTER);
        painelDiaria.add(lblInfoDiaria, BorderLayout.EAST);

        formPanel.add(new JLabel("Preço da Diária (R$)"), gbc);
        gbc.gridy++;
        formPanel.add(painelDiaria, gbc);
        gbc.gridy++;

        painel.add(formPanel, BorderLayout.CENTER);

        comboMarca.addActionListener(e -> carregarModelos());
        comboModelo.addActionListener(e -> carregarAnos());
        comboAno.addActionListener(e -> carregarValorFipe());

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoesPanel.setOpaque(false);
        // CORRIGIDO
        btnRemover = UIDesigner.createDangerButton("Remover", "icons/delete.png");
        // CORRIGIDO
        btnSalvar = UIDesigner.createPrimaryButton("Salvar", "icons/save.png");
        botoesPanel.add(btnRemover);
        botoesPanel.add(btnSalvar);
        painel.add(botoesPanel, BorderLayout.SOUTH);

        btnSalvar.addActionListener(e -> salvarVeiculo());
        btnRemover.addActionListener(e -> removerVeiculo());

        return painel;
    }

    private void mostrarPainelDetalhes(Veiculo veiculo) {
        this.veiculoSelecionado = veiculo;
        comboMarca.removeAllItems();
        comboModelo.removeAllItems();
        comboAno.removeAllItems();
        txtValorFipe.setText("Selecione marca, modelo e ano");

        if (veiculo != null) {
            lblTituloDetalhes.setText("Detalhes do Veículo");
            txtPlaca.setText(veiculo.getPlacaFormatada());
            txtPlaca.setEditable(false);

            comboMarca.addItem(new FipeItem(null, veiculo.getMarca()));
            comboModelo.addItem(new FipeItem(null, veiculo.getModelo()));
            comboAno.addItem(new FipeItem(null, String.valueOf(veiculo.getAno())));
            comboMarca.setEnabled(false);
            comboModelo.setEnabled(false);
            comboAno.setEnabled(false);

            comboCor.setSelectedItem(veiculo.getCor());
            txtPrecoDiaria.setText(String.format("%.2f", veiculo.getPrecoDiaria()).replace(",", "."));
            txtValorFipe.setText(veiculo.getValorFipe() > 0 ? String.format("R$ %.2f", veiculo.getValorFipe()) : "N/A");

            btnRemover.setVisible(true);
            lblInfoDiaria.setVisible(false);
        } else {
            lblTituloDetalhes.setText("Adicionar Novo Veículo");
            txtPlaca.setText("");
            txtPlaca.setEditable(true);

            comboModelo.setEnabled(false);
            comboAno.setEnabled(false);
            comboMarca.setEnabled(true);

            carregarMarcas();

            comboCor.setSelectedIndex(0);
            txtPrecoDiaria.setText("");
            btnRemover.setVisible(false);
            lblInfoDiaria.setVisible(true);
        }
        painelDetalhes.setVisible(true);
        revalidate();
        repaint();
    }

    private void carregarMarcas() {
        comboMarca.removeAllItems();
        comboModelo.removeAllItems();
        comboAno.removeAllItems();
        comboMarca.addItem(new FipeItem(null, "Selecione a Marca..."));
        new Thread(() -> {
            fipeApiClient.getMarcas().forEach(comboMarca::addItem);
        }).start();
    }

    private void carregarModelos() {
        if (comboMarca.getSelectedIndex() <= 0) {
            comboModelo.removeAllItems();
            comboModelo.setEnabled(false);
            return;
        }
        FipeItem marcaSelecionada = (FipeItem) comboMarca.getSelectedItem();
        comboModelo.removeAllItems();
        comboModelo.addItem(new FipeItem(null, "Carregando..."));
        comboModelo.setEnabled(false);
        new Thread(() -> {
            List<FipeItem> modelos = fipeApiClient.getModelos(marcaSelecionada.getCode());
            SwingUtilities.invokeLater(() -> {
                comboModelo.removeAllItems();
                comboModelo.addItem(new FipeItem(null, "Selecione o Modelo..."));
                modelos.forEach(comboModelo::addItem);
                comboModelo.setEnabled(true);
            });
        }).start();
    }

    private void carregarAnos() {
        if (comboModelo.getSelectedIndex() <= 0) {
            comboAno.removeAllItems();
            comboAno.setEnabled(false);
            return;
        }
        FipeItem marca = (FipeItem) comboMarca.getSelectedItem();
        FipeItem modelo = (FipeItem) comboModelo.getSelectedItem();
        comboAno.removeAllItems();
        comboAno.addItem(new FipeItem(null, "Carregando..."));
        comboAno.setEnabled(false);
        new Thread(() -> {
            // A lista de anos já virá filtrada do FipeApiClient
            List<FipeItem> anos = fipeApiClient.getAnos(marca.getCode(), modelo.getCode());
            SwingUtilities.invokeLater(() -> {
                comboAno.removeAllItems();
                comboAno.addItem(new FipeItem(null, "Selecione o Ano..."));
                anos.forEach(comboAno::addItem);
                comboAno.setEnabled(true);
            });
        }).start();
    }

    private void carregarValorFipe() {
        if (comboAno.getSelectedIndex() <= 0) {
            txtValorFipe.setText("Selecione um ano");
            txtPrecoDiaria.setText("");
            return;
        }
        FipeItem marca = (FipeItem) comboMarca.getSelectedItem();
        FipeItem modelo = (FipeItem) comboModelo.getSelectedItem();
        FipeItem ano = (FipeItem) comboAno.getSelectedItem();

        txtValorFipe.setText("Buscando valor...");
        new Thread(() -> {
            FipeApiClient.VeiculoFipe veiculoFipe = fipeApiClient.getValor(marca.getCode(), modelo.getCode(),
                    ano.getCode());
            SwingUtilities.invokeLater(() -> {
                if (veiculoFipe != null) {
                    double valor = veiculoFipe.getValorNumerico();
                    double diaria = valor * 0.01;

                    txtValorFipe.setText(veiculoFipe.getValor());
                    txtPrecoDiaria.setText(String.format("%.2f", diaria).replace(",", "."));
                } else {
                    txtValorFipe.setText("Valor não encontrado");
                }
            });
        }).start();
    }

    /**
     * MÉTODO CORRIGIDO
     * Valida os campos e salva um veículo novo ou atualiza um existente.
     * Inclui uma verificação para garantir que o valor FIPE foi carregado e usa um
     * método de parsing mais seguro.
     */
    private void salvarVeiculo() {
        try {
            if (veiculoSelecionado != null) { // Atualização
                veiculoSelecionado.setCor((String) comboCor.getSelectedItem());
                double precoDiaria = Double.parseDouble(txtPrecoDiaria.getText().replace(",", "."));
                veiculoSelecionado.setPrecoDiaria(precoDiaria);
            } else { // Novo Veículo
                String placa = txtPlaca.getText().replaceAll("-", "").trim().toUpperCase();
                FipeItem marca = (FipeItem) comboMarca.getSelectedItem();
                FipeItem modelo = (FipeItem) comboModelo.getSelectedItem();
                FipeItem ano = (FipeItem) comboAno.getSelectedItem();
                String cor = (String) comboCor.getSelectedItem();

                if (placa.isBlank() || comboMarca.getSelectedIndex() <= 0 || comboModelo.getSelectedIndex() <= 0
                        || comboAno.getSelectedIndex() <= 0) {
                    JOptionPane.showMessageDialog(this, "Todos os campos (Placa, Marca, Modelo, Ano) são obrigatórios.",
                            "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String valorFipeTexto = txtValorFipe.getText();
                if (valorFipeTexto == null || !valorFipeTexto.startsWith("R$")) {
                    JOptionPane.showMessageDialog(this, "Aguarde o carregamento do valor FIPE ou verifique a seleção.",
                            "Valor FIPE Inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Optional<Veiculo> existente = GerenciadorVeiculos.carregarVeiculos().stream()
                        .filter(v -> v.getPlaca().equals(placa)).findFirst();
                if (existente.isPresent()) {
                    JOptionPane.showMessageDialog(this, "Já existe um veículo com esta placa.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double precoDiaria = Double.parseDouble(txtPrecoDiaria.getText().replace(",", "."));

                // CORREÇÃO: Utiliza um método de parsing mais robusto para o valor FIPE.
                double valorFipe;
                try {
                    String valorLimpo = valorFipeTexto.replace("R$", "").trim().replace(".", "").replace(",", ".");
                    if (valorLimpo.isBlank()) {
                        throw new NumberFormatException("Valor FIPE vazio após limpeza.");
                    }
                    valorFipe = Double.parseDouble(valorLimpo);
                } catch (NumberFormatException ex) {
                    // Se o parsing falhar mesmo após a limpeza, lança uma ParseException
                    // para ser capturada pelo bloco de erro genérico abaixo.
                    throw new ParseException("Formato de valor FIPE inválido: " + valorFipeTexto, 0);
                }

                int anoInt = Integer.parseInt(ano.getName().split(" ")[0]);

                veiculoSelecionado = new Veiculo(placa, marca.getName(), modelo.getName(), anoInt, cor, precoDiaria,
                        valorFipe);
                listaCompletaVeiculos.add(veiculoSelecionado);
            }

            GerenciadorVeiculos.salvarVeiculos(listaCompletaVeiculos);
            JOptionPane.showMessageDialog(this, "Veículo salvo com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);

            carregarVeiculos();
            painelDetalhes.setVisible(false);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço da diária deve ser um número válido.", "Erro de Formato",
                    JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Não foi possível ler o valor FIPE. Verifique a seleção.",
                    "Erro de Valor", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado ao salvar: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

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