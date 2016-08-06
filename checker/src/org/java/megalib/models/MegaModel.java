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
	public Map<String, Map<Integer, LinkedList<String>>> relationInstances;
	public Map<String, Map<Integer, Function>> functionDeclarations;
	public Map<String, Map<Integer, Function>> functionInstances;
	public Map<String,LinkedList<String>> links;
	
	public MegaModel() {
		entityDeclarations =  new HashMap<String, String>();
		entityInstances = new HashMap<String,String>();
		relationDeclarations = new HashMap<String, Map<Integer,LinkedList<String>>>();
		relationInstances = new HashMap<String, Map<Integer,LinkedList<String>>>();
		functionDeclarations = new HashMap<String, Map<Integer,Function>>();
		functionInstances = new HashMap<String, Map<Integer,Function>>();
		links = new HashMap<String,LinkedList<String>>();
	}
}
