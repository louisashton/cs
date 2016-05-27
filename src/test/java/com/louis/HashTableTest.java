package com.louis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    HashTable table = new HashTable();

    /**
     * Tests construction of hashtables.
     *
     * Checks whether hashtables of various types can be constructed.
     */
    @Test
    public void canConstructHashTables() {
        assertNotNull(table);
    }

    /**
     * Tests illegal argument exceptions.
     *
     * Checks whether hashtables throw IllegalArgumentException when passed null values.
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotGetNull() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotPutNull() {
        table.put("k", null);
    }

    /**
     * Tests addition to hashtables.
     *
     * Checks whether entries can be put into the hashtables.
     */
    @Test
    public void canPutEntries() {
        for (int i = 0; i < 10; i++) {
            table.put(Integer.toString(i), Integer.toString(i));
            assertEquals(i + 1, table.size());
            table.put(Integer.toString(i), Integer.toString(i));
            assertEquals(i + 1, table.size());
        }
    }

    @Test
    public void resizesCorrectly() {
        for (int i = 0; i < 1000; i++) {
            table.put(Integer.toString(i), Integer.toString(i));
            assertEquals(true, table.contains(Integer.toString(i)));
            assertEquals(false, table.contains(Integer.toString(10000)));
            assertEquals(Integer.toString(i), table.get(Integer.toString(i)));
        }
    }

    @Test
    public void canDeleteEntries() {
        for (int i = 0; i < 33; i++) {
            table.put(Integer.toString(i), Integer.toString(i));
            assertEquals(true, table.contains(Integer.toString(i)));
        }
        for (int i = 0; i < 33; i++) {
            table.delete(Integer.toString(i));
            assertEquals(false, table.contains(Integer.toString(i)));
            table.delete(Integer.toString(i));
        }
    }

    @Test
    public void canGetSetOfKeys() {
        HashSet<String> entries = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            table.put(Integer.toString(i), Integer.toString(i));
            entries.add(Integer.toString(i));
        }
        HashSet<String> keys = table.getAllKeys();
        assertEquals(true, keys.equals(entries));
    }
}
