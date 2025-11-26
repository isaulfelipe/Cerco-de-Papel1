package br.edu.ufersa.ed1.cercoDePapel.entities;

import br.edu.ufersa.ed1.cercoDePapel.enums.CardType;
import br.edu.ufersa.ed1.cercoDePapel.enums.Rarity;
import br.edu.ufersa.ed1.cercoDePapel.enums.SpellType;

public class SpellCard extends Card {
    public SpellType spellType;

    public SpellCard(){
        super(CardType.SPELL);
    }
    public SpellCard(String id,
                     Rarity rarity,
                     String name,
                     int cost,
                     String description,
                     SpellType spellType){
        super(id, CardType.SPELL, rarity, name, cost, description);
        this.spellType = spellType;
    }

    public void setSpellType(SpellType spellType) {
        this.spellType = spellType;
    }

    @Override
    public String toString() {
        return "UnitCard{" +
            "id=" + id +
            ", tipo = " + type.getDescription() +
            ", raridade = " + rarity.getDescription() +
            ", nome = " + name +
            ", custo = " + cost +
            ", efeito = " + description +
            ", classe = " + spellType.getDescription() +
            "}";
    }
}
