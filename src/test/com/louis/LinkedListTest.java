package com.louis;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import java.util.ArrayList;
import org.junit.rules.ExpectedException;
import org.junit.Rule;

/**
 * Tests for {@link LinkedList}.
 * 
 * @author Louis Ashton  (louisashton@live.com)
 */
public class LinkedListTest {
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
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
    @Test
    public void identifiesIllegalArguments() {
        exception.expect(IllegalArgumentException.class);
        list.get(null);
        exception.expect(IllegalArgumentException.class);
        list.put(null);
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
    public void canGetEntries() {
        for (int i = 0; i < 1000; i++) {
            list.add(i);
            assertEquals(true, listIntegerTable.contains(i));
        }
    }
}