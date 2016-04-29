package com.louis;

import org.immutables.value.Value;
import java.lang.String;
//Define abstract value type using interface, abstract class or annotation
@Value.Immutable
public interface HashEntry {
	String getKey();
	String getValue();
}
