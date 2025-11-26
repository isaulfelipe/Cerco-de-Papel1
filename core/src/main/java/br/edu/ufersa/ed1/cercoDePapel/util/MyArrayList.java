package br.edu.ufersa.ed1.cercoDePapel.util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class MyArrayList<E> implements List<E> {

    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_ELEMENTDATA = {};

    private Object[] elementData;
    private int size;

    public MyArrayList() {
        this.elementData = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public MyArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        this.size = 0;
    }

    public MyArrayList(Collection<? extends E> c) {
        this.elementData = c.toArray();
        this.size = elementData.length;
        if (size != 0) {
            if (elementData.getClass() != Object[].class) {
                elementData = Arrays.copyOf(elementData, size, Object[].class);
            }
        } else {
            this.elementData = EMPTY_ELEMENTDATA;
        }
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
        return new Itr();
    }

    private class Itr implements Iterator<E> {
        int cursor = 0;
        int lastRet = -1;

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @SuppressWarnings("unchecked")
        @Override
        public E next() {
            if (cursor >= size) {
                throw new NoSuchElementException();
            }
            lastRet = cursor;
            return (E) elementData[cursor++];
        }

        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            MyArrayList.this.remove(lastRet);
            cursor = lastRet;
            lastRet = -1;
        }
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        }
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean add(E e) {
        ensureCapacity(size + 1);
        elementData[size++] = e;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index >= 0) {
            remove(index);
            return true;
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
        return addAll(size, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckForAdd(index);

        Object[] newElements = c.toArray();
        int numNew = newElements.length;
        if (numNew == 0) {
            return false;
        }

        ensureCapacity(size + numNew);

        int numMoved = size - index;
        if (numMoved > 0) {
            System.arraycopy(elementData, index, elementData, index + numNew, numMoved);
        }

        System.arraycopy(newElements, 0, elementData, index, numNew);
        size += numNew;
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        for (int i = 0; i < size; i++) {
            if (c.contains(elementData[i])) {
                remove(i);
                i--; // Ajustar índice após remoção
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        for (int i = 0; i < size; i++) {
            if (!c.contains(elementData[i])) {
                remove(i);
                i--; // Ajustar índice após remoção
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elementData[i] = null;
        }
        size = 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E get(int index) {
        rangeCheck(index);
        return (E) elementData[index];
    }

    @SuppressWarnings("unchecked")
    @Override
    public E set(int index, E element) {
        rangeCheck(index);
        E oldValue = (E) elementData[index];
        elementData[index] = element;
        return oldValue;
    }

    @Override
    public void add(int index, E element) {
        rangeCheckForAdd(index);
        ensureCapacity(size + 1);
        System.arraycopy(elementData, index, elementData, index + 1, size - index);
        elementData[index] = element;
        size++;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E remove(int index) {
        rangeCheck(index);
        E oldValue = (E) elementData[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        }
        elementData[--size] = null; // GC
        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (elementData[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals(elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size - 1; i >= 0; i--) {
                if (elementData[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = size - 1; i >= 0; i--) {
                if (o.equals(elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new ListItr(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        rangeCheckForAdd(index);
        return new ListItr(index);
    }

    private class ListItr extends Itr implements ListIterator<E> {
        ListItr(int index) {
            cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @SuppressWarnings("unchecked")
        @Override
        public E previous() {
            if (cursor <= 0) {
                throw new NoSuchElementException();
            }
            cursor--;
            lastRet = cursor;
            return (E) elementData[lastRet];
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void set(E e) {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            MyArrayList.this.set(lastRet, e);
        }

        @Override
        public void add(E e) {
            MyArrayList.this.add(cursor, e);
            cursor++;
            lastRet = -1;
        }
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size);
        return new SubList(this, fromIndex, toIndex);
    }

    private class SubList implements List<E> {
        private final MyArrayList<E> parent;
        private final int offset;
        private int size;

        SubList(MyArrayList<E> parent, int fromIndex, int toIndex) {
            this.parent = parent;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
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
            return listIterator();
        }

        @Override
        public Object[] toArray() {
            Object[] result = new Object[size];
            System.arraycopy(parent.elementData, offset, result, 0, size);
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T[] toArray(T[] a) {
            if (a.length < size) {
                return (T[]) Arrays.copyOfRange(parent.elementData, offset, offset + size, a.getClass());
            }
            System.arraycopy(parent.elementData, offset, a, 0, size);
            if (a.length > size) {
                a[size] = null;
            }
            return a;
        }

        @Override
        public boolean add(E e) {
            parent.add(offset + size, e);
            size++;
            return true;
        }

        @Override
        public boolean remove(Object o) {
            int index = indexOf(o);
            if (index >= 0) {
                remove(index);
                return true;
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
            return addAll(size, c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if (cSize == 0) {
                return false;
            }
            parent.addAll(offset + index, c);
            size += cSize;
            return true;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean modified = false;
            for (int i = 0; i < size; i++) {
                if (c.contains(parent.elementData[offset + i])) {
                    remove(i);
                    i--;
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            boolean modified = false;
            for (int i = 0; i < size; i++) {
                if (!c.contains(parent.elementData[offset + i])) {
                    remove(i);
                    i--;
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public void clear() {
            for (int i = 0; i < size; i++) {
                parent.elementData[offset + i] = null;
            }
            parent.size -= size;
            size = 0;
        }

        @SuppressWarnings("unchecked")
        @Override
        public E get(int index) {
            rangeCheck(index);
            return (E) parent.elementData[offset + index];
        }

        @SuppressWarnings("unchecked")
        @Override
        public E set(int index, E element) {
            rangeCheck(index);
            E oldValue = (E) parent.elementData[offset + index];
            parent.elementData[offset + index] = element;
            return oldValue;
        }

        @Override
        public void add(int index, E element) {
            rangeCheckForAdd(index);
            parent.add(offset + index, element);
            size++;
        }

        @SuppressWarnings("unchecked")
        @Override
        public E remove(int index) {
            rangeCheck(index);
            E result = (E) parent.elementData[offset + index];
            parent.remove(offset + index);
            size--;
            return result;
        }

        @Override
        public int indexOf(Object o) {
            if (o == null) {
                for (int i = 0; i < size; i++) {
                    if (parent.elementData[offset + i] == null) {
                        return i;
                    }
                }
            } else {
                for (int i = 0; i < size; i++) {
                    if (o.equals(parent.elementData[offset + i])) {
                        return i;
                    }
                }
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object o) {
            if (o == null) {
                for (int i = size - 1; i >= 0; i--) {
                    if (parent.elementData[offset + i] == null) {
                        return i;
                    }
                }
            } else {
                for (int i = size - 1; i >= 0; i--) {
                    if (o.equals(parent.elementData[offset + i])) {
                        return i;
                    }
                }
            }
            return -1;
        }

        @Override
        public ListIterator<E> listIterator() {
            return listIterator(0);
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            rangeCheckForAdd(index);
            return new ListIterator<E>() {
                private final ListIterator<E> i = parent.listIterator(offset + index);

                @Override
                public boolean hasNext() {
                    return nextIndex() < size;
                }

                @Override
                public E next() {
                    if (hasNext()) {
                        return i.next();
                    }
                    throw new NoSuchElementException();
                }

                @Override
                public boolean hasPrevious() {
                    return previousIndex() >= 0;
                }

                @Override
                public E previous() {
                    if (hasPrevious()) {
                        return i.previous();
                    }
                    throw new NoSuchElementException();
                }

                @Override
                public int nextIndex() {
                    return i.nextIndex() - offset;
                }

                @Override
                public int previousIndex() {
                    return i.previousIndex() - offset;
                }

                @Override
                public void remove() {
                    i.remove();
                    size--;
                }

                @Override
                public void set(E e) {
                    i.set(e);
                }

                @Override
                public void add(E e) {
                    i.add(e);
                    size++;
                }
            };
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new SubList(parent, offset + fromIndex, offset + toIndex);
        }

        private void rangeCheck(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            }
        }

        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            }
        }
    }

    // Métodos de utilidade
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elementData.length) {
            int newCapacity = elementData.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elementData = Arrays.copyOf(elementData, newCapacity);
        }
    }

    private void rangeCheck(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private void rangeCheckForAdd(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private void subListRangeCheck(int fromIndex, int toIndex, int size) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        }
        if (toIndex > size) {
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < size; i++) {
            sb.append(elementData[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof List)) return false;

        List<?> other = (List<?>) o;
        if (other.size() != size()) return false;

        for (int i = 0; i < size; i++) {
            if (!Objects.equals(elementData[i], other.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (int i = 0; i < size; i++) {
            Object element = elementData[i];
            hashCode = 31 * hashCode + (element == null ? 0 : element.hashCode());
        }
        return hashCode;
    }

    // Métodos adicionais úteis
    public void trimToSize() {
        if (size < elementData.length) {
            elementData = (size == 0) ? EMPTY_ELEMENTDATA : Arrays.copyOf(elementData, size);
        }
    }
}
