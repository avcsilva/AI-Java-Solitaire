# Resumo das respostas

## Resposta ao prompt 01 — plano e implementação

- **Leitura e ambiente:** li o `REQUISITOS.md` e conferi o ambiente: JDK 27.0.1 e Maven 3.10.0, com compilação em `--release 21`.
- **Plano apresentado antes de qualquer código, e aprovado:**
  - **Pacotes:**
    - `solitaire.model`: cartas, ranks, naipes, baralho, pilha genérica e serialização.
    - `solitaire.game`: locais (monte, descarte, fundações, colunas), ações reversíveis, histórico e o motor `Game`.
    - `solitaire.strategy`: jogo automático (`PriorityStrategy` e `DisabledStrategy`).
    - `solitaire.ui`: a interface JavaFX.
  - **Requisitos:** cada um mapeado para os tipos que o implementam.
  - **Interpretações das ambiguidades:**
    - Clicar no monte vazio inicia uma nova partida, sem reciclar o descarte (RF01).
    - Qualquer fundação vazia aceita qualquer Ás (RF14/RF15).
    - Origens válidas são o topo do descarte, o topo das fundações e as cartas viradas das colunas (RF21).
    - A jogada automática acontece uma por clique em um botão, e uma caixa de seleção a desativa (RF28).
    - Empates se resolvem da esquerda para a direita. Na prioridade 5, só se move a carta visível mais próxima da base (RF29).
    - A ação nula não entra no histórico (RF25).
- **Implementação:**
  - **Build:** `pom.xml` com JavaFX 21.0.5, JUnit 5.11.4 e `javafx-maven-plugin` 0.0.8.
  - **Código:** 29 classes de produção e 5 arquivos de teste.
  - **Cartas:** desenhadas em `Canvas`, sem imagens externas.
  - **Movimentação:** arraste com destaque verde/vermelho do destino, consultando `game.canMove`.
- **Verificação:**
  - `mvn clean test` passou. Isso inclui uma simulação de 500 partidas automáticas: todas terminaram, desfazer tudo voltou ao estado inicial e houve 6 vitórias, com média de 7,8 cartas nas fundações.
  - `mvn clean javafx:run` abriu a janela "Paciência", com apenas avisos do JDK 27 sobre o JavaFX 21. Fechei a janela em seguida.
- **Não verificado:** o arraste na interface não foi testado manualmente.

## Resposta ao prompt 02 — commit da implementação

- Criei um `.gitignore` excluindo `target/`.
- Fiz o commit `c9d7845` ("Implement Klondike Solitaire with JavaFX") no `main`, com 36 arquivos.
- Não fiz push.

## Resposta ao prompt 03 — README

- Reescrevi o `README.md`, mantendo as duas linhas originais. Ele traz:
  - os requisitos de ambiente;
  - os comandos de compilação, teste e execução;
  - como jogar;
  - a estrutura de pacotes;
  - as interpretações adotadas;
  - duas tabelas de rastreabilidade (RF01–RF32 e RNF01–RNF05), cada uma ligando o requisito às classes, métodos e testes correspondentes.
- Indiquei os requisitos sem teste automatizado:
  - RF32 depende de verificação manual, que ainda não foi feita.
  - RNF03 é verificado por inspeção do código.
  - RNF04 é garantido pela própria estrutura: a estratégia só recebe `GameView`.
- Nenhum código foi alterado.

## Resposta ao prompt 04 — commit do README

- Fiz o commit `6ca69f5` ("Add README with build instructions and requirements traceability") no `main`.
- Não fiz push.

## Resposta ao prompt 05 — registro da conversa

- Criei a pasta `conversas/`:
  - `prompt_01.md` a `prompt_05.md`, com o texto exato de cada prompt;
  - este resumo.
- Fiz o commit e o push para o repositório remoto. Os commits anteriores já estavam no remoto.
