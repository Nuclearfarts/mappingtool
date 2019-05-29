package com.nuclearfarts.mappingtool.util;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
/**
 * 
 * A pretty standard ClassRemapper with a few extra capabilities:
 * 1. It can also remap parameters/local variables.
 * 2. It also makes the compile source null so that Eclipse will find the source from a decompiler.
 *
 */
public class MappingToolClassRemapper extends ClassRemapper {
	
	private final ParameterRemapper pRemapper;
	
	public MappingToolClassRemapper(ClassVisitor classVisitor, Remapper remapper, ParameterRemapper pRemapper) {
		super(classVisitor, remapper);
		this.pRemapper = pRemapper;
	}
	
	@Override
	public void visitSource(String source, String debug) {
		super.visitSource(null, null);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions) {
		//System.out.println(name);
		return new LocalVarFixMethodVisitor(super.visitMethod(access, name, desc, sig, exceptions), className, name, desc, Type.getArgumentTypes(desc).length, pRemapper);
	}

}
