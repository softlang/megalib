package org.java.megalib.models;

import java.util.LinkedList;

public class Function {
	public String returnType;
	public LinkedList<String> parameterTypes;
	
	public Function() {
		returnType = new String();
		parameterTypes = new LinkedList<>();
	}
}
