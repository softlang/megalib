package org.java.megalib.models;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Function {
	private List<String> returnTypes;
	private List<String> parameterTypes;
	
	public Function() {
		returnTypes = new LinkedList<>();
		parameterTypes = new LinkedList<>();
	}

	public List<String> getReturnTypes() {
		return Collections.unmodifiableList(returnTypes);
	}
	
	public void setReturnTypes(LinkedList<String> returnTypes) {
		this.returnTypes = returnTypes;
	}

	public List<String> getParameterTypes() {
		return Collections.unmodifiableList(parameterTypes);
	}
	
	public void setParameterTypes(LinkedList<String> parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	
	
}
