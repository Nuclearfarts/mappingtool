package com.nuclearfarts.mappingtool.util.mapping;

import org.objectweb.asm.commons.Remapper;

public class ParameterMapping extends Mapping {

	public ParameterMapping(String obfName, String srgName) {
		super(obfName, srgName);
	}

	@Override
	public String getStringIdentifier() {
		return originalName;
	}

	@Override
	public ParameterMapping reverse(Remapper remapper) {
		return new ParameterMapping(newName, originalName);
	}

	@Override
	public String stringify() {
		return new StringBuilder(originalName).append(' ').append(newName).toString();
	}

}
