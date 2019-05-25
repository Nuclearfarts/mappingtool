package com.nuclearfarts.mappingtool.util;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LocalVarFixMethodVisitor extends MethodVisitor {

	public final int params;
	public final String owner;
	public final String methodName;
	public final String desc;
	private final ParameterRemapper remapper;
	
	public LocalVarFixMethodVisitor(MethodVisitor methodVisitor, String owner, String name, String desc, int params, ParameterRemapper remapper) {
		super(Opcodes.ASM6, methodVisitor);
		this.params = params;
		this.owner = owner;
		this.methodName = name;
		this.desc = desc;
		this.remapper = remapper;
	}
	
	public void visitLocalVariable(String name, String desc, String sig, Label start, Label end, int idx) {
		//Minecraft's obfuscator calls every local variable a unicode snowman.
		name = remapper.mapParameterName(owner, methodName, this.desc, name, idx);
		super.visitLocalVariable(name, desc, sig, start, end, idx);
	}
}
