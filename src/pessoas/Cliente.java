package pessoas;

public class Cliente extends Usuario {
    private String cpf;

    public Cliente(String nome, String telefone, String cpf) {
        super(nome, telefone);
        this.cpf = cpf;
    }

    public String getCpf() { return cpf; }
}
