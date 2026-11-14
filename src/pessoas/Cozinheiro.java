package pessoas;

import atendimento.Comanda;
import atendimento.ItemComanda;

public class Cozinheiro extends Funcionario {

    public Cozinheiro(String nome, String telefone, String username, String senha) {
        super(nome, telefone, username, senha);
    }

    public void visualizarComanda(Comanda comanda) {
        System.out.println("  Comanda #" + comanda.getId() + " (" + comanda.getStatus() + ")");
        for (ItemComanda item : comanda.getItens()) {
            String obs = item.getObservacoes().isBlank() ? "" : " [" + item.getObservacoes() + "]";
            System.out.println("    - item #" + item.getId() + ": " + item.getQuantidade() + "x "
                    + item.getProduto().getNome() + obs + " -> " + item.getStatusPreparo());
        }
    }

    public void iniciarPreparo(ItemComanda item) {
        item.iniciarPreparo();
    }

    public void finalizarPreparo(ItemComanda item) {
        item.finalizarPreparo();
    }
}
