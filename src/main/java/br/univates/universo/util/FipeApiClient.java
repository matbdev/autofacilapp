package br.univates.universo.util;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Cliente para interagir com a API FIPE de veículos.
 * <p>
 * Fornece métodos para buscar marcas, modelos, anos e o valor de um veículo
 * específico. Utiliza a biblioteca OkHttp para as requisições HTTP.
 *
 * @version 1.0
 */
public class FipeApiClient {

    private final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "https://parallelum.com.br/fipe/api/v1/carros";

    /**
     * Busca todas as marcas de carros disponíveis na API.
     *
     * @return Um {@link JsonArray} com as marcas.
     * @throws IOException se ocorrer um erro de rede.
     */
    public JsonArray getMarcas() throws IOException {
        Request request = new Request.Builder().url(BASE_URL + "/marcas").build();
        return executeRequest(request).getAsJsonArray();
    }

    /**
     * Busca os modelos de uma marca específica.
     *
     * @param marcaCodigo O código da marca.
     * @return Um {@link JsonObject} contendo uma lista de modelos.
     * @throws IOException se ocorrer um erro de rede.
     */
    public JsonObject getModelos(String marcaCodigo) throws IOException {
        Request request = new Request.Builder().url(BASE_URL + "/marcas/" + marcaCodigo + "/modelos").build();
        return executeRequest(request).getAsJsonObject();
    }

    /**
     * Busca os anos de um modelo específico.
     *
     * @param marcaCodigo  O código da marca.
     * @param modeloCodigo O código do modelo.
     * @return Um {@link JsonArray} com os anos disponíveis.
     * @throws IOException se ocorrer um erro de rede.
     */
    public JsonArray getAnos(String marcaCodigo, String modeloCodigo) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/marcas/" + marcaCodigo + "/modelos/" + modeloCodigo + "/anos")
                .build();
        return executeRequest(request).getAsJsonArray();
    }

    /**
     * Busca o valor FIPE de um veículo específico.
     *
     * @param marcaCodigo  O código da marca.
     * @param modeloCodigo O código do modelo.
     * @param anoCodigo    O código do ano.
     * @return Um {@link JsonObject} com os detalhes e o valor do veículo.
     * @throws IOException se ocorrer um erro de rede.
     */
    public JsonObject getValor(String marcaCodigo, String modeloCodigo, String anoCodigo) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/marcas/" + marcaCodigo + "/modelos/" + modeloCodigo + "/anos/" + anoCodigo)
                .build();
        return executeRequest(request).getAsJsonObject();
    }

    /**
     * Método auxiliar para executar a requisição HTTP e processar a resposta.
     *
     * @param request O objeto {@link Request} a ser executado.
     * @return O corpo da resposta como um {@link com.google.gson.JsonElement}.
     * @throws IOException se a requisição falhar ou o corpo da resposta for nulo.
     */
    private com.google.gson.JsonElement executeRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Response body is null");
            }
            return JsonParser.parseString(body.string());
        }
    }
}
