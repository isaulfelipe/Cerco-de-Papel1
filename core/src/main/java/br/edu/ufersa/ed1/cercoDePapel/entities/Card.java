package br.edu.ufersa.ed1.cercoDePapel.entities;

import br.edu.ufersa.ed1.cercoDePapel.enums.CardType;
import br.edu.ufersa.ed1.cercoDePapel.enums.Rarity;

import java.util.Objects;

public abstract class Card {
    public String id;
    public CardType type;
    public Rarity rarity;
    public String name;
    public int cost;
    public String description;

    public Card(CardType type){
        this.type = type;
    }
    public Card(String id,
                CardType type,
                Rarity rarity,
                String name,
                int cost,
                String description){
        this.id = id;
        this.type = type;
        this.rarity = rarity;
        this.name = name;
        this.cost = cost;
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setType(CardType type) {
        this.type = type;
    }
    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
