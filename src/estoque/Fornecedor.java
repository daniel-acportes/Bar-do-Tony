package estoque;

public class Fornecedor {
    private static int proximoId = 1;

    private int id;
    private String razaoSocial;
    private String cnpj;

    public Fornecedor(String razaoSocial, String cnpj) {
        this.id = proximoId++;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
    }

    public int getId() { return id; }
    public String getRazaoSocial() { return razaoSocial; }
    public String getCnpj() { return cnpj; }
}
