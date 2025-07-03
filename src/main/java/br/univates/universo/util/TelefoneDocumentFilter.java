package br.univates.universo.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * DocumentFilter para formatar e validar campos de texto de Telefone.
 * Garante que apenas números sejam inseridos e formata o telefone
 * nos padrões (00) 0000-0000 ou (00) 00000-0000.
 */
public class TelefoneDocumentFilter extends DocumentFilter {
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
        if (numbersOnly.length() > 0) {
            formatted.append('(').append(numbersOnly.substring(0, Math.min(2, numbersOnly.length())));
        }
        if (numbersOnly.length() > 2) {
            formatted.append(") ");
            if (numbersOnly.length() <= 10) { // Fixo
                formatted.append(numbersOnly.substring(2, Math.min(6, numbersOnly.length())));
                if (numbersOnly.length() > 6) {
                    formatted.append('-').append(numbersOnly.substring(6, Math.min(10, numbersOnly.length())));
                }
            } else { // Celular
                formatted.append(numbersOnly.substring(2, Math.min(7, numbersOnly.length())));
                if (numbersOnly.length() > 7) {
                    formatted.append('-').append(numbersOnly.substring(7, Math.min(11, numbersOnly.length())));
                }
            }
        }

        fb.replace(0, fb.getDocument().getLength(), formatted.toString(), attrs);
    }
}
