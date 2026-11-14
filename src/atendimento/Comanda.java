package atendimento;

import pessoas.Cliente;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Comanda {
    private static int proximoId = 1;

    private int id;
    private LocalDateTime dataAbertura;
    private StatusComanda status;
    private Cliente cliente;
    private final List<ItemComanda> itens = new ArrayList<>();

    public Comanda(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Toda comanda precisa de um cliente.");
        }
        this.id = proximoId++;
        this.cliente = cliente;
        this.dataAbertura = LocalDateTime.now();
        this.status = StatusComanda.ABERTA;
    }

    public void adicionarItem(ItemComanda item) {
        if (status != StatusComanda.ABERTA) {
            throw new IllegalStateException("Só é possível adicionar itens a uma comanda ABERTA (atual: " + status + ").");
        }
        itens.add(item);
    }

    public BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemComanda item : itens) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public void fecharComanda() {
        if (status != StatusComanda.ABERTA) {
            throw new IllegalStateException("Só é possível fechar uma comanda ABERTA (atual: " + status + ").");
        }
        if (itens.isEmpty()) {
            throw new IllegalStateException("Não é possível fechar uma comanda sem itens.");
        }
        status = StatusComanda.AGUARDANDO_PAGAMENTO;
    }

    public int getId() { return id; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public StatusComanda getStatus() { return status; }
    /** Chamado pela Venda quando o pagamento é concluído. */
    public void confirmarPagamento() {
        exigirAguardandoPagamento();
        status = StatusComanda.FECHADA;
    }

    /** Chamado pela Venda quando ela é cancelada: o cliente volta a poder pedir. */
    public void reabrir() {
        exigirAguardandoPagamento();
        status = StatusComanda.ABERTA;
    }

    private void exigirAguardandoPagamento() {
        if (status != StatusComanda.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("A comanda não está aguardando pagamento (atual: " + status + ").");
        }
    }

    public Cliente getCliente() { return cliente; }
    public List<ItemComanda> getItens() { return List.copyOf(itens); }
}
