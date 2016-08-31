package org.java.megalib.models;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Function {
	private List<String> returnList;
	private List<String> parameterList;
	
	public Function() {
		returnList = new LinkedList<>();
		parameterList = new LinkedList<>();
	}

	public List<String> getReturnList() {
		return Collections.unmodifiableList(returnList);
	}
	
	public void setReturnList(LinkedList<String> returnList) {
		this.returnList = returnList;
	}

	public List<String> getParameterList() {
		return Collections.unmodifiableList(parameterList);
	}
	
	public void setParameterList(LinkedList<String> parameterlist) {
		this.parameterList = parameterlist;
	}
	
	
}
