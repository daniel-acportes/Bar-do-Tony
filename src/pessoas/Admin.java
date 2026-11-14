package pessoas;

import java.util.List;

public class Admin extends Funcionario {
    /** Lista de usuários cadastrados que este Admin gerencia (substitui um banco de dados). */
    private final List<Usuario> cadastro;

    public Admin(String nome, String telefone, String username, String senha, List<Usuario> cadastro) {
        super(nome, telefone, username, senha);
        this.cadastro = cadastro;
    }

    public void cadastrarUsuario(Usuario usuario) {
        cadastro.add(usuario);
    }

    public void alterarUsuario(Usuario usuario, String nome, String telefone) {
        if (!cadastro.contains(usuario)) {
            throw new IllegalArgumentException("Usuário não cadastrado: " + usuario);
        }
        usuario.atualizarDados(nome, telefone);
    }

    public void excluirUsuario(Usuario usuario) {
        if (!cadastro.remove(usuario)) {
            throw new IllegalArgumentException("Usuário não cadastrado: " + usuario);
        }
    }
}
