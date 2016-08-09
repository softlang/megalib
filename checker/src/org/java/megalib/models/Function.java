package org.java.megalib.models;

import java.util.LinkedList;

public class Function {
	public LinkedList<String> returnType;
	public LinkedList<String> parameterTypes;
	
	public Function() {
		returnType = new LinkedList<>();
		parameterTypes = new LinkedList<>();
	}
}
