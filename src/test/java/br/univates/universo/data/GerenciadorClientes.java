package br.univates.universo.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import br.univates.universo.core.Cliente;

/**
 * Gerenciador de dados para a entidade Cliente.
 * <p>
 * Esta classe é responsável por intermediar as operações de carga e salvamento
 * de clientes, utilizando o {@link JsonDataManager} para a persistência
 * em arquivo JSON.
 *
 * @version 1.0
 */
public final class GerenciadorClientes {

    private static final String ARQUIVO_CLIENTES = "clientes.json";
    private static final Type TIPO_LISTA_CLIENTES = new TypeToken<ArrayList<Cliente>>() {
    }.getType();

    private GerenciadorClientes() {
    }

    /**
     * Carrega a lista de todos os clientes do arquivo JSON.
     *
     * @return Uma {@link List} de {@link Cliente}.
     */
    public static List<Cliente> carregarClientes() {
        return JsonDataManager.carregarDados(ARQUIVO_CLIENTES, TIPO_LISTA_CLIENTES);
    }

    /**
     * Salva a lista de clientes no arquivo JSON.
     *
     * @param clientes A {@link List} de {@link Cliente} a ser salva.
     */
    public static void salvarClientes(List<Cliente> clientes) {
        JsonDataManager.salvarDados(clientes, ARQUIVO_CLIENTES);
    }
}
