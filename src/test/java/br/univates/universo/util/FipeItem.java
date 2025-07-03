package br.univates.universo.util;

import java.util.Objects;

/**
 * Representa um item genérico retornado pela API FIPE (Marca, Modelo ou Ano).
 * <p>
 * Esta classe armazena um código e um nome, que são os dados padrão para
 * a maioria dos endpoints da API. O método `toString` é sobrescrito para
 * exibir apenas o nome, facilitando o uso em componentes Swing como JComboBox.
 *
 * @version 1.0
 */
public class FipeItem {
    private final String code;
    private final String name;

    public FipeItem(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FipeItem fipeItem = (FipeItem) o;
        return Objects.equals(code, fipeItem.code) && Objects.equals(name, fipeItem.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name);
    }
}
