package br.univates.universo.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import br.univates.universo.core.Veiculo;

/**
 * Gerenciador de dados para a entidade Veiculo.
 * <p>
 * Esta classe é responsável por intermediar as operações de carga e salvamento
 * de veículos, utilizando o {@link JsonDataManager} para a persistência
 * em arquivo JSON.
 *
 * @version 1.0
 */
public final class GerenciadorVeiculos {

    private static final String ARQUIVO_VEICULOS = "veiculos.json";
    private static final Type TIPO_LISTA_VEICULOS = new TypeToken<ArrayList<Veiculo>>() {
    }.getType();

    private GerenciadorVeiculos() {
    }

    /**
     * Carrega a lista de todos os veículos do arquivo JSON.
     *
     * @return Uma {@link List} de {@link Veiculo}.
     */
    public static List<Veiculo> carregarVeiculos() {
        return JsonDataManager.carregarDados(ARQUIVO_VEICULOS, TIPO_LISTA_VEICULOS);
    }

    /**
     * Salva a lista de veículos no arquivo JSON.
     *
     * @param veiculos A {@link List} de {@link Veiculo} a ser salva.
     */
    public static void salvarVeiculos(List<Veiculo> veiculos) {
        JsonDataManager.salvarDados(veiculos, ARQUIVO_VEICULOS);
    }
}
