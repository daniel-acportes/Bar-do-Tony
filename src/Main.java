import atendimento.Comanda;
import atendimento.ItemComanda;
import atendimento.StatusComanda;
import atendimento.StatusPreparo;
import estoque.Categoria;
import estoque.Fornecedor;
import estoque.Produto;
import estoque.TipoMovimentacao;
import financeiro.Caixa;
import financeiro.FormaPagamento;
import financeiro.Pagamento;
import financeiro.StatusCaixa;
import financeiro.StatusVenda;
import financeiro.Venda;
import pessoas.Admin;
import pessoas.Cliente;
import pessoas.Cozinheiro;
import pessoas.Funcionario;
import pessoas.Usuario;
import pessoas.Vendedor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/** Terminal simples: login de funcionário e menus numéricos por perfil. Os dados ficam só na memória. */
public class Main {

    private static final Scanner in = new Scanner(System.in);

    // "Banco de dados" em memória
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static final List<Categoria> categorias = new ArrayList<>();
    private static final List<Produto> produtos = new ArrayList<>();
    private static final List<Fornecedor> fornecedores = new ArrayList<>();
    private static final List<Comanda> comandas = new ArrayList<>();
    private static final List<Venda> vendas = new ArrayList<>();
    private static final List<FormaPagamento> formasPagamento = List.of(
            new FormaPagamento("Dinheiro"), new FormaPagamento("PIX"), new FormaPagamento("Cartão"));
    private static final Caixa caixa = new Caixa();

    public static void main(String[] args) {
        System.out.println("=== Sistema do Bar ===");
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado. Crie o primeiro Admin.");
            Admin admin = new Admin(lerTexto("Nome"), lerTexto("Telefone"), lerTexto("Usuário"), lerTexto("Senha"), usuarios);
            admin.cadastrarUsuario(admin);
            System.out.println("Admin criado.");
        }

        while (true) {
            System.out.println();
            System.out.println("1 - Entrar");
            System.out.println("0 - Sair do sistema");
            int opcao = lerInt("Opção");
            if (opcao == 0) {
                System.out.println("Até logo!");
                return;
            }
            if (opcao == 1) {
                Funcionario f = login();
                if (f instanceof Admin a) menuAdmin(a);
                else if (f instanceof Vendedor v) menuVendedor(v);
                else if (f instanceof Cozinheiro c) menuCozinheiro(c);
            } else {
                System.out.println("Opção inválida.");
            }
        }
    }

    private static Funcionario login() {
        String username = lerTexto("Usuário");
        String senha = lerTexto("Senha");
        for (Usuario u : usuarios) {
            if (u instanceof Funcionario f && f.getUsername().equals(username) && f.autenticar(senha)) {
                System.out.println("Bem-vindo(a), " + f.getNome() + "! (" + f.getClass().getSimpleName() + ")");
                return f;
            }
        }
        System.out.println("Usuário ou senha incorretos.");
        return null;
    }

    // ---------------------------------------------------------------- Admin

    private static void menuAdmin(Admin admin) {
        while (true) {
            int opcao = menu("ADMIN",
                    "Cadastrar funcionário",
                    "Cadastrar cliente",
                    "Listar usuários",
                    "Alterar usuário",
                    "Excluir usuário",
                    "Cadastrar categoria",
                    "Cadastrar produto",
                    "Cadastrar fornecedor",
                    "Movimentar estoque",
                    "Ver estoque",
                    "Alterar minha senha");
            if (opcao == 0) return;
            executar(() -> {
                switch (opcao) {
                    case 1 -> cadastrarFuncionario(admin);
                    case 2 -> {
                        admin.cadastrarUsuario(new Cliente(lerTexto("Nome"), lerTexto("Telefone"), lerTexto("CPF")));
                        System.out.println("Cliente cadastrado.");
                    }
                    case 3 -> listar(usuarios);
                    case 4 -> {
                        Usuario u = escolher("Usuário", usuarios, x -> true, Usuario::getId);
                        if (u != null) {
                            admin.alterarUsuario(u, lerTexto("Novo nome"), lerTexto("Novo telefone"));
                            System.out.println("Usuário alterado.");
                        }
                    }
                    case 5 -> {
                        Usuario u = escolher("Usuário", usuarios, x -> x != admin, Usuario::getId);
                        if (u != null) {
                            admin.excluirUsuario(u);
                            System.out.println("Usuário excluído.");
                        }
                    }
                    case 6 -> {
                        categorias.add(new Categoria(lerTexto("Nome da categoria")));
                        System.out.println("Categoria cadastrada.");
                    }
                    case 7 -> cadastrarProduto();
                    case 8 -> {
                        fornecedores.add(new Fornecedor(lerTexto("Razão social"), lerTexto("CNPJ")));
                        System.out.println("Fornecedor cadastrado.");
                    }
                    case 9 -> movimentarEstoque();
                    case 10 -> verEstoque();
                    case 11 -> alterarSenha(admin);
                    default -> System.out.println("Opção inválida.");
                }
            });
        }
    }

    private static void cadastrarFuncionario(Admin admin) {
        System.out.println("Tipo: 1 - Vendedor | 2 - Cozinheiro | 3 - Admin");
        int tipo = lerInt("Tipo");
        if (tipo < 1 || tipo > 3) {
            System.out.println("Tipo inválido.");
            return;
        }
        String nome = lerTexto("Nome");
        String telefone = lerTexto("Telefone");
        String username = lerTexto("Usuário");
        for (Usuario u : usuarios) {
            if (u instanceof Funcionario f && f.getUsername().equals(username)) {
                System.out.println("Já existe um funcionário com esse usuário.");
                return;
            }
        }
        String senha = lerTexto("Senha");
        Funcionario novo = switch (tipo) {
            case 1 -> new Vendedor(nome, telefone, username, senha);
            case 2 -> new Cozinheiro(nome, telefone, username, senha);
            default -> new Admin(nome, telefone, username, senha, usuarios);
        };
        admin.cadastrarUsuario(novo);
        System.out.println(novo + " cadastrado.");
    }

    private static void cadastrarProduto() {
        Categoria cat = escolher("Categoria", categorias, x -> true, Categoria::getId);
        if (cat == null) return;
        produtos.add(new Produto(lerTexto("Nome"), lerDecimal("Valor de venda"), lerTexto("Tamanho"), cat));
        System.out.println("Produto cadastrado.");
    }

    private static void movimentarEstoque() {
        Produto p = escolher("Produto", produtos, x -> true, Produto::getId);
        if (p == null) return;
        TipoMovimentacao[] tipos = TipoMovimentacao.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i + 1) + " - " + tipos[i]);
        }
        int t = lerInt("Tipo");
        if (t < 1 || t > tipos.length) {
            System.out.println("Tipo inválido.");
            return;
        }
        TipoMovimentacao tipo = tipos[t - 1];
        Fornecedor fornecedor = null;
        BigDecimal custo = null;
        if (tipo == TipoMovimentacao.ENTRADA_COMPRA) {
            fornecedor = escolher("Fornecedor", fornecedores, x -> true, Fornecedor::getId);
            if (fornecedor == null) return;
            custo = lerDecimal("Custo unitário");
        }
        String dica = tipo == TipoMovimentacao.AJUSTE_MANUAL ? " (negativo para retirar)" : "";
        p.movimentar(tipo, lerInt("Quantidade" + dica), custo, fornecedor);
        System.out.println("Movimentação registrada. Saldo de " + p.getNome() + ": " + p.getSaldoEstoque());
    }

    private static void verEstoque() {
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        for (Produto p : produtos) {
            System.out.println("  #" + p.getId() + " " + p.getNome() + " (" + p.getTamanho() + ", "
                    + p.getCategoria().getNome() + ") " + rs(p.getValor()) + " | saldo: " + p.getSaldoEstoque());
        }
    }

    // ---------------------------------------------------------------- Vendedor

    private static void menuVendedor(Vendedor vendedor) {
        while (true) {
            System.out.println("\nCaixa: " + caixa.getStatus());
            int opcao = menu("VENDEDOR",
                    "Abrir caixa",
                    "Fechar caixa",
                    "Abrir comanda",
                    "Adicionar item à comanda",
                    "Ver comandas",
                    "Fechar comanda",
                    "Realizar venda",
                    "Registrar pagamento",
                    "Cancelar venda",
                    "Alterar minha senha");
            if (opcao == 0) return;
            executar(() -> {
                switch (opcao) {
                    case 1 -> {
                        caixa.abrir(vendedor, lerDecimal("Valor de abertura"));
                        System.out.println("Caixa aberto.");
                    }
                    case 2 -> {
                        caixa.fechar();
                        System.out.println("Caixa fechado. Valor de fechamento: " + rs(caixa.getValorFechamento()));
                    }
                    case 3 -> abrirComanda();
                    case 4 -> adicionarItem();
                    case 5 -> verComandas();
                    case 6 -> {
                        Comanda c = escolher("Comanda", comandas, x -> x.getStatus() == StatusComanda.ABERTA, Comanda::getId);
                        if (c != null) {
                            c.fecharComanda();
                            System.out.println("Comanda fechada. Total: " + rs(c.calcularTotal()));
                        }
                    }
                    case 7 -> realizarVenda(vendedor);
                    case 8 -> registrarPagamento();
                    case 9 -> {
                        Venda v = escolher("Venda", vendas, x -> x.getStatus() == StatusVenda.PENDENTE, Venda::getId);
                        if (v != null) {
                            vendedor.cancelarVenda(v);
                            System.out.println("Venda cancelada. A comanda #" + v.getComanda().getId() + " voltou a ficar aberta.");
                        }
                    }
                    case 10 -> alterarSenha(vendedor);
                    default -> System.out.println("Opção inválida.");
                }
            });
        }
    }

    private static void abrirComanda() {
        List<Usuario> clientes = usuarios.stream().filter(u -> u instanceof Cliente).toList();
        Usuario u = escolher("Cliente", clientes, x -> true, Usuario::getId);
        if (u == null) return;
        Comanda c = new Comanda((Cliente) u);
        comandas.add(c);
        System.out.println("Comanda #" + c.getId() + " aberta para " + u.getNome() + ".");
    }

    private static void adicionarItem() {
        Comanda c = escolher("Comanda", comandas, x -> x.getStatus() == StatusComanda.ABERTA, Comanda::getId);
        if (c == null) return;
        Produto p = escolher("Produto", produtos, x -> true, Produto::getId);
        if (p == null) return;
        c.adicionarItem(new ItemComanda(p, lerInt("Quantidade"), lerTexto("Observações (Enter para nenhuma)", true)));
        System.out.println("Item adicionado. Total da comanda: " + rs(c.calcularTotal()));
    }

    private static void verComandas() {
        if (comandas.isEmpty()) {
            System.out.println("Nenhuma comanda.");
            return;
        }
        for (Comanda c : comandas) {
            System.out.println("  " + descrever(c));
            for (ItemComanda i : c.getItens()) {
                System.out.println("      " + i.getQuantidade() + "x " + i.getProduto().getNome()
                        + " " + rs(i.getSubtotal()) + " - " + i.getStatusPreparo());
            }
        }
    }

    private static void realizarVenda(Vendedor vendedor) {
        Comanda c = escolher("Comanda", comandas, x -> x.getStatus() == StatusComanda.AGUARDANDO_PAGAMENTO, Comanda::getId);
        if (c == null) return;
        System.out.println("Total da comanda: " + rs(c.calcularTotal()));
        Venda v = vendedor.realizarVenda(c, lerDecimal("Desconto (0 para nenhum)"));
        vendas.add(v);
        System.out.println("Venda #" + v.getId() + " registrada. Valor a pagar: " + rs(v.getValorTotal()));
    }

    private static void registrarPagamento() {
        if (caixa.getStatus() != StatusCaixa.ABERTO) {
            System.out.println("Abra o caixa antes de receber pagamentos.");
            return;
        }
        Venda v = escolher("Venda", vendas, x -> x.getStatus() == StatusVenda.PENDENTE, Venda::getId);
        if (v == null) return;
        System.out.println("Falta pagar: " + rs(v.getValorRestante()));
        FormaPagamento forma = escolher("Forma de pagamento", formasPagamento, x -> true, FormaPagamento::getId);
        if (forma == null) return;
        Pagamento p = v.registrarPagamento(lerDecimal("Valor pago"), forma, caixa);
        if (p.getTroco().signum() > 0) {
            System.out.println("Troco: " + rs(p.getTroco()));
        }
        if (v.getStatus() == StatusVenda.CONCLUIDA) {
            System.out.println("Venda concluída! Comanda #" + v.getComanda().getId() + " fechada.");
        } else {
            System.out.println("Pagamento parcial. Ainda falta: " + rs(v.getValorRestante()));
        }
    }

    // ---------------------------------------------------------------- Cozinheiro

    private static void menuCozinheiro(Cozinheiro cozinheiro) {
        while (true) {
            int opcao = menu("COZINHEIRO",
                    "Ver comandas abertas",
                    "Iniciar preparo de item",
                    "Finalizar preparo de item",
                    "Alterar minha senha");
            if (opcao == 0) return;
            executar(() -> {
                switch (opcao) {
                    case 1 -> {
                        List<Comanda> abertas = comandas.stream().filter(c -> c.getStatus() == StatusComanda.ABERTA).toList();
                        if (abertas.isEmpty()) System.out.println("Nenhuma comanda aberta.");
                        abertas.forEach(cozinheiro::visualizarComanda);
                    }
                    case 2 -> {
                        ItemComanda i = escolherItem(StatusPreparo.PENDENTE);
                        if (i != null) {
                            cozinheiro.iniciarPreparo(i);
                            System.out.println("Preparo iniciado.");
                        }
                    }
                    case 3 -> {
                        ItemComanda i = escolherItem(StatusPreparo.EM_PREPARO);
                        if (i != null) {
                            cozinheiro.finalizarPreparo(i);
                            System.out.println("Item pronto!");
                        }
                    }
                    case 4 -> alterarSenha(cozinheiro);
                    default -> System.out.println("Opção inválida.");
                }
            });
        }
    }

    private static ItemComanda escolherItem(StatusPreparo status) {
        List<ItemComanda> itens = new ArrayList<>();
        for (Comanda c : comandas) {
            for (ItemComanda i : c.getItens()) {
                if (i.getStatusPreparo() == status) itens.add(i);
            }
        }
        return escolher("Item", itens, x -> true, ItemComanda::getId);
    }

    // ---------------------------------------------------------------- Comum

    private static void alterarSenha(Funcionario f) {
        if (!f.autenticar(lerTexto("Senha atual"))) {
            System.out.println("Senha atual incorreta.");
            return;
        }
        f.alterarSenha(lerTexto("Nova senha"));
        System.out.println("Senha alterada.");
    }

    /** Mostra um menu numerado (0 = voltar) e devolve a opção digitada. */
    private static int menu(String titulo, String... opcoes) {
        System.out.println();
        System.out.println("--- " + titulo + " ---");
        for (int i = 0; i < opcoes.length; i++) {
            System.out.println((i + 1) + " - " + opcoes[i]);
        }
        System.out.println("0 - Sair (logout)");
        return lerInt("Opção");
    }

    /** Executa uma ação do menu; se uma regra do sistema for violada, mostra a mensagem e continua. */
    private static void executar(Runnable acao) {
        try {
            acao.run();
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    /** Lista os itens que passam no filtro e pede o id de um deles. Devolve null se não houver ou se o id não existir. */
    private static <T> T escolher(String rotulo, List<T> lista, Predicate<T> filtro, ToIntFunction<T> id) {
        List<T> opcoes = lista.stream().filter(filtro).toList();
        if (opcoes.isEmpty()) {
            System.out.println("Nenhum(a) " + rotulo.toLowerCase() + " disponível.");
            return null;
        }
        for (T t : opcoes) {
            System.out.println("  " + descrever(t));
        }
        int escolhido = lerInt("Id do(a) " + rotulo.toLowerCase());
        for (T t : opcoes) {
            if (id.applyAsInt(t) == escolhido) return t;
        }
        System.out.println("Id não encontrado.");
        return null;
    }

    private static void listar(List<? extends Usuario> lista) {
        if (lista.isEmpty()) System.out.println("Nenhum usuário.");
        for (Usuario u : lista) System.out.println("  " + descrever(u));
    }

    private static String descrever(Object o) {
        if (o instanceof Cliente c) return "#" + c.getId() + " " + c.getNome() + " (Cliente, CPF " + c.getCpf() + ", tel " + c.getTelefone() + ")";
        if (o instanceof Funcionario f) return "#" + f.getId() + " " + f.getNome() + " (" + f.getClass().getSimpleName() + ", usuário " + f.getUsername() + ", tel " + f.getTelefone() + ")";
        if (o instanceof Categoria c) return "#" + c.getId() + " " + c.getNome();
        if (o instanceof Fornecedor f) return "#" + f.getId() + " " + f.getRazaoSocial() + " (CNPJ " + f.getCnpj() + ")";
        if (o instanceof Produto p) return "#" + p.getId() + " " + p.getNome() + " " + p.getTamanho() + " - " + rs(p.getValor());
        if (o instanceof FormaPagamento f) return "#" + f.getId() + " " + f.getNome();
        if (o instanceof Comanda c) return "#" + c.getId() + " " + c.getCliente().getNome() + " - " + c.getStatus() + " - " + rs(c.calcularTotal());
        if (o instanceof Venda v) return "#" + v.getId() + " comanda #" + v.getComanda().getId() + " - total " + rs(v.getValorTotal()) + " - falta " + rs(v.getValorRestante());
        if (o instanceof ItemComanda i) {
            String obs = i.getObservacoes().isBlank() ? "" : " [" + i.getObservacoes() + "]";
            return "#" + i.getId() + " " + i.getQuantidade() + "x " + i.getProduto().getNome() + obs + " - " + i.getStatusPreparo();
        }
        return String.valueOf(o);
    }

    // ---------------------------------------------------------------- Leitura do teclado

    private static String lerTexto(String rotulo) {
        return lerTexto(rotulo, false);
    }

    private static String lerTexto(String rotulo, boolean podeVazio) {
        while (true) {
            System.out.print(rotulo + ": ");
            String s = in.nextLine().trim();
            if (podeVazio || !s.isEmpty()) return s;
            System.out.println("Campo obrigatório.");
        }
    }

    private static int lerInt(String rotulo) {
        while (true) {
            String s = lerTexto(rotulo);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro.");
            }
        }
    }

    private static BigDecimal lerDecimal(String rotulo) {
        while (true) {
            String s = lerTexto(rotulo).replace(',', '.');
            try {
                return new BigDecimal(s);
            } catch (NumberFormatException e) {
                System.out.println("Digite um valor, por exemplo 12,50.");
            }
        }
    }

    private static String rs(BigDecimal valor) {
        return "R$ " + valor.setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ',');
    }
}
