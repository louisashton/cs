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
	@SuppressWarnings("unchecked")
	public HashTable() {
		currentSize = 0;
		currentCapacity = MIN_CAPACITY;
		table = new HashEntry[currentCapacity];
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
		int index = calculateDesiredPositionOfKey(key);
		return searchForKeyFromIndex(key, index).isPresent();
	}

	private int calculateDesiredPositionOfKey(String key) {
		return hash(key) % currentCapacity;
	}

	private Optional<Integer> searchForKeyFromIndex(String key, int index) {
		if (key.equals(table[index])) {
			return Optional.of(index);
		} else {
			return searchForKeyForwardOfIndex(key, index + 1, index);
		}
	}

	private Optional<Integer> searchForKeyForwardOfIndex(String key, int currentIndex,
															 int finalIndex) {
		if (key.equals(table[currentIndex])) {
			return Optional.of(currentIndex);
		} else {
			if (currentIndex == finalIndex) {
				return Optional.empty();
			} else {
				return searchForKeyForwardOfIndex(key, (currentIndex + 1 % currentCapacity), finalIndex);
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
	 * @throws IllegalArgumentException if key or value is null.
	 */
	public synchronized void put(String key, String value) {
		Preconditions.checkArgument(key != null, "first argument to put(String key, ...) is null");
		int index = hash(key) % currentCapacity;
		Preconditions.checkArgument(value != null, "second argument to put(..., String value) is null");
		while (table[index] != null && !key.equals(table[index].getKey())) {
			index = (index + 1) % currentCapacity;
		}

		// Increases the size if a new key is being put in.
		if (table[index] == null) {
			currentSize++;
		}
		table[index] = ImmutableHashEntry.builder().key(key).value(value).build();

		resizeIfRequired();
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
		if (!((currentSize < currentCapacity * MIN_LOAD && currentCapacity > MIN_CAPACITY)
				|| currentSize > currentCapacity * MAX_LOAD)) {
			return;
		}
		int newCapacity = (int) (currentSize / RESIZE_LOAD);

		@SuppressWarnings("unchecked")
		// Makes the new array.
		HashEntry[] newArray = new HashEntry[newCapacity];

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
	public Set<String> getAll() {
		Set<String> keys = new HashSet<String>(currentSize);
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
	 * @param key is the key to be deleted.
	 * @throws IllegalArgumentException is thrown if key is null.
	 */
	public void delete(String key) {
		Preconditions.checkArgument(key != null, "first argument to delete(String key) is null");
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
}