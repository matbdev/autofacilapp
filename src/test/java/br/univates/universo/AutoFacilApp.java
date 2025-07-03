package br.univates.universo;

import br.univates.universo.gui.JanelaPrincipal; // CORREÇÃO: Import ajustado para o novo pacote.

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Classe principal que inicia a aplicação "AutoFácil Java Edition".
 * Responsável por configurar o tema da interface gráfica e instanciar a janela
 * principal.
 *
 * @version 2.1
 * @author Mateus Carniel Brambilla
 */
public class AutoFacilApp {
    /**
     * Ponto de entrada da aplicação (main).
     *
     * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        // Tenta aplicar o Look and Feel FlatLaf para uma interface moderna.
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Falha ao carregar o Look and Feel FlatLaf: " + e.getMessage());
        }

        // Traduz os botões e textos padrão do JOptionPane para Português.
        UIManager.put("OptionPane.yesButtonText", "Sim");
        UIManager.put("OptionPane.noButtonText", "Não");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "OK");

        // Garante que a GUI seja criada e atualizada na Event Dispatch Thread (EDT).
        SwingUtilities.invokeLater(() -> {
            JanelaPrincipal janela = new JanelaPrincipal();
            janela.setVisible(true);
        });
    }
}
