package org.sportspulse.platform.datastructures;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Academic Dynamic Array implementation with custom memory management.
 *
 * @param <T> Element type
 */
public class CustomDynamicArray<T> implements Iterable<T> {

    private static final int INITIAL_CAPACITY = 8;
    private Object[] data;
    private int size;

    public CustomDynamicArray(int capacity) {
        this.data = new Object[Math.max(capacity, INITIAL_CAPACITY)];
        this.size = 0;
    }

    public CustomDynamicArray() {
        this(INITIAL_CAPACITY);
    }

    public void add(T element) {
        ensureCapacity(size + 1);
        data[size++] = element;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) data[index];
    }

    @SuppressWarnings("unchecked")
    public T set(int index, T element) {
        checkIndex(index);
        T old = (T) data[index];
        data[index] = element;
        return old;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T removed = (T) data[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index, numMoved);
        }
        data[--size] = null;
        return removed;
    }

    public boolean removeElement(T element) {
        int idx = indexOf(element);
        if (idx >= 0) {
            remove(idx);
            return true;
        }
        return false;
    }

    public int indexOf(T element) {
        if (element == null) {
            for (int i = 0; i < size; i++) {
                if (data[i] == null) return i;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (element.equals(data[i])) return i;
            }
        }
        return -1;
    }

    public boolean contains(T element) {
        return indexOf(element) >= 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        Arrays.fill(data, 0, size, null);
        size = 0;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > data.length) {
            int newCap = Math.max(data.length * 2, minCapacity);
            data = Arrays.copyOf(data, newCap);
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (cursor >= size) throw new NoSuchElementException();
                return (T) data[cursor++];
            }
        };
    }
}
