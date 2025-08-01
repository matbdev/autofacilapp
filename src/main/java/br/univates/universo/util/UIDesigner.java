package br.univates.universo.util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image; // Importação adicionada
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon; // Importação adicionada
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

// A importação do FlatSVGIcon foi removida pois não é mais necessária.
// import com.formdev.flatlaf.extras.FlatSVGIcon;

/**
 * Classe utilitária para centralizar o design e estilo da aplicação.
 * Fornece cores, fontes e métodos para criar componentes estilizados.
 * * @version 2.0 (Atualizada para usar ícones PNG)
 */
public final class UIDesigner {

    // Paleta de Cores (Tema Escuro)
    public static final Color COLOR_BACKGROUND = new Color(24, 24, 27);
    public static final Color COLOR_FOREGROUND = new Color(244, 244, 245);
    public static final Color COLOR_CARD_BACKGROUND = new Color(39, 39, 42);
    public static final Color COLOR_PRIMARY = new Color(59, 130, 246);
    public static final Color COLOR_ACCENT = new Color(234, 179, 8);
    public static final Color COLOR_BORDER = new Color(63, 63, 70);
    public static final Color COLOR_TEXT_SECONDARY = new Color(161, 161, 170);
    public static final Color COLOR_SUCCESS = new Color(34, 197, 94);
    public static final Color COLOR_DANGER = new Color(239, 68, 68);

    // Fontes
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    // Bordas
    public static final Border BORDER_CARD = new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, COLOR_BORDER),
            new EmptyBorder(15, 15, 15, 15));
    public static final Border BORDER_CARD_HOVER = new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, COLOR_PRIMARY),
            new EmptyBorder(15, 15, 15, 15));

    private UIDesigner() {
    }

    /**
     * NOVO MÉTODO: Carrega um ícone PNG do caminho especificado e o redimensiona.
     *
     * @param caminhoIcone O caminho para o arquivo do ícone (ex: "icons/add.png").
     * @param largura      A largura desejada para o ícone.
     * @param altura       A altura desejada para o ícone.
     * @return Um objeto ImageIcon redimensionado, ou null se o ícone não for
     *         encontrado.
     */
    public static ImageIcon redimensionarIconePNG(String caminhoIcone, int largura, int altura) {
        try {
            ImageIcon icon = new ImageIcon(UIDesigner.class.getClassLoader().getResource(caminhoIcone));
            Image img = icon.getImage();
            Image imgRedimensionada = img.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
            return new ImageIcon(imgRedimensionada);
        } catch (Exception e) {
            System.err.println("Ícone PNG não encontrado: " + caminhoIcone);
            return null; // Retorna nulo se o recurso não for encontrado
        }
    }

    public static JButton createPrimaryButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(COLOR_PRIMARY);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        if (iconPath != null) {
            // ALTERADO: Usa o novo método para redimensionar ícones PNG
            button.setIcon(redimensionarIconePNG(iconPath, 16, 16));
        }
        return button;
    }

    public static JButton createDangerButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(COLOR_DANGER);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        if (iconPath != null) {
            // ALTERADO: Usa o novo método para redimensionar ícones PNG
            button.setIcon(redimensionarIconePNG(iconPath, 16, 16));
        }
        return button;
    }

    public static void addHoverEffect(JComponent component) {
        component.setCursor(new Cursor(Cursor.HAND_CURSOR));
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                component.setBorder(BORDER_CARD_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                component.setBorder(BORDER_CARD);
            }
        });
    }

    public static JButton createNavButton(String text, String iconPath) {
        JButton button = new JButton(text);
        // ALTERADO: Usa o novo método para redimensionar ícones PNG
        button.setIcon(redimensionarIconePNG(iconPath, 20, 20));
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFont(FONT_SUBTITLE);
        button.setForeground(COLOR_TEXT_SECONDARY);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 15, 10, 15));
        button.setFocusable(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(COLOR_FOREGROUND);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(COLOR_TEXT_SECONDARY);
            }
        });
        return button;
    }
}