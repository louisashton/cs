package com.louis;

import java.util.HashMap;

/**
 * Implements a hashset.
 *
 * Hashsets implement the set abstract data type. The set contains unique elements. A hash function computes the 
 * location of an element.The location is checked to see if it contains the element. This implementation merely 
 * modifies the inbuilt HashMap.
 *
 * @author Louis Ashton (louisashton@live.com)
 */
public class HashSet<E> {
	// Dummy value; represents a value in the backing Map.
	private static final Object PRESENT = new Object();
	private final HashMap<E, Object> map = new HashMap<E,Object>();

	/**
	 * Adds an element to the set.
	 *
	 * @param element is to be added.
	 * @throws IllegalArgumentException is thrown if element is null.
	 */
	public void add(E element) {
		if (!contains(element)) {
			map.put(element, PRESENT);
		}
	}

	/**
	 * Checks the set for an element.
	 *
	 * @param element is to be checked.
	 * @return Returns true if the set contains that value.
	 */
	public boolean contains(E element) {
		return map.containsKey(element);
	}

	/**
	 * Removes an element from the set.
	 *
	 * @param element is to be removed.
	 * @return Returns true if the set contained that value.
	 */
	public void remove(E element) {
		map.remove(element);
	}
}