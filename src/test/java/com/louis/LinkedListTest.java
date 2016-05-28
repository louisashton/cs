package com.louis;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.ListIterator;

/**
 * Tests for {@link LinkedList}.
 *
 * @author Louis Ashton  (louisashton@live.com)
 */
public class LinkedListTest {

    LinkedList<Integer> list = new LinkedList<>();

    @Test
    public void canConstructLinkedLists() {
        assertEquals(list.size(), 0);
    }

    /**
     * Tests illegal argument exceptions.
     *
     * Checks whether Linkedlists throw IllegalArgumentException when passed null values.
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotContainNull() {
        list.contains(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddNull() {
        list.add(null);
    }

    /**
     * Tests addition to LinkedLists.
     *
     * Checks whether entries can be put into the list.
     */
    @Test
    public void canAddEntries() {
        for (int i = 0; i < 1000; i++) {
            list.add(i);
            assertEquals(i + 1, list.size());
        }
    }

    /**
     * Tests LinkedList search.
     *
     * Checks whether entries can be found in the list.
     * Does not discern whether values were stored incorrectly, or retrieved incorrectly.
     */
    @Test
    public void canFindEntries() {
        for (int i = 0; i < 1000; i++) {
            list.add(i);
            assertEquals(true, list.contains(i));
            assertEquals(false, list.contains(10000));
        }
    }

    /**
     * Tests deletion from LinkedLists.
     *
     * Checks whether entries can be removed from the list.
     */
    @Test
    public void canRemoveEntries() {
        assertEquals(0, list.size());
        for (int i = 0; i < 1000; i++) {
            list.add(i);
            assertEquals(true, list.contains(i));
            list.remove(i);
            assertEquals(false, list.contains(i));
            assertEquals(0,list.size());
        }
    }

    @Test
    public void canRemoveFirst() {
        assertEquals(0, list.size());
        list.add(1);
        list.remove(1);
    }

    @Test
    public void canIterate() {
        ListIterator<Integer> iteration = list.iterator();
        assertEquals(0, iteration.nextIndex());
        assertEquals(-1, iteration.previousIndex());
        iteration.add(1);
        assertEquals(false, iteration.hasNext());
        iteration.add(2);
        iteration.add(3);
        assertEquals(true, iteration.hasPrevious());
        assertEquals(Integer.valueOf(3), iteration.previous());
        assertEquals(Integer.valueOf(2), iteration.previous());
        iteration.set(1);
        assertEquals(Integer.valueOf(1), iteration.next());
        iteration.remove();
    }
}