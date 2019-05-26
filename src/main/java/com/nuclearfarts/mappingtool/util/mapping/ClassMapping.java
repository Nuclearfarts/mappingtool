package com.nuclearfarts.mappingtool.util.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.objectweb.asm.commons.Remapper;

public class ClassMapping extends Mapping {
	
	public final Map<String, FieldMapping> fields = new HashMap<String, FieldMapping>();
	public final Map<String, MethodMapping> methods = new HashMap<String, MethodMapping>();
	public ClassMapping superClass;
	public final List<ClassMapping> interfaces = new ArrayList<ClassMapping>();
	
	public ClassMapping(String obfName, String srgName, Collection<FieldMapping> fields, Collection<MethodMapping> methods) {
		super(obfName, srgName);
		fields.stream().forEach(fM -> this.fields.put(fM.getStringIdentifier(), fM));
		methods.stream().forEach(mM -> this.methods.put(mM.getStringIdentifier(), mM));
	}
	
	@Override
	public ClassMapping reverse(Remapper remapper) {
		List<FieldMapping> revFields = fields.values().stream().map(fM -> fM.reverse(remapper)).collect(Collectors.toList());
		List<MethodMapping> revMethods = methods.values().stream().map(mM -> mM.reverse(remapper)).collect(Collectors.toList());
		return new ClassMapping(newName, originalName, revFields, revMethods);
	}
	
	@Override
	public String getStringIdentifier() {
		return originalName;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[" + fields.values().toString() + "," + methods.values().toString() + "]";
	}
}
