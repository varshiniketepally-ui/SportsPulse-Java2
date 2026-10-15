package org.sportspulse.platform.datastructures;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Binary Heap based Priority Queue supporting custom comparators.
 *
 * @param <T> Element type
 */
public class CustomPriorityQueue<T> {

    private static final int INITIAL_CAPACITY = 11;
    private Object[] heap;
    private int size;
    private final Comparator<? super T> comparator;

    @SuppressWarnings("unchecked")
    public CustomPriorityQueue(Comparator<? super T> comparator) {
        this.heap = new Object[INITIAL_CAPACITY];
        this.size = 0;
        this.comparator = comparator != null ? comparator : (a, b) -> ((Comparable<T>) a).compareTo((T) b);
    }

    public CustomPriorityQueue() {
        this(null);
    }

    public void offer(T element) {
        if (element == null) {
            throw new NullPointerException("Null elements not allowed.");
        }
        if (size == heap.length) {
            heap = Arrays.copyOf(heap, heap.length * 2);
        }
        heap[size] = element;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T poll() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty.");
        }
        T result = (T) heap[0];
        T last = (T) heap[--size];
        heap[size] = null;
        if (size > 0) {
            heap[0] = last;
            siftDown(0);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty.");
        }
        return (T) heap[0];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    private void siftUp(int k) {
        T key = (T) heap[k];
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            T p = (T) heap[parent];
            if (comparator.compare(key, p) >= 0) {
                break;
            }
            heap[k] = p;
            k = parent;
        }
        heap[k] = key;
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int k) {
        T key = (T) heap[k];
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = heap[child];
            int right = child + 1;
            if (right < size && comparator.compare((T) c, (T) heap[right]) > 0) {
                child = right;
                c = heap[child];
            }
            if (comparator.compare(key, (T) c) <= 0) {
                break;
            }
            heap[k] = c;
            k = child;
        }
        heap[k] = key;
    }
}
