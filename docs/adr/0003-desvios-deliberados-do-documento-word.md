# Desvios deliberados em relação ao documento "Bar do Tony.docx"

O documento da disciplina (`docs/Word/Bar do Tony.docx`) é a fonte original dos requisitos, mas algumas regras foram decididas de outro jeito durante a modelagem. Onde o documento e o `CONTEXT.md` divergem, vale o `CONTEXT.md`, e o documento deve ser corrigido:

- **RF16**: Venda não é "opcionalmente" vinculada a Comanda; toda Venda nasce de uma Comanda (compra rápida usa o Cliente Balcão).
- **RF18**: só Venda não quitada pode ser cancelada; não há estorno de venda concluída. Venda em Fiado não é cancelada.
- **RF04**: CPF do Cliente é opcional; só é exigido para pendurar no Fiado (minimização de dados, LGPD/RNF06).
- **RF15**: o Preparo é acompanhado por item, e só para Categorias que passam pela cozinha.
- **Tabela 2 / RNF05**: o Admin pode executar qualquer função dos demais perfis.
- **Tabela POO**: `quantidadeEstoque` não é atributo guardado; o Saldo de Estoque é derivado das Movimentações.
- **Fiado**: o minimundo e as telas o tratam como central, mas não há RF; deve ganhar requisitos próprios (pendurar, acerto de fiado, total em fiado).
