/**
 * 
 */
package com.louis;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests my HashSet implementation.
 * 
 * Uses JUnit over the lifespan of a hashset.
 * 
 * @author Louis Ashton (louisashton@live.com)
 *
 */
public class HashSetTest {

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
     * Tests the hashset.
     * 
     * Checks whether an element can be added and removed from the set.
     * Also determines that non-unique values can't be added to the set.
     */
    @Test
    public void test() {
        HashSet<Integer> set = new HashSet<>();
        assertEquals(true,set.add(3));
        assertEquals(false,set.add(3));
        assertEquals(true,set.contains(3));
        assertEquals(true,set.remove(3));
    }

}
