package org.sportspulse.platform.algorithms.randomized;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Reservoir Sampling Algorithm (Algorithm R).
 * Maintains a uniform random sample of size K from an unbounded data stream
 * in a single pass O(N) time and O(K) space, without storing the full stream.
 * Mathematical Proof: Every element has exact probability K / N of inclusion.
 */
public class StreamReservoirSampler<T> {

    private final int capacity;
    private final List<T> reservoir;
    private long totalItemsObserved;
    private final Random random;

    public StreamReservoirSampler(int capacity, Random random) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }
        this.capacity = capacity;
        this.reservoir = new ArrayList<>(capacity);
        this.totalItemsObserved = 0;
        this.random = random != null ? random : new Random();
    }

    public StreamReservoirSampler(int capacity) {
        this(capacity, new Random());
    }

    public StreamReservoirSampler(int capacity, long seed) {
        this(capacity, new Random(seed));
    }

    /**
     * Feeds the next item from the continuous stream into the reservoir.
     */
    public void feed(T item) {
        totalItemsObserved++;

        if (reservoir.size() < capacity) {
            reservoir.add(item);
        } else {
            // Generate random index in [0, totalItemsObserved - 1]
            long j = Math.abs(random.nextLong()) % totalItemsObserved;
            if (j < capacity) {
                reservoir.set((int) j, item);
            }
        }
    }

    public List<T> getSample() {
        return Collections.unmodifiableList(reservoir);
    }

    public int getCapacity() {
        return capacity;
    }

    public long getTotalItemsObserved() {
        return totalItemsObserved;
    }

    public double getInclusionProbability() {
        if (totalItemsObserved == 0) return 0.0;
        return Math.min(1.0, (double) capacity / totalItemsObserved);
    }

    public void clear() {
        reservoir.clear();
        totalItemsObserved = 0;
    }
}
