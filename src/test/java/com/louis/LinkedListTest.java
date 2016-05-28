// CHECKSTYLE:OFF

// CHECKSTYLE:ON

package com.louis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ListIterator;
import org.junit.Test;

/**
 * Tests for {@link LinkedList}.
 *
 * @author Louis Ashton  (louisashton@live.com)
 */
public class LinkedListTest {

    private LinkedList<Integer> list = new LinkedList<>();

    @Test
    public final void canConstructLinkedLists() {
        assertEquals(list.size(), 0);
    }

    /**
     * Tests illegal argument exceptions.
     * <p>
     * Checks whether Linkedlists throw IllegalArgumentException when passed null values.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void cannotContainNull() {
        list.contains(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void cannotAddNull() {
        list.add(null);
    }

    /**
     * Tests addition to LinkedLists.
     * <p>
     * Checks whether entries can be put into the list.
     */
    @Test
    public final void canAddEntries() {
        for (int i = 0; i < 10; i++) {
            list.add(i);
            assertEquals(i + 1, list.size());
        }
    }

    /**
     * Tests LinkedList search.
     * <p>
     * Checks whether entries can be found in the list. Does not discern whether values were stored incorrectly, or
     * retrieved incorrectly.
     */
    @Test
    public final void canFindEntries() {
        for (int i = 0; i < 10; i++) {
            list.add(i);
            assertTrue(list.contains(i));
            assertFalse(list.contains(10000));
        }
    }

    /**
     * Tests deletion from LinkedLists.
     * <p>
     * Checks whether entries can be removed from the list.
     */
    @Test
    public final void canRemoveEntries() {
        assertEquals(0, list.size());
        for (int i = 0; i < 10; i++) {
            list.add(i);
            assertTrue(list.contains(i));
            list.remove(i);
            assertFalse(list.contains(i));
            assertEquals(0, list.size());
        }
    }

    @Test
    public final void canRemoveFirst() {
        assertEquals(0, list.size());
        list.add(1);
        list.remove(1);
    }

    @Test
    public final void canIterate() {
        ListIterator<Integer> iteration = list.iterator();
        assertEquals(0, iteration.nextIndex());
        assertEquals(-1, iteration.previousIndex());
        iteration.add(1);
        assertFalse(iteration.hasNext());
        iteration.add(2);
        iteration.add(3);
        assertTrue(iteration.hasPrevious());
        assertEquals(Integer.valueOf(3), iteration.previous());
        assertEquals(Integer.valueOf(2), iteration.previous());
        iteration.set(1);
        assertEquals(Integer.valueOf(1), iteration.next());
        iteration.remove();
    }
}
