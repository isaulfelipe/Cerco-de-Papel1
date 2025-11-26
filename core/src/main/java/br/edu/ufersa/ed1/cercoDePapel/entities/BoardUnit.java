package br.edu.ufersa.ed1.cercoDePapel.entities;

import java.util.HashMap;
import java.util.Map;

public class BoardUnit {
    private UnitCard cardData;
    private int gridX;
    private int gridY;
    private int currentHp;
    private boolean isPlayerUnit;

    // Sistema de efeitos
    private Map<String, Integer> effects;
    private int cooldown;
    private boolean hasRevived;

    // Controles de Turno
    private boolean hasMovedThisTurn;
    private boolean hasAttackedThisTurn;

    public BoardUnit(UnitCard card, int x, int y, boolean isPlayer) {
        this.cardData = card;
        this.gridX = x;
        this.gridY = y;
        this.currentHp = card.hp;
        this.isPlayerUnit = isPlayer;
        this.hasMovedThisTurn = false;
        this.hasAttackedThisTurn = false;
        this.effects = new HashMap<>();
        this.cooldown = 0;
        this.hasRevived = false;
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

    public void takeDamage(int dmg) {
        // Se o dano for negativo, é cura
        if (dmg < 0) {
            this.currentHp -= dmg; // Subtrai negativo = soma
            // Garante que não ultrapasse a vida máxima
            if (this.currentHp > this.cardData.hp) {
                this.currentHp = this.cardData.hp;
            }
        } else {
            this.currentHp -= dmg;
        }
    }

    public boolean isDead() {
        // Habilidade do Esqueleto: revive uma vez
        if (this.currentHp <= 0 && "Esqueleto".equals(this.cardData.name) && !hasRevived) {
            this.currentHp = this.cardData.hp / 2; // Revive com metade da vida
            this.hasRevived = true;
            System.out.println("Esqueleto reviveu com " + this.currentHp + " de vida!");
            return false;
        }
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
        this.decreaseEffectDurations();
        this.decreaseCooldown();

        // Aplicar dano de veneno se estiver envenenado
        if (hasEffect("poisoned")) {
            int poisonDmg = (int) (Math.random() * 2) + 1; // 1-2 de dano
            this.currentHp -= poisonDmg;
            System.out.println(cardData.name + " sofreu " + poisonDmg + " de dano por veneno!");
        }
    }

    // Métodos para gerenciar efeitos
    public void addEffect(String effect, int duration) {
        effects.put(effect, duration);
    }

    public boolean hasEffect(String effect) {
        return effects.containsKey(effect) && effects.get(effect) > 0;
    }

    public void removeEffect(String effect) {
        effects.remove(effect);
    }

    public void decreaseEffectDurations() {
        effects.entrySet().removeIf(entry -> {
            entry.setValue(entry.getValue() - 1);
            return entry.getValue() <= 0;
        });
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void decreaseCooldown() {
        if (cooldown > 0) cooldown--;
    }

    public boolean canUseAbility() {
        return cooldown == 0;
    }

    public void setHasRevived(boolean revived) {
        this.hasRevived = revived;
    }

    public boolean hasRevived() {
        return hasRevived;
    }
}
