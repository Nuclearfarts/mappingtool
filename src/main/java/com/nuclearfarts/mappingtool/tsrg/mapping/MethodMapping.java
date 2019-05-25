package com.nuclearfarts.mappingtool.tsrg.mapping;

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
		return new MethodMapping(newName, originalName, remapper.mapMethodDesc(desc));
	}


}
