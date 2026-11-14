package pessoas;

public abstract class Usuario {
    private static int proximoId = 1;

    private int id;
    protected String nome;
    private String telefone;

    protected Usuario(String nome, String telefone) {
        this.id = proximoId++;
        this.nome = nome;
        this.telefone = telefone;
    }

    public void atualizarDados(String nome, String telefone) {
        this.nome = nome;
        this.telefone = telefone;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getTelefone() { return telefone; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " #" + id + " " + nome;
    }
}
