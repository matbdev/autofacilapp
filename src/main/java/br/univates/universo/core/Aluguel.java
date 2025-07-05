package br.univates.universo.core;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Representa um aluguel de veículo, vinculando um cliente a um veículo por um
 * período.
 * Inclui lógica para cálculo de multa por atraso.
 * 
 * @version 2.0 (Refatorado)
 */
public class Aluguel {
    private static final AtomicInteger count = new AtomicInteger(0);

    private final int idAluguel;
    private final String placaVeiculo;
    private final String cpfCliente;
    private final String dataSaida;
    private final String dataPrevistaDevolucao;
    private String dataDevolucaoEfetiva;
    private double valorTotal;
    private double precoDiaria;
    private double valorMulta;
    private String statusAluguel;

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
        this.valorMulta = 0.0;
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

    public double getValorMulta() {
        return valorMulta;
    }

    // --- Setters ---
    public void setPrecoDiaria(double precoDiaria) {
        this.precoDiaria = precoDiaria;
    }

    /**
     * AJUSTADO: Finaliza o aluguel, calculando os valores finais (incluindo multas)
     * e atualizando o status.
     * O preço da diária é obtido do próprio objeto, que foi definido na criação do
     * aluguel.
     *
     * @param dataDevolucaoEfetivaStr A data em que o veículo foi devolvido, no
     *                                formato "dd/MM/yyyy".
     */
    public void finalizarAluguel(String dataDevolucaoEfetivaStr) {
        this.dataDevolucaoEfetiva = dataDevolucaoEfetivaStr;

        // A lógica de cálculo foi movida para um método privado para maior clareza.
        calcularValoresFinais();

        // O status só deve ser alterado se o cálculo for bem-sucedido.
        if (this.valorTotal > 0) {
            long diasAtraso = calcularDiasAtraso();
            this.statusAluguel = diasAtraso > 0 ? "Finalizado com Atraso" : "Finalizado";
        }
    }

    /**
     * Calcula os dias de atraso entre a data prevista e a data efetiva de
     * devolução.
     */
    private long calcularDiasAtraso() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate prevista = LocalDate.parse(dataPrevistaDevolucao, formatter);
            LocalDate devolucao = LocalDate.parse(dataDevolucaoEfetiva, formatter);
            return ChronoUnit.DAYS.between(prevista, devolucao);
        } catch (DateTimeParseException e) {
            return 0;
        }
    }

    /**
     * Calcula o valor total do aluguel, aplicando juros compostos de 10% ao dia
     * sobre o valor base
     * em caso de atraso.
     */
    private void calcularValoresFinais() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate saida = LocalDate.parse(dataSaida, formatter);
            LocalDate devolucao = LocalDate.parse(dataDevolucaoEfetiva, formatter);

            long diasAlugados = ChronoUnit.DAYS.between(saida, devolucao);
            if (diasAlugados <= 0) {
                diasAlugados = 1; // Garante a cobrança de pelo menos uma diária.
            }

            double valorBase = diasAlugados * this.precoDiaria;
            long diasAtraso = calcularDiasAtraso();

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