package com.nuclearfarts.mappingtool.mcpmapping;

import org.objectweb.asm.commons.Remapper;

import com.nuclearfarts.mappingtool.tsrg.mapping.FieldMapping;
import com.nuclearfarts.mappingtool.tsrg.mapping.MethodMapping;
import com.nuclearfarts.mappingtool.tsrg.mapping.ParameterMapping;
import com.nuclearfarts.mappingtool.util.ParameterRemapper;

public class MCPMappingBasedRemapper extends Remapper implements ParameterRemapper {
	
	public final MCPMappings mappings;
	
	public MCPMappingBasedRemapper(MCPMappings mappings) {
		this.mappings = mappings;
	}
	
	@Override
	public String mapMethodName(String owner, String name, String descriptor) {
		MethodMapping mapping = mappings.methods.get(name);
		if(mapping != null) {
			//System.out.println("mapped method " + name + " to " + mapping.newName);
			return mapping.newName;
		}
		return name;
	}
	
	@Override
	public String mapFieldName(String owner, String name, String descriptor) {
		FieldMapping mapping = mappings.fields.get(name);
		if(mapping != null) {
			//System.out.println("mapped field " + name + " to " + mapping.newName);
			return mapping.newName;
		}
		return name;
	}
	
	@Override
	public String mapParameterName(String owner, String methodName, String methodDescriptor, String paramName, int localVarIndex) {
		ParameterMapping mapping = mappings.parameters.get(paramName);
		if(mapping != null) {
			//System.out.println("mapped param " + paramName + " to " + mapping.newName);
			return mapping.newName;
		}
		return paramName;
	}

}
