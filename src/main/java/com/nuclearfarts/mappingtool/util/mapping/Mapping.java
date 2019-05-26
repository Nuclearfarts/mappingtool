package com.nuclearfarts.mappingtool.util.mapping;

import org.objectweb.asm.commons.Remapper;

public abstract class Mapping {
	
	public abstract String getStringIdentifier();
	public abstract Mapping reverse(Remapper remapper);
	public abstract String stringify();
	
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
