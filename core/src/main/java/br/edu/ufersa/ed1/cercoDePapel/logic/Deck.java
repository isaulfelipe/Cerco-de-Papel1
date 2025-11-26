package br.edu.ufersa.ed1.cercoDePapel.logic;

import br.edu.ufersa.ed1.cercoDePapel.entities.Card;
import br.edu.ufersa.ed1.cercoDePapel.util.MyStack;

import java.util.Collections;

public class Deck {
    private MyStack<Card> cards;

    public Deck() {
        this.cards = new MyStack<>();
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    // Método auxiliar para adicionar várias de uma vez (vindo de um CSV, por exemplo)
    public void addCards(Iterable<Card> newCards) {
        for(Card c : newCards) {
            this.cards.add(c);
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card draw() {
        if (cards.isEmpty()) return null;
        // Remove do topo (índice 0)
        return cards.pop();
    }

    public int size() {
        return cards.size();
    }
}
