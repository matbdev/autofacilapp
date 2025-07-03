package br.univates.universo.gui;

import java.awt.BorderLayout;
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
 * @version 3.1
 */
public class PainelGerenciamentoAlugueis extends JPanel {

    private JComboBox<Cliente> comboClientes;
    private JComboBox<Veiculo> comboVeiculos;
    private DatePicker pickerDataSaida, pickerDataPrevistaDevolucao, pickerDataDevolucaoEfetiva;
    private final JTable tabelaAlugueisAtivos, tabelaHistorico;
    private final DefaultTableModel modeloAtivos, modeloHistorico;
    // A referência ao painel de veículos ainda é útil, mas não para o método que
    // causava erro.
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

        modeloAtivos = new DefaultTableModel(new String[] { "ID", "Cliente", "Veículo", "Saída", "Previsão" }, 0);
        tabelaAlugueisAtivos = new JTable(modeloAtivos);
        tabelaAlugueisAtivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollAtivos = new JScrollPane(tabelaAlugueisAtivos);

        modeloHistorico = new DefaultTableModel(new String[] { "ID", "Cliente", "Veículo", "Status", "Valor Total" },
                0);
        tabelaHistorico = new JTable(modeloHistorico);
        JScrollPane scrollHistorico = new JScrollPane(tabelaHistorico);

        painelTabelas.addTab("Aluguéis Ativos", scrollAtivos);
        painelTabelas.addTab("Histórico de Locações", scrollHistorico);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, painelFormularios, painelTabelas);
        splitPane.setDividerLocation(280);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setOpaque(false);

        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Cria o card para registrar a saída de um veículo.
     */
    private JPanel createPainelSaida() {
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

    /**
     * Cria o card para registrar a devolução de um veículo.
     */
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
        painel.add(infoLabel, BorderLayout.CENTER);

        JButton btnRegistrar = UIDesigner.createPrimaryButton("Registrar Devolução", "icons/check.svg");
        btnRegistrar.addActionListener(e -> registrarDevolucao());

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(formPanel, BorderLayout.NORTH);
        southPanel.add(btnRegistrar, BorderLayout.CENTER);
        painel.add(southPanel, BorderLayout.SOUTH);

        return painel;
    }

    /**
     * Carrega e atualiza todos os dados exibidos no painel (combos e tabelas).
     */
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
                modeloHistorico.addRow(new Object[] { aluguel.getIdAluguel(), nomeCliente, descVeiculo,
                        aluguel.getStatusAluguel(), String.format("R$ %.2f", aluguel.getValorTotal()) });
            }
        }
    }

    /**
     * Valida e executa o processo de registrar a saída de um veículo.
     */
    private void registrarSaida() {
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
        // A linha abaixo foi removida para corrigir o erro de compilação.
        // A atualização do painel de veículos agora é gerenciada pela JanelaPrincipal.
        // painelVeiculosRef.carregarVeiculos();
    }

    /**
     * Valida e executa o processo de registrar a devolução de um veículo.
     */
    private void registrarDevolucao() {
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
                aluguel.finalizarAluguel(dataDevolucao.format(formatter), veiculo.getPrecoDiaria());
                veiculo.devolver();

                GerenciadorAlugueis.salvarAlugueis(alugueis);
                GerenciadorVeiculos.salvarVeiculos(veiculos);

                String msg = String.format("Devolução registrada! Valor Total: R$ %.2f", aluguel.getValorTotal());
                JOptionPane.showMessageDialog(this, msg, "Devolução Concluída", JOptionPane.INFORMATION_MESSAGE);

                atualizarDados();
                // A linha abaixo foi removida para corrigir o erro de compilação.
                // A atualização do painel de veículos agora é gerenciada pela JanelaPrincipal.
                // painelVeiculosRef.carregarVeiculos();
            }
        }
    }
}
