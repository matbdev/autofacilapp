package br.univates.universo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import br.univates.universo.data.GerenciadorAlugueis;
import br.univates.universo.data.GerenciadorClientes;
import br.univates.universo.data.GerenciadorVeiculos;
import br.univates.universo.util.UIDesigner;

/**
 * Painel que funciona como a tela inicial (Dashboard) da aplicação.
 * Exibe cartões com estatísticas chave, como veículos disponíveis,
 * aluguéis ativos e total de clientes.
 *
 * @version 3.1
 */
public class PainelDashboard extends JPanel {

        private final JLabel lblVeiculosDisponiveis, lblAlugueisAtivos, lblClientesCadastrados;

        public PainelDashboard() {
                super(new BorderLayout(20, 20));
                setBorder(new EmptyBorder(20, 30, 20, 30));
                setBackground(UIDesigner.COLOR_BACKGROUND);

                JLabel titleLabel = new JLabel("Dashboard");
                titleLabel.setFont(UIDesigner.FONT_TITLE);
                titleLabel.setForeground(UIDesigner.COLOR_FOREGROUND);
                add(titleLabel, BorderLayout.NORTH);

                JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
                statsPanel.setOpaque(false);

                lblVeiculosDisponiveis = new JLabel("0");
                lblAlugueisAtivos = new JLabel("0");
                lblClientesCadastrados = new JLabel("0");

                // Alterado para usar caminhos de ícones .png
                statsPanel.add(createStatCard("Veículos Disponíveis", lblVeiculosDisponiveis, "icons/vehicle.png"));
                statsPanel.add(createStatCard("Aluguéis Ativos", lblAlugueisAtivos, "icons/rental.png"));
                statsPanel.add(createStatCard("Clientes Cadastrados", lblClientesCadastrados, "icons/customer.png"));

                add(statsPanel, BorderLayout.CENTER);
        }

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
         * Utiliza o método de redimensionamento de UIDesigner.
         *
         * @param title      O título do cartão (ex: "Veículos Disponíveis").
         * @param valueLabel O JLabel que exibirá o valor numérico.
         * @param iconPath   O caminho para o ícone PNG.
         * @return Um JPanel estilizado como um cartão de estatística.
         */
        private JPanel createStatCard(String title, JLabel valueLabel, String iconPath) {
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

                // Carrega e redimensiona o ícone PNG
                JLabel iconLabel = new JLabel(UIDesigner.redimensionarIconePNG(iconPath, 48, 48));
                iconLabel.setBorder(new EmptyBorder(0, 0, 0, 15));

                card.add(textPanel, BorderLayout.CENTER);
                card.add(iconLabel, BorderLayout.EAST);

                return card;
        }
}