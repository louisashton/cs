package com.louis;

import org.immutables.value.Value;

import java.lang.String;

/**
 * Defines a HashTable entry.
 *
 * Each entry consists of a key and an associated value. Both can be retrieved.
 */
@Value.Immutable
public interface HashEntry {
    String getKey();

    String getValue();
}
