package br.edu.ufersa.ed1.cercoDePapel.logic;

import br.edu.ufersa.ed1.cercoDePapel.entities.BoardUnit;
import br.edu.ufersa.ed1.cercoDePapel.entities.Card;
import br.edu.ufersa.ed1.cercoDePapel.entities.SpellCard;
import br.edu.ufersa.ed1.cercoDePapel.entities.UnitCard;
import br.edu.ufersa.ed1.cercoDePapel.util.FileController;
import br.edu.ufersa.ed1.cercoDePapel.util.MyLinkedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BattleManager {
    // --- ESTADOS DO JOGO ---
    public enum BattleState {
        PLAYING,
        VICTORY,
        DEFEAT
    }

    private BattleState currentState;
    private List<BoardUnit> units;
    public final int MAP_WIDTH = 10;
    public final int MAP_HEIGHT = 7;
    private Random random;

    private TurnManager turnManager;

    // Deck e Mão
    private Deck playerDeck;
    private List<Card> playerHand;
    private final int MAX_HAND_SIZE = 5;

    // Controle de habilidade em andamento
    private BoardUnit unitUsingAbility;

    public BattleManager() {
        this.units = new ArrayList<>();
        this.random = new Random();
        this.turnManager = new TurnManager();
        this.currentState = BattleState.PLAYING;
        this.unitUsingAbility = null;

        this.playerDeck = new Deck();
        this.playerHand = new MyLinkedList<>();

        initializeDeck();
        startBattle();
    }

    public void addUnit(BoardUnit unit) {
        units.add(unit);
    }

    private void initializeDeck() {
        try {
            Set<UnitCard> allUnits = FileController.readAllUnits();
            Set<SpellCard> allSpells = FileController.readAllSpells();

            for (UnitCard u : allUnits) {
                int amount = switch (u.rarity){
                    case COMMON -> 4;
                    case RARE -> 3;
                    case EPIC -> 2;
                    case LEGENDARY -> 1;
                    default -> 0;
                };

                for (int i = 0; i < amount; i++) {
                    playerDeck.addCard(u);
                }
            }

            for (SpellCard s : allSpells) {
                int amount = switch (s.rarity){
                    case COMMON -> 4;
                    case RARE -> 3;
                    case EPIC -> 2;
                    case LEGENDARY -> 1;
                    default -> 0;
                };

                for (int i = 0; i < amount; i++) {
                    playerDeck.addCard(s);
                }
            }

            playerDeck.shuffle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startBattle() {
        drawCard();
        drawCard();
        drawCard();
        drawCard();
    }

    public void drawCard() {
        if (currentState != BattleState.PLAYING) return;

        if (playerHand.size() >= MAX_HAND_SIZE) {
            System.out.println("Mão cheia!");
            return;
        }
        Card card = playerDeck.draw();
        if (card != null) {
            playerHand.add(card);
        }
    }

    private void checkWinCondition() {
        boolean hasPlayer = false;
        boolean hasEnemy = false;

        for (BoardUnit u : units) {
            if (u.isPlayer()) hasPlayer = true;
            else hasEnemy = true;
        }

        if (!hasPlayer) {
            currentState = BattleState.DEFEAT;
            System.out.println(">>> GAME OVER - DERROTA <<<");
        } else if (!hasEnemy) {
            if (!units.isEmpty()) {
                currentState = BattleState.VICTORY;
                System.out.println(">>> VITÓRIA! <<<");
            }
        }
    }

    public BattleState getState() { return currentState; }

    public BoardUnit getUnitAt(int x, int y) {
        for (BoardUnit unit : units) {
            if (unit.getGridX() == x && unit.getGridY() == y) {
                return unit;
            }
        }
        return null;
    }

    // --- NOVOS MÉTODOS PARA HABILIDADES ---
    public boolean startSpecialAbility(BoardUnit unit) {
        if (currentState != BattleState.PLAYING) return false;
        if (!turnManager.isPlayerTurn() || !unit.isPlayer()) return false;
        if (unit.hasAttacked() || unit.hasMoved()) return false;

        unitUsingAbility = unit;
        System.out.println("Selecione o alvo para " + unit.getCardData().name);
        return true;
    }

    public boolean completeSpecialAbility(int targetX, int targetY) {
        if (unitUsingAbility == null) return false;

        boolean success = AbilityManager.executeAbility(unitUsingAbility, this, targetX, targetY);
        if (success) {
            unitUsingAbility.setAttacked(true);
            unitUsingAbility.setMoved(true);
            System.out.println("Habilidade especial usada com sucesso!");
        }

        unitUsingAbility = null;
        return success;
    }

    public boolean canUseAbilityOnTarget(BoardUnit unit, int targetX, int targetY) {
        return AbilityManager.canUseAbilityOnTarget(unit, targetX, targetY, this);
    }

    public BoardUnit getUnitUsingAbility() {
        return unitUsingAbility;
    }

    public void cancelSpecialAbility() {
        unitUsingAbility = null;
        System.out.println("Uso de habilidade cancelado.");
    }

    // --- MÉTODO ATUALIZADO PARA USAR HABILIDADE ---
    public void useSpecialAbility(BoardUnit unit) {
        if (unit.hasAttacked() || unit.hasMoved()) {
            System.out.println("Unidade já agiu neste turno!");
            return;
        }

        // Para habilidades que não precisam de alvo específico
        boolean success = AbilityManager.executeAbility(unit, this, -1, -1);
        if (success) {
            unit.setAttacked(true);
            unit.setMoved(true);
            System.out.println("Habilidade especial usada com sucesso!");
        }
    }

    // --- LOGICA DE MAGIAS ---
    public boolean tryPlaySpellCard(SpellCard card, int targetX, int targetY) {
        if (currentState != BattleState.PLAYING) return false;
        if (!turnManager.isPlayerTurn()) return false;
        if (turnManager.getCurrentMana() < card.cost) {
            System.out.println("Mana insuficiente para magia!");
            return false;
        }

        BoardUnit targetUnit = getUnitAt(targetX, targetY);
        boolean success = false;

        int power = extractValueFromDescription(card.description);
        if (power == 0) power = 2;

        switch (card.spellType) {
            case DAMAGE:
                if (targetUnit != null) {
                    System.out.println("Magia de Dano: " + power + " em " + targetUnit.getCardData().name);
                    targetUnit.takeDamage(power);
                    if (targetUnit.isDead()) {
                        units.remove(targetUnit);
                        checkWinCondition();
                    }
                    success = true;
                }
                break;

            case HEAL:
            case BUFF:
                if (targetUnit != null) {
                    System.out.println("Magia de Cura/Buff: +" + power + " HP em " + targetUnit.getCardData().name);
                    targetUnit.takeDamage(-power);
                    success = true;
                }
                break;

            default:
                System.out.println("Tipo de magia ainda não implementado: " + card.spellType);
                success = true;
                break;
        }

        if (success) {
            turnManager.spendMana(card.cost);
            playerHand.remove(card);
            return true;
        }
        return false;
    }

    private int extractValueFromDescription(String desc) {
        if (desc == null) return 0;
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(desc);
        if (m.find()) {
            return Integer.parseInt(m.group());
        }
        return 0;
    }

    // --- AÇÕES DE UNIDADE ---
    public boolean tryPlayUnitCard(UnitCard card, int targetX, int targetY) {
        if (currentState != BattleState.PLAYING || !turnManager.isPlayerTurn()) return false;
        if (turnManager.getCurrentMana() < card.cost) return false;
        if (getUnitAt(targetX, targetY) != null) return false;
        if (targetX > 2) return false;

        if (turnManager.spendMana(card.cost)) {
            BoardUnit newUnit = new BoardUnit(card, targetX, targetY, true);
            newUnit.setMoved(true);
            newUnit.setAttacked(true);
            addUnit(newUnit);
            playerHand.remove(card);
            return true;
        }
        return false;
    }

    public boolean tryMove(BoardUnit unit, int targetX, int targetY) {
        if (currentState != BattleState.PLAYING) return false;
        if (!turnManager.isPlayerTurn() || !unit.isPlayer()) return false;
        return performMove(unit, targetX, targetY);
    }

    private boolean performMove(BoardUnit unit, int targetX, int targetY) {
        if (unit.hasMoved()) return false;
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
        if (currentState != BattleState.PLAYING) return false;
        if (!turnManager.isPlayerTurn() || !attacker.isPlayer()) return false;
        if (attacker.hasAttacked()) return false;
        if (attacker.isPlayer() == target.isPlayer()) return false;

        return performAttack(attacker, target);
    }

    private boolean performAttack(BoardUnit attacker, BoardUnit target) {
        if (AbilityManager.isProtected(target)) {
            System.out.println("Alvo protegido por Paladino!");
            return false;
        }

        if (attacker.hasAttacked()) return false;
        if (attacker.isPlayer() == target.isPlayer()) return false;

        int distance = Math.abs(attacker.getGridX() - target.getGridX()) +
            Math.abs(attacker.getGridY() - target.getGridY());

        if (distance <= attacker.getCardData().range) {
            int baseDamage = calculateDamage(attacker);
            int finalDamage = AbilityManager.applyAttackEffects(attacker, target, baseDamage);

            target.takeDamage(finalDamage);
            System.out.println("Ataque: " + finalDamage + " dano.");

            if (target.isDead()) {
                units.remove(target);
                checkWinCondition();
            }

            attacker.setAttacked(true);
            attacker.setMoved(true);
            return true;
        }
        return false;
    }

    private int calculateDamage(BoardUnit unit) {
        int min = unit.getCardData().dmgMin;
        int max = unit.getCardData().dmgMax;
        return random.nextInt((max - min) + 1) + min;
    }

    public void endTurn() {
        if (currentState != BattleState.PLAYING) return;

        turnManager.endCurrentTurn();
        if (turnManager.isPlayerTurn()) {
            resetUnitsState();
            drawCard();
        } else {
            System.out.println("=== TURNO INIMIGO ===");
            executeEnemyTurn();
        }
    }

    // --- IA ---
    private void executeEnemyTurn() {
        List<BoardUnit> enemies = new ArrayList<>();
        List<BoardUnit> players = new ArrayList<>();

        for (BoardUnit unit : units) {
            if (!unit.isPlayer()) enemies.add(unit);
            else players.add(unit);
        }

        for (BoardUnit enemy : enemies) {
            if (currentState != BattleState.PLAYING) break;
            if (enemy.isDead()) continue;

            enemy.newTurn();
            BoardUnit target = getClosestTarget(enemy, players);

            if (target != null) {
                boolean attacked = performAttack(enemy, target);
                if (!attacked) {
                    moveTowards(enemy, target);
                    performAttack(enemy, target);
                }
            }
        }

        if (currentState == BattleState.PLAYING) {
            endTurn();
        }
    }

    private BoardUnit getClosestTarget(BoardUnit origin, List<BoardUnit> targets) {
        BoardUnit closest = null;
        int minDistance = Integer.MAX_VALUE;
        for (BoardUnit t : targets) {
            int dist = Math.abs(origin.getGridX() - t.getGridX()) + Math.abs(origin.getGridY() - t.getGridY());
            if (dist < minDistance) {
                minDistance = dist;
                closest = t;
            }
        }
        return closest;
    }

    private void moveTowards(BoardUnit unit, BoardUnit target) {
        if (unit.hasMoved()) return;
        int moves = unit.getCardData().mov;
        int cx = unit.getGridX();
        int cy = unit.getGridY();
        int tx = target.getGridX();
        int ty = target.getGridY();

        for (int i = 0; i < moves; i++) {
            int dist = Math.abs(cx - tx) + Math.abs(cy - ty);
            if (dist <= unit.getCardData().range) break;

            int nx = cx;
            int ny = cy;

            if (cx < tx) nx++; else if (cx > tx) nx--;
            else if (cy < ty) ny++; else if (cy > ty) ny--;

            if (getUnitAt(nx, ny) == null) {
                cx = nx; cy = ny;
            } else {
                if (cx != nx) { if (cy < ty) ny++; else if (cy > ty) ny--; }
                else { if (cx < tx) nx++; else if (cx > tx) nx--; }

                if (getUnitAt(nx, ny) == null) { cx = nx; cy = ny; }
                else break;
            }
        }
        if (cx != unit.getGridX() || cy != unit.getGridY()) {
            unit.setPosition(cx, cy);
            unit.setMoved(true);
        }
    }

    private void resetUnitsState() {
        for(BoardUnit u : units) u.newTurn();
    }

    public TurnManager getTurnManager() { return turnManager; }
    public List<BoardUnit> getUnits() { return units; }
    public MyLinkedList<Card> getPlayerHand() { return (MyLinkedList<Card>) playerHand; }
    public Deck getPlayerDeck() { return playerDeck; }
}
