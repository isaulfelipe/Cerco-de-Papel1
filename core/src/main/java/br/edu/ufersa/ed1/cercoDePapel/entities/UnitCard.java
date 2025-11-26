package br.edu.ufersa.ed1.cercoDePapel.entities;

import br.edu.ufersa.ed1.cercoDePapel.enums.CardType;
import br.edu.ufersa.ed1.cercoDePapel.enums.Rarity;
import br.edu.ufersa.ed1.cercoDePapel.enums.UnitClass;

public class UnitCard extends Card {
    public UnitClass unitClass;
    public int dmgMin;
    public int dmgMax;
    public int hp;
    public int mov;
    public int range;

    public UnitCard(){
        super(CardType.UNIT);
    }
    public UnitCard(String id,
                    Rarity rarity,
                    String name,
                    int cost,
                    String description,
                    UnitClass unitClass,
                    int dmgMin,
                    int dmgMax,
                    int hp,
                    int mov,
                    int range){
        super(id, CardType.UNIT, rarity, name, cost, description);
        this.unitClass = unitClass;
        this.dmgMin = dmgMin;
        this.dmgMax = dmgMax;
        this.hp = hp;
        this.mov = mov;
        this.range = range;
    }

    public void setUnitClass(UnitClass unitClass) {
        this.unitClass = unitClass;
    }
    public void setDmgMin(int dmgMin) {
        this.dmgMin = dmgMin;
    }
    public void setDmgMax(int dmgMax) {
        this.dmgMax = dmgMax;
    }
    public void setHP(int hp) {
        this.hp = hp;
    }
    public void setMov(int mov) {
        this.mov = mov;
    }
    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return "UnitCard{" +
            "id=" + id +
            ", tipo = " + type.getDescription() +
            ", raridade = " + rarity.getDescription() +
            ", nome = " + name +
            ", custo = " + cost +
            ", descrição = " + description +
            ", classe = " + unitClass.getDescription() +
            ", dano = " + dmgMin + "-" + dmgMax +
            ", hp = " + hp +
            ", movimento = " + mov +
            ", alcance = " + range +
            "}";
    }
}
