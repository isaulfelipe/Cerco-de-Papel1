package br.edu.ufersa.ed1.cercoDePapel.util;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;

public class MyLinkedHashSet<E> implements Set<E> {

    private static class Node<E> {
        E element;
        Node<E> next;
        Node<E> before, after;

        Node(E element) {
            this.element = element;
        }
    }

    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Node<E>[] table;
    private int size;
    private final float loadFactor;

    // Manter a ordem de inserção
    private Node<E> head;
    private Node<E> tail;

    public MyLinkedHashSet() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MyLinkedHashSet(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);

        this.loadFactor = loadFactor;
        this.table = (Node<E>[]) new Node[initialCapacity];
        this.size = 0;
    }

    public MyLinkedHashSet(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return getNode(o) != null;
    }

    private Node<E> getNode(Object o) {
        if (o == null) return null;

        int index = getIndex(o);
        Node<E> node = table[index];

        while (node != null) {
            if (Objects.equals(o, node.element)) {
                return node;
            }
            node = node.next;
        }
        return null;
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedHashSetIterator();
    }

    private class LinkedHashSetIterator implements Iterator<E> {
        private Node<E> current = head;
        private Node<E> lastReturned = null;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastReturned = current;
            E element = current.element;
            current = current.after;
            return element;
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            MyLinkedHashSet.this.remove(lastReturned.element);
            lastReturned = null;
        }
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        int i = 0;
        for (E element : this) {
            array[i++] = element;
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
        }

        int i = 0;
        for (E element : this) {
            a[i++] = (T) element;
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    @Override
    public boolean add(E e) {
        if (e == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        if (contains(e)) {
            return false;
        }

        ensureCapacity();

        int index = getIndex(e);
        Node<E> newNode = new Node<>(e);

        // Adicionar à tabela hash
        newNode.next = table[index];
        table[index] = newNode;

        // Adicionar à lista encadeada para manter ordem
        linkNodeLast(newNode);

        size++;
        return true;
    }

    private void linkNodeLast(Node<E> node) {
        if (head == null) {
            head = node;
        } else {
            tail.after = node;
            node.before = tail;
        }
        tail = node;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) return false;

        int index = getIndex(o);
        Node<E> prev = null;
        Node<E> current = table[index];

        while (current != null) {
            if (Objects.equals(o, current.element)) {
                // Remover da tabela hash
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }

                // Remover da lista encadeada
                unlinkNode(current);

                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }

        return false;
    }

    private void unlinkNode(Node<E> node) {
        if (node.before == null) {
            head = node.after;
        } else {
            node.before.after = node.after;
        }

        if (node.after == null) {
            tail = node.before;
        } else {
            node.after.before = node.before;
        }

        node.before = null;
        node.after = null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E element : c) {
            if (add(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object element : c) {
            if (remove(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        Arrays.fill(table, null);
        head = tail = null;
        size = 0;
    }

    private int getIndex(Object o) {
        return (o.hashCode() & 0x7FFFFFFF) % table.length;
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size >= table.length * loadFactor) {
            int newCapacity = table.length * 2;
            Node<E>[] newTable = (Node<E>[]) new Node[newCapacity];

            // Rehash todos os elementos
            Node<E> current = head;
            while (current != null) {
                int newIndex = (current.element.hashCode() & 0x7FFFFFFF) % newCapacity;
                current.next = newTable[newIndex];
                newTable[newIndex] = current;
                current = current.after;
            }

            table = newTable;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    // Métodos opcionais para melhor funcionalidade
    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        Node<E> current = head;
        while (current != null) {
            action.accept(current.element);
            current = current.after;
        }
    }
}
