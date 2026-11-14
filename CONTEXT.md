# PI-Unileste: Gestão de Bar/Restaurante

Sistema de atendimento por comandas, vendas, caixa e controle de estoque de um bar/restaurante.

## Language

### Atendimento

**Comanda**:
Registro aberto em nome de um Cliente onde se acumulam os itens consumidos até o fechamento. Um Cliente tem no máximo uma Comanda aberta por vez (exceto o Cliente Balcão).
_Avoid_: Pedido, conta, mesa

**ItemComanda**:
Uma linha da Comanda: um Produto, a quantidade, o preço unitário cobrado e observações.
_Avoid_: Item de pedido

**Preparo**:
O andamento de um ItemComanda na cozinha: pendente, em preparo ou pronto. É acompanhado por item, não pela Comanda inteira, e só existe para itens cuja Categoria passa pela cozinha.
_Avoid_: Produção, pedido da cozinha

**Fechamento da Comanda**:
O momento em que a Comanda para de aceitar itens e passa a aguardar pagamento. Não depende da cozinha: itens ainda em Preparo continuam sendo preparados. Se a Venda gerada for cancelada, a Comanda volta a ficar aberta.

**Cancelamento da Comanda**:
Encerramento de uma Comanda ainda aberta sem gerar Venda (cliente desistiu, comanda aberta por engano). Itens já prontos viram perda de estoque; itens pendentes simplesmente deixam de existir. Comandas que já aguardam pagamento não são canceladas; cancela-se a Venda.

### Catálogo e estoque

**Produto**:
Algo vendável no estabelecimento, com nome, valor de venda e tamanho.
_Avoid_: Item, mercadoria

**Categoria**:
Agrupamento que classifica Produtos (ex.: cervejas, porções) e define se eles passam pela cozinha.

**Fornecedor**:
Empresa, identificada por CNPJ, que fornece Produtos em entradas de estoque.

**MovimentacaoEstoque**:
Registro de uma entrada ou saída de quantidade de um Produto, com o motivo dado pelo seu tipo (compra, venda, perda/avaria, ajuste manual). Perdas e ajustes sempre trazem uma observação explicando o porquê.
_Avoid_: Lançamento, transação

**Saldo de Estoque**:
A quantidade disponível de um Produto, sempre derivada da soma das suas Movimentações de Estoque; não existe como valor guardado à parte. Pode ficar negativo (vendeu-se o que ainda não foi lançado), o que sinaliza entrada ou ajuste pendente; a venda nunca é bloqueada por falta de saldo.
_Avoid_: Estoque atual, quantidade em estoque

**Baixa por Venda**:
A saída de estoque dos itens de uma Venda, que acontece quando a mercadoria sai de vez: ao ser quitada ou ao ser pendurada no Fiado.

### Financeiro

**Venda**:
Fechamento financeiro de uma Comanda: valor total, desconto e situação do pagamento. Toda Venda nasce de exatamente uma Comanda; não existe venda avulsa.
_Avoid_: Pedido, transação

**Cancelamento da Venda**:
Desfazer uma Venda que ainda não foi quitada; a Comanda volta a ficar aberta. Venda concluída não se cancela.
_Avoid_: Estorno

**Fiado**:
Venda que o Cliente leva sem quitar, ficando com valor restante a pagar até ser quitada por Pagamentos posteriores. Uma Venda só vira Fiado quando um Funcionário a **pendura** explicitamente, e só para Cliente com CPF cadastrado (nunca o Cliente Balcão). Fiado não é cancelado nem perdoado: só sai do fiado sendo pago. Fiado não é dinheiro recebido: só entra no Caixa quando cada Pagamento acontece.

**Acerto de Fiado**:
Valor que o Cliente entrega para abater seu Fiado; é distribuído automaticamente em Pagamentos das suas Vendas em Fiado, da mais antiga para a mais nova.
_Avoid_: Riscar, quitar conta
_Avoid_: Conta, dívida, venda pendente

**Total em Fiado**:
Soma dos valores restantes de todas as Vendas em Fiado; sempre derivado, nunca guardado à parte.

**Pagamento**:
Um valor pago em uma Forma de Pagamento para quitar (total ou parcialmente) uma Venda. Pertence à Venda e é recebido num Caixa aberto.

**Troco**:
O excedente de um Pagamento sobre o que ainda faltava pagar na Venda, devolvido ao Cliente.

**FormaPagamento**:
Meio pelo qual um Pagamento é feito (ex.: dinheiro, PIX, cartão).

**Caixa**:
Período de operação financeira entre abertura e fechamento, com valores de abertura e fechamento, aberto por um Funcionário que fica responsável por ele. Há no máximo um Caixa aberto no bar por vez, e todo Pagamento é recebido nele.
_Avoid_: Turno, PDV

**Fechamento do Caixa**:
Conferência em que o Funcionário informa o dinheiro contado na gaveta e o sistema o compara com o esperado em dinheiro (abertura + Pagamentos em dinheiro − trocos); as demais Formas de Pagamento são totalizadas à parte.

**Quebra de Caixa**:
Diferença entre o dinheiro contado e o esperado no Fechamento do Caixa (positiva é sobra, negativa é falta).
_Avoid_: Diferença de caixa, furo

**Receita do Dia**:
Soma dos Pagamentos recebidos no dia, incluindo Acertos de Fiado antigos; o que foi pendurado no dia não entra.
_Avoid_: Faturamento, vendas do dia

### Pessoas

**Usuario**:
Qualquer pessoa cadastrada no sistema: um Cliente ou um Funcionário.

**Cliente**:
Usuário cadastrado com nome e telefone que consome e realiza Comandas; o CPF é opcional e só é exigido para pendurar no Fiado. Não acessa o sistema: pede a um Funcionário que cadastre ou altere seus dados. Toda Comanda pertence a exatamente um Cliente, e um Cliente com Fiado em aberto não pode ser excluído.
_Avoid_: Consumidor, freguês

**Cliente Balcão**:
Cliente genérico usado para compras rápidas de quem não se identifica; a compra é uma Comanda aberta e fechada na hora em nome dele. O "tipo" de uma Comanda (identificada ou balcão) é derivado do seu Cliente.
_Avoid_: Cliente avulso, venda avulsa

**Funcionario**:
Usuário com credenciais de acesso que opera o sistema; é sempre Admin, Vendedor, Cozinheiro ou Operador de Caixa/Estoque.
_Avoid_: Colaborador

**Operador de Caixa/Estoque**:
Funcionário que abre e fecha o Caixa, recebe Pagamentos, cadastra Fornecedores e registra Movimentações de Estoque (entradas e perdas).
_Avoid_: Caixa (a pessoa), estoquista

**Vendedor**:
Funcionário que gera Vendas a partir de Comandas fechadas e pode cancelá-las; fica registrado como responsável pela Venda.
_Avoid_: Garçom, atendente

**Admin**:
Funcionário que gerencia o cadastro de Usuários e pode também fazer tudo o que os demais perfis fazem (cobrir qualquer função).
_Avoid_: Gerente, administrador do sistema

**Cozinheiro**:
Funcionário que acompanha o Preparo dos itens das Comandas.
