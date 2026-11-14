# PI-Unileste: Gestão de Bar/Restaurante

Sistema de atendimento por comandas, vendas, caixa e controle de estoque de um bar/restaurante.

## Language

### Atendimento

**Comanda**:
Registro aberto em nome de um Cliente onde se acumulam os itens consumidos até o fechamento.
_Avoid_: Pedido, conta, mesa

**ItemComanda**:
Uma linha da Comanda: um Produto, a quantidade, o preço unitário cobrado e observações.
_Avoid_: Item de pedido

**Preparo**:
O andamento de um ItemComanda na cozinha: pendente, em preparo ou pronto. É acompanhado por item, não pela Comanda inteira.
_Avoid_: Produção, pedido da cozinha

**Fechamento da Comanda**:
O momento em que a Comanda para de aceitar itens e passa a aguardar pagamento. Se a Venda gerada for cancelada, a Comanda volta a ficar aberta.

### Catálogo e estoque

**Produto**:
Algo vendável no estabelecimento, com nome, valor de venda e tamanho.
_Avoid_: Item, mercadoria

**Categoria**:
Agrupamento que classifica Produtos (ex.: bebidas, porções).

**Fornecedor**:
Empresa, identificada por CNPJ, que fornece Produtos em entradas de estoque.

**MovimentacaoEstoque**:
Registro de uma entrada ou saída de quantidade de um Produto, com o motivo dado pelo seu tipo (compra, venda, perda/avaria, ajuste manual).
_Avoid_: Lançamento, transação

**Saldo de Estoque**:
A quantidade disponível de um Produto, sempre derivada da soma das suas Movimentações de Estoque; não existe como valor guardado à parte.
_Avoid_: Estoque atual, quantidade em estoque

### Financeiro

**Venda**:
Fechamento financeiro de uma Comanda: valor total, desconto e situação do pagamento.
_Avoid_: Pedido, transação

**Pagamento**:
Um valor pago em uma Forma de Pagamento para quitar (total ou parcialmente) uma Venda. Pertence à Venda e é recebido num Caixa aberto.

**Troco**:
O excedente de um Pagamento sobre o que ainda faltava pagar na Venda, devolvido ao Cliente.

**FormaPagamento**:
Meio pelo qual um Pagamento é feito (ex.: dinheiro, PIX, cartão).

**Caixa**:
Período de operação financeira entre abertura e fechamento, com valores de abertura e fechamento, operado por um Funcionário.
_Avoid_: Turno, PDV

### Pessoas

**Usuario**:
Qualquer pessoa cadastrada no sistema: um Cliente ou um Funcionário.

**Cliente**:
Usuário identificado por CPF que consome e realiza Comandas. Não acessa o sistema; toda Comanda pertence a exatamente um Cliente.
_Avoid_: Consumidor, freguês

**Funcionario**:
Usuário com credenciais de acesso que opera o sistema; é sempre Vendedor, Admin ou Cozinheiro.
_Avoid_: Colaborador, operador

**Vendedor**:
Funcionário que gera Vendas a partir de Comandas fechadas e pode cancelá-las; fica registrado como responsável pela Venda.
_Avoid_: Garçom, atendente

**Admin**:
Funcionário que gerencia o cadastro de Usuários.
_Avoid_: Gerente, administrador do sistema

**Cozinheiro**:
Funcionário que acompanha o Preparo dos itens das Comandas.
