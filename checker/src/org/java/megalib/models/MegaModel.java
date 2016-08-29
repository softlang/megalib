/**
 * 
 */
package org.java.megalib.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Merlin May
 *
 */
public class MegaModel {
	private Map<String, String> subtypesMap;
	private Map<String, String> instanceOfMap;
	private Map<String, Map<Integer, List<String>>> relationDeclarationMap;
	private Map<String, Map<Integer, List<String>>> relationInstanceMap;
	private Map<String, Map<Integer, Function>> functionDeclarations;
	private Map<String, Map<Integer, Function>> functionInstances;
	private Map<String,List<String>> linkMap;
	
	public MegaModel() {
		subtypesMap = new HashMap<>();
		instanceOfMap = new HashMap<>();
		relationDeclarationMap = new HashMap<>();
		relationInstanceMap = new HashMap<>();
		functionDeclarations = new HashMap<>();
		functionInstances = new HashMap<>();
		linkMap = new HashMap<>();
	}

	public Map<String, String> getSubtypesMap() {
		return Collections.unmodifiableMap(subtypesMap);
	}
	
	public void addSubtypeOf(String subtype, String type){
		subtypesMap.put(subtype, type);
	}

	public Map<String, String> getInstanceOfMap() {
		return Collections.unmodifiableMap(instanceOfMap);
	}
	
	public void addInstanceOf(String instance, String type) {
		instanceOfMap.put(instance, type);
	}

	public Map<String, Map<Integer, List<String>>> getRelationDeclarationMap() {
		return Collections.unmodifiableMap(relationDeclarationMap);
	}
	
	public void addRelationDeclaration(String relation, Map<Integer, List<String>> relationTypes) {
		relationDeclarationMap.put(relation, relationTypes);
	}

	public Map<String, Map<Integer, List<String>>> getRelationInstanceMap() {
		return Collections.unmodifiableMap(relationInstanceMap);
	}
	
	public void addRelationInstances(String relation, Map<Integer, List<String>> relationObjects) {
		relationInstanceMap.put(relation, relationObjects);
	}

	public Map<String, Map<Integer, Function>> getFunctionDeclarations() {
		return Collections.unmodifiableMap(functionDeclarations);
	}
	
	public void addFunctionDeclarations(String functionName, Map<Integer, Function> functionObjects) {
		functionDeclarations.put(functionName, functionObjects);
	}

	public Map<String, Map<Integer, Function>> getFunctionInstances() {
		return Collections.unmodifiableMap(functionInstances);
	}
	
	public void addFunctionInstance(String functionName, Map<Integer, Function> functionInstance) {
		functionInstances.put(functionName, functionInstance);
	}

	public Map<String, List<String>> getLinkMap() {
		return Collections.unmodifiableMap(linkMap);
	}

	public void addLinks(String object, List<String> links) {
		linkMap.put(object, links);
	}





	

	

	
}
