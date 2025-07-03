package br.univates.universo.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import br.univates.universo.core.Aluguel;

/**
 * Gerenciador de dados para a entidade Aluguel.
 * <p>
 * Esta classe é responsável por intermediar as operações de carga e salvamento
 * de aluguéis, utilizando o {@link JsonDataManager} para a persistência
 * em arquivo JSON.
 *
 * @version 1.0
 */
public final class GerenciadorAlugueis {

    private static final String ARQUIVO_ALUGUEIS = "alugueis.json";
    private static final Type TIPO_LISTA_ALUGUEIS = new TypeToken<ArrayList<Aluguel>>() {
    }.getType();

    private GerenciadorAlugueis() {
    }

    /**
     * Carrega a lista de todos os aluguéis do arquivo JSON.
     *
     * @return Uma {@link List} de {@link Aluguel}.
     */
    public static List<Aluguel> carregarAlugueis() {
        return JsonDataManager.carregarDados(ARQUIVO_ALUGUEIS, TIPO_LISTA_ALUGUEIS);
    }

    /**
     * Salva a lista de aluguéis no arquivo JSON.
     *
     * @param alugueis A {@link List} de {@link Aluguel} a ser salva.
     */
    public static void salvarAlugueis(List<Aluguel> alugueis) {
        JsonDataManager.salvarDados(alugueis, ARQUIVO_ALUGUEIS);
    }
}
