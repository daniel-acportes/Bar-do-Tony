package financeiro;

import pessoas.Funcionario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Caixa {
    private static int proximoId = 1;

    private int id;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private BigDecimal valorAbertura;
    private BigDecimal valorFechamento;
    private StatusCaixa status = StatusCaixa.FECHADO;
    private Funcionario operador;
    private final List<Pagamento> pagamentos = new ArrayList<>();

    public Caixa() {
        this.id = proximoId++;
    }

    public void abrir(Funcionario operador, BigDecimal valorAbertura) {
        if (status == StatusCaixa.ABERTO) {
            throw new IllegalStateException("O caixa já está aberto.");
        }
        this.operador = operador;
        this.valorAbertura = valorAbertura;
        this.dataAbertura = LocalDateTime.now();
        this.dataFechamento = null;
        this.valorFechamento = null;
        this.pagamentos.clear();
        this.status = StatusCaixa.ABERTO;
    }

    /** valorFechamento = valorAbertura + pagamentos recebidos - trocos devolvidos. */
    public void fechar() {
        if (status != StatusCaixa.ABERTO) {
            throw new IllegalStateException("O caixa já está fechado.");
        }
        BigDecimal total = valorAbertura;
        for (Pagamento p : pagamentos) {
            total = total.add(p.getValorAbatido());
        }
        this.valorFechamento = total;
        this.dataFechamento = LocalDateTime.now();
        this.status = StatusCaixa.FECHADO;
    }

    void receber(Pagamento pagamento) {
        pagamentos.add(pagamento);
    }

    public int getId() { return id; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public LocalDateTime getDataFechamento() { return dataFechamento; }
    public BigDecimal getValorAbertura() { return valorAbertura; }
    public BigDecimal getValorFechamento() { return valorFechamento; }
    public StatusCaixa getStatus() { return status; }
    public Funcionario getOperador() { return operador; }
    public List<Pagamento> getPagamentos() { return List.copyOf(pagamentos); }
}
