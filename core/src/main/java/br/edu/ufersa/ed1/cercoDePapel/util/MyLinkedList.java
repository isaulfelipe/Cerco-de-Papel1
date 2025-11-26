package br.edu.ufersa.ed1.cercoDePapel.util;

import java.lang.reflect.Array;
import java.util.*;

public class MyLinkedList<E> implements List<E> {
    class Node {
        public E data;
        public Node next;
        public Node prev;

        public Node(E data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public MyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
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
        return indexOf(o) >= 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                E data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        Node current = head;

        for (int i = 0; i < size; i++) {
            array[i] = current.data;
            current = current.next;
        }

        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
        }

        Node current = head;
        for (int i = 0; i < size; i++) {
            a[i] = (T) current.data;
            current = current.next;
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    @Override
    public boolean add(E e) {
        addLast(e);
        return true;
    }

    @Override
    public void addFirst(E e) {
        Node newNode = new Node(e);

        if (head == null) {
            tail = newNode;
        } else {
            head.prev = newNode;
            newNode.next = head;
        }
        head = newNode;

        size++;
    }

    @Override
    public void addLast(E e) {
        Node newNode = new Node(e);

        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;

        size++;
    }

    @Override
    public boolean remove(Object o) {
        Node current = head;

        while (current != null) {
            if (Objects.equals(o, current.data)) {
                removeNode(current);
                return true;
            }
            current = current.next;
        }

        return false;
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
        if (c.isEmpty()) return false;

        for (E element : c) {
            add(element);
        }

        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index);

        if (c.isEmpty()) return false;

        if (index == size) {
            return addAll(c);
        }

        Node nextNode = searchNode(index);
        Node prevNode = nextNode.prev;

        for (E element : c) {
            Node newNode = new Node(element);

            if (prevNode == null) {
                head = newNode;
            } else {
                prevNode.next = newNode;
                newNode.prev = prevNode;
            }

            prevNode = newNode;
            size++;
        }

        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        Node current = head;

        while (current != null) {
            Node next = current.next;
            if (c.contains(current.data)) {
                removeNode(current);
                modified = true;
            }
            current = next;
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Node current = head;

        while (current != null) {
            Node next = current.next;
            if (!c.contains(current.data)) {
                removeNode(current);
                modified = true;
            }
            current = next;
        }

        return modified;
    }

    @Override
    public void clear() {
        Node current = head;
        while (current != null) {
            Node next = current.next;
            current.prev = null;
            current.next = null;
            current.data = null;
            current = next;
        }

        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public E get(int index) {
        checkElementIndex(index);
        return searchNode(index).data;
    }

    @Override
    public E getLast() {
        if (isEmpty()) throw new NoSuchElementException();
        return tail.data;
    }

    @Override
    public E getFirst() {
        if (isEmpty()) throw new NoSuchElementException();
        return head.data;
    }

    @Override
    public E set(int index, E element) {
        checkElementIndex(index);
        Node node = searchNode(index);
        E oldData = node.data;
        node.data = element;
        return oldData;
    }

    @Override
    public void add(int index, E element) {
        checkPositionIndex(index);

        if (index == size) {
            add(element);
        } else {
            Node successor = searchNode(index);
            Node predecessor = successor.prev;
            Node newNode = new Node(element);

            newNode.prev = predecessor;
            newNode.next = successor;
            successor.prev = newNode;

            if (predecessor == null) {
                head = newNode;
            } else {
                predecessor.next = newNode;
            }

            size++;
        }
    }

    @Override
    public E remove(int index) {
        checkElementIndex(index);
        Node node = searchNode(index);
        E oldData = node.data;
        removeNode(node);
        return oldData;
    }

    @Override
    public E removeFirst() {
        if(isEmpty()) return null;

        E headValue = head.data;
        removeNode(head);
        return headValue;
    }

    @Override
    public E removeLast() {
        if(isEmpty()) return null;

        E tailValue = tail.data;
        removeNode(tail);
        return tailValue;
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        Node current = head;

        while (current != null) {
            if (Objects.equals(o, current.data)) {
                return index;
            }
            current = current.next;
            index++;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int index = size - 1;
        Node current = tail;

        while (current != null) {
            if (Objects.equals(o, current.data)) {
                return index;
            }
            current = current.prev;
            index--;
        }

        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        checkPositionIndex(index);
        return new ListIterator<>() {
            private Node next = (index == size) ? null : searchNode(index);
            private Node lastReturned = null;
            private int nextIndex = index;

            @Override
            public boolean hasNext() {
                return nextIndex < size;
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();

                lastReturned = next;
                next = next.next;
                nextIndex++;
                return lastReturned.data;
            }

            @Override
            public boolean hasPrevious() {
                return nextIndex > 0;
            }

            @Override
            public E previous() {
                if (!hasPrevious()) throw new NoSuchElementException();

                next = (next == null) ? tail : next.prev;
                lastReturned = next;
                nextIndex--;
                return lastReturned.data;
            }

            @Override
            public int nextIndex() {
                return nextIndex;
            }

            @Override
            public int previousIndex() {
                return nextIndex - 1;
            }

            @Override
            public void remove() {
                if (lastReturned == null) throw new IllegalStateException();

                Node lastNext = lastReturned.next;
                removeNode(lastReturned);

                if (next == lastReturned) {
                    next = lastNext;
                } else {
                    nextIndex--;
                }

                lastReturned = null;
            }

            @Override
            public void set(E e) {
                if (lastReturned == null) throw new IllegalStateException();
                lastReturned.data = e;
            }

            @Override
            public void add(E e) {
                lastReturned = null;

                if (next == null) {
                    MyLinkedList.this.add(e);
                } else {
                    MyLinkedList.this.add(nextIndex, e);
                }

                nextIndex++;
            }
        };
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException(
                "fromIndex: " + fromIndex + ", toIndex: " + toIndex + ", Size: " + size);
        }

        MyLinkedList<E> subList = new MyLinkedList<>();
        Node current = searchNode(fromIndex);

        for (int i = fromIndex; i < toIndex; i++) {
            subList.add(current.data);
            current = current.next;
        }

        return subList;
    }

    // Métodos auxiliares

    private Node searchNode(int index) {
        if (index < size / 2) {
            Node current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            return current;
        } else {
            Node current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
            return current;
        }
    }

    private void removeNode(Node node) {
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }

        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }

        node.data = null;
        node.next = null;
        node.prev = null;
        size--;
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}
