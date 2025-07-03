package br.univates.universo.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * DocumentFilter para validar campos de texto de Nomes.
 * Permite apenas letras e espaços, removendo números e outros caracteres
 * especiais.
 */
public class NomeDocumentFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string == null)
            return;
        super.insertString(fb, offset, string.replaceAll("[^\\p{L}\\s]", ""), attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (text == null)
            return;
        super.replace(fb, offset, length, text.replaceAll("[^\\p{L}\\s]", ""), attrs);
    }
}
