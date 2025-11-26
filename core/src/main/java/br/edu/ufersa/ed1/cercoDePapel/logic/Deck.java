package br.edu.ufersa.ed1.cercoDePapel.logic;

import br.edu.ufersa.ed1.cercoDePapel.entities.Card;
import br.edu.ufersa.ed1.cercoDePapel.util.MyStack;

public class Deck {
    private MyStack<Card> cards;

    public Deck() {
        this.cards = new MyStack<>();
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public void addCards(Iterable<Card> newCards) {
        for(Card c : newCards) {
            this.cards.add(c);
        }
    }

    public void shuffle() {
        // CORREÇÃO: Chama o método shuffle da própria MyStack
        cards.shuffle();
    }

    public Card draw() {
        if (cards.isEmpty()) return null;
        return cards.pop();
    }

    public int size() {
        return cards.size();
    }
}
