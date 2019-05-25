package com.nuclearfarts.mappingtool.tsrg.mapping;

import org.objectweb.asm.commons.Remapper;

public abstract class Mapping {
	
	public abstract String getStringIdentifier();
	public abstract Mapping reverse(Remapper remapper);
	
	public final String originalName;
	public final String newName;
	
	public Mapping(String obfName, String srgName) {
		this.originalName = obfName;
		this.newName = srgName;
	}
	
	public String toString() {
		return getStringIdentifier() + " -> " + newName;
	}
}
