package atendimento;

import estoque.Produto;

import java.math.BigDecimal;

public class ItemComanda {
    private static int proximoId = 1;

    private int id;
    private int quantidade;
    private BigDecimal precoUnitario;
    private String observacoes;
    private Produto produto;
    private StatusPreparo statusPreparo = StatusPreparo.PENDENTE;

    public ItemComanda(Produto produto, int quantidade, String observacoes) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        this.id = proximoId++;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = produto.getValor(); // congela o preço do momento do pedido
        this.observacoes = observacoes == null ? "" : observacoes;
    }

    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public int getId() { return id; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public String getObservacoes() { return observacoes; }
    public Produto getProduto() { return produto; }
    public StatusPreparo getStatusPreparo() { return statusPreparo; }
    public void iniciarPreparo() {
        if (statusPreparo != StatusPreparo.PENDENTE) {
            throw new IllegalStateException("O item #" + id + " não está pendente.");
        }
        statusPreparo = StatusPreparo.EM_PREPARO;
    }

    public void finalizarPreparo() {
        if (statusPreparo != StatusPreparo.EM_PREPARO) {
            throw new IllegalStateException("O item #" + id + " não está em preparo.");
        }
        statusPreparo = StatusPreparo.PRONTO;
    }
}
