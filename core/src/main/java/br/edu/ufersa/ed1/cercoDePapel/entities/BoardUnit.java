package br.edu.ufersa.ed1.cercoDePapel.entities;

public class BoardUnit {
    private UnitCard cardData;
    private int gridX;
    private int gridY;
    private int currentHp;
    private boolean isPlayerUnit;

    // Controles de Turno
    private boolean hasMovedThisTurn;
    private boolean hasAttackedThisTurn; // NOVO: Controle de ataque

    public BoardUnit(UnitCard card, int x, int y, boolean isPlayer) {
        this.cardData = card;
        this.gridX = x;
        this.gridY = y;
        this.currentHp = card.hp;
        this.isPlayerUnit = isPlayer;
        this.hasMovedThisTurn = false;
        this.hasAttackedThisTurn = false;
    }

    // --- Métodos Existentes ---
    public UnitCard getCardData() { return cardData; }
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public void setPosition(int x, int y) { this.gridX = x; this.gridY = y; }
    public int getCurrentHp() { return currentHp; }
    public boolean isPlayer() { return isPlayerUnit; }
    public boolean hasMoved() { return hasMovedThisTurn; }
    public void setMoved(boolean moved) { this.hasMovedThisTurn = moved; }

    // --- NOVOS MÉTODOS PARA O ATAQUE ---

    public void takeDamage(int dmg) {
        this.currentHp -= dmg;
    }

    public boolean isDead() {
        return this.currentHp <= 0;
    }

    public boolean hasAttacked() {
        return hasAttackedThisTurn;
    }

    public void setAttacked(boolean attacked) {
        this.hasAttackedThisTurn = attacked;
    }

    // Reinicia o estado da unidade para o novo turno
    public void newTurn() {
        this.hasMovedThisTurn = false;
        this.hasAttackedThisTurn = false;
    }
}
