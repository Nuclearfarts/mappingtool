package com.nuclearfarts.mappingtool.util;

public interface ParameterRemapper {
	public String mapParameterName(String owner, String methodName, String methodDescriptor, String paramName, int localVarIndex);
}
