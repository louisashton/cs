package com.louis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/**
 * Tests for {@link HashTable}.
 *
 * @author Louis Ashton  (louisashton@live.com)
 */
public class HashTableTest {

    private HashTable table = new HashTable();

    /**
     * Tests construction of hashtables.
     * <p>
     * Checks whether hashtables of various types can be constructed.
     */
    @Test
    public final void canConstructHashTables() {
        assertNotNull(table);
    }

    /**
     * Tests illegal argument exceptions.
     * <p>
     * Checks whether hashtables throw IllegalArgumentException when passed null values.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void cannotGetNull() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void cannotPutNull() {
        table.put("k", null);
    }

    /**
     * Tests addition to hashtables.
     * <p>
     * Checks whether entries can be put into the hashtables.
     */
    @Test
    public final void canPutEntries() {
        for (int i = 0; i < 10; i++) {
            table.put(Integer.toString(i), Integer.toString(i));
            assertEquals(i + 1, table.size());
            table.put(Integer.toString(i), Integer.toString(i));
            assertEquals(i + 1, table.size());
        }
    }

    @Test
    public final void resizesCorrectly() {
        for (int i = 0; i < 1000; i++) {
            table.put(Integer.toString(i), Integer.toString(i));
            assertTrue(table.contains(Integer.toString(i)));
            assertFalse(table.contains(Integer.toString(10000)));
            assertEquals(Integer.toString(i), table.get(Integer.toString(i)));
        }
    }

    @Test
    public final void canDeleteEntries() {
        for (int i = 0; i < 33; i++) {
            table.put(Integer.toString(i), Integer.toString(i));
            assertTrue(table.contains(Integer.toString(i)));
        }
        for (int i = 0; i < 33; i++) {
            table.delete(Integer.toString(i));
            assertFalse(table.contains(Integer.toString(i)));
            table.delete(Integer.toString(i));
        }
    }

    @Test
    public final void canGetSetOfKeys() {
        Set<String> entries = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            table.put(Integer.toString(i), Integer.toString(i));
            entries.add(Integer.toString(i));
        }
        Set<String> keys = table.getAllKeys();
        assertTrue(keys.equals(entries));
    }
}
