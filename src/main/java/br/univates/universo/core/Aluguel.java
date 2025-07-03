package br.univates.universo.core; // Pacote atualizado

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Representa um aluguel de veículo, vinculando um cliente a um veículo por um
 * período.
 * Inclui lógica para cálculo de multa por atraso.
 */
public class Aluguel {
    // Contador estático para gerar IDs únicos para cada aluguel de forma
    // thread-safe.
    private static final AtomicInteger count = new AtomicInteger(0);

    private final int idAluguel;
    private final String placaVeiculo;
    private final String cpfCliente;
    private final String dataSaida;
    private final String dataPrevistaDevolucao;
    private String dataDevolucaoEfetiva;
    private double valorTotal;
    private double precoDiaria;
    private double valorMulta; // NOVO: Armazena o valor da multa por atraso
    private String statusAluguel; // "Ativo" ou "Concluído"

    public Aluguel(String placaVeiculo, String cpfCliente, String dataSaida, String dataPrevistaDevolucao) {
        this.idAluguel = count.incrementAndGet();
        this.placaVeiculo = placaVeiculo;
        this.cpfCliente = cpfCliente;
        this.dataSaida = dataSaida;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.statusAluguel = "Ativo";
        this.dataDevolucaoEfetiva = null;
        this.valorTotal = 0.0;
        this.precoDiaria = 0.0;
        this.valorMulta = 0.0; // Inicializa a multa como zero
    }

    // --- Getters ---
    public int getIdAluguel() {
        return idAluguel;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public String getCpfCliente() {
        return cpfCliente;
    }

    public String getDataSaida() {
        return dataSaida;
    }

    public String getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public String getDataDevolucaoEfetiva() {
        return dataDevolucaoEfetiva;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public double getPrecoDiaria() {
        return precoDiaria;
    }

    public String getStatusAluguel() {
        return statusAluguel;
    }

    /** @return O valor da multa por atraso. */
    public double getValorMulta() {
        return valorMulta;
    } // NOVO GETTER

    // --- Setters ---
    public void setPrecoDiaria(double precoDiaria) {
        this.precoDiaria = precoDiaria;
    }

    /**
     * Finaliza o aluguel, calculando os valores finais (incluindo multas) e
     * atualizando o status.
     *
     * @param dataDevolucaoEfetiva A data em que o veículo foi devolvido.
     * @param precoDiaria          O preço da diária para cálculo.
     */
    public void finalizarAluguel(String dataDevolucaoEfetiva, double precoDiaria) {
        this.dataDevolucaoEfetiva = dataDevolucaoEfetiva;
        this.statusAluguel = "Concluído";
        calcularValoresFinais(precoDiaria); // Chamada para o método de cálculo atualizado
    }

    /**
     * Calcula o valor total do aluguel, aplicando juros compostos de 10% ao dia
     * em caso de atraso.
     *
     * @param precoDiaria O valor da diária do veículo.
     */
    private void calcularValoresFinais(double precoDiaria) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate saida = LocalDate.parse(dataSaida, formatter);
            LocalDate prevista = LocalDate.parse(dataPrevistaDevolucao, formatter);
            LocalDate devolucao = LocalDate.parse(dataDevolucaoEfetiva, formatter);

            long diasAlugados = ChronoUnit.DAYS.between(saida, devolucao);
            if (diasAlugados <= 0) {
                diasAlugados = 1; // Garante a cobrança de pelo menos uma diária.
            }

            double valorBase = diasAlugados * precoDiaria;

            // Calcula os dias de atraso
            long diasAtraso = ChronoUnit.DAYS.between(prevista, devolucao);

            if (diasAtraso > 0) {
                // Aplica juros compostos de 10% (fator 1.10) sobre o valor base
                double fatorJuros = Math.pow(1.10, diasAtraso);
                this.valorTotal = valorBase * fatorJuros;
                this.valorMulta = this.valorTotal - valorBase; // A multa é a diferença
            } else {
                this.valorTotal = valorBase;
                this.valorMulta = 0.0; // Sem atraso, sem multa
            }
        } catch (DateTimeParseException e) {
            System.err.println("Erro ao converter data para calcular o valor total: " + e.getMessage());
            this.valorTotal = 0.0;
            this.valorMulta = 0.0;
        }
    }
}
