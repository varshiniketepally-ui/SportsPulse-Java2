package org.sportspulse.platform.datastructures;

import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * Custom LIFO Stack implementation.
 *
 * @param <T> Element type
 */
public class CustomArrayStack<T> {

    private static final int INITIAL_CAPACITY = 8;
    private Object[] data;
    private int top;

    public CustomArrayStack(int capacity) {
        this.data = new Object[Math.max(capacity, INITIAL_CAPACITY)];
        this.top = 0;
    }

    public CustomArrayStack() {
        this(INITIAL_CAPACITY);
    }

    public void push(T element) {
        if (top == data.length) {
            data = Arrays.copyOf(data, data.length * 2);
        }
        data[top++] = element;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T element = (T) data[--top];
        data[top] = null;
        return element;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return (T) data[top - 1];
    }

    public int size() {
        return top;
    }

    public boolean isEmpty() {
        return top == 0;
    }
}
