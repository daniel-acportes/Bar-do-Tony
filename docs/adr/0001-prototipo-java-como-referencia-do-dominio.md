# Protótipo Java como implementação de referência do domínio

O protótipo em `src/` é o entregável da disciplina (foco em POO e fidelidade ao diagrama de classes) e continua em terminal, com dados só em memória. As specs de novas funcionalidades descrevem regras de negócio e classes, não telas ou APIs. Os RNFs de infraestrutura do documento (web/touch, tempo real, bcrypt, ACID, backup, disponibilidade, carga) ficam fora do escopo do protótipo, e as telas do documento são projeto de interface, não algo a implementar aqui.

## Consequences

Quem vier implementar algo "como na tela do Dashboard" deve modelar a regra (ex.: Total em Fiado derivado) no domínio, não construir interface web.
