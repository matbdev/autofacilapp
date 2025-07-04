package br.univates.universo;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;

import br.univates.universo.gui.JanelaPrincipal;

/**
 * Classe principal que inicia a aplicação "AutoFácil" com a nova interface.
 * Responsável por configurar o tema da interface gráfica (Look and Feel)
 * e instanciar a janela principal.
 *
 * @version 3.0
 */
public class Main {
    /**
     * Ponto de entrada da aplicação (main).
     *
     * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        // Configura o tema FlatLaf Darcula (escuro) para toda a aplicação.
        FlatLaf.setup(new FlatDarculaLaf());

        // Melhora a renderização de fontes (anti-aliasing) para um visual mais suave.
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Traduz os botões e textos padrão do JOptionPane para Português.
        UIManager.put("OptionPane.yesButtonText", "Sim");
        UIManager.put("OptionPane.noButtonText", "Não");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "OK");

        // Garante que a GUI seja criada e atualizada na Event Dispatch Thread (EDT),
        // que é a prática recomendada para aplicações Swing.
        SwingUtilities.invokeLater(() -> {
            JanelaPrincipal janela = new JanelaPrincipal();
            janela.setVisible(true);
        });
    }
}
