package com.nuclearfarts.mappingtool.tsrg;

import org.objectweb.asm.commons.Remapper;

import com.nuclearfarts.mappingtool.tsrg.mapping.ClassMapping;
import com.nuclearfarts.mappingtool.tsrg.mapping.FieldMapping;
import com.nuclearfarts.mappingtool.tsrg.mapping.MethodMapping;
import com.nuclearfarts.mappingtool.util.ParameterRemapper;

public class TSRGBasedRemapper extends Remapper implements ParameterRemapper {
	
	public final TSRG tsrg;
	
	public TSRGBasedRemapper(TSRG tsrg) {
		this.tsrg = tsrg;
	}
	
	@Override
	public String map(String typeName) {
		ClassMapping mapping = tsrg.mappings.get(typeName);
		if(mapping != null) {
			return mapping.newName;
		}
		return typeName;
	}
	
	@Override
	public String mapFieldName(String owner, String name, String descriptor) {
		ClassMapping clazz = tsrg.mappings.get(owner);
		if(clazz != null) {
			FieldMapping mapping = clazz.fields.get(name);
			if(mapping != null) {
				return mapping.newName;
			} else {
				String mapped = name;
				if(clazz.superClass != null) {
					mapped = mapFieldName(clazz.superClass.originalName, name, descriptor);
				}
				if(!clazz.interfaces.isEmpty() && mapped.equals(name)) {
					for(ClassMapping itf : clazz.interfaces) {
						mapped = mapFieldName(itf.originalName, name, descriptor);
						if(!mapped.equals(name)) {
							break;
						}
					}
				}
				return mapped;
			}
		}
		FieldMapping mapping = getFieldMapping(owner, name, descriptor);
		if(mapping != null) {
			return mapping.newName;
		}
		return name;
	}
	
	public FieldMapping getFieldMapping(String owner, String name, String descriptor) {
		//System.out.println("checking class " + owner + " for field " + name);
		ClassMapping clazz = tsrg.mappings.get(owner);
		if(clazz != null) {
			FieldMapping mapping = clazz.fields.get(name);
			if(mapping != null) {
				return mapping;
			} else {
				if(clazz.superClass != null) {
					mapping = getFieldMapping(clazz.superClass.originalName, name, descriptor);
					if(mapping != null) {
						return mapping;
					}
				}
				if(!clazz.interfaces.isEmpty() && mapping == null) {
					for(ClassMapping itf : clazz.interfaces) {
						mapping = getFieldMapping(itf.originalName, name, descriptor);
						if(mapping != null) {
							return mapping;
						}
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public String mapMethodName(String owner, String name, String descriptor) {
		/*ClassMapping clazz = tsrg.mappings.get(owner);
		if(clazz != null) {
			MethodMapping mapping = clazz.methods.get(name + descriptor);
			if(mapping != null) {
				return mapping.srgName;
			} else {
				String mapped = name;
				if(clazz.superClass != null) {
					mapped = mapMethodName(clazz.superClass.obfName, name, descriptor);
				}
				if(!clazz.interfaces.isEmpty() && mapped.equals(name)) {
					for(ClassMapping itf : clazz.interfaces) {
						mapped = mapMethodName(itf.obfName, name, descriptor);
						if(!mapped.equals(name)) {
							break;
						}
					}
				}
				return mapped;
			}
		}*/
		MethodMapping mapping = getMethodMapping(owner, name, descriptor);
		if(mapping != null) {
			return mapping.newName;
		}
		return name;
	}
	
	public MethodMapping getMethodMapping(String owner, String name, String descriptor) {
		ClassMapping clazz = tsrg.mappings.get(owner);
		if(clazz != null) {
			MethodMapping mapping = clazz.methods.get(name + descriptor);
			if(mapping != null) {
				return mapping;
			} else {
				if(clazz.superClass != null) {
					mapping = getMethodMapping(clazz.superClass.originalName, name, descriptor);
				}
				if(mapping != null) {
					return mapping;
				}
				if(!clazz.interfaces.isEmpty() && mapping == null) {
					for(ClassMapping itf : clazz.interfaces) {
						mapping = getMethodMapping(itf.originalName, name, descriptor);
						if(mapping != null) {
							return mapping;
						}
					}
					
				}
			}
		}
		return null;
	}

	@Override
	public String mapParameterName(String owner, String methodName, String methodDescriptor, String paramName, int localVarIndex) {
		//U+2603 is the unicode snowman, the character MC's obfuscator renames local variables to.
		if(paramName.charAt(0) == 0x2603) {
			MethodMapping parent = getMethodMapping(owner, methodName, methodDescriptor);
			if(parent != null) {
				String[] splitName = parent.newName.split("_");
				if(splitName.length != 3) {
					return "localVar_" + localVarIndex;
				}
				//System.out.println("work");
				return "p_" + parent.newName.split("_")[1] + "_" + localVarIndex + "_";
			} else {
				return "localVar_" + localVarIndex;
			}
		} else {
			return paramName;
		}
	}
}
