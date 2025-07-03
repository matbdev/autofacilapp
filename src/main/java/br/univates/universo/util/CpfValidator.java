package br.univates.universo.util;

import java.util.InputMismatchException;

/**
 * Classe utilitária para validar números de CPF.
 * Realiza a verificação completa dos dígitos verificadores.
 *
 * @version 1.0
 */
public final class CpfValidator {

    private CpfValidator() {
        // Classe utilitária não deve ser instanciada
    }

    /**
     * Valida um CPF.
     *
     * @param cpf O CPF a ser validado, pode conter máscara.
     * @return {@code true} se o CPF for válido, {@code false} caso contrário.
     */
    public static boolean isValid(String cpf) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        // Considera-se erro CPFs com todos os números iguais
        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            return false;
        }

        if (cpfLimpo.length() != 11) {
            return false;
        }

        try {
            char dig10, dig11;
            int sm, i, r, num, peso;

            // Cálculo do 1º Dígito Verificador
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (cpfLimpo.charAt(i) - 48); // Converte o 'char' para 'int'
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig10 = '0';
            } else {
                dig10 = (char) (r + 48);
            }

            // Cálculo do 2º Dígito Verificador
            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (cpfLimpo.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig11 = '0';
            } else {
                dig11 = (char) (r + 48);
            }

            // Verifica se os dígitos calculados conferem com os dígitos informados.
            return (dig10 == cpfLimpo.charAt(9)) && (dig11 == cpfLimpo.charAt(10));

        } catch (InputMismatchException erro) {
            return false;
        }
    }
}
