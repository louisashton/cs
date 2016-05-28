// CHECKSTYLE:OFF

// CHECKSTYLE:ON

package com.louis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;
import org.junit.Test;

/**
 * Tests my BinarySearchTree implementation.
 *
 * @author Louis Ashton (louisashton@live.com)
 */
public class BinarySearchTreeTest {

    private BinarySearchTree<Integer, Integer> bst = new BinarySearchTree<>();

    @Test(expected = IllegalArgumentException.class)
    public final void canAddSearchAndDelete() {
        assertEquals(0, bst.size());
        assertNull(bst.get(1));
        bst.put(1, 2);
        assertTrue(bst.contains(1));
        assertEquals(1, bst.size());
        assertFalse(bst.contains(2));
        assertEquals(Integer.valueOf(2), bst.get(1));
        bst.delete(1);
        assertFalse(bst.contains(1));
        bst.put(1, 2);
        bst.put(1, null);
        bst.contains(null);
    }

    @Test(expected = NoSuchElementException.class)
    public final void cannotDeleteFromEmptyTree() {
        bst.deleteMin();
    }

    @Test
    public final void canRemoveBySettingNull() {
        bst.put(1, 2);
        assertTrue(bst.contains(1));
        bst.put(1, null);
        assertFalse(bst.contains(1));
    }

    @Test(expected = NoSuchElementException.class)
    public final void emptyTreeHasNoMax() {
        bst.max();
    }

    @Test(expected = NoSuchElementException.class)
    public final void emptyTreeHasNoMin() {
        bst.min();
    }

    @Test
    public final void canMaintainOrder() {
        bst.delete(0);
        bst.put(1, 2);
        bst.put(2, 3);
        bst.put(0, 4);
        bst.put(2, 6);
        assertEquals(Integer.valueOf(4), bst.get(0));
        assertEquals(Integer.valueOf(0), bst.min());
        assertEquals(Integer.valueOf(2), bst.max());
        bst.deleteMin();
        assertEquals(Integer.valueOf(1), bst.min());
        bst.put(0, 4);
        bst.delete(0);
        assertEquals(Integer.valueOf(1), bst.min());
        bst.delete(2);
        assertEquals(Integer.valueOf(1), bst.max());
        bst.put(2, 6);
        bst.put(0, 6);
        bst.delete(1);
        assertEquals(Integer.valueOf(0), bst.min());
        assertEquals(Integer.valueOf(2), bst.max());
        bst.put(1, 6);
        bst.delete(0);
        bst.delete(1);
        assertEquals(Integer.valueOf(2), bst.min());
        assertEquals(Integer.valueOf(2), bst.max());
    }
}
