package br.univates.universo.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * Janela principal da aplicação (JFrame).
 * <p>
 * Organiza os diferentes painéis de gerenciamento (Veículos, Clientes,
 * Aluguéis) em um {@link JTabbedPane} (abas), servindo como a estrutura
 * central da interface do usuário.
 *
 * @version 2.0
 */
public class JanelaPrincipal extends JFrame {

    /**
     * Construtor da JanelaPrincipal.
     * Configura o título, tamanho e comportamento de fechamento da janela.
     * Inicializa e adiciona os painéis de gerenciamento às abas.
     */
    public JanelaPrincipal() {
        setTitle("AutoFácil - Sistema de Locadora");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // 1. Cria os painéis de gerenciamento
        PainelGerenciamentoVeiculos painelVeiculos = new PainelGerenciamentoVeiculos();
        PainelGerenciamentoClientes painelClientes = new PainelGerenciamentoClientes();
        // O painel de aluguéis precisa de uma referência ao de veículos para atualizar
        // a tabela de frota
        PainelGerenciamentoAlugueis painelAlugueis = new PainelGerenciamentoAlugueis(painelVeiculos);

        int iconSize = 24;

        // 2. Adiciona os painéis às abas com ícones (CAMINHOS CORRIGIDOS)
        abas.addTab("Gerenciar Veículos", createIcon("/images/car.png", iconSize, iconSize), painelVeiculos,
                "Cadastro e gestão da frota de veículos");
        abas.addTab("Gerenciar Clientes", createIcon("/images/customer.png", iconSize, iconSize), painelClientes,
                "Cadastro e gestão de clientes");
        abas.addTab("Gerenciar Aluguéis", createIcon("/images/rental.png", iconSize, iconSize), painelAlugueis,
                "Registro de saída e devolução de veículos");

        // 3. Adiciona um listener para atualizar os dados sempre que uma aba for
        // selecionada
        abas.addChangeListener(e -> {
            Component painelSelecionado = abas.getSelectedComponent();

            // Utilizando switch com pattern matching para um código mais limpo
            switch (painelSelecionado) {
                case PainelGerenciamentoAlugueis painel -> painel.atualizarDados();
                case PainelGerenciamentoVeiculos painel -> painel.carregarVeiculosNaTabela();
                case PainelGerenciamentoClientes painel -> painel.carregarClientesNaTabela();
                // O caso 'default' é opcional se você não precisar tratar outros tipos de
                // painéis.
                default -> {
                    // Nenhuma ação necessária para outros tipos de painéis.
                }
            }
        });

        add(abas);
    }

    /**
     * Cria e redimensiona um ImageIcon a partir de um caminho de recurso.
     *
     * @param path   O caminho para o arquivo do ícone (ex: "/icons/car.png").
     * @param width  A largura desejada para o ícone.
     * @param height A altura desejada para o ícone.
     * @return um ImageIcon redimensionado ou null se o recurso não for encontrado.
     */
    private ImageIcon createIcon(String path, int width, int height) {
        // O método getResource precisa do caminho correto a partir da raiz dos
        // recursos.
        URL resourceUrl = getClass().getResource(path);
        if (resourceUrl != null) {
            ImageIcon originalIcon = new ImageIcon(resourceUrl);
            Image resizedImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        } else {
            // Esta mensagem de erro agora mostrará o caminho correto que está sendo
            // procurado.
            System.err.println("Aviso: O recurso do ícone não foi encontrado em: " + path);
            return null;
        }
    }
}
