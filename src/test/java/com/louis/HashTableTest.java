package com.louis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import java.util.HashSet;
import org.junit.rules.ExpectedException;
import org.junit.Rule;

/**
 * Tests for {@link HashTable}.
 *
 * @author Louis Ashton  (louisashton@live.com)
 */
public class HashTableTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    HashTable listIntegerTable = new HashTable();
    HashTable stringListTable = new HashTable();

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
    public void cannotGetNull() {
        exception.expect(IllegalArgumentException.class);
        stringListTable.get(null);
    }

    @Test
    public void cannotPutNull() {
        exception.expect(IllegalArgumentException.class);
        stringListTable.put("k", null);
    }

    /**
     * Tests addition to hashtables.
     *
     * Checks whether entries can be put into the hashtables.
     */
    @Test
    public void canPutEntries() {
        for (int i = 0; i < 1000; i++) {
            stringListTable.put(Integer.toString(i), Integer.toString(i));
            assertEquals(i + 1, stringListTable.size());
            listIntegerTable.put(Integer.toString(i), Integer.toString(i));
            assertEquals(i + 1, listIntegerTable.size());
        }
    }

    /**
     * Tests hashtable search.
     *
     * Checks whether entries can be retrieved from the hashtables. Does not discern whether values
     * were stored incorrectly, or retrieved incorrectly.
     */
    @Test
    public void canGetEntries() {
        for (int i = 0; i < 1000; i++) {
            stringListTable.put(Integer.toString(i), Integer.toString(i));
            listIntegerTable.put(Integer.toString(i), Integer.toString(i));
            assertEquals(true, stringListTable.contains(Integer.toString(i)));
            assertEquals(false, stringListTable.contains(Integer.toString(10000)));
            assertEquals(Integer.toString(i), stringListTable.get(Integer.toString(i)));
            assertEquals(Integer.toString(i), listIntegerTable.get(Integer.toString(i)));
        }
    }

    @Test
    public void canDeleteEntries() {
        for (int i = 0; i < 750; i++) {
            stringListTable.put(Integer.toString(i), Integer.toString(i));
            assertEquals(true, stringListTable.contains(Integer.toString(i)));
        }
        for (int i = 0; i < 750; i++) {
            stringListTable.delete(Integer.toString(i));
            assertEquals(false, stringListTable.contains(Integer.toString(i)));
            stringListTable.delete(Integer.toString(i));
        }
    }

    @Test
    public void canGetSetOfKeys() {
        HashSet<String> entries = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            stringListTable.put(Integer.toString(i), Integer.toString(i));
            entries.add(Integer.toString(i));
        }
        HashSet<String> keys = stringListTable.getAll();
        assertEquals(true, keys.equals(entries));
    }
}
