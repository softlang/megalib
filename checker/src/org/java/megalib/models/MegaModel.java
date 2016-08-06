/**
 * 
 */
package org.java.megalib.models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Merlin May
 *
 */
public class MegaModel {
	public Map<String, String> entityDeclarations;
	public Map<String, String> entityInstances;
	public Map<String, Map<Integer, LinkedList<String>>> relationDeclarations;
	public Map<String, LinkedList<String>> relationInstances;
	public Map<String, LinkedList<String>> functionDeclarations;
	public Map<String, LinkedList<String>> functionInstances;
	
	public MegaModel() {
		entityDeclarations =  new HashMap<String, String>();
		entityInstances = new HashMap<String,String>();
		relationDeclarations = new HashMap<String, Map<Integer,LinkedList<String>>>();
		relationInstances = new HashMap<String, LinkedList<String>>();
		functionDeclarations = new HashMap<String, LinkedList<String>>();
		functionInstances = new HashMap<String, LinkedList<String>>();
	}
}
