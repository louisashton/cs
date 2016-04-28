/**
 * 
 */
package com.louis;

import java.util.HashMap;

/**
 * Implements a hashset.
 *
 * Hashsets implement the set abstract data type.
 * The set contains unique elements.
 * A hash function computes the location of an element.
 * The location is checked to see if it contains the element.
 * This implementation merely modifies the inbuilt HashMap.
 *
 * @author Louis Ashton (louisashton@live.com)
 *
 */
public class HashSet<E> {
    // Dummy value; represents a value in the backing Map.
    private static final Object PRESENT = new Object();
    private final HashMap<E,Object > map = new HashMap<E,Object>();

    /**
     * Adds an element to the set.
     *
     * @param element is the element to be added.
     * @throws IllegalArgumentException is thrown if element is null.
     * @return Returns false if the set already contains that value.
     */
    public boolean add(E element) {
        return map.put(element, PRESENT)==null;
    }

    /**
     * Checks the set for an element.
     *
     * @param o is the element to be checked.
     * @return Returns false if the set doesn't contain that value.
     */
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    /**
     * Removes an element from the set.
     *
     * @param o is the element to be removed.
     * @return Returns false if the set doesn't contain that value.
     */
    public boolean remove(Object o) {
        return map.remove(o) != null;
    }

}