package estoque;

public class Categoria {
    private static int proximoId = 1;

    private int id;
    private String nome;

    public Categoria(String nome) {
        this.id = proximoId++;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
}
