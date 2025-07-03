package br.univates.universo.core; // Pacote atualizado

/**
 * Representa um veículo na frota da locadora.
 * Contém todos os atributos de um veículo e métodos para gerenciar seu estado.
 */
public class Veiculo {
    private final String placa; // Armazenada sem formatação (ex: ABC1D23)
    private String marca;
    private String modelo;
    private int ano;
    private String cor;
    private double precoDiaria;
    private String status; // "Disponível" ou "Alugado"

    /**
     * Construtor para criar uma nova instância de Veiculo.
     * O status inicial de qualquer veículo novo é "Disponível".
     *
     * @param placa       A placa do veículo (deve conter 7 caracteres).
     * @param marca       A marca do veículo (ex: Fiat).
     * @param modelo      O modelo do veículo (ex: Uno).
     * @param ano         O ano de fabricação do veículo.
     * @param cor         A cor do veículo.
     * @param precoDiaria O valor da diária de aluguel do veículo.
     */
    public Veiculo(String placa, String marca, String modelo, int ano, String cor, double precoDiaria) {
        // Garante que a placa seja armazenada em maiúsculas e sem hífens.
        this.placa = placa.toUpperCase().replaceAll("-", "");
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.cor = cor;
        this.precoDiaria = precoDiaria;
        this.status = "Disponível"; // Valor padrão ao criar um novo veículo
    }

    // --- Getters ---

    /**
     * @return A placa do veículo sem formatação (7 caracteres).
     */
    public String getPlaca() {
        return placa;
    }

    /**
     * Retorna a placa formatada com um hífen, no padrão visual comum.
     * Exemplo: "ABC-1234" ou "ABC-1D23".
     *
     * @return A placa formatada.
     */
    public String getPlacaFormatada() {
        if (placa == null || placa.length() != 7) {
            return placa; // Retorna a placa original se não tiver 7 caracteres
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

    // --- Setters ---

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public void setPrecoDiaria(double precoDiaria) {
        this.precoDiaria = precoDiaria;
    }

    // --- Métodos de negócio que alteram o estado do objeto ---

    /**
     * Altera o status do veículo para "Alugado".
     * Este método deve ser chamado quando um aluguel é registrado.
     */
    public void alugar() {
        this.status = "Alugado";
    }

    /**
     * Altera o status do veículo para "Disponível".
     * Este método deve ser chamado quando um veículo é devolvido.
     */
    public void devolver() {
        this.status = "Disponível";
    }

    /**
     * Retorna uma representação textual do objeto Veiculo.
     * Utilizado para exibição em componentes como JComboBox.
     *
     * @return Uma String no formato "Marca Modelo (PlacaFormatada)".
     */
    @Override
    public String toString() {
        return marca + " " + modelo + " (" + getPlacaFormatada() + ")";
    }
}
