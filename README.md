# AI-Java-Solitaire
Solitaire game built in Java fully by AI, as part of EXA871 UEFS discipline

Jogo de Paciência (variante Klondike) para desktop, em Java 21 com interface JavaFX. A especificação está em [REQUISITOS.md](REQUISITOS.md).

## Requisitos de ambiente

- JDK 21 ou superior (o código é compilado com `--release 21`)
- Maven 3.8 ou superior
- Acesso ao repositório Maven Central na primeira compilação, para baixar o JavaFX 21.0.5 e o JUnit 5

## Compilação e execução

Na raiz do projeto:

```bash
mvn clean javafx:run
```

Compila e abre a janela do jogo.

```bash
mvn clean test
```

Compila e roda os testes automatizados, incluindo a simulação de 500 partidas automáticas.

```bash
mvn clean package
```

Gera o `.jar` em `target/`. Para jogar, use `mvn javafx:run`, porque o JavaFX precisa estar no module path.

> Em JDKs mais novos que o 21, o JavaFX 21 imprime avisos sobre `--enable-native-access` e `sun.misc.Unsafe`. São só avisos e não afetam o jogo.

## Como jogar

| Ação | Como fazer |
|---|---|
| Comprar uma carta | Clicar no monte (canto superior esquerdo) |
| Nova partida | Clicar no monte quando ele estiver vazio (aparece o símbolo ↻) |
| Mover cartas | Arrastar com o mouse. Ao arrastar uma carta do tableau, as cartas sobre ela vão junto. O destino fica verde se aceita a jogada e vermelho se não aceita |
| Desfazer | Botão **Desfazer** |
| Jogada automática | Botão **Jogada automática** (faz uma jogada por clique) |
| Desativar o jogo automático | Desmarcar **Jogo automático ativado** |

## Estrutura

| Pacote | Conteúdo |
|---|---|
| `solitaire.model` | Cartas, naipes, ranks, baralho, pilha genérica e serialização. Não depende do JavaFX |
| `solitaire.game` | Locais (monte, descarte, fundações e colunas), regras, ações reversíveis, histórico e o motor `Game`. Os métodos que alteram estado são package-private; o resto do programa só enxerga as interfaces somente leitura `GameView` e `Location` |
| `solitaire.strategy` | Jogo automático: `Strategy`, `PriorityStrategy` e `DisabledStrategy` |
| `solitaire.ui` | Interface JavaFX: `SolitaireApp`, `BoardView` e `CardView` |

## Interpretações adotadas

Onde os requisitos admitiam mais de uma leitura, adotei estas:

1. **RF01:** clicar no monte vazio inicia uma nova partida. O descarte não é reciclado para o monte. A nova partida não é uma jogada: ela limpa o histórico.
2. **RF14/RF15:** qualquer fundação vazia aceita qualquer Ás, e o naipe da fundação passa a ser o desse Ás.
3. **RF21:** podem ser movidos o topo do descarte, o topo de uma fundação e qualquer carta virada para cima numa coluna. O monte só serve para comprar.
4. **RF28:** a jogada automática acontece sob demanda, uma por clique no botão.
5. **RF29:** quando há empate, valem as colunas da esquerda para a direita e as fundações da 1ª à 4ª. Na prioridade 5, só se move a sequência que começa na carta visível mais próxima da base.
6. **RF25:** a ação nula não entra no histórico, porque não muda o estado.

## Rastreabilidade dos requisitos

Caminhos relativos a `src/main/java/solitaire/`; testes em `src/test/java/solitaire/`.

### Requisitos funcionais

| Requisito | Implementação (classe → métodos) | Testes |
|---|---|---|
| **RF01** Nova partida | `game/Game` → `newGame()`, `deal()`, `drawFromStock()`; `ui/BoardView` → clique no `Slot` do monte | `GameTest.initialDeal`, `GameTest.drawFromEmptyStockStartsNewGame` |
| **RF02** Vitória | `game/Game` → `isWon()`; `ui/SolitaireApp` → listener que exibe "Você venceu!" | `GameTest.winDetection` |
| **RF03** Pontuação | `game/Game` → `score()` (não aparece na interface) | `GameTest.winDetection`, `SimulationTest` (estatísticas de pontuação) |
| **RF04** Composição e unicidade | `model/Card` → construtor privado, `of(Rank, Suit)`, `all()`, `fromId(String)`; `model/Suit`; `model/Rank` | `CardTest.deckHas52UniqueCards`, `CardTest.sameRankAndSuitIsSameInstance` |
| **RF05** Ordem dos ranks | `model/Rank` → ordem do enum, `isImmediatelyAbove(Rank)`, `isImmediatelyBelow(Rank)` | `CardTest.ranksHaveTotalOrder` |
| **RF06** Cor dos naipes | `model/SuitColor`; `model/Suit` → `color()`, `hasSameColorAs(Suit)`; `model/Card` → `color()` | `CardTest.suitColors` |
| **RF07** Embaralhamento | `model/Deck` → `shuffled(Random)` | `CardTest.shuffleKeepsAllCardsInDifferentOrders` |
| **RF08** Pilha | `model/CardStack` → `push`, `pop`, `cards`, `pushAll`, `removeFrom(int)` (os dois últimos para sequências do RF18); `game/Pile` → `add`, `removeTop` | `CardTest.stackKeepsOrderAndTop` |
| **RF09** Consultas à pilha | `model/CardStack` → `isEmpty()`, `peek()`; `game/Location` → `isEmpty()`, `peek()` | `CardTest.stackKeepsOrderAndTop` |
| **RF10** Locais | `game/Location` (interface comum), `game/Pile` (implementação base de `Stock`, `Discard`, `Foundation`, `Column`); `game/Game` → `locationOf(Card)` | `GameTest.locationOfOnlyForAccessibleCards` |
| **RF11** Compra | `game/Move.Draw`; `game/DrawAction` → `execute()`; `game/Game` → `toAction(Move)`, `drawFromStock()` | `GameTest.drawAndUndoReturnSameCard` |
| **RF12** Desfazer compra | `game/DrawAction` → `undo()` (confere que é a mesma carta) | `GameTest.drawAndUndoReturnSameCard` |
| **RF13** Restrição do descarte | `game/Discard` → `accepts(List)` sempre `false`; `game/Stock` → `accepts(List)` sempre `false` | `GameTest.discardAndStockAreNotTargets` |
| **RF14** Estrutura das fundações | `game/Game` → `FOUNDATION_COUNT`, `foundations()`; `game/Foundation` | `GameTest.winDetection` |
| **RF15** Empilhamento na fundação | `game/Foundation` → `accepts(List)`: uma carta por vez; vazia aceita Ás; senão mesmo naipe e rank imediatamente superior | `GameTest.foundationRules`, `GameTest.foundationAcceptsOneCardAtATime` |
| **RF16** Organização inicial do tableau | `game/Game` → `COLUMN_COUNT`, `deal()`; `game/Column` → `addHidden(Card)`, `revealTop()` | `GameTest.initialDeal` |
| **RF17** Empilhamento no tableau | `game/Column` → `accepts(List)`: vazia aceita Rei; senão rank imediatamente inferior e cor diferente | `GameTest.columnRules` |
| **RF18** Movimentação de sequências | `game/Column` → `movableSequenceFrom(Card)`; `game/MoveAction` → `execute()`, `undo()`; `game/Game` → `transferAction(Card, Location)`; `ui/BoardView.Slot` → `startDrag(CardView)` | `GameTest.sequenceMovesTogetherAndRevealIsUndone` |
| **RF19** Revelação automática | `game/Column` → `followUpAfterRemoving(int)`; `game/RevealAction` → `execute()`, `undo()`; `game/Game` → `transferAction` (monta `CompositeAction(MoveAction, RevealAction)`) | `GameTest.sequenceMovesTogetherAndRevealIsUndone` |
| **RF20** Consultas ao tableau | `game/GameView` / `game/Game` → `columns()`, `sequenceFrom(Card)`, `isFaceUp(Card)`, `isLowestFaceUp(Card)`, `isKingAtBase(Card)`; `game/Column` → `lowestFaceUp()`, `isAtBase(Card)` | `GameTest.locationOfOnlyForAccessibleCards`, `GameTest.kingAtBase`, `GameTest.queriesDoNotChangeState` |
| **RF21** Legalidade | `game/Game` → `canMove(Card, Location)`, `transferAction(Card, Location)` (jogada ilegal vira `NullAction`); `game/Location` → `accepts(List)`; `ui/BoardView.Slot` → `onDragOver`, `onDragDropped` (só consultam o jogo) | `GameTest.illegalMoveChangesNothingAndDoesNotNotify`, `GameTest.columnRules` |
| **RF22** Ações reversíveis | `game/Action` → `execute()`, `undo()`, `hasEffect()`; implementações `DrawAction`, `MoveAction`, `RevealAction`, `CompositeAction`, `NullAction` | `GameTest.drawAndUndoReturnSameCard`, `SimulationTest` |
| **RF23** Ação composta | `game/CompositeAction` → `execute()` (em ordem), `undo()` (ordem inversa), `hasEffect()` | `GameTest.sequenceMovesTogetherAndRevealIsUndone` |
| **RF24** Ação nula | `game/NullAction` → `INSTANCE`, `hasEffect()` = `false` | `PriorityStrategyTest.priority7NoMove` |
| **RF25** Histórico e desfazer | `game/Game` → `history`, `perform(Action)`, `undo()`, `canUndo()`; `ui/SolitaireApp` → botão "Desfazer" | `SimulationTest.automaticPlayTerminatesAndUndoAllRestoresInitialState` |
| **RF26** Notificação | `game/GameListener` → `onGameChanged()`; `game/Game` → `addListener`, `removeListener`, `notifyListeners()` (chamado em `perform`, `undo`, `newGame`); `ui/BoardView` → `refresh()` | `GameTest.changesAndUndoNotify`, `GameTest.illegalMoveChangesNothingAndDoesNotNotify` |
| **RF27** Consulta somente leitura | `game/GameView` e `game/Location` (só métodos de consulta; os mutadores de `Pile` e subclasses são package-private); `model/CardStack` → `cards()` devolve cópia imutável | `GameTest.queriesDoNotChangeState` |
| **RF28** Jogada automática | `game/Game` → `autoPlay()`, `setStrategy(Strategy)`; `strategy/DisabledStrategy` → `suggest` devolve `Move.none()`; `ui/SolitaireApp` → botão "Jogada automática" e caixa "Jogo automático ativado" | `PriorityStrategyTest.disabledStrategyNeverPlays`, `PriorityStrategyTest.priority7NoMove` |
| **RF29** Ordem de prioridade | `strategy/PriorityStrategy` → `suggest(GameView)` (prioridades 1 a 7, nesta ordem), `firstAccepting(...)` | `PriorityStrategyTest.priority1…` até `priority7NoMove` |
| **RF30** Sem ciclos | `strategy/PriorityStrategy` → prioridade 5 só move a carta para a qual `isLowestFaceUp` é verdadeiro e `isKingAtBase` é falso | `PriorityStrategyTest.kingAtBaseIsNotMovedToEmptyColumn`, `PriorityStrategyTest.priority5IgnoresMovesWithoutGain`, `SimulationTest` (500 partidas terminam) |
| **RF31** Separação entre decidir e executar | Decidir: `strategy/Strategy` → `suggest(GameView)` devolve um `game/Move`. Executar: `game/Game` → `autoPlay()` → `play(Move)` → `toAction(Move)` (valida) → `perform(Action)` (histórico e notificação) | `GameTest.queriesDoNotChangeState`, `SimulationTest` |
| **RF32** Movimentação direta | `ui/BoardView.Slot` → `startDrag(CardView)` (arrasta a sequência inteira), `onDragOver(DragEvent)` (verde/vermelho via `game.canMove`), `onDragDropped(DragEvent)` → `game.play(Move.transfer(...))` | Verificação manual na interface |

### Requisitos não funcionais

| Requisito | Implementação (classe → métodos) | Testes |
|---|---|---|
| **RNF01** Consistência de visibilidade | `game/Column` → conjunto `faceUp` indexado pela própria carta (não pela posição), `isFaceUp(Card)`; `ui/BoardView` → `refresh()` e `Slot.render()` redesenham tudo a partir do estado atual a cada notificação | `GameTest.sequenceMovesTogetherAndRevealIsUndone`, `GameTest.initialDeal` |
| **RNF02** Tratamento uniforme de ações | `game/Action` (interface única); `game/Game` → `perform(Action)` decide só por `hasEffect()`, sem `instanceof`; `strategy/DisabledStrategy` passa pelo mesmo `autoPlay()` | `PriorityStrategyTest.disabledStrategyNeverPlays`, `GameTest.illegalMoveChangesNothingAndDoesNotNotify` |
| **RNF03** Separação entre apresentação e regras | `ui/BoardView` não tem regras: consulta `game.canMove`, `game.locationOf`, `game.sequenceFrom` e envia `game.play`/`game.drawFromStock` | Inspeção do código de `ui/` |
| **RNF04** Separação entre estratégia e regras | `strategy/Strategy` e `strategy/PriorityStrategy` recebem só `GameView` (somente leitura) e não têm acesso aos mutadores package-private | `GameTest.queriesDoNotChangeState` |
| **RNF05** Fidelidade na serialização | `model/CardSerializer` → `serialize(List<Card>)`, `deserialize(String)` (devolve as mesmas instâncias, na mesma ordem); usado no `Dragboard` em `ui/BoardView.Slot` → `startDrag`, `draggedCards` | `CardTest.serializationPreservesIdentityAndOrder` |
