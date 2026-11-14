package financeiro;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pagamento {
    private static int proximoId = 1;

    private int id;
    private BigDecimal valor;
    private LocalDateTime dataPagamento;
    private BigDecimal troco;
    private FormaPagamento formaPagamento;

    /** Criado por Venda.registrarPagamento(...). */
    Pagamento(BigDecimal valor, BigDecimal troco, FormaPagamento formaPagamento) {
        this.id = proximoId++;
        this.valor = valor;
        this.troco = troco;
        this.formaPagamento = formaPagamento;
        this.dataPagamento = LocalDateTime.now();
    }

    /** Quanto deste pagamento realmente abateu da venda. */
    public BigDecimal getValorAbatido() {
        return valor.subtract(troco);
    }

    public int getId() { return id; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public BigDecimal getTroco() { return troco; }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
}
