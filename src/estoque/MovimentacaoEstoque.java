package estoque;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimentacaoEstoque {
    private static int proximoId = 1;

    private int id;
    private int quantidade;
    private LocalDateTime data;
    private TipoMovimentacao tipo;
    private BigDecimal valorCustoUnitario;
    private Produto produto;
    private Fornecedor fornecedor; // só em ENTRADA_COMPRA

    /** Use Produto.movimentar(...) para criar e já associar ao produto. */
    MovimentacaoEstoque(Produto produto, TipoMovimentacao tipo, int quantidade,
                        BigDecimal valorCustoUnitario, Fornecedor fornecedor) {
        if (tipo == TipoMovimentacao.ENTRADA_COMPRA && fornecedor == null) {
            throw new IllegalArgumentException("Entrada por compra exige um fornecedor.");
        }
        if (tipo == TipoMovimentacao.AJUSTE_MANUAL ? quantidade == 0 : quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade inválida para " + tipo + ": " + quantidade);
        }
        this.id = proximoId++;
        this.produto = produto;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.valorCustoUnitario = valorCustoUnitario;
        this.fornecedor = fornecedor;
        this.data = LocalDateTime.now();
    }

    public int getId() { return id; }
    public int getQuantidade() { return quantidade; }
    public LocalDateTime getData() { return data; }
    public TipoMovimentacao getTipo() { return tipo; }
    public BigDecimal getValorCustoUnitario() { return valorCustoUnitario; }
    public Produto getProduto() { return produto; }
    public Fornecedor getFornecedor() { return fornecedor; }
}
