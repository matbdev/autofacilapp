package br.univates.universo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

/**
 * Cliente para interagir com a API FIPE de veículos.
 * Fornece métodos tipados para buscar marcas, modelos, anos e o valor de um
 * veículo.
 *
 * @version 2.1 (Com filtro de ano 32000)
 */
public class FipeApiClient {

    private final OkHttpClient client = new OkHttpClient();
    // Gson é usado para converter JSON em objetos Java automaticamente
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(FipeItem.class, new FipeItemDeserializer())
            .create();

    private static final String BASE_URL = "https://parallelum.com.br/fipe/api/v1/carros";

    /**
     * Busca todas as marcas de carros disponíveis na API.
     *
     * @return Uma {@link List} de {@link FipeItem} com as marcas.
     */
    public List<FipeItem> getMarcas() {
        try {
            String json = executeRequest(BASE_URL + "/marcas");
            Type listType = new TypeToken<List<FipeItem>>() {
            }.getType();
            return gson.fromJson(json, listType);
        } catch (IOException e) {
            handleError("marcas", e);
            return Collections.emptyList();
        }
    }

    /**
     * Busca os modelos de uma marca específica.
     *
     * @param marcaCodigo O código da marca.
     * @return Um {@link List} de {@link FipeItem} com os modelos.
     */
    public List<FipeItem> getModelos(String marcaCodigo) {
        try {
            String json = executeRequest(BASE_URL + "/marcas/" + marcaCodigo + "/modelos");
            ModelosResponse response = gson.fromJson(json, ModelosResponse.class);
            return response != null ? response.modelos : Collections.emptyList();
        } catch (IOException e) {
            handleError("modelos da marca " + marcaCodigo, e);
            return Collections.emptyList();
        }
    }

    /**
     * MÉTODO CORRIGIDO
     * Busca os anos de um modelo específico, já filtrando os veículos 0km (código
     * 32000).
     *
     * @param marcaCodigo  O código da marca.
     * @param modeloCodigo O código do modelo.
     * @return Um {@link List} de {@link FipeItem} com os anos.
     */
    public List<FipeItem> getAnos(String marcaCodigo, String modeloCodigo) {
        try {
            String json = executeRequest(BASE_URL + "/marcas/" + marcaCodigo + "/modelos/" + modeloCodigo + "/anos");
            Type listType = new TypeToken<List<FipeItem>>() {
            }.getType();
            List<FipeItem> anos = gson.fromJson(json, listType);

            // CORREÇÃO: Filtra o ano "32000" que representa veículos 0km.
            if (anos != null) {
                return anos.stream()
                        .filter(ano -> ano != null && ano.getCode() != null && !ano.getCode().startsWith("32000"))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();

        } catch (IOException e) {
            handleError("anos para o modelo " + modeloCodigo, e);
            return Collections.emptyList();
        }
    }

    /**
     * Busca o valor FIPE e detalhes de um veículo específico.
     *
     * @param marcaCodigo  O código da marca.
     * @param modeloCodigo O código do modelo.
     * @param anoCodigo    O código do ano.
     * @return Um objeto {@link VeiculoFipe} com os detalhes, ou null se ocorrer
     *         erro.
     */
    public VeiculoFipe getValor(String marcaCodigo, String modeloCodigo, String anoCodigo) {
        try {
            String json = executeRequest(
                    BASE_URL + "/marcas/" + marcaCodigo + "/modelos/" + modeloCodigo + "/anos/" + anoCodigo);
            return gson.fromJson(json, VeiculoFipe.class);
        } catch (IOException e) {
            handleError("valor do veículo", e);
            return null;
        }
    }

    /**
     * Método auxiliar para executar a requisição HTTP e retornar o corpo como
     * String.
     */
    private String executeRequest(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Código inesperado da API: " + response);
            }
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Corpo da resposta está nulo");
            }
            return body.string();
        }
    }

    /**
     * Centraliza o tratamento de erros da API.
     */
    private void handleError(String context, Exception e) {
        System.err.println("Erro ao buscar " + context + ": " + e.getMessage());
        JOptionPane.showMessageDialog(null,
                "Não foi possível buscar " + context + ".\nVerifique sua conexão com a internet.",
                "Erro de API FIPE",
                JOptionPane.ERROR_MESSAGE);
    }

    // --- Classes internas para ajudar o Gson a interpretar o JSON ---

    /**
     * Representa a resposta do endpoint de modelos, que tem um objeto aninhado.
     */
    private static class ModelosResponse {
        List<FipeItem> modelos;
    }

    /**
     * Representa um veículo completo retornado pela API FIPE.
     */
    public static class VeiculoFipe {
        @SerializedName("Valor")
        private String valor;
        @SerializedName("Marca")
        private String marca;
        @SerializedName("Modelo")
        private String modelo;
        @SerializedName("AnoModelo")
        private int ano;

        public String getValor() {
            return valor;
        }

        public String getMarca() {
            return marca;
        }

        public String getModelo() {
            return modelo;
        }

        public int getAno() {
            return ano;
        }

        public double getValorNumerico() {
            if (valor == null || valor.isBlank())
                return 0.0;
            try {
                String valorLimpo = valor.replace("R$", "").trim().replace(".", "").replace(",", ".");
                return Double.parseDouble(valorLimpo);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }

    /**
     * Desserializador customizado para FipeItem, pois os nomes dos campos
     * no seu FipeItem.java ('code', 'name') não são os mesmos do JSON da API
     * ('codigo', 'nome').
     */
    private static class FipeItemDeserializer implements com.google.gson.JsonDeserializer<FipeItem> {
        @Override
        public FipeItem deserialize(com.google.gson.JsonElement json, Type typeOfT,
                com.google.gson.JsonDeserializationContext context) {
            com.google.gson.JsonObject jsonObject = json.getAsJsonObject();
            String codigo = jsonObject.get("codigo").getAsString();
            String nome = jsonObject.get("nome").getAsString();
            return new FipeItem(codigo, nome);
        }
    }
}
