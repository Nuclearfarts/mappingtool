package com.nuclearfarts.mappingtool.util.mapping;

import org.objectweb.asm.commons.Remapper;

public class FieldMapping extends MemberMapping {

	public FieldMapping(String obfName, String srgName) {
		super(obfName, srgName);
		
	}
	
	@Override
	public FieldMapping reverse(Remapper remapper) {
		return new FieldMapping(newName, originalName);
	}
	
	@Override
	public String getStringIdentifier() {
		return originalName;
	}

}
