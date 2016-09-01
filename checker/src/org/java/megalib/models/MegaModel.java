/**
 * 
 */
package org.java.megalib.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Merlin May
 *
 */
public class MegaModel {
	private Map<String, String> subtypesMap;
	private Map<String, String> instanceOfMap;
	private Map<String, Set<List<String>>> relationDeclarationMap;
	private Map<String, Set<List<String>>> relationInstanceMap;
	private Map<String, Set<Function>> functionDeclarations;
	private Map<String, Set<Function>> functionInstances;
	private Map<String,List<String>> linkMap;
	private Set<String> toImport;
	
	public MegaModel() {
		subtypesMap = new HashMap<>();
		instanceOfMap = new HashMap<>();
		relationDeclarationMap = new HashMap<>();
		relationInstanceMap = new HashMap<>();
		functionDeclarations = new HashMap<>();
		functionInstances = new HashMap<>();
		linkMap = new HashMap<>();
		toImport = new HashSet<>();
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

	public Map<String, Set<List<String>>> getRelationDeclarationMap() {
		return Collections.unmodifiableMap(relationDeclarationMap);
	}
	
	/**
	 * Adds a declaration for the relationname with given types to the
	 * relationDeclarationMap
	 * @param relationname
	 * @param relationTypes
	 */
	public void addRelationDeclaration(String relationname, List<String> relationTypes) {
		Set<List<String>> set = new HashSet<>();
		if(relationDeclarationMap.containsKey(relationname)){
			set = relationDeclarationMap.get(relationname);
		}
		set.add(relationTypes);
		relationDeclarationMap.put(relationname, set);
	}

	public Map<String, Set<List<String>>> getRelationshipInstanceMap() {
		return Collections.unmodifiableMap(relationInstanceMap);
	}
	
	public void addRelationInstances(String relationname, List<String> instances) {
		Set<List<String>> set = new HashSet<>();
		if(relationInstanceMap.containsKey(relationname)){
			set = relationInstanceMap.get(relationname);
		}
		set.add(instances);
		relationInstanceMap.put(relationname, set);
	}

	public Map<String, Set<Function>> getFunctionDeclarations() {
		return Collections.unmodifiableMap(functionDeclarations);
	}
	
	public void addFunctionDeclaration(String functionName, Function function) {
		Set<Function> set = new HashSet<>();
		if(functionDeclarations.containsKey(functionName)){
			set = functionDeclarations.get(functionName);
		}
		set.add(function);
		functionDeclarations.put(functionName, set);
	}

	public Map<String, Set<Function>> getFunctionInstances() {
		return Collections.unmodifiableMap(functionInstances);
	}
	
	public void addFunctionInstance(String functionName, Function functionInstance) {
		Set<Function> set = new HashSet<>();
		if(functionInstances.containsKey(functionName)){
			set = functionInstances.get(functionName);
		}
		set.add(functionInstance);
		functionInstances.put(functionName, set);
	}

	public Map<String, List<String>> getLinkMap() {
		return Collections.unmodifiableMap(linkMap);
	}

	public void addLinks(String object, List<String> links) {
		linkMap.put(object, links);
	}

	public Set<String> getImports() {
		return Collections.unmodifiableSet(toImport);
	}

	public void addImport(String filepath) {
		toImport.add(filepath);
	}
}
