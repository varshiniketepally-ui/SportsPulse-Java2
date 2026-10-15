package org.sportspulse.platform.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Open Addressing Hash Table implementation using Quadratic Probing.
 * Features:
 * - Insert, Search, Delete with O(1) amortized complexity.
 * - Dynamic table resizing and rehashing based on load factor (threshold = 0.60).
 * - Tombstone markers to maintain probe sequences after deletions.
 * - Real-time diagnostic telemetry: total insertions, collision count, probe metrics.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class OpenAddressingHashTable<K, V> {

    private static final int DEFAULT_CAPACITY = 17; // Prime capacity
    private static final double MAX_LOAD_FACTOR = 0.60;

    private static class Entry<K, V> {
        final K key;
        V value;
        boolean isDeleted;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.isDeleted = false;
        }
    }

    private Entry<K, V>[] table;
    private int size;
    private int tombstoneCount;
    private long totalCollisions;
    private long totalProbes;

    @SuppressWarnings("unchecked")
    public OpenAddressingHashTable(int initialCapacity) {
        int cap = Math.max(nextPrime(initialCapacity), DEFAULT_CAPACITY);
        this.table = (Entry<K, V>[]) new Entry[cap];
        this.size = 0;
        this.tombstoneCount = 0;
        this.totalCollisions = 0;
        this.totalProbes = 0;
    }

    public OpenAddressingHashTable() {
        this(DEFAULT_CAPACITY);
    }

    private int hash(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        h ^= (h >>> 16);
        return Math.abs(h);
    }

    /**
     * Inserts or updates a key-value mapping.
     * Uses quadratic probing: index = (h + i + i*i) % capacity
     */
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Null keys are not supported.");
        }

        if ((double) (size + tombstoneCount + 1) / table.length >= MAX_LOAD_FACTOR) {
            rehash(nextPrime(table.length * 2));
        }

        int h = hash(key);
        int cap = table.length;
        int firstTombstone = -1;

        for (int i = 0; i < cap; i++) {
            totalProbes++;
            int idx = (int) (((long) h + i + (long) i * i) % cap);
            Entry<K, V> entry = table[idx];

            if (entry == null) {
                if (firstTombstone != -1) {
                    table[firstTombstone] = new Entry<>(key, value);
                    tombstoneCount--;
                } else {
                    table[idx] = new Entry<>(key, value);
                }
                size++;
                return;
            }

            if (entry.isDeleted) {
                if (firstTombstone == -1) {
                    firstTombstone = idx;
                }
            } else if (entry.key.equals(key)) {
                // Key already exists, update value
                entry.value = value;
                return;
            } else {
                totalCollisions++;
            }
        }

        // If looped entire table and found tombstone:
        if (firstTombstone != -1) {
            table[firstTombstone] = new Entry<>(key, value);
            tombstoneCount--;
            size++;
        } else {
            // Need rehash
            rehash(nextPrime(table.length * 2));
            put(key, value);
        }
    }

    /**
     * Searches for the value associated with key.
     */
    public V get(K key) {
        if (key == null) return null;
        int h = hash(key);
        int cap = table.length;

        for (int i = 0; i < cap; i++) {
            totalProbes++;
            int idx = (int) (((long) h + i + (long) i * i) % cap);
            Entry<K, V> entry = table[idx];

            if (entry == null) {
                return null; // Not found
            }
            if (!entry.isDeleted && entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Removes the key-value pair and leaves a tombstone.
     */
    public boolean remove(K key) {
        if (key == null) return false;
        int h = hash(key);
        int cap = table.length;

        for (int i = 0; i < cap; i++) {
            totalProbes++;
            int idx = (int) (((long) h + i + (long) i * i) % cap);
            Entry<K, V> entry = table[idx];

            if (entry == null) {
                return false;
            }
            if (!entry.isDeleted && entry.key.equals(key)) {
                entry.isDeleted = true;
                entry.value = null;
                tombstoneCount++;
                size--;
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void rehash(int newCapacity) {
        Entry<K, V>[] oldTable = table;
        table = (Entry<K, V>[]) new Entry[newCapacity];
        size = 0;
        tombstoneCount = 0;

        for (Entry<K, V> entry : oldTable) {
            if (entry != null && !entry.isDeleted) {
                put(entry.key, entry.value);
            }
        }
    }

    public List<K> keys() {
        List<K> list = new ArrayList<>();
        for (Entry<K, V> entry : table) {
            if (entry != null && !entry.isDeleted) {
                list.add(entry.key);
            }
        }
        return list;
    }

    public List<V> values() {
        List<V> list = new ArrayList<>();
        for (Entry<K, V> entry : table) {
            if (entry != null && !entry.isDeleted) {
                list.add(entry.value);
            }
        }
        return list;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getCapacity() {
        return table.length;
    }

    public double getLoadFactor() {
        return (double) size / table.length;
    }

    public long getTotalCollisions() {
        return totalCollisions;
    }

    public long getTotalProbes() {
        return totalProbes;
    }

    public int getTombstoneCount() {
        return tombstoneCount;
    }

    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
        tombstoneCount = 0;
        totalCollisions = 0;
        totalProbes = 0;
    }

    private static int nextPrime(int n) {
        if (n <= 2) return 2;
        int p = (n % 2 == 0) ? n + 1 : n;
        while (!isPrime(p)) {
            p += 2;
        }
        return p;
    }

    private static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; (long) i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }
}
