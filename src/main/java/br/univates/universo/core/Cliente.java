package br.univates.universo.core; // Pacote atualizado

/**
 * Representa um cliente da locadora.
 */
public class Cliente {
    private final String cpf;
    private String nome;
    private String telefone;
    private String email;

    /**
     * Construtor da classe Cliente.
     *
     * @param cpf      O CPF do cliente (chave primária).
     * @param nome     O nome completo do cliente.
     * @param telefone O número de telefone do cliente.
     * @param email    O endereço de e-mail do cliente.
     */
    public Cliente(String cpf, String nome, String telefone, String email) {
        this.cpf = cpf;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    // Getters e Setters
    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retorna uma representação em String do objeto.
     * Este formato é ideal para exibição em componentes como JComboBox.
     *
     * @return Uma String no formato "Nome (CPF)".
     */
    @Override
    public String toString() {
        return nome + " (" + cpf + ")";
    }
}
