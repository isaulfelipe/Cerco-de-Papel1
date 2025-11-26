package br.edu.ufersa.ed1.cercoDePapel.util;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;

public class MyStack<E> extends Stack<E> {
    private MyLinkedList<E> list;

    public MyStack() {
        this.list = new MyLinkedList<>();
    }

    @Override
    public E push(E item) {
        list.addLast(item);
        return item;
    }

    @Override
    public E pop() {
        if (empty()) {
            throw new EmptyStackException();
        }
        return list.removeLast();
    }

    @Override
    public E peek() {
        if (empty()) {
            throw new EmptyStackException();
        }
        return list.getLast();
    }

    @Override
    public boolean empty() {
        return list.isEmpty();
    }

    @Override
    public int search(Object o) {
        int index = list.lastIndexOf(o);
        if (index >= 0) {
            return list.size() - index;
        }
        return -1;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
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

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
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

        // Percorre do topo para a base
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
