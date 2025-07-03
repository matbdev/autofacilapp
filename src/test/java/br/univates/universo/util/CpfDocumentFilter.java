package br.univates.universo.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * DocumentFilter para formatar e validar campos de texto de CPF.
 * Garante que apenas números sejam inseridos e formata o CPF automaticamente
 * no padrão 000.000.000-00.
 */
public class CpfDocumentFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string == null)
            return;
        replace(fb, offset, 0, string, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        currentText = currentText.substring(0, offset) + text + currentText.substring(offset + length);

        String numbersOnly = currentText.replaceAll("[^0-9]", "");

        if (numbersOnly.length() > 11) {
            return; // Impede a inserção de mais de 11 dígitos
        }

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < numbersOnly.length(); i++) {
            formatted.append(numbersOnly.charAt(i));
            if (i == 2 || i == 5) {
                if (i != numbersOnly.length() - 1) {
                    formatted.append('.');
                }
            } else if (i == 8) {
                if (i != numbersOnly.length() - 1) {
                    formatted.append('-');
                }
            }
        }

        fb.replace(0, fb.getDocument().getLength(), formatted.toString(), attrs);
    }
}
