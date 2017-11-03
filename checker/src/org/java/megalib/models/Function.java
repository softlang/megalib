package org.java.megalib.models;

import java.util.Collections;
import java.util.List;

public class Function {
	private List<String> inputs;
	private List<String> outputs;
	
	private Block block;
	public boolean isDecl;
	
	public Function(List<String> inputs, List<String> outputs, boolean isDecl) {
		this.inputs = inputs;
		this.outputs=outputs;
		this.isDecl = isDecl;
	}

	public List<String> getInputs() {
		return Collections.unmodifiableList(inputs);
	}

	public List<String> getOutputs() {
		return Collections.unmodifiableList(outputs);
	}
	
	public Block getBlock(){
		return block;
	}
	
	public void setBlock(Block b){
		this.block = b;
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
		return inputs.hashCode() + outputs.hashCode();
	}
}
