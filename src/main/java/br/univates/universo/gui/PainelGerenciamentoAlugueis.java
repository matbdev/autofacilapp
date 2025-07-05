package br.univates.universo.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import br.univates.universo.core.Aluguel;
import br.univates.universo.core.Cliente;
import br.univates.universo.core.Veiculo;
import br.univates.universo.data.GerenciadorAlugueis;
import br.univates.universo.data.GerenciadorClientes;
import br.univates.universo.data.GerenciadorVeiculos;
import br.univates.universo.util.UIDesigner;

/**
 * Painel para gerenciamento de aluguéis, com interface moderna.
 * Separa as ações de Saída e Devolução em cards e utiliza abas para
 * organizar os aluguéis ativos e o histórico.
 *
 * @version 3.2 (Modificado)
 */
public class PainelGerenciamentoAlugueis extends JPanel {

    private JComboBox<Cliente> comboClientes;
    private JComboBox<Veiculo> comboVeiculos;
    private DatePicker pickerDataSaida, pickerDataPrevistaDevolucao, pickerDataDevolucaoEfetiva;
    private final JTable tabelaAlugueisAtivos, tabelaHistorico;
    private final DefaultTableModel modeloAtivos, modeloHistorico;
    @SuppressWarnings("unused")
    private final PainelGerenciamentoVeiculos painelVeiculosRef;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PainelGerenciamentoAlugueis(PainelGerenciamentoVeiculos painelVeiculosRef) {
        super(new BorderLayout(20, 20));
        this.painelVeiculosRef = painelVeiculosRef;
        setBorder(new EmptyBorder(20, 30, 20, 30));
        setBackground(UIDesigner.COLOR_BACKGROUND);

        JLabel titulo = new JLabel("Gerenciar Aluguéis");
        titulo.setFont(UIDesigner.FONT_TITLE);
        titulo.setForeground(UIDesigner.COLOR_FOREGROUND);
        add(titulo, BorderLayout.NORTH);

        JPanel painelFormularios = new JPanel(new GridLayout(1, 2, 20, 20));
        painelFormularios.setOpaque(false);
        painelFormularios.add(createPainelSaida());
        painelFormularios.add(createPainelDevolucao());

        JTabbedPane painelTabelas = new JTabbedPane();

        modeloAtivos = new DefaultTableModel(new String[] { "ID", "Cliente", "Veículo", "Saída", "Previsão" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaAlugueisAtivos = new JTable(modeloAtivos);
        tabelaAlugueisAtivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollAtivos = new JScrollPane(tabelaAlugueisAtivos);

        // NOVO: Adicionada a coluna "Valor Multa"
        modeloHistorico = new DefaultTableModel(
                new String[] { "ID", "Cliente", "Veículo", "Status", "Valor Total", "Valor Multa" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaHistorico = new JTable(modeloHistorico);
        JScrollPane scrollHistorico = new JScrollPane(tabelaHistorico);

        painelTabelas.addTab("Aluguéis Ativos", scrollAtivos);
        painelTabelas.addTab("Histórico de Locações", scrollHistorico);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, painelFormularios, painelTabelas);
        splitPane.setDividerLocation(320); // Aumentado para acomodar o aviso
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setOpaque(false);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createPainelSaida() {
        // ... (código original sem alterações)
        JPanel painel = new JPanel(new BorderLayout(15, 15));
        painel.setBorder(UIDesigner.BORDER_CARD);
        painel.setBackground(UIDesigner.COLOR_CARD_BACKGROUND);

        JLabel titulo = new JLabel("Registrar Saída de Veículo");
        titulo.setFont(UIDesigner.FONT_SUBTITLE);
        titulo.setForeground(UIDesigner.COLOR_FOREGROUND);
        painel.add(titulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        comboClientes = new JComboBox<>();
        formPanel.add(comboClientes, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Veículo:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        comboVeiculos = new JComboBox<>();
        formPanel.add(comboVeiculos, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Data Saída:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        DatePickerSettings settingsSaida = new DatePickerSettings();
        pickerDataSaida = new DatePicker(settingsSaida);
        pickerDataSaida.getSettings().setDateRangeLimits(LocalDate.now(), null);
        pickerDataSaida.setDateToToday();
        formPanel.add(pickerDataSaida, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Previsão Devolução:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        DatePickerSettings settingsPrevista = new DatePickerSettings();
        pickerDataPrevistaDevolucao = new DatePicker(settingsPrevista);
        pickerDataPrevistaDevolucao.setDate(LocalDate.now().plusDays(7));
        formPanel.add(pickerDataPrevistaDevolucao, gbc);

        painel.add(formPanel, BorderLayout.CENTER);

        JButton btnRegistrar = UIDesigner.createPrimaryButton("Registrar Saída", "icons/rental.svg");
        btnRegistrar.addActionListener(e -> registrarSaida());
        painel.add(btnRegistrar, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel createPainelDevolucao() {
        JPanel painel = new JPanel(new BorderLayout(15, 15));
        painel.setBorder(UIDesigner.BORDER_CARD);
        painel.setBackground(UIDesigner.COLOR_CARD_BACKGROUND);

        JLabel titulo = new JLabel("Registrar Devolução");
        titulo.setFont(UIDesigner.FONT_SUBTITLE);
        titulo.setForeground(UIDesigner.COLOR_FOREGROUND);
        painel.add(titulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Data Devolução:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        DatePickerSettings settingsDev = new DatePickerSettings();
        pickerDataDevolucaoEfetiva = new DatePicker(settingsDev);
        pickerDataDevolucaoEfetiva.setDateToToday();
        formPanel.add(pickerDataDevolucaoEfetiva, gbc);

        JLabel infoLabel = new JLabel(
                "<html>Selecione um aluguel na tabela 'Ativos' abaixo para registrar a devolução.</html>");
        infoLabel.setForeground(UIDesigner.COLOR_TEXT_SECONDARY);

        // NOVO: Aviso sobre a multa por atraso
        JLabel avisoMulta = new JLabel(
                "<html><b>Aviso:</b> Para cada dia de atraso na devolução, será aplicada uma multa cumulativa de 10% sobre o valor total do aluguel.</html>");
        avisoMulta.setForeground(new Color(239, 68, 68)); // Tom de vermelho
        avisoMulta.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(infoLabel, BorderLayout.NORTH);
        centerPanel.add(avisoMulta, BorderLayout.CENTER);

        painel.add(centerPanel, BorderLayout.CENTER);

        JButton btnRegistrar = UIDesigner.createPrimaryButton("Registrar Devolução", "icons/check.svg");
        btnRegistrar.addActionListener(e -> registrarDevolucao());

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(formPanel, BorderLayout.NORTH);
        southPanel.add(btnRegistrar, BorderLayout.CENTER);
        painel.add(southPanel, BorderLayout.SOUTH);

        return painel;
    }

    public void atualizarDados() {
        comboClientes.removeAllItems();
        GerenciadorClientes.carregarClientes().forEach(comboClientes::addItem);

        comboVeiculos.removeAllItems();
        GerenciadorVeiculos.carregarVeiculos().stream()
                .filter(v -> "Disponível".equals(v.getStatus()))
                .forEach(comboVeiculos::addItem);

        modeloAtivos.setRowCount(0);
        modeloHistorico.setRowCount(0);

        List<Aluguel> alugueis = GerenciadorAlugueis.carregarAlugueis();
        List<Cliente> clientes = GerenciadorClientes.carregarClientes();
        List<Veiculo> veiculos = GerenciadorVeiculos.carregarVeiculos();

        for (Aluguel aluguel : alugueis) {
            String nomeCliente = clientes.stream().filter(c -> c.getCpf().equals(aluguel.getCpfCliente())).findFirst()
                    .map(Cliente::getNome).orElse("N/A");
            String descVeiculo = veiculos.stream().filter(v -> v.getPlaca().equals(aluguel.getPlacaVeiculo()))
                    .findFirst().map(Object::toString).orElse("N/A");

            if ("Ativo".equals(aluguel.getStatusAluguel())) {
                modeloAtivos.addRow(new Object[] { aluguel.getIdAluguel(), nomeCliente, descVeiculo,
                        aluguel.getDataSaida(), aluguel.getDataPrevistaDevolucao() });
            } else {
                // NOVO: Adiciona o valor da multa na linha da tabela de histórico
                String valorMultaFormatado = "---";
                if (aluguel.getValorMulta() > 0) {
                    valorMultaFormatado = String.format("R$ %.2f", aluguel.getValorMulta());
                }
                modeloHistorico.addRow(new Object[] {
                        aluguel.getIdAluguel(),
                        nomeCliente,
                        descVeiculo,
                        aluguel.getStatusAluguel(),
                        String.format("R$ %.2f", aluguel.getValorTotal()),
                        valorMultaFormatado
                });
            }
        }
    }

    private void registrarSaida() {
        // ... (código original sem alterações)
        Cliente cliente = (Cliente) comboClientes.getSelectedItem();
        Veiculo veiculo = (Veiculo) comboVeiculos.getSelectedItem();
        LocalDate dataSaida = pickerDataSaida.getDate();
        LocalDate dataPrevista = pickerDataPrevistaDevolucao.getDate();

        if (cliente == null || veiculo == null || dataSaida == null || dataPrevista == null) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (dataPrevista.isBefore(dataSaida) || dataPrevista.isEqual(dataSaida)) {
            JOptionPane.showMessageDialog(this, "A data de devolução prevista deve ser posterior à data de saída.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Aluguel novoAluguel = new Aluguel(veiculo.getPlaca(), cliente.getCpf(), dataSaida.format(formatter),
                dataPrevista.format(formatter));
        novoAluguel.setPrecoDiaria(veiculo.getPrecoDiaria());

        List<Aluguel> alugueis = GerenciadorAlugueis.carregarAlugueis();
        alugueis.add(novoAluguel);
        GerenciadorAlugueis.salvarAlugueis(alugueis);

        List<Veiculo> veiculos = GerenciadorVeiculos.carregarVeiculos();
        veiculos.stream().filter(v -> v.getPlaca().equals(veiculo.getPlaca())).findFirst().ifPresent(Veiculo::alugar);
        GerenciadorVeiculos.salvarVeiculos(veiculos);

        JOptionPane.showMessageDialog(this, "Saída registrada com sucesso!", "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
        atualizarDados();
    }

    private void registrarDevolucao() {
        // ... (código original sem alterações)
        int selectedRow = tabelaAlugueisAtivos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um aluguel ativo na tabela.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idAluguel = (int) modeloAtivos.getValueAt(selectedRow, 0);
        LocalDate dataDevolucao = pickerDataDevolucaoEfetiva.getDate();

        if (dataDevolucao == null) {
            JOptionPane.showMessageDialog(this, "A data de devolução é obrigatória.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Aluguel> alugueis = GerenciadorAlugueis.carregarAlugueis();
        Optional<Aluguel> aluguelOpt = alugueis.stream().filter(a -> a.getIdAluguel() == idAluguel).findFirst();

        if (aluguelOpt.isPresent()) {
            Aluguel aluguel = aluguelOpt.get();

            LocalDate dataSaida = LocalDate.parse(aluguel.getDataSaida(), formatter);
            if (dataDevolucao.isBefore(dataSaida)) {
                JOptionPane.showMessageDialog(this,
                        "A data de devolução não pode ser anterior à data de saída do aluguel.", "Erro de Validação",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Veiculo> veiculos = GerenciadorVeiculos.carregarVeiculos();
            Optional<Veiculo> veiculoOpt = veiculos.stream()
                    .filter(v -> v.getPlaca().equals(aluguel.getPlacaVeiculo())).findFirst();

            if (veiculoOpt.isPresent()) {
                Veiculo veiculo = veiculoOpt.get();
                // Assumindo que o método finalizarAluguel foi atualizado para calcular a multa
                // internamente
                aluguel.finalizarAluguel(dataDevolucao.format(formatter));
                veiculo.devolver();

                GerenciadorAlugueis.salvarAlugueis(alugueis);
                GerenciadorVeiculos.salvarVeiculos(veiculos);

                String msg = String.format("Devolução registrada! Valor Total: R$ %.2f", aluguel.getValorTotal());
                if (aluguel.getValorMulta() > 0) {
                    msg += String.format("\nMulta por atraso: R$ %.2f", aluguel.getValorMulta());
                }

                JOptionPane.showMessageDialog(this, msg, "Devolução Concluída", JOptionPane.INFORMATION_MESSAGE);

                atualizarDados();
            }
        }
    }
}