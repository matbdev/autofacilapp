package br.univates.universo.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import br.univates.universo.util.UIDesigner;

/**
 * A janela principal da aplicação, com um design moderno.
 * Organiza a navegação lateral e o conteúdo principal usando um CardLayout.
 * Esta classe é o orquestrador central da UI.
 *
 * @version 3.2
 */
public class JanelaPrincipal extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel painelConteudo;
    private final PainelGerenciamentoVeiculos painelVeiculos;
    private final PainelGerenciamentoClientes painelClientes;
    private final PainelGerenciamentoAlugueis painelAlugueis;
    private final PainelDashboard painelDashboard;

    public JanelaPrincipal() {
        setTitle("AutoFácil - Sistema de Locadora");
        // Tenta carregar o ícone da aplicação.
        try {
            // Alterado para usar ImageIcon e carregar um PNG
            ImageIcon appIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/car.png"));
            setIconImage(appIcon.getImage());
        } catch (Exception e) {
            System.err.println(
                    "Ícone 'icons/car.png' não encontrado. Verifique o caminho nos recursos do projeto.");
        }
        setSize(1366, 768);
        setMinimumSize(new Dimension(1200, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(createPainelNavegacao(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        painelConteudo = new JPanel(cardLayout);

        painelDashboard = new PainelDashboard();
        painelVeiculos = new PainelGerenciamentoVeiculos();
        painelClientes = new PainelGerenciamentoClientes();
        painelAlugueis = new PainelGerenciamentoAlugueis(painelVeiculos);

        painelConteudo.add(painelDashboard, "DASHBOARD");
        painelConteudo.add(painelVeiculos, "VEICULOS");
        painelConteudo.add(painelClientes, "CLIENTES");
        painelConteudo.add(painelAlugueis, "ALUGUEIS");

        add(painelConteudo, BorderLayout.CENTER);
    }

    private JPanel createPainelNavegacao() {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(new Color(24, 24, 27));
        navPanel.setBorder(new EmptyBorder(10, 5, 10, 5));
        navPanel.setPreferredSize(new Dimension(250, 0));

        JPanel buttonsPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        buttonsPanel.setOpaque(false);

        // Título da aplicação no topo do menu - Ícone PNG redimensionado
        JLabel titleLabel = new JLabel("AutoFácil", UIDesigner.redimensionarIconePNG("icons/car.png", 32, 32),
                JLabel.CENTER);
        titleLabel.setFont(UIDesigner.FONT_TITLE.deriveFont(24f));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(10, 10, 20, 10));
        buttonsPanel.add(titleLabel);
        buttonsPanel.add(new JSeparator());

        // Criação dos botões de navegação com ícones PNG
        JButton btnDashboard = UIDesigner.createNavButton("Dashboard", "icons/dashboard.png");
        JButton btnVeiculos = UIDesigner.createNavButton("Veículos", "icons/vehicle.png");
        JButton btnClientes = UIDesigner.createNavButton("Clientes", "icons/customer.png");
        JButton btnAlugueis = UIDesigner.createNavButton("Aluguéis", "icons/rental.png");

        btnDashboard.addActionListener(e -> {
            painelDashboard.atualizarDados();
            cardLayout.show(painelConteudo, "DASHBOARD");
        });
        btnVeiculos.addActionListener(e -> {
            painelVeiculos.carregarVeiculos();
            cardLayout.show(painelConteudo, "VEICULOS");
        });
        btnClientes.addActionListener(e -> {
            painelClientes.carregarClientes();
            cardLayout.show(painelConteudo, "CLIENTES");
        });
        btnAlugueis.addActionListener(e -> {
            painelAlugueis.atualizarDados();
            cardLayout.show(painelConteudo, "ALUGUEIS");
        });

        buttonsPanel.add(btnDashboard);
        buttonsPanel.add(btnVeiculos);
        buttonsPanel.add(btnClientes);
        buttonsPanel.add(btnAlugueis);

        navPanel.add(buttonsPanel, BorderLayout.NORTH);
        return navPanel;
    }
}