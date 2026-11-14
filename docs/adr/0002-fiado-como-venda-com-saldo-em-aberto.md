# Fiado é uma Venda com saldo em aberto

O fiado (dor central do minimundo) é modelado como uma Venda que fica com valor restante, quitada depois por Pagamentos parciais registrados no Caixa aberto no dia em que cada pagamento acontece. O Total em Fiado é a soma dos valores restantes dessas Vendas.

## Considered Options

- **FormaPagamento "Fiado"**: rejeitada porque o Caixa registraria como recebido um dinheiro que não entrou.
- **Comanda aberta até o cliente pagar**: rejeitada porque o estoque não baixaria e a cozinha continuaria vendo a comanda.
