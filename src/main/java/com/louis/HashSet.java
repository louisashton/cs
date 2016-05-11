package com.louis;

import com.google.common.base.Preconditions;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Array;

/**
 * Implements a hashset.
 *
 * Hashsets implement the set abstract data type. The set contains unique elements. A hash function
 * computes the location of an element.The location is checked to see if it contains the element.
 * This implementation merely modifies the inbuilt HashMap.
 *
 * @author Louis Ashton (louisashton@live.com)
 */
public class HashSet<E> {

    private static final int MIN_CAPACITY = 32;
    private static final double RESIZE_LOAD = 0.5;
    private static final double MAX_LOAD = 0.75;
    private static final double MIN_LOAD = 0.25;

    private HashEntry[] set;
    private int currentSize;
    private int currentCapacity;

    /**
     * Creates a new HashSet.
     */
    @SuppressWarnings("unchecked")
    public HashSet() {
        currentSize = 0;
        currentCapacity = MIN_CAPACITY;
        set = (HashEntry[]) Array.newInstance(HashEntry.class, currentCapacity);
    }

    /**
     * Defines a HashTable entry.
     * 
     * Each entry consists of a element and an associated value. Both can be retrieved.
     */
    private class HashEntry {

        private final E element;

        /**
         * Creates a new HashEntry.
         */
        HashEntry(E element) {
            this.element = element;
        }

        /**
         * Returns the element.
         * 
         * @return Returns the entries' element.
         */
        public E getElement() {
            return element;
        }
    }

    /**
     * Checks the set for an element.
     *
     * @param element is to be checked.
     * @return Returns true if the set contains that value.
     * @throws IllegalArgumentException if element is null.
     */
    public synchronized boolean contains(E element) {
        Preconditions.checkArgument(element != null,
                "First argument to contains(E element) is null");
        // Finds the element.
        int index = hash(element) % currentCapacity;
        while (!element.equals(set[index].getElement())) {
            index = (index + 1) % currentCapacity;
        }
        return set[index] != null;
    }

    /**
     * Adds an element to the set.
     *
     * @param element is the element to be added.
     * @throws IllegalArgumentException if element is null.
     */
    public synchronized void add(E element) {
        Preconditions.checkArgument(element != null, "First argument to add(E element) is null");
        int index = hash(element) % currentCapacity;
        while (set[index] != null && !element.equals(set[index].getElement())) {
            index = (index + 1) % currentCapacity;
        }

        // Increases the size if a new element is being put in.
        if (set[index] == null) {
            currentSize++;
        }
        set[index] = new HashEntry(element);

        resizeIfRequired();
    }

    /**
     * Hashs an element.
     *
     * The location of the element in the set is equivalent to this.
     * 
     * @param element is the element to be hashed.
     * @return Returns the hash of the element.
     */
    private int hash(E element) {
        int hashValue = element.hashCode();
        hashValue ^= (hashValue >>> 20) ^ (hashValue >>> 12);
        hashValue = hashValue ^ (hashValue >>> 7) ^ (hashValue >>> 4);
        return Math.abs(hashValue);
    }

    /**
     * Resizes the underlying array if required.
     */
    private void resizeIfRequired() {
        if (!((currentSize < currentCapacity * MIN_LOAD && currentCapacity > MIN_CAPACITY)
                || currentSize > currentCapacity * MAX_LOAD)) {
            return;
        }
        int newCapacity = (int) (currentSize / RESIZE_LOAD);

        @SuppressWarnings("unchecked")
        // Makes the new array.
        HashEntry[] newArray = (HashEntry[]) Array.newInstance(HashEntry.class, newCapacity);
        // HashEntry[] newArray = new HashEntry[newCapacity];

        for (int oldIndex = 0; oldIndex < currentCapacity; oldIndex++) {
            HashEntry oldEntry = set[oldIndex];
            if (oldEntry == null) {
                continue;
            }
            int index = hash(oldEntry.getElement()) % newCapacity;
            while (newArray[index] != null
                    && !oldEntry.getElement().equals(newArray[index].getElement())) {
                // Gets the next index.
                index = (index + 1) % newCapacity;
            }
            newArray[index] = oldEntry;
        }
        set = newArray;
        currentCapacity = newCapacity;
    }

    /**
     * Removes the element from the hashtable.
     * 
     * @param element is the element to be deleted.
     * @throws IllegalArgumentException is thrown if element is null.
     */
    public void remove(E element) {
        Preconditions.checkArgument(element != null, "First argument to remove(E element) is null");
        List<HashEntry> entries = new ArrayList<HashEntry>();

        // Locates the element.
        int index = hash(element) % currentCapacity;
        while (set[index] != null && !element.equals(set[index].getElement())) {
            index = (index + 1) % currentCapacity;
            if (set[index] == null)
                System.out.printf("Element %s already deleted %n", element.toString());
        }

        // Extracts elements that collided with this element.
        while (set[index] != null) {
            entries.add(set[index]);
            set[index] = null;
            currentSize--;
            index = (index + 1) % currentCapacity;
        }

        // Ignore the element to be deleted.
        entries.remove(0);

        for (HashEntry entry : entries) {
            // Puts the rest back in the hashtable, so that the linear probe is maintained.
            this.add(entry.getElement());
        }
    }
}
