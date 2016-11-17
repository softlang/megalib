package org.java.megalib.models;

import java.util.Collections;
import java.util.List;

public class Function {
	private List<String> inputs;
	private List<String> outputs;
	
	public Function(List<String> inputs, List<String> outputs) {
		this.inputs = inputs;
		this.outputs=outputs;
	}

	public List<String> getInputs() {
		return Collections.unmodifiableList(inputs);
	}

	public List<String> getOutputs() {
		return Collections.unmodifiableList(outputs);
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
		      return true;
		if ( obj == null ){
			System.out.println("Null");
			return false;
		}
		      
		if ( getClass() != obj.getClass() ){
			System.out.println("Class unequal");
		      return false;
		}
		Function o = (Function) obj;
		return o.hashCode()==hashCode();
	}
	
	@Override
	public int hashCode() {
		String concat = "";
		for(String i : inputs)
			concat+=i;
		concat+="->";
		for(String o : outputs)
			concat+=o;
		return concat.hashCode();
	}
}
