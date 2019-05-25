package com.nuclearfarts.mappingtool.util;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

public class LocalVarClassRemapper extends ClassRemapper {
	
	private final ParameterRemapper pRemapper;
	
	public LocalVarClassRemapper(ClassVisitor classVisitor, Remapper remapper, ParameterRemapper pRemapper) {
		super(classVisitor, remapper);
		this.pRemapper = pRemapper;
	}
	
	public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions) {
		//System.out.println(name);
		return new LocalVarFixMethodVisitor(super.visitMethod(access, name, desc, sig, exceptions), className, name, desc, Type.getArgumentTypes(desc).length, pRemapper);
	}

}
