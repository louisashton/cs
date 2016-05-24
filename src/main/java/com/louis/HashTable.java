package com.louis;

import com.google.common.base.Preconditions;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Array;
import org.immutables.value.Value;
import com.louis.HashEntry;
import java.lang.String;
import java.util.Optional;

/**
 * Implements a hashtable.
 *
 * Hashtables implement the associative array abstract data type. The tables contain keys and associated values.
 * A hash function computes the location of a value for a given key.
 *
 * @author Louis Ashton (louisashton@live.com)
 */
public class HashTable {

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
	public HashTable() {
		currentSize = 0;
		currentCapacity = MIN_CAPACITY;
		table = new HashEntry[MIN_CAPACITY];
	}

	/**
	 * Returns true if key is a member of this table.
	 *
	 * @param key the key to check for in the table. May not be null.
	 * @return Returns true if the table contains that key.
	 * @throws IllegalArgumentException if key is null.
	 */
	public boolean contains(String key) {
		Preconditions.checkArgument(key != null,
				"First argument to contains(String key) is null");
		int index = calculateDesiredPositionOfKey(key, currentCapacity);
		return searchForKeyFromIndex(key, index).isPresent();
	}

	private int calculateDesiredPositionOfKey(String key, int modulus) {
		return hash(key) % modulus;
	}

	private Optional<Integer> searchForKeyFromIndex(String key, int index) {
        if (table[index] == null) {
            return Optional.empty();
        }
		if (key.equals(table[index].getKey())) {
			return Optional.of(index);
		} else {
			return searchForKeyForwardOfIndex(key, ((index + 1) % currentCapacity), index);
		}
	}

	private Optional<Integer> searchForKeyForwardOfIndex(String key, int currentIndex,
															 int finalIndex) {
        if (table[currentIndex] == null) {
            return Optional.empty();
        }
		if (key.equals(table[currentIndex].getKey())) {
			return Optional.of(currentIndex);
		} else {
			if (currentIndex == finalIndex) {
				return Optional.empty();
			} else {
				return searchForKeyForwardOfIndex(key, ((currentIndex + 1) % currentCapacity), finalIndex);
			}
		}
	}

	/**
	 * Returns the value associated with a key.
	 *
	 * @param key The key for which to look up the value.
	 * @return The value associated with the key or null if no such value.
	 * @throws IllegalArgumentException if key is null.
	 */
	public synchronized String get(String key) {
		Preconditions.checkArgument(key != null, "first argument to get(String key) is null");
		int startIndex = calculateDesiredPositionOfKey(key, currentCapacity);
		Optional<Integer> index = searchForKeyFromIndex(key, startIndex);
		return index.isPresent() ? table[index.get()].getValue() : null;
	}

	/**
	 * Adds a key and its value to the table.
	 *
	 * @param key is the key to be added.
	 * @param value is the value for this key.
	 * @throws IllegalArgumentException if key or value is null.
	 */
	public synchronized void put(String key, String value) {
		Preconditions.checkArgument(key != null, "first argument to put(String key, ...) is null");
		int startIndex = calculateDesiredPositionOfKey(key, currentCapacity);
		Preconditions.checkArgument(value != null, "second argument to put(..., String value) is null");
		Optional<Integer> index = putKeyFromIndex(key, startIndex, table, currentCapacity);

        if (!index.isPresent()) {
            resizeIfRequired();
            put(key, value);
            return;
        }

		// Increases the size if a new key is being put in.
		if (table[index.get()] == null) {
			currentSize++;
		}
		table[index.get()] = ImmutableHashEntry.builder().key(key).value(value).build();

		resizeIfRequired();
	}

	private Optional<Integer> putKeyFromIndex(String key, int index, HashEntry[] entries, int capacity) {
		if (entries[index] == null || key.equals(entries[index].getKey())) {
			return Optional.of(index);
		} else {
			return putKeyForwardOfIndex(key, ((index + 1) % capacity), index, entries, capacity);
		}
	}

	private Optional<Integer> putKeyForwardOfIndex(String key, int currentIndex,
												   int finalIndex, HashEntry[] entries, int capacity) {
		if (entries[currentIndex] == null || key.equals(entries[currentIndex].getKey())) {
			return Optional.of(currentIndex);
		} else {
			if (currentIndex == finalIndex) {
				return Optional.empty();
			} else {
				return putKeyForwardOfIndex(key, ((currentIndex + 1) % capacity), finalIndex, entries, capacity);
			}
		}
	}

	/**
	 * Hashs a key.
	 *
	 * The location of the key in the table is equivalent to this.
	 * 
	 * @param key is the key to be hashed.
	 * @return Returns the hash of the key.
	 */
	private int hash(String key) {
		int hashValue = key.hashCode();
		hashValue ^= (hashValue >>> 20) ^ (hashValue >>> 12);
		hashValue = hashValue ^ (hashValue >>> 7) ^ (hashValue >>> 4);
		return Math.abs(hashValue);
	}

	/**
	 * Resizes the underlying array if required.
	 */
	private void resizeIfRequired() {
		if (!(lessThanMinimumLoad() || moreThanMaximumLoad())) {
			return;
		}
		int newCapacity = (int) (currentSize / RESIZE_LOAD);

		HashEntry[] newArray = new HashEntry[newCapacity];

        copyArray(newCapacity, newArray);
        table = newArray;
		currentCapacity = newCapacity;
	}

    private void copyArray(int newCapacity, HashEntry[] newArray) {
        for (int oldIndex = 0; oldIndex < currentCapacity; oldIndex++) {
            HashEntry oldEntry = table[oldIndex];
            if (oldEntry != null) {
                int startIndex = calculateDesiredPositionOfKey(oldEntry.getKey(), newCapacity);
                Optional<Integer> index = putKeyFromIndex(oldEntry.getKey(), startIndex, newArray, newCapacity);
                if (index.isPresent()) {
newArray[index.get()] = oldEntry;
}
            }
        }
    }

    private boolean moreThanMaximumLoad() {
        return currentSize > currentCapacity * MAX_LOAD;
    }

    private boolean lessThanMinimumLoad() {
        return currentSize < currentCapacity * MIN_LOAD && currentCapacity > MIN_CAPACITY;
    }

	/**
	 * Gets the size of the table.
	 * 
	 * @return Returns the number of pairs in the table.
	 */
	public int size() {
		return currentSize;
	}

	/**
	 * Returns all the keys in the hashtable.
	 * 
	 * @return Returns the set of all keys.
	 */
	public HashSet<String> getAll() {
		HashSet<String> keys = new HashSet<String>(currentSize);
		for (HashEntry entry : table) {
			if (entry != null) {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}

	/**
	 * Removes the key from the hashtable.
	 * 
	 * @param key the key to be deleted.
	 * @throws IllegalArgumentException is thrown if key is null.
	 */
	public void delete(String key) {
		Preconditions.checkArgument(key != null, "first argument to delete(String key) is null");
		List<HashEntry> entries = new ArrayList<HashEntry>();

        int startIndex = calculateDesiredPositionOfKey(key, currentCapacity);

        Optional<Integer> optionalIndex = putKeyFromIndex(key, startIndex, table, currentCapacity);

        if (!optionalIndex.isPresent()) {
            //System.out.printf("Key %s already deleted %n", key.toString());
            return;
        }

        int index = optionalIndex.get();

        if (table[index] == null) {
            //System.out.printf("Key %s already deleted %n", key.toString());
            return;
        }

		// Extracts keys that collided with this key.
		extractContiguousElements(entries, index);

		// Ignore the key to be deleted.
		entries.remove(0);

		for (HashEntry entry : entries) {
			// Puts the rest back in the hashtable, so that the linear probe is maintained.
			this.put(entry.getKey(), entry.getValue());
		}
	}

	private void extractContiguousElements(List<HashEntry> entries, int index) {
		while (table[index] != null) {
			entries.add(table[index]);
			table[index] = null;
			currentSize--;
			index = (index + 1) % currentCapacity;
		}
	}
}