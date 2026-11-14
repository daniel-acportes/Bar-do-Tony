package financeiro;

import atendimento.Comanda;
import atendimento.ItemComanda;
import atendimento.StatusComanda;
import estoque.TipoMovimentacao;
import pessoas.Funcionario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venda {
    private static int proximoId = 1;

    private int id;
    private LocalDateTime data;
    private BigDecimal valorTotal;
    private BigDecimal desconto;
    private StatusVenda status;
    private Comanda comanda;
    private Funcionario funcionario; // quem registrou a venda
    private final List<Pagamento> pagamentos = new ArrayList<>();

    /** Normalmente criada por Vendedor.realizarVenda(...). */
    public Venda(Comanda comanda, Funcionario funcionario, BigDecimal desconto) {
        if (comanda.getStatus() != StatusComanda.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("A comanda precisa estar fechada (AGUARDANDO_PAGAMENTO) para gerar a venda.");
        }
        if (desconto.signum() < 0 || desconto.compareTo(comanda.calcularTotal()) > 0) {
            throw new IllegalArgumentException("Desconto inválido: " + desconto);
        }
        this.id = proximoId++;
        this.comanda = comanda;
        this.funcionario = funcionario;
        this.desconto = desconto;
        this.data = LocalDateTime.now();
        this.status = StatusVenda.PENDENTE;
        this.valorTotal = calcularTotal();
    }

    public BigDecimal calcularTotal() {
        return comanda.calcularTotal().subtract(desconto);
    }

    public BigDecimal getTotalPago() {
        BigDecimal pago = BigDecimal.ZERO;
        for (Pagamento p : pagamentos) {
            pago = pago.add(p.getValorAbatido());
        }
        return pago;
    }

    public BigDecimal getValorRestante() {
        return valorTotal.subtract(getTotalPago());
    }

    public Pagamento registrarPagamento(BigDecimal valor, FormaPagamento forma, Caixa caixa) {
        if (status != StatusVenda.PENDENTE) {
            throw new IllegalStateException("Só é possível pagar uma venda PENDENTE (atual: " + status + ").");
        }
        if (caixa.getStatus() != StatusCaixa.ABERTO) {
            throw new IllegalStateException("O caixa precisa estar ABERTO para receber pagamentos.");
        }
        if (valor.signum() <= 0) {
            throw new IllegalArgumentException("O valor do pagamento deve ser positivo.");
        }

        BigDecimal restante = getValorRestante();
        BigDecimal troco = valor.compareTo(restante) > 0 ? valor.subtract(restante) : BigDecimal.ZERO;

        Pagamento pagamento = new Pagamento(valor, troco, forma);
        pagamentos.add(pagamento);
        caixa.receber(pagamento);

        if (getValorRestante().signum() <= 0) {
            concluir();
        }
        return pagamento;
    }

    private void concluir() {
        status = StatusVenda.CONCLUIDA;
        comanda.confirmarPagamento();
        for (ItemComanda item : comanda.getItens()) {
            item.getProduto().movimentar(TipoMovimentacao.SAIDA_VENDA, item.getQuantidade(), null, null);
        }
    }

    /** Chamado por Vendedor.cancelarVenda(...): a comanda volta a ficar aberta. */
    public void cancelar() {
        if (status != StatusVenda.PENDENTE) {
            throw new IllegalStateException("Só é possível cancelar uma venda PENDENTE (atual: " + status + ").");
        }
        status = StatusVenda.CANCELADA;
        comanda.reabrir();
    }

    public int getId() { return id; }
    public LocalDateTime getData() { return data; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public BigDecimal getDesconto() { return desconto; }
    public StatusVenda getStatus() { return status; }
    public Comanda getComanda() { return comanda; }
    public Funcionario getFuncionario() { return funcionario; }
    public List<Pagamento> getPagamentos() { return List.copyOf(pagamentos); }
}
