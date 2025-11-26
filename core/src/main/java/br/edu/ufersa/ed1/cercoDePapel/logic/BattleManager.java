package br.edu.ufersa.ed1.cercoDePapel.logic;

import br.edu.ufersa.ed1.cercoDePapel.entities.BoardUnit;
import br.edu.ufersa.ed1.cercoDePapel.entities.Card;
import br.edu.ufersa.ed1.cercoDePapel.entities.UnitCard;
import br.edu.ufersa.ed1.cercoDePapel.util.FileController;
import br.edu.ufersa.ed1.cercoDePapel.util.MyLinkedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BattleManager {
    private List<BoardUnit> units;
    public final int MAP_WIDTH = 10;
    public final int MAP_HEIGHT = 7;
    private Random random;

    private TurnManager turnManager;

    // Deck e Mão usando ListaEncadeada
    private Deck playerDeck;
    private List<Card> playerHand;
    private final int MAX_HAND_SIZE = 5;

    public BattleManager() {
        this.units = new ArrayList<>();
        this.random = new Random();
        this.turnManager = new TurnManager();

        this.playerDeck = new Deck();
        this.playerHand = new MyLinkedList<>();

        initializeDeck();
        startBattle();
    }

    // --- O MÉTODO QUE FALTAVA ---
    public void addUnit(BoardUnit unit) {
        units.add(unit);
    }
    // ----------------------------

    private void initializeDeck() {
        try {
            Set<UnitCard> allUnits = FileController.readAllUnits();

            // Adiciona cartas ao deck
            for (UnitCard u : allUnits) {
                playerDeck.addCard(u);
                playerDeck.addCard(u);
            }
            playerDeck.shuffle();
            System.out.println("Deck inicializado com " + playerDeck.size() + " cartas.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startBattle() {
        drawCard();
        drawCard();
        drawCard();
    }

    public void drawCard() {
        if (playerHand.size() >= MAX_HAND_SIZE) {
            System.out.println("Mão cheia!");
            return;
        }
        Card card = playerDeck.draw();
        if (card != null) {
            playerHand.add(card);
            System.out.println("Comprou: " + card.name);
        } else {
            System.out.println("Deck vazio! (Fadiga)");
        }
    }

    // Lógica de Invocação
    public boolean tryPlayUnitCard(UnitCard card, int targetX, int targetY) {
        if (!turnManager.isPlayerTurn()) return false;

        if (turnManager.getCurrentMana() < card.cost) {
            System.out.println("Mana insuficiente!");
            return false;
        }

        if (getUnitAt(targetX, targetY) != null) {
            System.out.println("Espaço ocupado!");
            return false;
        }

        if (targetX > 2) {
            System.out.println("Apenas pode invocar nas primeiras colunas!");
            return false;
        }

        if (turnManager.spendMana(card.cost)) {
            BoardUnit newUnit = new BoardUnit(card, targetX, targetY, true);
            newUnit.setMoved(true);
            newUnit.setAttacked(true);
            addUnit(newUnit); // Usa o método addUnit

            playerHand.remove(card);

            System.out.println("Invocou " + card.name);
            return true;
        }
        return false;
    }

    public TurnManager getTurnManager() { return turnManager; }
    public List<BoardUnit> getUnits() { return units; }

    public MyLinkedList<Card> getPlayerHand() { return (MyLinkedList<Card>) playerHand; }

    public Deck getPlayerDeck() { return playerDeck; }

    public BoardUnit getUnitAt(int x, int y) {
        for (BoardUnit unit : units) {
            if (unit.getGridX() == x && unit.getGridY() == y) {
                return unit;
            }
        }
        return null;
    }

    public boolean tryMove(BoardUnit unit, int targetX, int targetY) {
        if (!turnManager.isPlayerTurn() || !unit.isPlayer()) return false;
        if (unit.hasMoved() || unit.hasAttacked()) return false;
        if (targetX < 0 || targetX >= MAP_WIDTH || targetY < 0 || targetY >= MAP_HEIGHT) return false;
        if (getUnitAt(targetX, targetY) != null) return false;

        int distance = Math.abs(unit.getGridX() - targetX) + Math.abs(unit.getGridY() - targetY);

        if (distance <= unit.getCardData().mov) {
            unit.setPosition(targetX, targetY);
            unit.setMoved(true);
            return true;
        }
        return false;
    }

    public boolean tryAttack(BoardUnit attacker, BoardUnit target) {
        if (!turnManager.isPlayerTurn() || !attacker.isPlayer()) return false;
        if (attacker.hasAttacked()) return false;
        if (attacker.isPlayer() == target.isPlayer()) return false;

        int distance = Math.abs(attacker.getGridX() - target.getGridX()) +
            Math.abs(attacker.getGridY() - target.getGridY());

        if (distance <= attacker.getCardData().range) {
            int min = attacker.getCardData().dmgMin;
            int max = attacker.getCardData().dmgMax;
            int damage = random.nextInt((max - min) + 1) + min;

            target.takeDamage(damage);
            System.out.println("Ataque: " + damage + " de dano!");

            if (target.isDead()) {
                units.remove(target);
            }

            attacker.setAttacked(true);
            attacker.setMoved(true);
            return true;
        }
        return false;
    }

    public void endTurn() {
        turnManager.endCurrentTurn();
        if (turnManager.isPlayerTurn()) {
            resetUnitsState();
            drawCard();
        } else {
            System.out.println("Inimigo jogando...");
            endTurn();
        }
    }

    private void resetUnitsState() {
        for(BoardUnit u : units) {
            u.newTurn();
        }
    }
}
