package org.sportspulse.platform.datastructures;

import java.util.NoSuchElementException;

/**
 * Circular buffer array-based Queue.
 *
 * @param <T> Element type
 */
public class CustomArrayQueue<T> {

    private static final int INITIAL_CAPACITY = 8;
    private Object[] data;
    private int head;
    private int tail;
    private int size;

    public CustomArrayQueue(int capacity) {
        this.data = new Object[Math.max(capacity, INITIAL_CAPACITY)];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    public CustomArrayQueue() {
        this(INITIAL_CAPACITY);
    }

    public void enqueue(T element) {
        if (size == data.length) {
            resize(data.length * 2);
        }
        data[tail] = element;
        tail = (tail + 1) % data.length;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty.");
        }
        T element = (T) data[head];
        data[head] = null;
        head = (head + 1) % data.length;
        size--;
        return element;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty.");
        }
        return (T) data[head];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void resize(int newCapacity) {
        Object[] newData = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[(head + i) % data.length];
        }
        data = newData;
        head = 0;
        tail = size;
    }
}
