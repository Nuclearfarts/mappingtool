package com.nuclearfarts.mappingtool.util.mapping;

import org.objectweb.asm.commons.Remapper;

public class MethodMapping extends MemberMapping {

	public final String desc;
	
	public MethodMapping(String obfName, String srgName, String desc) {
		super(obfName, srgName);
		this.desc = desc;
	}

	@Override
	public String getStringIdentifier() {
		return originalName + desc;
	}

	@Override
	public MethodMapping reverse(Remapper remapper) {
		if(remapper != null) {
			return new MethodMapping(newName, originalName, remapper.mapMethodDesc(desc));
		} else {
			return new MethodMapping(newName, originalName, desc);
		}
	}

	@Override
	public String stringify() {
		return new StringBuilder(originalName).append(' ').append(desc).append(' ').append(newName).toString();
	}


}
