package com.louis;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for {@link HashSet}.
 * 
 * @author Louis Ashton (louisashton@live.com)
 */
public class HashSetTest {

    /**
     * Tests the hashset.
     * 
     * Checks whether an element can be added and removed from the set.
     * Also determines that non-unique values can't be added to the set.
     */
    @Test
    public void elementsUniquelyAdded() {
        HashSet<Integer> set = new HashSet<>();
        set.add(3);
        assertEquals(true,set.contains(3));
        set.add(3);
        set.remove(3);
        assertEquals(false,set.contains(3));
    }

}
