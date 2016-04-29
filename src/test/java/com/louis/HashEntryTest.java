package com.louis;

import com.louis.HashEntry;

public class HashEntryTest {
	HashEntry entry =
			ImmutableHashEntry.builder()
			.key("abc")
			.value("def")
			.build();
}