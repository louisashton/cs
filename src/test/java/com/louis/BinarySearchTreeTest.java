/**
 * 
 */
package com.louis;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.Rule;

/**
 * Tests my BinarySearchTree implementation.
 * 
 * Uses JUnit over the lifespan of a BST.
 * 
 * @author Louis Ashton (louisashton@live.com)
 *
 */
public class BinarySearchTreeTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {}

    /**
     * Tests the BST.
     * 
     * Checks whether an element can be added and removed from the BST.
     * Checks the important contains function.
     */
    @Test
    public void test() {
        BinarySearchTree<Integer,Integer> bst = new BinarySearchTree<>();
        bst.put(1,2);
        assertEquals(true, bst.contains(1));
        assertEquals(false, bst.contains(2));
        assertEquals(Integer.valueOf(2), bst.get(1));
        bst.delete(1);
        assertEquals(false, bst.contains(1));
        exception.expect(IllegalArgumentException.class);
        bst.contains(null);
        exception.expect(NoSuchElementException.class);
        bst.deleteMin();
    }

}
