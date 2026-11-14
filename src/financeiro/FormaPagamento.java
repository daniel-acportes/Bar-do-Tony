package financeiro;

public class FormaPagamento {
    private static int proximoId = 1;

    private int id;
    private String nome;

    public FormaPagamento(String nome) {
        this.id = proximoId++;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
}
