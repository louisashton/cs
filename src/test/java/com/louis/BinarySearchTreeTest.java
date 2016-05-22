package com.louis;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.Rule;

/**
 * Tests my BinarySearchTree implementation.
 * 
 * @author Louis Ashton (louisashton@live.com)
 */
public class BinarySearchTreeTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    @Test
    public void canAddSearchAndDelete() {
        BinarySearchTree<Integer,Integer> bst = new BinarySearchTree<>();
        assertEquals(0, bst.size());
        assertEquals(null, bst.get(1));
        bst.put(1,2);
        assertEquals(true, bst.contains(1));
        assertEquals(1, bst.size());
        assertEquals(false, bst.contains(2));
        assertEquals(Integer.valueOf(2), bst.get(1));
        bst.delete(1);
        assertEquals(false, bst.contains(1));
        bst.put(1,2);
        bst.put(1,null);
        exception.expect(IllegalArgumentException.class);
        bst.contains(null);
    }

    @Test
    public void cannotDeleteFromEmptyTree() {
        BinarySearchTree<Integer, Integer> bst = new BinarySearchTree<>();
        exception.expect(NoSuchElementException.class);
        bst.deleteMin();
    }

    @Test
    public void canRemoveBySettingNull() {
        BinarySearchTree<Integer,Integer> bst = new BinarySearchTree<>();
        bst.put(1,2);
        assertEquals(true, bst.contains(1));
        bst.put(1,null);
        assertEquals(false, bst.contains(1));
    }

    @Test
    public void emptyTreeHasNoMax() {
        BinarySearchTree<Integer, Integer> bst = new BinarySearchTree<>();
        exception.expect(NoSuchElementException.class);
        bst.max();
    }

    @Test
    public void emptyTreeHasNoMin() {
        BinarySearchTree<Integer, Integer> bst = new BinarySearchTree<>();
        exception.expect(NoSuchElementException.class);
        bst.min();
    }

    @Test
    public void canMaintainOrder() {
        BinarySearchTree<Integer,Integer> bst = new BinarySearchTree<>();
        bst.delete(0);
        bst.put(1,2);
        bst.put(2,3);
        bst.put(0,4);
        bst.put(2,6);
        assertEquals(Integer.valueOf(4), bst.get(0));
        assertEquals(Integer.valueOf(0), bst.min());
        assertEquals(Integer.valueOf(2), bst.max());
        bst.deleteMin();
        assertEquals(Integer.valueOf(1), bst.min());
        bst.put(0,4);
        bst.delete(0);
        assertEquals(Integer.valueOf(1), bst.min());
        bst.delete(2);
        assertEquals(Integer.valueOf(1), bst.max());
        bst.put(2,6);
        bst.put(0,6);
        bst.delete(1);
        assertEquals(Integer.valueOf(0), bst.min());
        assertEquals(Integer.valueOf(2), bst.max());
        bst.put(1,6);
        bst.delete(0);
        bst.delete(1);
        assertEquals(Integer.valueOf(2), bst.min());
        assertEquals(Integer.valueOf(2), bst.max());
    }
}
