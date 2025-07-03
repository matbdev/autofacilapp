package br.univates.universo.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import br.univates.universo.core.Aluguel;
import br.univates.universo.core.Cliente;
import br.univates.universo.core.Veiculo;
import br.univates.universo.data.GerenciadorAlugueis;
import br.univates.universo.data.GerenciadorClientes;
import br.univates.universo.data.GerenciadorVeiculos;

public final class PainelGerenciamentoAlugueis extends JPanel {

    private JComboBox<Cliente> comboClientes;
    private JComboBox<Veiculo> comboVeiculos;
    private DatePicker pickerDataSaida, pickerDataPrevistaDevolucao, pickerDataDevolucaoEfetiva;
    private JButton btnRegistrarSaida, btnRegistrarDevolucao;
    private JTable tabelaAlugueisAtivos, tabelaHistorico;
    private DefaultTableModel modeloTabelaAtivos, modeloTabelaHistorico;
    private final PainelGerenciamentoVeiculos painelVeiculosRef;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PainelGerenciamentoAlugueis(PainelGerenciamentoVeiculos painelVeiculosRef) {
        super(new BorderLayout(10, 10));
        this.painelVeiculosRef = painelVeiculosRef;
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        setupListeners();
        atualizarDados();
    }

    private void initComponents() {
        JPanel painelRegistros = new JPanel(new GridLayout(1, 2, 20, 0));
        painelRegistros.add(createPainelSaida());
        painelRegistros.add(createPainelDevolucao());

        JTabbedPane painelComAbas = new JTabbedPane();
        String[] colunas = { "ID", "Cliente", "Veículo", "Data Saída", "Previsão", "Devolução", "Multa", "Valor Total",
                "Status" };

        modeloTabelaAtivos = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabelaAlugueisAtivos = new JTable(modeloTabelaAtivos);
        tabelaAlugueisAtivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painelComAbas.addTab("Aluguéis Ativos", new JScrollPane(tabelaAlugueisAtivos));

        modeloTabelaHistorico = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabelaHistorico = new JTable(modeloTabelaHistorico);
        tabelaHistorico.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painelComAbas.addTab("Histórico de Locações", new JScrollPane(tabelaHistorico));

        add(painelRegistros, BorderLayout.NORTH);
        add(painelComAbas, BorderLayout.CENTER);
    }

    private JPanel createPainelSaida() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Registrar Saída de Veículo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0;
        painel.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        comboClientes = new JComboBox<>();
        painel.add(comboClientes, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        painel.add(new JLabel("Veículo:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        comboVeiculos = new JComboBox<>();
        painel.add(comboVeiculos, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        painel.add(new JLabel("Data Saída:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        DatePickerSettings settingsSaida = new DatePickerSettings();
        settingsSaida.setAllowKeyboardEditing(false);
        pickerDataSaida = new DatePicker(settingsSaida);
        // CORREÇÃO: Definir limites DEPOIS de criar o DatePicker
        pickerDataSaida.getSettings().setDateRangeLimits(LocalDate.now(), null);
        pickerDataSaida.setDateToToday();
        painel.add(pickerDataSaida, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        painel.add(new JLabel("Previsão Devolução:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        DatePickerSettings settingsPrevista = new DatePickerSettings();
        settingsPrevista.setAllowKeyboardEditing(false);
        pickerDataPrevistaDevolucao = new DatePicker(settingsPrevista);
        pickerDataPrevistaDevolucao.getSettings().setDateRangeLimits(
                pickerDataSaida.getDate().plusDays(1), null);
        pickerDataPrevistaDevolucao.setDate(pickerDataSaida.getDate().plusDays(7));
        painel.add(pickerDataPrevistaDevolucao, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        btnRegistrarSaida = new JButton("Registrar Saída");
        painel.add(btnRegistrarSaida, gbc);
        return painel;
    }

    private JPanel createPainelDevolucao() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Registrar Devolução"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        painel.add(new JLabel("Selecione um aluguel ativo na tabela."), gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        painel.add(new JLabel("Data Devolução:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        DatePickerSettings settingsDevolucao = new DatePickerSettings();
        settingsDevolucao.setAllowKeyboardEditing(false);
        pickerDataDevolucaoEfetiva = new DatePicker(settingsDevolucao);
        pickerDataDevolucaoEfetiva.setDateToToday();
        painel.add(pickerDataDevolucaoEfetiva, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        btnRegistrarDevolucao = new JButton("Registrar Devolução");
        painel.add(btnRegistrarDevolucao, gbc);

        gbc.gridy++;
        JLabel lblAvisoJuros = new JLabel("<html><i>Atrasos na devolução geram juros de 10% ao dia.</i></html>");
        lblAvisoJuros.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        painel.add(lblAvisoJuros, gbc);

        return painel;
    }

    private void setupListeners() {
        btnRegistrarSaida.addActionListener(e -> registrarSaida());
        btnRegistrarDevolucao.addActionListener(e -> registrarDevolucao());

        pickerDataSaida.addDateChangeListener(e -> {
            LocalDate dataSaida = pickerDataSaida.getDate();
            if (dataSaida != null) {
                LocalDate dataMinimaDevolucao = dataSaida.plusDays(1);
                pickerDataPrevistaDevolucao.getSettings().setDateRangeLimits(dataMinimaDevolucao, null);
                if (pickerDataPrevistaDevolucao.getDate() != null
                        && pickerDataPrevistaDevolucao.getDate().isBefore(dataMinimaDevolucao)) {
                    pickerDataPrevistaDevolucao.setDate(dataMinimaDevolucao);
                }
            }
        });
    }

    private void registrarSaida() {
        Cliente cliente = (Cliente) comboClientes.getSelectedItem();
        Veiculo veiculo = (Veiculo) comboVeiculos.getSelectedItem();
        LocalDate dataSaida = pickerDataSaida.getDate();
        LocalDate dataPrevista = pickerDataPrevistaDevolucao.getDate();

        if (cliente == null || veiculo == null || dataSaida == null || dataPrevista == null) {
            JOptionPane.showMessageDialog(this, "Todos os campos para registrar a saída são obrigatórios.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Aluguel> alugueis = GerenciadorAlugueis.carregarAlugueis();
        Aluguel novoAluguel = new Aluguel(veiculo.getPlaca(), cliente.getCpf(),
                dataSaida.format(formatter), dataPrevista.format(formatter));
        novoAluguel.setPrecoDiaria(veiculo.getPrecoDiaria());
        alugueis.add(novoAluguel);
        GerenciadorAlugueis.salvarAlugueis(alugueis);

        List<Veiculo> veiculos = GerenciadorVeiculos.carregarVeiculos();
        veiculos.stream().filter(v -> v.getPlaca().equals(veiculo.getPlaca())).findFirst().ifPresent(Veiculo::alugar);
        GerenciadorVeiculos.salvarVeiculos(veiculos);

        JOptionPane.showMessageDialog(this, "Saída registrada com sucesso!", "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
        atualizarDados();
        painelVeiculosRef.carregarVeiculosNaTabela();
    }

    private void registrarDevolucao() {
        int linha = tabelaAlugueisAtivos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um aluguel na tabela de 'Aluguéis Ativos' para registrar a devolução.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idAluguel = (int) modeloTabelaAtivos.getValueAt(tabelaAlugueisAtivos.convertRowIndexToModel(linha), 0);
        LocalDate dataDevolucao = pickerDataDevolucaoEfetiva.getDate();

        if (dataDevolucao == null) {
            JOptionPane.showMessageDialog(this, "A data de devolução é obrigatória.", "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Aluguel> alugueis = GerenciadorAlugueis.carregarAlugueis();
        alugueis.stream().filter(a -> a.getIdAluguel() == idAluguel).findFirst().ifPresent(aluguel -> {

            LocalDate dataSaida = LocalDate.parse(aluguel.getDataSaida(), formatter);
            if (dataDevolucao.isBefore(dataSaida)) {
                JOptionPane.showMessageDialog(this, "A data de devolução não pode ser anterior à data de saída.",
                        "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Veiculo> veiculos = GerenciadorVeiculos.carregarVeiculos();
            veiculos.stream().filter(v -> v.getPlaca().equals(aluguel.getPlacaVeiculo())).findFirst()
                    .ifPresent(veiculo -> {
                        aluguel.finalizarAluguel(dataDevolucao.format(formatter), veiculo.getPrecoDiaria());
                        veiculo.devolver();

                        GerenciadorAlugueis.salvarAlugueis(alugueis);
                        GerenciadorVeiculos.salvarVeiculos(veiculos);

                        String msg = String.format("Devolução registrada! Valor Total: R$ %.2f",
                                aluguel.getValorTotal());
                        if (aluguel.getValorMulta() > 0) {
                            msg += String.format(" (inclui R$ %.2f de multa por atraso)", aluguel.getValorMulta());
                        }
                        JOptionPane.showMessageDialog(this, msg, "Devolução Concluída",
                                JOptionPane.INFORMATION_MESSAGE);

                        atualizarDados();
                        painelVeiculosRef.carregarVeiculosNaTabela();
                    });
        });
    }

    public void atualizarDados() {
        comboClientes.removeAllItems();
        GerenciadorClientes.carregarClientes().forEach(comboClientes::addItem);

        comboVeiculos.removeAllItems();
        GerenciadorVeiculos.carregarVeiculos().stream()
                .filter(v -> "Disponível".equals(v.getStatus()))
                .forEach(comboVeiculos::addItem);

        modeloTabelaAtivos.setRowCount(0);
        modeloTabelaHistorico.setRowCount(0);
        List<Aluguel> todosAlugueis = GerenciadorAlugueis.carregarAlugueis();
        List<Cliente> todosClientes = GerenciadorClientes.carregarClientes();
        List<Veiculo> todosVeiculos = GerenciadorVeiculos.carregarVeiculos();

        for (Aluguel a : todosAlugueis) {
            String nomeCliente = todosClientes.stream().filter(c -> c.getCpf().equals(a.getCpfCliente())).findFirst()
                    .map(Cliente::getNome).orElse("N/A");
            String descVeiculo = todosVeiculos.stream().filter(v -> v.getPlaca().equals(a.getPlacaVeiculo()))
                    .findFirst().map(Veiculo::toString).orElse("N/A");
            Object[] linha = {
                    a.getIdAluguel(), nomeCliente, descVeiculo, a.getDataSaida(), a.getDataPrevistaDevolucao(),
                    a.getDataDevolucaoEfetiva() != null ? a.getDataDevolucaoEfetiva() : "---",
                    a.getValorMulta() > 0 ? String.format("R$ %.2f", a.getValorMulta()) : "---",
                    a.getValorTotal() > 0 ? String.format("R$ %.2f", a.getValorTotal()) : "---",
                    a.getStatusAluguel()
            };
            if ("Ativo".equals(a.getStatusAluguel())) {
                modeloTabelaAtivos.addRow(linha);
            } else {
                modeloTabelaHistorico.addRow(linha);
            }
        }
    }
}
