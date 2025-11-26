package br.edu.ufersa.ed1.cercoDePapel.logic;

public class TurnManager {
    public enum TurnPhase {
        PLAYER_TURN,
        ENEMY_TURN
    }

    private TurnPhase currentPhase;
    private int currentMana;
    private int maxMana;
    private int turnCount;

    public TurnManager() {
        this.turnCount = 0;
        // Começa com o jogador
        startPlayerTurn();
    }

    public void startPlayerTurn() {
        currentPhase = TurnPhase.PLAYER_TURN;
        turnCount++;

        // Regra simples de Mana: Começa com 3 e aumenta 1 por turno até o máximo de 10
        // Você pode ajustar essa regra conforme o balanceamento do seu jogo
        int baseMana = 3;
        maxMana = Math.min(10, baseMana + (turnCount - 1));
        currentMana = maxMana;

        System.out.println("=== TURNO " + turnCount + " (JOGADOR) ===");
        System.out.println("Mana: " + currentMana + "/" + maxMana);
    }

    public void startEnemyTurn() {
        currentPhase = TurnPhase.ENEMY_TURN;
        System.out.println("=== TURNO INIMIGO ===");
        // Aqui a IA faria suas jogadas (implementaremos IA futuramente)
    }

    public void endCurrentTurn() {
        if (currentPhase == TurnPhase.PLAYER_TURN) {
            startEnemyTurn();
        } else {
            startPlayerTurn();
        }
    }

    public boolean isPlayerTurn() {
        return currentPhase == TurnPhase.PLAYER_TURN;
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public boolean spendMana(int cost) {
        if (currentMana >= cost) {
            currentMana -= cost;
            return true;
        }
        return false;
    }
}
