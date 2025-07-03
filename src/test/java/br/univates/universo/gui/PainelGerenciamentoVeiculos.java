package br.univates.universo.gui;

import br.univates.universo.core.Veiculo;
import br.univates.universo.data.GerenciadorAlugueis;
import br.univates.universo.data.GerenciadorVeiculos;
import br.univates.universo.util.FipeApiClient;
import br.univates.universo.util.FipeItem;
import br.univates.universo.util.PlacaDocumentFilter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.PlainDocument;

public final class PainelGerenciamentoVeiculos extends JPanel {

    private JTextField txtPlaca, txtPrecoFipe, txtPrecoDiaria;
    private JComboBox<FipeItem> comboMarca, comboModelo, comboAno;
    private JComboBox<String> comboCor; // ALTERADO: de JTextField para JComboBox
    private JButton btnSalvar, btnLimpar, btnRemover;
    private JTable tabelaVeiculos;
    private DefaultTableModel modeloTabela;

    private final FipeApiClient fipeApiClient = new FipeApiClient();
    private boolean isAdjusting = false;

    public PainelGerenciamentoVeiculos() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        setupListeners();
        carregarVeiculosNaTabela();
        carregarMarcas();
    }

    private void initComponents() {
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Dados do Veículo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Linha 0: Placa e Cor ---
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Placa:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtPlaca = createPlacaField();
        painelFormulario.add(txtPlaca, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Cor:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        // ALTERADO: Campo de texto por ComboBox de cores
        comboCor = new JComboBox<>(new String[] { "Selecione...", "Branco", "Preto", "Prata", "Cinza", "Vermelho",
                "Azul", "Verde", "Amarelo", "Outra" });
        painelFormulario.add(comboCor, gbc);

        // --- Linha 1: Marca e Modelo ---
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Marca:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        comboMarca = new JComboBox<>();
        painelFormulario.add(comboMarca, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Modelo:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        comboModelo = new JComboBox<>();
        comboModelo.setEnabled(false);
        painelFormulario.add(comboModelo, gbc);

        // --- Linha 2: Ano e Preço FIPE ---
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Ano:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        comboAno = new JComboBox<>();
        comboAno.setEnabled(false);
        painelFormulario.add(comboAno, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Preço FIPE (R$):"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        txtPrecoFipe = new JTextField();
        txtPrecoFipe.setEditable(false);
        painelFormulario.add(txtPrecoFipe, gbc);

        // --- Linha 3: Preço Diária e Aviso ---
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Preço Diária (R$):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtPrecoDiaria = new JTextField();
        painelFormulario.add(txtPrecoDiaria, gbc);

        // NOVO: Aviso sobre o preço da diária
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        JLabel lblAvisoPreco = new JLabel("<html><i>Padrão: 1% do valor FIPE (alterável)</i></html>");
        lblAvisoPreco.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        painelFormulario.add(lblAvisoPreco, gbc);
        gbc.gridwidth = 1;

        // --- Painel de Botões ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnSalvar = new JButton("Salvar / Atualizar");
        btnLimpar = new JButton("Limpar Campos");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnLimpar);

        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.add(painelFormulario, BorderLayout.CENTER);
        painelSuperior.add(painelBotoes, BorderLayout.SOUTH);

        // --- Tabela de Veículos ---
        String[] colunas = { "Placa", "Marca", "Modelo", "Ano", "Cor", "Preço Diária", "Status" };
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabelaVeiculos = new JTable(modeloTabela);
        tabelaVeiculos.setRowSorter(new TableRowSorter<>(modeloTabela));
        tabelaVeiculos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tabelaVeiculos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Frota de Veículos"));

        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRemover = new JButton("Remover Selecionado");
        painelInferior.add(btnRemover);

        add(painelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(painelInferior, BorderLayout.SOUTH);
    }

    private JTextField createPlacaField() {
        JTextField field = new JTextField(10);
        ((PlainDocument) field.getDocument()).setDocumentFilter(new PlacaDocumentFilter());
        return field;
    }

    private void setupListeners() {
        btnSalvar.addActionListener(e -> salvarVeiculo());
        btnLimpar.addActionListener(e -> limparCampos());
        btnRemover.addActionListener(e -> removerVeiculo());

        tabelaVeiculos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaVeiculos.getSelectedRow() != -1) {
                preencherFormularioComLinhaSelecionada();
            }
        });

        comboMarca.addActionListener(e -> {
            if (!isAdjusting && comboMarca.getSelectedIndex() > 0) {
                carregarModelos((FipeItem) comboMarca.getSelectedItem());
            }
        });

        comboModelo.addActionListener(e -> {
            if (!isAdjusting && comboModelo.getSelectedIndex() > 0) {
                carregarAnos((FipeItem) comboModelo.getSelectedItem());
            }
        });

        comboAno.addActionListener(e -> {
            if (!isAdjusting && comboAno.getSelectedIndex() > 0) {
                sugerirPreco((FipeItem) comboAno.getSelectedItem());
            }
        });
    }

    private void salvarVeiculo() {
        // This variable is now "effectively final"
        String placaComHifen = txtPlaca.getText().trim();

        // Perform validation on the original plate string
        if (!placaComHifen.matches("^[A-Z]{3}-\\d[A-Z0-9]\\d{2}$")) {
            JOptionPane.showMessageDialog(this, "Placa inválida. Formato esperado: ABC-1234 ou ABC-1D23.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a new variable for the value without the hyphen
        final String placaParaSalvar = placaComHifen.replace("-", "");

        try {
            if (comboCor.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(this, "A cor é obrigatória.", "Erro de Validação",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            String cor = (String) comboCor.getSelectedItem();
            double precoDiaria = Double.parseDouble(txtPrecoDiaria.getText().replace(",", "."));

            List<Veiculo> veiculos = GerenciadorVeiculos.carregarVeiculos();
            Optional<Veiculo> existente = veiculos.stream().filter(v -> v.getPlaca().equals(placaParaSalvar))
                    .findFirst();

            if (existente.isPresent()) { // Atualização
                Veiculo v = existente.get();
                v.setCor(cor);
                v.setPrecoDiaria(precoDiaria);
                JOptionPane.showMessageDialog(this, "Veículo atualizado com sucesso!");
            } else { // Novo Cadastro
                if (comboMarca.getSelectedIndex() <= 0 || comboModelo.getSelectedIndex() <= 0
                        || comboAno.getSelectedIndex() <= 0) {
                    JOptionPane.showMessageDialog(this, "Marca, Modelo e Ano são obrigatórios para um novo cadastro.",
                            "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String marca = ((FipeItem) comboMarca.getSelectedItem()).getName();
                String modelo = ((FipeItem) comboModelo.getSelectedItem()).getName();
                int ano = Integer.parseInt(((FipeItem) comboAno.getSelectedItem()).getName().split(" ")[0]);

                veiculos.add(new Veiculo(placaParaSalvar, marca, modelo, ano, cor, precoDiaria));
                JOptionPane.showMessageDialog(this, "Veículo cadastrado com sucesso!");
            }
            GerenciadorVeiculos.salvarVeiculos(veiculos);
            carregarVeiculosNaTabela();
            limparCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço da diária deve ser um número válido.", "Erro de Formato",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherFormularioComLinhaSelecionada() {
        int linha = tabelaVeiculos.getSelectedRow();
        if (linha == -1)
            return;

        String placa = ((String) modeloTabela.getValueAt(tabelaVeiculos.convertRowIndexToModel(linha), 0)).replace("-",
                "");

        GerenciadorVeiculos.carregarVeiculos().stream()
                .filter(v -> v.getPlaca().equals(placa))
                .findFirst().ifPresent(v -> {
                    isAdjusting = true;
                    txtPlaca.setText(v.getPlacaFormatada());
                    txtPlaca.setEditable(false); // Placa não pode ser editada

                    comboCor.setSelectedItem(v.getCor());
                    txtPrecoDiaria.setText(String.format("%.2f", v.getPrecoDiaria()).replace('.', ','));

                    // NOVO: Bloqueia a alteração de Marca, Modelo e Ano na atualização
                    comboMarca.removeAllItems();
                    comboMarca.addItem(new FipeItem(null, v.getMarca()));
                    comboMarca.setEnabled(false);

                    comboModelo.removeAllItems();
                    comboModelo.addItem(new FipeItem(null, v.getModelo()));
                    comboModelo.setEnabled(false);

                    comboAno.removeAllItems();
                    comboAno.addItem(new FipeItem(null, String.valueOf(v.getAno())));
                    comboAno.setEnabled(false);

                    txtPrecoFipe.setText("N/A (Existente)");
                    isAdjusting = false;
                });
    }

    private void limparCampos() {
        isAdjusting = true;
        txtPlaca.setText("");
        txtPlaca.setEditable(true);
        comboCor.setSelectedIndex(0);
        txtPrecoDiaria.setText("");
        txtPrecoFipe.setText("");

        comboMarca.setEnabled(true);
        comboModelo.setEnabled(false);
        comboAno.setEnabled(false);

        carregarMarcas(); // Recarrega as marcas originais
        comboModelo.removeAllItems();
        comboAno.removeAllItems();

        tabelaVeiculos.clearSelection();
        isAdjusting = false;
    }

    public void carregarVeiculosNaTabela() {
        modeloTabela.setRowCount(0);
        GerenciadorVeiculos.carregarVeiculos().forEach(v -> modeloTabela.addRow(new Object[] {
                v.getPlacaFormatada(), v.getMarca(), v.getModelo(), v.getAno(), v.getCor(),
                String.format("R$ %.2f", v.getPrecoDiaria()), v.getStatus()
        }));
    }

    // --- Métodos de comunicação com a API FIPE (sem alterações) ---

    private void carregarMarcas() {
        new SwingWorker<List<FipeItem>, Void>() {
            @Override
            protected List<FipeItem> doInBackground() throws Exception {
                JsonArray marcasJson = fipeApiClient.getMarcas();
                List<FipeItem> marcas = new ArrayList<>();
                marcas.add(new FipeItem(null, "Selecione..."));
                for (JsonElement el : marcasJson) {
                    JsonObject obj = el.getAsJsonObject();
                    marcas.add(new FipeItem(obj.get("codigo").getAsString(), obj.get("nome").getAsString()));
                }
                return marcas;
            }

            @Override
            protected void done() {
                try {
                    updateComboBox(comboMarca, get());
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(PainelGerenciamentoVeiculos.this, "Erro ao carregar marcas da FIPE.",
                            "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void carregarModelos(FipeItem marca) {
        new SwingWorker<List<FipeItem>, Void>() {
            @Override
            protected List<FipeItem> doInBackground() throws Exception {
                JsonObject modelosJson = fipeApiClient.getModelos(marca.getCode());
                List<FipeItem> modelos = new ArrayList<>();
                modelos.add(new FipeItem(null, "Selecione..."));
                for (JsonElement el : modelosJson.getAsJsonArray("modelos")) {
                    JsonObject obj = el.getAsJsonObject();
                    modelos.add(new FipeItem(obj.get("codigo").getAsString(), obj.get("nome").getAsString()));
                }
                return modelos;
            }

            @Override
            protected void done() {
                try {
                    updateComboBox(comboModelo, get());
                    comboModelo.setEnabled(true);
                    comboAno.setEnabled(false);
                    comboAno.removeAllItems();
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(PainelGerenciamentoVeiculos.this, "Erro ao carregar modelos da FIPE.",
                            "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void carregarAnos(FipeItem modelo) {
        FipeItem marca = (FipeItem) comboMarca.getSelectedItem();
        new SwingWorker<List<FipeItem>, Void>() {
            @Override
            protected List<FipeItem> doInBackground() throws Exception {
                JsonArray anosJson = fipeApiClient.getAnos(marca.getCode(), modelo.getCode());
                List<FipeItem> anos = new ArrayList<>();
                anos.add(new FipeItem(null, "Selecione..."));
                for (JsonElement el : anosJson) {
                    JsonObject obj = el.getAsJsonObject();
                    anos.add(new FipeItem(obj.get("codigo").getAsString(), obj.get("nome").getAsString()));
                }
                return anos;
            }

            @Override
            protected void done() {
                try {
                    updateComboBox(comboAno, get());
                    comboAno.setEnabled(true);
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(PainelGerenciamentoVeiculos.this, "Erro ao carregar anos da FIPE.",
                            "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void sugerirPreco(FipeItem ano) {
        FipeItem marca = (FipeItem) comboMarca.getSelectedItem();
        FipeItem modelo = (FipeItem) comboModelo.getSelectedItem();
        new SwingWorker<JsonObject, Void>() {
            @Override
            protected JsonObject doInBackground() throws Exception {
                return fipeApiClient.getValor(marca.getCode(), modelo.getCode(), ano.getCode());
            }

            @Override
            protected void done() {
                try {
                    JsonObject valorObj = get();
                    String precoFipeStr = valorObj.get("Valor").getAsString();
                    txtPrecoFipe.setText(precoFipeStr);

                    double valorFipe = Double.parseDouble(precoFipeStr.replaceAll("[^0-9,]", "").replace(",", "."));
                    double precoSugerido = valorFipe * 0.01;
                    txtPrecoDiaria.setText(String.format("%.2f", precoSugerido).replace('.', ','));

                } catch (InterruptedException | NumberFormatException | ExecutionException e) {
                    txtPrecoFipe.setText("N/A");
                    txtPrecoDiaria.setText("");
                }
            }
        }.execute();
    }

    private void removerVeiculo() {
        int linha = tabelaVeiculos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um veículo para remover.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String placa = ((String) modeloTabela.getValueAt(tabelaVeiculos.convertRowIndexToModel(linha), 0)).replace("-",
                "");

        boolean temAluguelAtivo = GerenciadorAlugueis.carregarAlugueis().stream()
                .anyMatch(a -> a.getPlacaVeiculo().equals(placa) && "Ativo".equals(a.getStatusAluguel()));

        if (temAluguelAtivo) {
            JOptionPane.showMessageDialog(this, "Veículo não pode ser removido pois possui um aluguel ativo.",
                    "Operação Negada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover o veículo?",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<Veiculo> veiculos = GerenciadorVeiculos.carregarVeiculos();
            veiculos.removeIf(v -> v.getPlaca().equals(placa));
            GerenciadorVeiculos.salvarVeiculos(veiculos);
            carregarVeiculosNaTabela();
            limparCampos();
        }
    }

    private void updateComboBox(JComboBox<FipeItem> combo, List<FipeItem> items) {
        DefaultComboBoxModel<FipeItem> model = new DefaultComboBoxModel<>();
        items.forEach(model::addElement);
        combo.setModel(model);
    }
}
