package br.univates.universo.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Classe genérica responsável pela persistência de dados em arquivos JSON.
 * <p>
 * Esta classe abstrata fornece a lógica de baixo nível para serializar (salvar)
 * e desserializar (carregar) listas de objetos de/para arquivos JSON,
 * utilizando
 * a biblioteca Gson. Ela lida com a criação de diretórios e tratamento de
 * exceções de I/O.
 *
 * @version 1.0
 */
public final class JsonDataManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DIRETORIO_REGISTROS = "registros";

    /**
     * Construtor privado para prevenir a instanciação da classe utilitária.
     */
    private JsonDataManager() {
    }

    /**
     * Carrega uma lista de objetos de um arquivo JSON especificado.
     * <p>
     * Se o arquivo ou o diretório não existirem, um novo arquivo será criado
     * e uma lista vazia será retornada, garantindo que a aplicação não falhe.
     *
     * @param <T>         O tipo genérico dos objetos na lista.
     * @param nomeArquivo O nome do arquivo JSON (ex: "veiculos.json").
     * @param tipo        O {@link Type} da lista a ser desserializada (ex:
     *                    TypeToken<ArrayList<Veiculo>>(){}.getType()).
     * @return Uma {@link List} contendo os objetos do arquivo, ou uma lista vazia
     *         se o arquivo não existir ou ocorrer um erro.
     */
    public static <T> List<T> carregarDados(String nomeArquivo, Type tipo) {
        File arquivo = new File(DIRETORIO_REGISTROS, nomeArquivo);
        if (!arquivo.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(arquivo)) {
            List<T> dados = GSON.fromJson(reader, tipo);
            return dados != null ? dados : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Erro ao carregar dados de " + nomeArquivo + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Salva (serializa) uma lista de objetos em um arquivo JSON especificado.
     * <p>
     * Garante que o diretório de destino exista antes de tentar salvar o arquivo.
     * Se o diretório não existir, ele será criado.
     *
     * @param dados       A lista de objetos a ser salva.
     * @param nomeArquivo O nome do arquivo JSON (ex: "veiculos.json").
     */
    public static void salvarDados(List<?> dados, String nomeArquivo) {
        File diretorio = new File(DIRETORIO_REGISTROS);
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        File arquivo = new File(diretorio, nomeArquivo);
        try (FileWriter writer = new FileWriter(arquivo)) {
            GSON.toJson(dados, writer);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro crítico ao salvar dados em " + nomeArquivo,
                    "Erro de Arquivo",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
