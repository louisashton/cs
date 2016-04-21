package com.louis;

import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import java.util.ArrayList;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import static org.junit.Assert.assertEquals;


/**
 * Tests my HashTable implementation.
 * 
 * Uses JUnit over the lifecycle of two hashtables.
 * 
 * @author Louis Ashton  (louisashton@live.com)
 *
 */
public class HashTableTest {
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    HashTable<ArrayList<Integer>, Integer> listIntegerTable = new HashTable<>();
    HashTable<String, ArrayList<String>> stringListTable = new HashTable<>();
    
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
     * Tests construction of hashtables.
     * 
     * Checks whether hashtables of various types can be constructed.
     */
    @Test
    public void canConstructHashTables() {
        assertEquals(listIntegerTable.size(), 0);
        assertEquals(stringListTable.size(), 0);
    }
    
    /**
     * Tests illegal argument exceptions.
     * 
     * Checks whether hashtables throw IllegalArgumentException when passed null values.
     */
    @Test
    public void identifiesIllegalArguments() {
        exception.expect(IllegalArgumentException.class);
        listIntegerTable.get(null);
        exception.expect(IllegalArgumentException.class);
        stringListTable.get(null);
        exception.expect(IllegalArgumentException.class);
        stringListTable.put("k",null);
    }
    
    /**
     * Tests addition to hashtables.
     * 
     * Checks whether entries can be put into the hashtables.
     */
    @Test
    public void canPutEntries() {
        for (int i = 0; i < 1000; i++) {
            ArrayList<Integer> integerList = new ArrayList<>();
            ArrayList<String> stringList = new ArrayList<>();
            stringList.add(Integer.toString(i));
            integerList.add(i);
            stringListTable.put(Integer.toString(i), stringList);
            assertEquals(i + 1, stringListTable.size());
            listIntegerTable.put(integerList, i);
            assertEquals(i + 1, listIntegerTable.size());
        }
    }
    
    /**
     * Tests hashtable search.
     * 
     * Checks whether entries can be retrieved from the hashtables.
     * Does not discern whether values were stored incorrectly, or retrieved incorrectly.
     */
    @Test
    public void canGetEntries() {
        for (int i = 0; i < 1000; i++) {
            ArrayList<Integer> integerList = new ArrayList<>();
            ArrayList<String> stringList = new ArrayList<>();
            stringList.add(Integer.toString(i));
            integerList.add(i);
            stringListTable.put(Integer.toString(i), stringList);
            listIntegerTable.put(integerList, i);
            assertEquals(stringList, stringListTable.get(Integer.toString(i)));
            assertEquals(Integer.valueOf(i), listIntegerTable.get(integerList));
        }
    }
}
