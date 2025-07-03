package br.univates.universo.util;

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * DocumentFilter para formatar e validar campos de texto de Placa de Veículo.
 * Formata a placa no padrão Mercosul (LLLNLNN) ou antigo (LLLNNNN),
 * convertendo letras para maiúsculas e validando a digitação.
 *
 * @version 2.0
 */
public class PlacaDocumentFilter extends DocumentFilter {

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {

        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
        newText = newText.toUpperCase().replaceAll("-", "");

        if (!isValid(newText)) {
            Toolkit.getDefaultToolkit().beep(); // Emite um som de erro
            return;
        }

        StringBuilder formatted = new StringBuilder(newText);
        if (formatted.length() > 3) {
            formatted.insert(3, '-');
        }

        super.replace(fb, 0, fb.getDocument().getLength(), formatted.toString(), attrs);
    }

    /**
     * Valida a string da placa (sem máscara) de acordo com as regras.
     *
     * @param placa A string da placa, sem máscara e em maiúsculas.
     * @return true se a placa for válida até o momento, false caso contrário.
     */
    private boolean isValid(String placa) {
        if (placa.length() > 7) {
            return false;
        }

        for (int i = 0; i < placa.length(); i++) {
            char c = placa.charAt(i);
            switch (i) {
                case 0, 1, 2 -> {
                    // Posições 1, 2, 3: Devem ser letras
                    if (!Character.isLetter(c))
                        return false;
                }
                case 3 -> {
                    // Posição 4: Deve ser número
                    if (!Character.isDigit(c))
                        return false;
                }
                case 4 -> {
                    // Posição 5: Pode ser letra ou número
                    if (!Character.isLetterOrDigit(c))
                        return false;
                }
                case 5, 6 -> {
                    // Posições 6, 7: Devem ser números
                    if (!Character.isDigit(c))
                        return false;
                }
            }
            // Regra específica para o 5º caractere (posição 4)
            // Se o 5º caractere for uma letra, a placa é Mercosul.
            // Se for um número, a placa é do modelo antigo.
            if (placa.length() > 4) {
                if (Character.isLetter(placa.charAt(4))) { // Modelo Mercosul (LLLNLNN)
                    if (i > 4 && Character.isLetter(c))
                        return false; // Posições 6 e 7 não podem ser letras
                } else { // Modelo antigo (LLLNNNN)
                    if (i > 4 && !Character.isDigit(c))
                        return false; // Posições 6 e 7 devem ser números
                }
            }
        }
        return true;
    }
}
