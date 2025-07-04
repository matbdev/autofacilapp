package br.univates.universo.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import br.univates.universo.data.GerenciadorAlugueis;
import br.univates.universo.data.GerenciadorClientes;
import br.univates.universo.data.GerenciadorVeiculos;
import br.univates.universo.util.UIDesigner;

/**
 * Painel que funciona como a tela inicial (Dashboard) da aplicação.
 * Exibe cartões com estatísticas chave, como veículos disponíveis,
 * aluguéis ativos e total de clientes.
 *
 * @version 3.0
 */
public class PainelDashboard extends JPanel {

        private final JLabel lblVeiculosDisponiveis, lblAlugueisAtivos, lblClientesCadastrados;

        public PainelDashboard() {
                super(new BorderLayout(20, 20));
                setBorder(new EmptyBorder(20, 30, 20, 30));
                setBackground(UIDesigner.COLOR_BACKGROUND);

                // Título principal do painel
                JLabel titleLabel = new JLabel("Dashboard");
                titleLabel.setFont(UIDesigner.FONT_TITLE);
                titleLabel.setForeground(UIDesigner.COLOR_FOREGROUND);
                add(titleLabel, BorderLayout.NORTH);

                // Painel para organizar os cartões de estatísticas
                JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
                statsPanel.setOpaque(false); // Fundo transparente

                // Inicializa os labels que conterão os números
                lblVeiculosDisponiveis = new JLabel("0");
                lblAlugueisAtivos = new JLabel("0");
                lblClientesCadastrados = new JLabel("0");

                // Cria e adiciona cada cartão de estatística
                statsPanel.add(createStatCard("Veículos Disponíveis", lblVeiculosDisponiveis, "icons/vehicle.svg",
                                UIDesigner.COLOR_SUCCESS));
                statsPanel.add(createStatCard("Aluguéis Ativos", lblAlugueisAtivos, "icons/rental.svg",
                                UIDesigner.COLOR_ACCENT));
                statsPanel.add(createStatCard("Clientes Cadastrados", lblClientesCadastrados, "icons/customer.svg",
                                UIDesigner.COLOR_PRIMARY));

                add(statsPanel, BorderLayout.CENTER);
        }

        /**
         * Carrega os dados mais recentes dos arquivos e atualiza os valores nos
         * cartões.
         */
        public void atualizarDados() {
                long veiculosDisponiveis = GerenciadorVeiculos.carregarVeiculos().stream()
                                .filter(v -> "Disponível".equals(v.getStatus())).count();
                long alugueisAtivos = GerenciadorAlugueis.carregarAlugueis().stream()
                                .filter(a -> "Ativo".equals(a.getStatusAluguel())).count();
                long clientesCadastrados = GerenciadorClientes.carregarClientes().size();

                lblVeiculosDisponiveis.setText(String.valueOf(veiculosDisponiveis));
                lblAlugueisAtivos.setText(String.valueOf(alugueisAtivos));
                lblClientesCadastrados.setText(String.valueOf(clientesCadastrados));
        }

        /**
         * Método fábrica para criar um cartão de estatística padronizado.
         *
         * @param title      O título do cartão (ex: "Veículos Disponíveis").
         * @param valueLabel O JLabel que exibirá o valor numérico.
         * @param iconPath   O caminho para o ícone SVG.
         * @param iconColor  A cor a ser aplicada ao ícone.
         * @return Um JPanel estilizado como um cartão de estatística.
         */
        private JPanel createStatCard(String title, JLabel valueLabel, String iconPath, Color iconColor) {
                JPanel card = new JPanel(new BorderLayout(10, 5));
                card.setBackground(UIDesigner.COLOR_CARD_BACKGROUND);
                card.setBorder(UIDesigner.BORDER_CARD);
                card.setPreferredSize(new Dimension(280, 130));

                JLabel titleLabel = new JLabel(title);
                titleLabel.setFont(UIDesigner.FONT_SUBTITLE);
                titleLabel.setForeground(UIDesigner.COLOR_TEXT_SECONDARY);

                valueLabel.setFont(UIDesigner.FONT_TITLE.deriveFont(Font.BOLD, 48f));
                valueLabel.setForeground(UIDesigner.COLOR_FOREGROUND);

                JPanel textPanel = new JPanel(new BorderLayout());
                textPanel.setOpaque(false);
                textPanel.add(titleLabel, BorderLayout.NORTH);
                textPanel.add(valueLabel, BorderLayout.CENTER);

                // Carrega e colore o ícone SVG
                FlatSVGIcon icon = new FlatSVGIcon(iconPath);
                icon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> iconColor));
                JLabel iconLabel = new JLabel(icon.derive(48, 48));
                iconLabel.setBorder(new EmptyBorder(0, 0, 0, 15));

                card.add(textPanel, BorderLayout.CENTER);
                card.add(iconLabel, BorderLayout.EAST);

                return card;
        }
}
