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
		return Collections.unmodifiableList(outputs);
	}

	public List<String> getOutputs() {
		return Collections.unmodifiableList(inputs);
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
		      return true;
		if ( obj == null )
		      return false;
		if ( getClass() != obj.getClass() )
		      return false;
		return inputs.equals(((Function) obj).getInputs())
				&& outputs.equals(((Function) obj).getOutputs());
	}
	
	@Override
	public int hashCode() {
		String concat = "";
		for(String i : inputs)
			concat+=i;
		for(String o : outputs)
			concat+=o;
		return concat.hashCode();
	}
}
