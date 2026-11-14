package estoque;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Produto {
    private static int proximoId = 1;

    private int id;
    private String nome;
    private BigDecimal valor;
    private String tamanho;
    private Categoria categoria;
    private final List<MovimentacaoEstoque> movimentacoes = new ArrayList<>();

    public Produto(String nome, BigDecimal valor, String tamanho, Categoria categoria) {
        this.id = proximoId++;
        this.nome = nome;
        this.valor = valor;
        this.tamanho = tamanho;
        this.categoria = categoria;
    }

    /** Registra uma entrada/saída de estoque deste produto. */
    public MovimentacaoEstoque movimentar(TipoMovimentacao tipo, int quantidade,
                                          BigDecimal valorCustoUnitario, Fornecedor fornecedor) {
        MovimentacaoEstoque mov = new MovimentacaoEstoque(this, tipo, quantidade, valorCustoUnitario, fornecedor);
        movimentacoes.add(mov);
        return mov;
    }

    /** O saldo não é guardado: é sempre a soma das movimentações. */
    public int getSaldoEstoque() {
        int saldo = 0;
        for (MovimentacaoEstoque mov : movimentacoes) {
            switch (mov.getTipo()) {
                case ENTRADA_COMPRA -> saldo += mov.getQuantidade();
                case SAIDA_VENDA, PERDA_AVARIA -> saldo -= mov.getQuantidade();
                case AJUSTE_MANUAL -> saldo += mov.getQuantidade(); // pode ser negativo
            }
        }
        return saldo;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getTamanho() { return tamanho; }
    public Categoria getCategoria() { return categoria; }
    public List<MovimentacaoEstoque> getMovimentacoes() { return List.copyOf(movimentacoes); }
}
