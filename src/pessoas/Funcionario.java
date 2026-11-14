package pessoas;

public abstract class Funcionario extends Usuario {
    private String username;
    private String senha;

    protected Funcionario(String nome, String telefone, String username, String senha) {
        super(nome, telefone);
        this.username = username;
        this.senha = senha;
    }

    public boolean autenticar(String senha) {
        return this.senha.equals(senha);
    }

    public void alterarSenha(String novaSenha) {
        if (novaSenha == null || novaSenha.isBlank()) {
            throw new IllegalArgumentException("A nova senha não pode ser vazia.");
        }
        this.senha = novaSenha;
    }

    public String getUsername() { return username; }
}
