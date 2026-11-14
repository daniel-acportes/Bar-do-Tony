package pessoas;

import atendimento.Comanda;
import financeiro.Venda;
import java.math.BigDecimal;

public class Vendedor extends Funcionario {

    public Vendedor(String nome, String telefone, String username, String senha) {
        super(nome, telefone, username, senha);
    }

    public Venda realizarVenda(Comanda comanda, BigDecimal desconto) {
        return new Venda(comanda, this, desconto);
    }

    public Venda realizarVenda(Comanda comanda) {
        return realizarVenda(comanda, BigDecimal.ZERO);
    }

    public void cancelarVenda(Venda venda) {
        venda.cancelar();
    }
}
