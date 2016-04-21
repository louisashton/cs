package com.louis;

import com.google.common.base.Preconditions;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Array;

/**
 * Implements a hashtable.
 *
 * Hashtables implement the associative array abstract data type.
 * The tables contain keys and associated values.
 * A hash function computes the location of a value for a given key.
 *
 * @author Louis Ashton (louisashton@live.com)
 */
public class HashTable<K, V> {

    private static final int MIN_CAPACITY = 32;
    private static final double RESIZE_LOAD = 0.5;
    private static final double MAX_LOAD = 0.75;
    private static final double MIN_LOAD = 0.25;

    private HashEntry[] table;
    private int currentSize;
    private int currentCapacity;


    /**
     * Creates a new HashTable.
     */
    @SuppressWarnings("unchecked")
    public HashTable() {
        currentSize = 0;
        currentCapacity = MIN_CAPACITY;
        table = (HashEntry[]) Array.newInstance(HashEntry.class, currentCapacity);
    }

    /**
     * Defines a HashTable entry.
     * Similar to java.util.AbstractMap.SimpleEntry.
     */
    private class HashEntry {

        private final K key;
        private final V value;

        /**
         * Creates a new HashEntry.
         */
        HashEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Returns the key.
         * @return Returns the entries' key.
         */
        public K getKey() {
            return key;
        }

        /**
         * Returns the value.
         * @return Returns the entries' value.
         */
        public V getValue() {
            return value;
        }
    }

    /**
     * Returns the value associated with a key.
     *
     * @param key is the specified key.
     * @return Returns the value associated with the key; null if no such value.
     * @throws IllegalArgumentException is thrown if key is null.
     */
    public synchronized V get(K key) {
        Preconditions.checkArgument(key != null, "first argument to get(K key) is null");
        // Finds the key.
        int index = hash(key) % currentCapacity;
        while (!key.equals(table[index].getKey())) {
            index = (index + 1) % currentCapacity;
        }
        return table[index] != null ? table[index].getValue() : null;
    }

    /**
     * Adds a key and its value to the table.
     *
     * @param key is the key to be added.
     * @param value is the value for this key.
     * @throws IllegalArgumentException is thrown if key or value is null.
     */
    public synchronized void put(K key, V value) {
        Preconditions.checkArgument(key != null, "first argument to put(K key, ...) is null");
        int index = hash(key) % currentCapacity;
        Preconditions.checkArgument(value != null, "second argument to put(..., V Value) is null");
        while (table[index] != null && !key.equals(table[index].getKey())) {
            index = (index + 1) % currentCapacity;
        }

        // Increases the size if a new key is being put in.
        if (table[index] == null) {
            currentSize++;
        }
        table[index] = new HashEntry(key, value);

        resizeIfRequired();
    }

    /**
     * Hashs a key.
     *
     * @param key is the key to be hashed.
     * @return Returns the hash of the key.
     */
    private int hash(K key) {
        int hashValue = key.hashCode();
        hashValue ^= (hashValue >>> 20) ^ (hashValue >>> 12);
        hashValue = hashValue ^ (hashValue >>> 7) ^ (hashValue >>> 4);
        return Math.abs(hashValue);
    }

    /**
     * Resizes the array when required.
     */
    private void resizeIfRequired() {
        if (!((currentSize < currentCapacity * MIN_LOAD && currentCapacity > MIN_CAPACITY)
                || currentSize > currentCapacity * MAX_LOAD)) {
            return;
        }
        int newCapacity = (int) (currentSize / RESIZE_LOAD);

        @SuppressWarnings("unchecked")
        // Makes the new array
        HashEntry[] newArray = (HashEntry[]) Array.newInstance(HashEntry.class, newCapacity);
        //HashEntry[] newArray = new HashEntry[newCapacity];

        for (int oldIndex = 0; oldIndex < currentCapacity; oldIndex++) {
            HashEntry oldEntry = table[oldIndex];
            if (oldEntry == null) {
                continue;
            }
            int index = hash(oldEntry.getKey()) % newCapacity;
            while (newArray[index] != null && !oldEntry.getKey().equals(newArray[index].getKey())) {
                // Gets the next index.
                index = (index + 1) % newCapacity;
            }
            newArray[index] = oldEntry;
        }
        table = newArray;
        currentCapacity = newCapacity;
    }

    /**
     * Gets the size of the table.
     * @return Returns the number of pairs in the table.
     */
    public int size() {
        return currentSize;
    }

    /**
     * Returns all the keys in the hashtable.
     * @return Returns the set of all keys.
     */
    public Set<K> getAll() {
        Set<K> keys = new HashSet<K>(currentSize);
        for (HashEntry entry : table) {
            if (entry != null) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    /**
     * Removes the key from the hashtable.
     * @param key is the key to be deleted.
     * @throws IllegalArgumentException is thrown if key is null.
     */
    public void delete(K key) {
        Preconditions.checkArgument(key != null, "first argument to delete(K key) is null");
        List<HashEntry> entries = new ArrayList<HashEntry>();

        // Locates the key.
        int index = hash(key) % currentCapacity;
        while (table[index] != null && !key.equals(table[index].getKey())) {
            index = (index + 1) % currentCapacity;
            if (table[index] == null) System.out.printf("Key %s already deleted %n", key.toString());
        }

        // Extracts keys that collided with this key.
        while (table[index] != null) {
            entries.add(table[index]);
            table[index] = null;
            currentSize--;
            index = (index + 1) % currentCapacity;
        }

        // Ignore the key to be deleted.
        entries.remove(0);

        for (HashEntry entry : entries) {
            // Puts the rest back in the hashtable, so that the linear probe is maintained.
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String toString() {
        return String.format("Hashtable(%f, %f, %f)", MAX_LOAD, MIN_LOAD, RESIZE_LOAD);
    }
}