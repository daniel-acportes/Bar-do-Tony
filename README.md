# PI-Unileste: protótipo em Java

Protótipo do diagrama de classes em [assets/diagrama-UML.png](assets/diagrama-UML.png): bar/restaurante com comandas, vendas, caixa e estoque. Sem banco de dados: o `Main` é um menu de terminal (opções numéricas) e os dados ficam só na memória enquanto o programa roda.

Na primeira execução o sistema pede para criar o Admin. Depois, cada Funcionário entra com usuário e senha e vê o menu do seu perfil:

- **Admin**: cadastra funcionários, clientes, categorias, produtos e fornecedores; movimenta e consulta o estoque.
- **Vendedor**: abre/fecha o caixa, abre comandas, adiciona itens, fecha comandas, realiza e cancela vendas, registra pagamentos.
- **Cozinheiro**: vê as comandas abertas e inicia/finaliza o preparo dos itens.

As formas de pagamento (Dinheiro, PIX, Cartão) já vêm cadastradas.

## Como rodar

Requer Java 17 ou superior.

```sh
javac -encoding UTF-8 -d out src/Main.java src/*/*.java
java -cp out Main
```

## Módulos

Cada pasta em `src/` corresponde a uma cor do diagrama:

| Módulo | Cor | Responsabilidade | Classes |
| --- | --- | --- | --- |
| `pessoas` | azul | Quem usa o sistema e o que cada perfil pode fazer | Usuario, Cliente, Funcionario, Vendedor, Admin, Cozinheiro |
| `atendimento` | amarelo | Comandas, itens pedidos e preparo na cozinha | Comanda, ItemComanda, StatusComanda, StatusPreparo |
| `estoque` | vermelho | Produtos, categorias, fornecedores e movimentações | Produto, Categoria, Fornecedor, MovimentacaoEstoque, TipoMovimentacao |
| `financeiro` | verde | Vendas, pagamentos e caixa | Venda, Pagamento, FormaPagamento, Caixa, StatusVenda, StatusCaixa |

`Main.java` fica na raiz de `src/` e contém o menu de terminal.

## Diferenças em relação ao diagrama

- **Pagamento** pertence à Venda (composição); o Caixa apenas guarda os pagamentos que recebeu. O diagrama desenhava duas composições, o que o UML não permite.
- **StatusPreparo** (PENDENTE, EM_PREPARO, PRONTO) foi criado em `ItemComanda`, porque `iniciarPreparo`/`finalizarPreparo` não tinham onde guardar o estado.
- **Saldo de estoque** é calculado a partir das movimentações (`Produto.getSaldoEstoque()`); concluir uma venda gera `SAIDA_VENDA` automaticamente.
- **Caixa** ganhou `abrir(operador, valorAbertura)` e `fechar()`.
- Métodos passaram a receber os objetos de que precisam, por exemplo `realizarVenda(Comanda, desconto)`, `iniciarPreparo(ItemComanda)` e `registrarPagamento(valor, forma, caixa)`. `realizarVenda` e `registrarPagamento` devolvem a Venda/Pagamento criados.
- IDs são gerados automaticamente por um contador em cada classe.
- A multiplicidade Comanda–ItemComanda ("0..") foi lida como 0..*.
- Tipos: `decimal` → `BigDecimal`, `Date` → `LocalDateTime`.
