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
		if(inputs.size()!=o.getInputs().size()){
			System.out.println(inputs);
			System.out.println(o.getInputs().size()+"input size unequal "+inputs.size());
			return false;
		}
		if(outputs.size()!=o.getOutputs().size()){
			System.out.println("output size unequal");
			return false;
		}
		for(int i=0;i<inputs.size();i++){
			if(!inputs.get(i).equals(o.getInputs().get(i))){
				System.out.println("'"+inputs.get(i)+"' vs '"+o.getInputs().get(i)+"'");
				return false;
			}
		}
		for(int i=0;i<outputs.size();i++){
			if(!outputs.get(i).equals(o.getOutputs().get(i))){
				System.out.println("'"+outputs.get(i)+"' vs '"+o.getOutputs().get(i)+"'");
				return false;
			}
		}
		return true;
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
