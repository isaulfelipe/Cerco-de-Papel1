package br.edu.ufersa.ed1.cercoDePapel.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Iterator;

public class MyStack<E> implements Iterable<E> {
    private MyLinkedList<E> list;

    public MyStack() {
        this.list = new MyLinkedList<>();
    }

    // Método shuffle necessário para o Deck
    public void shuffle() {
        ArrayList<E> temp = new ArrayList<>();
        for (E item : list) {
            temp.add(item);
        }
        Collections.shuffle(temp);

        list.clear();
        for (E item : temp) {
            list.addLast(item);
        }
    }

    public boolean add(E item) {
        push(item);
        return true;
    }

    public E push(E item) {
        list.addLast(item);
        return item;
    }

    public E pop() {
        if (empty()) {
            throw new EmptyStackException();
        }
        return list.removeLast();
    }

    public E peek() {
        if (empty()) {
            throw new EmptyStackException();
        }
        return list.getLast();
    }

    // Mantido para compatibilidade
    public boolean empty() {
        return list.isEmpty();
    }

    // --- CORREÇÃO: ADICIONADO ESTE MÉTODO ---
    public boolean isEmpty() {
        return list.isEmpty();
    }
    // ----------------------------------------

    public int search(Object o) {
        int index = list.lastIndexOf(o);
        if (index >= 0) {
            return list.size() - index;
        }
        return -1;
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int currentIndex = list.size() - 1;

            @Override
            public boolean hasNext() {
                return currentIndex >= 0;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                return list.get(currentIndex--);
            }
        };
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public String toString() {
        if (empty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = list.size() - 1; i >= 0; i--) {
            sb.append(list.get(i));
            if (i > 0) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
