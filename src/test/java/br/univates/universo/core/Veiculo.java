package br.univates.universo.core;

/**
 * Representa um veículo na frota da locadora.
 * Contém todos os atributos de um veículo e métodos para gerenciar seu estado.
 * 
 * @version 2.0 (Ajustado para incluir valorFipe)
 */
public class Veiculo {
    private final String placa;
    private final String marca;
    private final String modelo;
    private final int ano;
    private String cor;
    private double precoDiaria;
    private String status;
    private final double valorFipe; // NOVO: Campo para armazenar o valor FIPE do veículo.

    /**
     * Construtor original para compatibilidade.
     */
    public Veiculo(String placa, String marca, String modelo, int ano, String cor, double precoDiaria) {
        this.placa = placa.toUpperCase().replaceAll("-", "");
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.cor = cor;
        this.precoDiaria = precoDiaria;
        this.status = "Disponível";
        this.valorFipe = 0.0; // Valor padrão para veículos cadastrados sem FIPE.
    }

    /**
     * NOVO: Construtor para criar uma instância de Veiculo com o valor da FIPE.
     * Utilizado ao adicionar um novo veículo através da busca na API.
     *
     * @param placa       A placa do veículo.
     * @param marca       A marca do veículo.
     * @param modelo      O modelo do veículo.
     * @param ano         O ano de fabricação do veículo.
     * @param cor         A cor do veículo.
     * @param precoDiaria O valor da diária de aluguel.
     * @param valorFipe   O valor do veículo segundo a tabela FIPE.
     */
    public Veiculo(String placa, String marca, String modelo, int ano, String cor, double precoDiaria,
            double valorFipe) {
        this.placa = placa.toUpperCase().replaceAll("-", "");
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.cor = cor;
        this.precoDiaria = precoDiaria;
        this.status = "Disponível";
        this.valorFipe = valorFipe; // Armazena o valor FIPE.
    }

    // --- Getters ---

    public String getPlaca() {
        return placa;
    }

    public String getPlacaFormatada() {
        if (placa == null || placa.length() != 7) {
            return placa;
        }
        return placa.substring(0, 3) + "-" + placa.substring(3);
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

    public String getCor() {
        return cor;
    }

    public double getPrecoDiaria() {
        return precoDiaria;
    }

    public String getStatus() {
        return status;
    }

    /**
     * NOVO: Retorna o valor FIPE do veículo.
     * 
     * @return O valor do veículo conforme a tabela FIPE.
     */
    public double getValorFipe() {
        return valorFipe;
    }

    // --- Setters ---

    public void setCor(String cor) {
        this.cor = cor;
    }

    public void setPrecoDiaria(double precoDiaria) {
        this.precoDiaria = precoDiaria;
    }

    // Setters de marca, modelo e ano removidos para garantir a imutabilidade após a
    // criação via FIPE.
    // Apenas cor e preço da diária podem ser alterados em um veículo existente.

    // --- Métodos de negócio ---

    public void alugar() {
        this.status = "Alugado";
    }

    public void devolver() {
        this.status = "Disponível";
    }

    @Override
    public String toString() {
        return marca + " " + modelo + " (" + getPlacaFormatada() + ")";
    }
}