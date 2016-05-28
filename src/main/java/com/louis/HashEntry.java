// CHECKSTYLE:OFF

// CHECKSTYLE:ON

package com.louis;

import org.immutables.value.Value;

/**
 * Defines a HashTable entry.
 * <p>
 * Each entry consists of a key and an associated value. Both can be retrieved.
 *
 * @author Louis Ashton (louisashton@live.com)
 */
@Value.Immutable
public interface HashEntry {
    String getKey();

    String getValue();
}
