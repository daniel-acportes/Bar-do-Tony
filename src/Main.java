
import atendimento.*;
import estoque.*;
import financeiro.*;
import pessoas.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface gráfica do Bar do Tony.
 * A camada visual utiliza objetos Swing e listeners; as regras de negócio
 * continuam nas classes de domínio existentes.
 */
public class Main {
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static final List<Categoria> categorias = new ArrayList<>();
    private static final List<Produto> produtos = new ArrayList<>();
    private static final List<Fornecedor> fornecedores = new ArrayList<>();
    private static final List<Comanda> comandas = new ArrayList<>();
    private static final List<Venda> vendas = new ArrayList<>();
    private static final List<FormaPagamento> formasPagamento = List.of(
            new FormaPagamento("Dinheiro"), new FormaPagamento("PIX"), new FormaPagamento("Cartão"));
    private static final Caixa caixa = new Caixa();

    private static final Color FUNDO = new Color(22, 25, 31);
    private static final Color PAINEL = new Color(32, 37, 45);
    private static final Color PAINEL_CLARO = new Color(42, 48, 58);
    private static final Color AMARELO = new Color(245, 176, 45);
    private static final Color TEXTO = new Color(239, 241, 244);
    private static final Color SUAVE = new Color(167, 176, 188);
    private static JFrame janela;
    private static Funcionario logado;
    private static JPanel conteudo;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            aplicarTema();
            janela = new JFrame("Bar do Tony | Sistema de Gestão");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setMinimumSize(new Dimension(1050, 680));
            janela.setSize(1250, 780);
            janela.setLocationRelativeTo(null);
            if (usuarios.isEmpty()) telaCriarAdmin();
        });
    }

    private static void aplicarTema() {
        UIManager.put("OptionPane.background", PAINEL);
        UIManager.put("Panel.background", PAINEL);
        UIManager.put("OptionPane.messageForeground", TEXTO);
        UIManager.put("Button.background", PAINEL_CLARO);
        UIManager.put("Button.foreground", TEXTO);
        UIManager.put("TextField.background", new Color(51, 58, 69));
        UIManager.put("TextField.foreground", TEXTO);
        UIManager.put("PasswordField.background", new Color(51, 58, 69));
        UIManager.put("PasswordField.foreground", TEXTO);
        UIManager.put("ComboBox.background", PAINEL_CLARO);
        UIManager.put("ComboBox.foreground", TEXTO);
    }

    private static void telaCriarAdmin() {
        JPanel form = formPanel();
        JTextField nome = campo();
        JTextField telefone = campo();
        JTextField user = campo();
        JPasswordField senha = new JPasswordField(20);
        estilizar(senha);
        addField(form, "Nome completo", nome);
        addField(form, "Telefone", telefone);
        addField(form, "Usuário de acesso", user);
        addField(form, "Senha", senha);
        int op = JOptionPane.showConfirmDialog(null, form, "Primeiro acesso • Criar administrador",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (op != JOptionPane.OK_OPTION) { System.exit(0); return; }
        if (vazio(nome.getText()) || vazio(user.getText()) || senha.getPassword().length == 0) {
            aviso("Preencha nome, usuário e senha para continuar."); telaCriarAdmin(); return;
        }
        Admin admin = new Admin(nome.getText().trim(), telefone.getText().trim(),
                user.getText().trim(), new String(senha.getPassword()), usuarios);
        admin.cadastrarUsuario(admin);
        telaLogin();
    }

    private static void telaLogin() {
        JPanel base = new JPanel(new GridBagLayout());
        base.setBackground(FUNDO);
        JPanel card = new JPanel();
        card.setBackground(PAINEL);
        card.setBorder(new EmptyBorder(35, 42, 35, 42));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel marca = titulo("BAR DO TONY", 30);
        marca.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitulo = texto("Sistema de gestão • Acesso de funcionários", SUAVE, 13);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField usuario = campo();
        JPasswordField senha = new JPasswordField(22); estilizar(senha);
        card.add(marca); card.add(Box.createVerticalStrut(7)); card.add(subtitulo);
        card.add(Box.createVerticalStrut(28));
        card.add(texto("USUÁRIO", SUAVE, 11)); card.add(Box.createVerticalStrut(6)); card.add(usuario);
        card.add(Box.createVerticalStrut(15));
        card.add(texto("SENHA", SUAVE, 11)); card.add(Box.createVerticalStrut(6)); card.add(senha);
        card.add(Box.createVerticalStrut(22));
        JButton entrar = botao("ENTRAR", AMARELO);
        entrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(entrar);
        entrar.addActionListener(e -> autenticar(usuario.getText().trim(), new String(senha.getPassword())));
        senha.addActionListener(e -> autenticar(usuario.getText().trim(), new String(senha.getPassword())));
        base.add(card);
        janela.setContentPane(base); janela.setVisible(true);
    }

    private static void autenticar(String username, String password) {
        for (Usuario u : usuarios) {
            if (u instanceof Funcionario f && f.getUsername().equals(username) && f.autenticar(password)) {
                logado = f; montarPrincipal(); return;
            }
        }
        aviso("Usuário ou senha incorretos.");
    }

    private static void montarPrincipal() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FUNDO);
        JPanel lateral = new JPanel();
        lateral.setBackground(PAINEL);
        lateral.setBorder(new EmptyBorder(22, 16, 18, 16));
        lateral.setPreferredSize(new Dimension(235, 0));
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        JLabel logo = titulo("BAR DO TONY", 22); logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(logo); lateral.add(Box.createVerticalStrut(5));
        lateral.add(texto("GESTÃO DO ESTABELECIMENTO", SUAVE, 9));
        lateral.add(Box.createVerticalStrut(28));
        lateral.add(texto(logado.getNome().toUpperCase(), TEXTO, 13));
        lateral.add(texto(logado.getClass().getSimpleName(), AMARELO, 11));
        lateral.add(Box.createVerticalStrut(24));
        adicionarNav(lateral, "Visão geral", () -> mostrarDashboard());
        if (logado instanceof Admin) {
            adicionarNav(lateral, "Funcionários e clientes", () -> telaUsuarios());
            adicionarNav(lateral, "Produtos e categorias", () -> telaProdutos());
            adicionarNav(lateral, "Fornecedores", () -> telaFornecedores());
            adicionarNav(lateral, "Estoque", () -> telaEstoque());
        }
        if (logado instanceof Vendedor) {
            adicionarNav(lateral, "Comandas", () -> telaComandas());
            adicionarNav(lateral, "Vendas e pagamentos", () -> telaVendas());
            adicionarNav(lateral, "Caixa", () -> telaCaixa());
        }
        if (logado instanceof Cozinheiro) adicionarNav(lateral, "Painel da cozinha", () -> telaCozinha());
        lateral.add(Box.createVerticalGlue());
        adicionarNav(lateral, "Alterar minha senha", () -> alterarSenha());
        adicionarNav(lateral, "Sair da conta", () -> telaLogin());
        conteudo = new JPanel(new BorderLayout());
        conteudo.setBackground(FUNDO);
        raiz.add(lateral, BorderLayout.WEST); raiz.add(conteudo, BorderLayout.CENTER);
        janela.setContentPane(raiz); mostrarDashboard(); janela.revalidate(); janela.repaint();
    }

    private static void adicionarNav(JPanel painel, String nome, Runnable acao) {
        JButton b = new JButton(nome);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setForeground(TEXTO); b.setBackground(PAINEL_CLARO);
        b.setFocusPainted(false); b.setBorder(new EmptyBorder(12, 13, 12, 13));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        b.addActionListener(e -> acao.run());
        painel.add(b); painel.add(Box.createVerticalStrut(7));
    }

    private static void cabecalho(String titulo, String descricao) {
        conteudo.removeAll();
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false); topo.setBorder(new EmptyBorder(28, 32, 20, 32));
        JPanel textos = new JPanel(); textos.setOpaque(false); textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(titulo(titulo, 26)); textos.add(Box.createVerticalStrut(5));
        textos.add(texto(descricao, SUAVE, 12));
        topo.add(textos, BorderLayout.WEST);
        conteudo.add(topo, BorderLayout.NORTH);
    }

    private static JPanel corpo() {
        JPanel p = new JPanel(new BorderLayout(0, 15));
        p.setOpaque(false); p.setBorder(new EmptyBorder(0, 32, 30, 32));
        conteudo.add(p, BorderLayout.CENTER);
        // A tela é montada em etapas (cabeçalho + conteúdo). Atualiza o layout
        // depois que o método da tela terminar de adicionar todos os componentes.
        SwingUtilities.invokeLater(() -> {
            if (conteudo != null) {
                conteudo.revalidate();
                conteudo.repaint();
            }
        });
        return p;
    }

    private static void mostrarDashboard() {
        cabecalho("Visão geral", "Resumo da operação do Bar do Tony");
        JPanel p = corpo();
        JPanel cards = new JPanel(new GridLayout(1, 3, 14, 0)); cards.setOpaque(false);
        cards.add(cardNumero("PRODUTOS", String.valueOf(produtos.size()), "Itens cadastrados"));
        cards.add(cardNumero("COMANDAS ABERTAS", String.valueOf(comandas.stream().filter(c -> c.getStatus()==StatusComanda.ABERTA).count()), "Em atendimento"));
        cards.add(cardNumero("VENDAS PENDENTES", String.valueOf(vendas.stream().filter(v -> v.getStatus()==StatusVenda.PENDENTE).count()), "Aguardando pagamento"));
        JPanel inferior = new JPanel(new BorderLayout(12,12)); inferior.setOpaque(false);
        inferior.add(cardTexto("Bem-vindo ao sistema", "Utilize o menu lateral para acessar as operações disponíveis para o seu perfil."), BorderLayout.CENTER);
        inferior.add(cardTexto("Status do caixa", caixa.getStatus().toString(), "ABERTO".equals(caixa.getStatus().toString()) ? AMARELO : SUAVE), BorderLayout.EAST);
        p.add(cards, BorderLayout.NORTH); p.add(inferior, BorderLayout.CENTER);
    }

    private static JPanel cardNumero(String rotulo, String numero, String apoio) {
        JPanel c = cardBase(); c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.add(texto(rotulo, SUAVE, 11)); c.add(Box.createVerticalStrut(12));
        c.add(titulo(numero, 30)); c.add(Box.createVerticalStrut(5)); c.add(texto(apoio, SUAVE, 11)); return c;
    }
    private static JPanel cardTexto(String titulo, String descricao) { return cardTexto(titulo, descricao, TEXTO); }
    private static JPanel cardTexto(String titulo, String descricao, Color cor) {
        JPanel c=cardBase(); c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        c.add(texto(titulo,cor,16)); c.add(Box.createVerticalStrut(10)); c.add(texto(descricao,SUAVE,12)); return c;
    }
    private static JPanel cardBase() { JPanel c=new JPanel(); c.setBackground(PAINEL); c.setBorder(new EmptyBorder(20,20,20,20)); return c; }

    private static void telaUsuarios() {
        cabecalho("Funcionários e clientes", "Gerencie os usuários cadastrados no estabelecimento");
        JPanel p=corpo(); DefaultTableModel m=modelo("ID","Nome","Perfil","Telefone","Identificação");
        for(Usuario u:usuarios) m.addRow(new Object[]{u.getId(),u.getNome(),u.getClass().getSimpleName(),u.getTelefone(),
                u instanceof Cliente c ? c.getCpf() : ((Funcionario)u).getUsername()});
        JTable tabela=tabela(m); p.add(new JScrollPane(tabela),BorderLayout.CENTER);
        JPanel a=acoes(); JButton func=botao("Novo funcionário",AMARELO), cli=botao("Novo cliente",PAINEL_CLARO);
        JButton editar=botao("Editar selecionado",PAINEL_CLARO), excluir=botao("Excluir selecionado",new Color(145,60,55));
        a.add(func);a.add(cli);a.add(editar);a.add(excluir);p.add(a,BorderLayout.SOUTH);
        func.addActionListener(e->dialogFuncionario()); cli.addActionListener(e->dialogCliente());
        editar.addActionListener(e->{Usuario u=selecionadoUsuario(tabela);if(u!=null)dialogEditarUsuario(u);});
        excluir.addActionListener(e->{Usuario u=selecionadoUsuario(tabela);if(u!=null&&u!=logado&&confirmar("Excluir "+u.getNome()+"?")){usuarios.remove(u);telaUsuarios();}});
    }

    private static Usuario selecionadoUsuario(JTable t) {
        int r=t.getSelectedRow(); if(r<0){aviso("Selecione um registro.");return null;}
        int id=(int)t.getValueAt(r,0); return usuarios.stream().filter(u->u.getId()==id).findFirst().orElse(null);
    }
    private static void dialogFuncionario() {
        JTextField nome=campo(),tel=campo(),user=campo();JPasswordField pass=new JPasswordField(18);estilizar(pass);
        JComboBox<String> tipo=new JComboBox<>(new String[]{"Vendedor","Cozinheiro","Admin"});
        JPanel f=formPanel();addField(f,"Nome",nome);addField(f,"Telefone",tel);addField(f,"Usuário",user);addField(f,"Senha",pass);addField(f,"Perfil",tipo);
        if(!confirmForm("Cadastrar funcionário",f))return;
        if(vazio(nome.getText())||vazio(user.getText())||pass.getPassword().length==0){aviso("Preencha os campos obrigatórios.");return;}
        if(usuarios.stream().anyMatch(u->u instanceof Funcionario x&&x.getUsername().equals(user.getText().trim()))){aviso("Este usuário já existe.");return;}
        String n=nome.getText().trim(),t=tel.getText().trim(),us=user.getText().trim(),s=new String(pass.getPassword());
        Funcionario novo=switch((String)tipo.getSelectedItem()){case "Vendedor"->new Vendedor(n,t,us,s);case "Cozinheiro"->new Cozinheiro(n,t,us,s);default->new Admin(n,t,us,s,usuarios);};
        usuarios.add(novo);telaUsuarios();
    }
    private static void dialogCliente() {
        JTextField n=campo(),t=campo(),cpf=campo();JPanel f=formPanel();addField(f,"Nome",n);addField(f,"Telefone",t);addField(f,"CPF",cpf);
        if(confirmForm("Cadastrar cliente",f)){if(vazio(n.getText())||vazio(cpf.getText())){aviso("Nome e CPF são obrigatórios.");return;}usuarios.add(new Cliente(n.getText().trim(),t.getText().trim(),cpf.getText().trim()));telaUsuarios();}
    }
    private static void dialogEditarUsuario(Usuario u) {
        JTextField n=campo(),t=campo();n.setText(u.getNome());t.setText(u.getTelefone());
        JPanel f=formPanel();addField(f,"Nome",n);addField(f,"Telefone",t);
        if(confirmForm("Editar usuário",f)){u.atualizarDados(n.getText().trim(),t.getText().trim());telaUsuarios();}
    }

    private static void telaProdutos() {
        cabecalho("Produtos e categorias","Cadastre o catálogo que será utilizado nas comandas");
        JPanel p=corpo();DefaultTableModel m=modelo("ID","Produto","Categoria","Tamanho","Preço","Saldo");
        for(Produto x:produtos)m.addRow(new Object[]{x.getId(),x.getNome(),x.getCategoria().getNome(),x.getTamanho(),dinheiro(x.getValor()),x.getSaldoEstoque()});
        p.add(new JScrollPane(tabela(m)),BorderLayout.CENTER);JPanel a=acoes();
        JButton novo=botao("Novo produto",AMARELO),cat=botao("Nova categoria",PAINEL_CLARO);
        a.add(novo);a.add(cat);p.add(a,BorderLayout.SOUTH);
        novo.addActionListener(e->dialogProduto());cat.addActionListener(e->dialogCategoria());
    }
    private static void dialogCategoria() {
        JTextField n=campo();JPanel f=formPanel();addField(f,"Nome da categoria",n);
        if(confirmForm("Nova categoria",f)&&!vazio(n.getText())){categorias.add(new Categoria(n.getText().trim()));aviso("Categoria cadastrada.");}
    }
    private static void dialogProduto() {
        if(categorias.isEmpty()){aviso("Cadastre uma categoria antes de criar produtos.");return;}
        JTextField n=campo(),tam=campo(),preco=campo();JComboBox<Categoria> cat=new JComboBox<>(categorias.toArray(Categoria[]::new));
        JPanel f=formPanel();addField(f,"Nome",n);addField(f,"Tamanho",tam);addField(f,"Preço (ex.: 12,50)",preco);addField(f,"Categoria",cat);
        if(confirmForm("Novo produto",f)){try{produtos.add(new Produto(n.getText().trim(),decimal(preco.getText()),tam.getText().trim(),(Categoria)cat.getSelectedItem()));telaProdutos();}catch(Exception ex){aviso("Confira os dados informados.");}}
    }

    private static void telaFornecedores() {
        cabecalho("Fornecedores","Empresas que abastecem o estabelecimento");
        JPanel p=corpo();DefaultTableModel m=modelo("ID","Razão social","CNPJ");
        for(Fornecedor f:fornecedores)m.addRow(new Object[]{f.getId(),f.getRazaoSocial(),f.getCnpj()});
        p.add(new JScrollPane(tabela(m)),BorderLayout.CENTER);JPanel a=acoes();JButton b=botao("Novo fornecedor",AMARELO);a.add(b);p.add(a,BorderLayout.SOUTH);
        b.addActionListener(e->{JTextField n=campo(),c=campo();JPanel f=formPanel();addField(f,"Razão social",n);addField(f,"CNPJ",c);if(confirmForm("Fornecedor",f)){fornecedores.add(new Fornecedor(n.getText().trim(),c.getText().trim()));telaFornecedores();}});
    }

    private static void telaEstoque() {
        cabecalho("Controle de estoque","Consulte saldos e registre entradas, perdas e ajustes");
        JPanel p=corpo();DefaultTableModel m=modelo("ID","Produto","Categoria","Saldo atual","Preço");
        for(Produto x:produtos)m.addRow(new Object[]{x.getId(),x.getNome(),x.getCategoria().getNome(),x.getSaldoEstoque(),dinheiro(x.getValor())});
        p.add(new JScrollPane(tabela(m)),BorderLayout.CENTER);JPanel a=acoes();JButton mov=botao("Registrar movimentação",AMARELO);a.add(mov);p.add(a,BorderLayout.SOUTH);
        mov.addActionListener(e->dialogMovimentacao());
    }
    private static void dialogMovimentacao() {
        if(produtos.isEmpty()){aviso("Cadastre produtos primeiro.");return;}
        JComboBox<Produto> prod=new JComboBox<>(produtos.toArray(Produto[]::new));
        JComboBox<TipoMovimentacao> tipo=new JComboBox<>(TipoMovimentacao.values());JTextField qtd=campo(),custo=campo();
        JComboBox<Fornecedor> forn=new JComboBox<>(fornecedores.toArray(Fornecedor[]::new));
        JPanel f=formPanel();addField(f,"Produto",prod);addField(f,"Tipo",tipo);addField(f,"Quantidade (ajuste pode ser negativo)",qtd);
        addField(f,"Custo unitário (entrada de compra)",custo);if(!fornecedores.isEmpty())addField(f,"Fornecedor",forn);
        if(confirmForm("Movimentar estoque",f)){try{TipoMovimentacao t=(TipoMovimentacao)tipo.getSelectedItem();Fornecedor fo=forn.getSelectedItem() instanceof Fornecedor?(Fornecedor)forn.getSelectedItem():null;
            BigDecimal c=t==TipoMovimentacao.ENTRADA_COMPRA?decimal(custo.getText()):null;
            ((Produto)prod.getSelectedItem()).movimentar(t,Integer.parseInt(qtd.getText().trim()),c,fo);telaEstoque();
        }catch(Exception ex){aviso("Não foi possível registrar: "+ex.getMessage());}}
    }

    private static void telaComandas() {
        cabecalho("Comandas","Abra comandas, adicione produtos e encaminhe para pagamento");
        JPanel p=corpo();DefaultTableModel m=modelo("ID","Cliente","Status","Itens","Total");
        for(Comanda c:comandas)m.addRow(new Object[]{c.getId(),c.getCliente().getNome(),c.getStatus(),c.getItens().size(),dinheiro(c.calcularTotal())});
        JTable t=tabela(m);p.add(new JScrollPane(t),BorderLayout.CENTER);JPanel a=acoes();
        JButton abrir=botao("Abrir comanda",AMARELO),item=botao("Adicionar item",PAINEL_CLARO),fechar=botao("Fechar comanda",PAINEL_CLARO);
        a.add(abrir);a.add(item);a.add(fechar);p.add(a,BorderLayout.SOUTH);
        abrir.addActionListener(e->dialogAbrirComanda());item.addActionListener(e->{Comanda c=selecionarComanda(t);if(c!=null)dialogAdicionarItem(c);});
        fechar.addActionListener(e->{Comanda c=selecionarComanda(t);if(c!=null)try{c.fecharComanda();telaComandas();}catch(Exception ex){aviso(ex.getMessage());}});
    }
    private static Comanda selecionarComanda(JTable t){int r=t.getSelectedRow();if(r<0){aviso("Selecione uma comanda.");return null;}int id=(int)t.getValueAt(r,0);return comandas.stream().filter(c->c.getId()==id).findFirst().orElse(null);}
    private static void dialogAbrirComanda() {
        List<Cliente> cs=usuarios.stream().filter(u->u instanceof Cliente).map(u->(Cliente)u).toList();
        if(cs.isEmpty()){aviso("Cadastre um cliente primeiro.");return;}
        JComboBox<Cliente> c=new JComboBox<>(cs.toArray(Cliente[]::new));JPanel f=formPanel();addField(f,"Cliente",c);
        if(confirmForm("Abrir comanda",f)){comandas.add(new Comanda((Cliente)c.getSelectedItem()));telaComandas();}
    }
    private static void dialogAdicionarItem(Comanda c) {
        if(c.getStatus()!=StatusComanda.ABERTA){aviso("A comanda não está aberta.");return;}
        if(produtos.isEmpty()){aviso("Cadastre produtos primeiro.");return;}
        JComboBox<Produto> p=new JComboBox<>(produtos.toArray(Produto[]::new));JTextField q=campo(),obs=campo();
        JPanel f=formPanel();addField(f,"Produto",p);addField(f,"Quantidade",q);addField(f,"Observações",obs);
        if(confirmForm("Adicionar item à comanda #"+c.getId(),f)){try{c.adicionarItem(new ItemComanda((Produto)p.getSelectedItem(),Integer.parseInt(q.getText()),obs.getText()));telaComandas();}catch(Exception ex){aviso(ex.getMessage());}}
    }

    private static void telaVendas() {
        cabecalho("Vendas e pagamentos","Gere vendas a partir de comandas fechadas e registre pagamentos");
        JPanel p=corpo();DefaultTableModel m=modelo("ID","Comanda","Cliente","Total","Restante","Status");
        for(Venda v:vendas)m.addRow(new Object[]{v.getId(),v.getComanda().getId(),v.getComanda().getCliente().getNome(),dinheiro(v.getValorTotal()),dinheiro(v.getValorRestante()),v.getStatus()});
        JTable t=tabela(m);p.add(new JScrollPane(t),BorderLayout.CENTER);JPanel a=acoes();
        JButton gerar=botao("Gerar venda",AMARELO),pagar=botao("Registrar pagamento",PAINEL_CLARO),cancelar=botao("Cancelar venda",PAINEL_CLARO);
        a.add(gerar);a.add(pagar);a.add(cancelar);p.add(a,BorderLayout.SOUTH);
        gerar.addActionListener(e->dialogGerarVenda());pagar.addActionListener(e->{Venda v=selecionarVenda(t);if(v!=null)dialogPagamento(v);});
        cancelar.addActionListener(e->{Venda v=selecionarVenda(t);if(v!=null)try{((Vendedor)logado).cancelarVenda(v);telaVendas();}catch(Exception ex){aviso(ex.getMessage());}});
    }
    private static Venda selecionarVenda(JTable t){int r=t.getSelectedRow();if(r<0){aviso("Selecione uma venda.");return null;}int id=(int)t.getValueAt(r,0);return vendas.stream().filter(v->v.getId()==id).findFirst().orElse(null);}
    private static void dialogGerarVenda() {
        List<Comanda> cs=comandas.stream().filter(c->c.getStatus()==StatusComanda.AGUARDANDO_PAGAMENTO).toList();
        if(cs.isEmpty()){aviso("Não há comandas aguardando pagamento. Feche uma comanda primeiro.");return;}
        JComboBox<Comanda> c=new JComboBox<>(cs.toArray(Comanda[]::new));JTextField d=campo();d.setText("0");
        JPanel f=formPanel();addField(f,"Comanda",c);addField(f,"Desconto",d);
        if(confirmForm("Gerar venda",f)){try{vendas.add(((Vendedor)logado).realizarVenda((Comanda)c.getSelectedItem(),decimal(d.getText())));telaVendas();}catch(Exception ex){aviso(ex.getMessage());}}
    }
    private static void dialogPagamento(Venda v) {
        if(caixa.getStatus()!=StatusCaixa.ABERTO){aviso("Abra o caixa antes de registrar pagamentos.");return;}
        JComboBox<FormaPagamento> forma=new JComboBox<>(formasPagamento.toArray(FormaPagamento[]::new));JTextField valor=campo();valor.setText(v.getValorRestante().toPlainString());
        JPanel f=formPanel();addField(f,"Valor restante: "+dinheiro(v.getValorRestante()),new JLabel(""));addField(f,"Forma de pagamento",forma);addField(f,"Valor recebido",valor);
        if(confirmForm("Pagamento da venda #"+v.getId(),f)){try{Pagamento pg=v.registrarPagamento(decimal(valor.getText()),(FormaPagamento)forma.getSelectedItem(),caixa);aviso("Pagamento registrado. Troco: "+dinheiro(pg.getTroco()));telaVendas();}catch(Exception ex){aviso(ex.getMessage());}}
    }

    private static void telaCaixa() {
        cabecalho("Caixa","Abertura, acompanhamento e fechamento do caixa");
        JPanel p=corpo();JPanel info=cardTexto("Situação atual: "+caixa.getStatus(),
                caixa.getStatus()==StatusCaixa.ABERTO?"Operação aberta por "+caixa.getOperador().getNome():"O caixa está fechado.",
                caixa.getStatus()==StatusCaixa.ABERTO?AMARELO:TEXTO);
        JPanel a=acoes();JButton abrir=botao("Abrir caixa",AMARELO),fechar=botao("Fechar caixa",PAINEL_CLARO);
        a.add(abrir);a.add(fechar);p.add(info,BorderLayout.CENTER);p.add(a,BorderLayout.SOUTH);
        abrir.setEnabled(caixa.getStatus()==StatusCaixa.FECHADO);fechar.setEnabled(caixa.getStatus()==StatusCaixa.ABERTO);
        abrir.addActionListener(e->{JTextField v=campo();JPanel f=formPanel();addField(f,"Valor inicial",v);if(confirmForm("Abertura de caixa",f)){try{caixa.abrir(logado,decimal(v.getText()));telaCaixa();}catch(Exception ex){aviso(ex.getMessage());}}});
        fechar.addActionListener(e->{if(confirmar("Deseja fechar o caixa?")){try{caixa.fechar();aviso("Caixa fechado. Valor: "+dinheiro(caixa.getValorFechamento()));telaCaixa();}catch(Exception ex){aviso(ex.getMessage());}}});
    }

    private static void telaCozinha() {
        cabecalho("Painel da cozinha","Acompanhe os itens e atualize o andamento do preparo");
        JPanel p=corpo();DefaultTableModel m=modelo("Item","Comanda","Produto","Quantidade","Observações","Status");
        for(Comanda c:comandas)for(ItemComanda i:c.getItens())m.addRow(new Object[]{i.getId(),c.getId(),i.getProduto().getNome(),i.getQuantidade(),i.getObservacoes(),i.getStatusPreparo()});
        JTable t=tabela(m);p.add(new JScrollPane(t),BorderLayout.CENTER);JPanel a=acoes();
        JButton iniciar=botao("Iniciar preparo",AMARELO),pronto=botao("Marcar como pronto",PAINEL_CLARO);a.add(iniciar);a.add(pronto);p.add(a,BorderLayout.SOUTH);
        iniciar.addActionListener(e->alterarPreparo(t,true));pronto.addActionListener(e->alterarPreparo(t,false));
    }
    private static void alterarPreparo(JTable t,boolean iniciar) {
        int r=t.getSelectedRow();if(r<0){aviso("Selecione um item.");return;}int id=(int)t.getValueAt(r,0);
        ItemComanda item=comandas.stream().flatMap(c->c.getItens().stream()).filter(i->i.getId()==id).findFirst().orElse(null);
        if(item==null)return;try{if(iniciar)((Cozinheiro)logado).iniciarPreparo(item);else((Cozinheiro)logado).finalizarPreparo(item);telaCozinha();}catch(Exception ex){aviso(ex.getMessage());}
    }

    private static void alterarSenha() {
        JPasswordField atual=new JPasswordField(18),nova=new JPasswordField(18);estilizar(atual);estilizar(nova);
        JPanel f=formPanel();addField(f,"Senha atual",atual);addField(f,"Nova senha",nova);
        if(confirmForm("Alterar senha",f)){if(!logado.autenticar(new String(atual.getPassword()))){aviso("Senha atual incorreta.");return;}try{logado.alterarSenha(new String(nova.getPassword()));aviso("Senha alterada.");}catch(Exception ex){aviso(ex.getMessage());}}
    }

    // Componentes reutilizáveis: cada botão registra um ActionListener.
    private static JPanel formPanel(){JPanel p=new JPanel(new GridBagLayout());p.setBackground(PAINEL);p.setBorder(new EmptyBorder(12,12,12,12));return p;}
    private static int linha=0;
    private static void addField(JPanel p,String rotulo,Component c){
        GridBagConstraints g=new GridBagConstraints();g.insets=new Insets(7,7,7,7);g.gridy=linha;g.gridx=0;g.anchor=GridBagConstraints.LINE_END;
        JLabel l=new JLabel(rotulo);l.setForeground(TEXTO);p.add(l,g);g.gridx=1;g.anchor=GridBagConstraints.LINE_START;g.fill=GridBagConstraints.HORIZONTAL;g.weightx=1;p.add(c,g);linha++;
    }
    private static boolean confirmForm(String titulo,JPanel p){linha=0;int r=JOptionPane.showConfirmDialog(janela,p,titulo,JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);return r==JOptionPane.OK_OPTION;}
    private static JTextField campo(){JTextField t=new JTextField(20);estilizar(t);return t;}
    private static void estilizar(JComponent c){c.setBackground(new Color(51,58,69));c.setForeground(TEXTO);if(c instanceof javax.swing.text.JTextComponent tc)tc.setCaretColor(TEXTO);c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(72,80,92)),new EmptyBorder(7,9,7,9)));}
    private static JButton botao(String nome,Color cor){JButton b=new JButton(nome);b.setBackground(cor);b.setForeground(cor.equals(AMARELO)?new Color(30,30,30):TEXTO);b.setFocusPainted(false);b.setBorder(new EmptyBorder(10,16,10,16));b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));return b;}
    private static JPanel acoes(){JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,9,8));p.setOpaque(false);return p;}
    private static JLabel titulo(String s,int tam){JLabel l=new JLabel(s);l.setForeground(TEXTO);l.setFont(new Font("SansSerif",Font.BOLD,tam));return l;}
    private static JLabel texto(String s,Color c,int tam){JLabel l=new JLabel(s);l.setForeground(c);l.setFont(new Font("SansSerif",Font.PLAIN,tam));return l;}
    private static DefaultTableModel modelo(String... cols){return new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};}
    private static JTable tabela(DefaultTableModel m){JTable t=new JTable(m);t.setRowHeight(34);t.setBackground(PAINEL);t.setForeground(TEXTO);t.setGridColor(new Color(58,65,75));t.setSelectionBackground(new Color(100,77,37));t.setSelectionForeground(TEXTO);t.getTableHeader().setBackground(PAINEL_CLARO);t.getTableHeader().setForeground(TEXTO);return t;}
    private static boolean confirmar(String s){return JOptionPane.showConfirmDialog(janela,s,"Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;}
    private static void aviso(String s){JOptionPane.showMessageDialog(janela,s,"Bar do Tony",JOptionPane.INFORMATION_MESSAGE);}
    private static boolean vazio(String s){return s==null||s.trim().isEmpty();}
    private static BigDecimal decimal(String s){return new BigDecimal(s.trim().replace(',','.'));}
    private static String dinheiro(BigDecimal v){return "R$ "+v.setScale(2,RoundingMode.HALF_UP).toPlainString().replace('.',',');}
}
