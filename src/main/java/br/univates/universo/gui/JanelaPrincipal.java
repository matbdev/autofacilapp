package br.univates.universo.gui;

import br.univates.universo.util.UIDesigner;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

/**
 * A janela principal da aplicação, com um design moderno.
 * Organiza a navegação lateral e o conteúdo principal usando um CardLayout.
 * Esta classe é o orquestrador central da UI.
 *
 * @version 3.1
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
            setIconImage(new FlatSVGIcon("icons/car.svg").getImage());
        } catch (Exception e) {
            System.err.println(
                    "Ícone 'icons/car.svg' não encontrado. Verifique o caminho em 'src/main/resources/icons'.");
        }
        setSize(1366, 768);
        setMinimumSize(new Dimension(1200, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Define o layout principal da janela.
        setLayout(new BorderLayout());

        // Adiciona o painel de navegação à esquerda.
        add(createPainelNavegacao(), BorderLayout.WEST);

        // Cria o painel que conterá as diferentes "telas" da aplicação.
        cardLayout = new CardLayout();
        painelConteudo = new JPanel(cardLayout);

        // Inicializa todos os painéis de gerenciamento.
        painelDashboard = new PainelDashboard();
        painelVeiculos = new PainelGerenciamentoVeiculos();
        painelClientes = new PainelGerenciamentoClientes();
        // A referência ao painel de veículos é passada, mas não usada para chamadas de
        // método diretas.
        painelAlugueis = new PainelGerenciamentoAlugueis(painelVeiculos);

        // Adiciona cada painel ao CardLayout com um nome único.
        painelConteudo.add(painelDashboard, "DASHBOARD");
        painelConteudo.add(painelVeiculos, "VEICULOS");
        painelConteudo.add(painelClientes, "CLIENTES");
        painelConteudo.add(painelAlugueis, "ALUGUEIS");

        // Adiciona o painel de conteúdo ao centro da janela.
        add(painelConteudo, BorderLayout.CENTER);
    }

    /**
     * Cria e configura o painel de navegação lateral.
     * 
     * @return Um JPanel configurado como menu lateral.
     */
    private JPanel createPainelNavegacao() {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(new Color(24, 24, 27)); // Fundo escuro para o menu
        navPanel.setBorder(new EmptyBorder(10, 5, 10, 5));
        navPanel.setPreferredSize(new Dimension(250, 0));

        JPanel buttonsPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        buttonsPanel.setOpaque(false); // Transparente para usar a cor do navPanel

        // Título da aplicação no topo do menu
        JLabel titleLabel = new JLabel("AutoFácil", new FlatSVGIcon("icons/car.svg", 32, 32), JLabel.CENTER);
        titleLabel.setFont(UIDesigner.FONT_TITLE.deriveFont(24f));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(10, 10, 20, 10));
        buttonsPanel.add(titleLabel);
        buttonsPanel.add(new JSeparator());

        // Criação dos botões de navegação
        JButton btnDashboard = UIDesigner.createNavButton("Dashboard", "icons/dashboard.svg");
        JButton btnVeiculos = UIDesigner.createNavButton("Veículos", "icons/vehicle.svg");
        JButton btnClientes = UIDesigner.createNavButton("Clientes", "icons/customer.svg");
        JButton btnAlugueis = UIDesigner.createNavButton("Aluguéis", "icons/rental.svg");

        // Ações dos botões para trocar os painéis visíveis.
        // A lógica de atualização de dados está centralizada aqui.
        btnDashboard.addActionListener(e -> {
            painelDashboard.atualizarDados();
            cardLayout.show(painelConteudo, "DASHBOARD");
        });
        btnVeiculos.addActionListener(e -> {
            // CORREÇÃO: Esta chamada depende do método público carregarVeiculos()
            // na classe PainelGerenciamentoVeiculos.
            painelVeiculos.carregarVeiculos();
            cardLayout.show(painelConteudo, "VEICULOS");
        });
        btnClientes.addActionListener(e -> {
            // CORREÇÃO: Esta chamada depende do método público carregarClientes()
            // na classe PainelGerenciamentoClientes.
            painelClientes.carregarClientes();
            cardLayout.show(painelConteudo, "CLIENTES");
        });
        btnAlugueis.addActionListener(e -> {
            painelAlugueis.atualizarDados();
            cardLayout.show(painelConteudo, "ALUGUEIS");
        });

        // Adiciona os botões ao painel
        buttonsPanel.add(btnDashboard);
        buttonsPanel.add(btnVeiculos);
        buttonsPanel.add(btnClientes);
        buttonsPanel.add(btnAlugueis);

        navPanel.add(buttonsPanel, BorderLayout.NORTH);
        return navPanel;
    }
}
