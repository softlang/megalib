/**
 * 
 */
package org.java.megalib.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MegaModel {
	private Map<String, String> subtypeOfMap;
	private Map<String, String> instanceOfMap;
	private Map<String, String> elementOfMap;
	private Map<String, String> subsetOfMap; 
	private Map<String, Set<Relation>> relationDeclarationMap;
	private Map<String, Set<Relation>> relationInstanceMap;
	private Map<String, Function> functionDeclarations;
	private Map<String, Set<Function>> functionInstances;
	private Map<String, List<String>> linkMap;
	private Map<String, String> substMap;
	
	private List<String> criticalWarnings;
	
	public MegaModel() {
		subtypeOfMap = new HashMap<>();
		instanceOfMap = new HashMap<>();
		elementOfMap = new HashMap<>();
		subsetOfMap = new HashMap<>();
		relationDeclarationMap = new HashMap<>();
		relationInstanceMap = new HashMap<>();
		functionDeclarations = new HashMap<>();
		functionInstances = new HashMap<>();
		linkMap = new HashMap<>();
		substMap = new HashMap<>();
		criticalWarnings = new ArrayList<>();
	}

	public Map<String, String> getSubtypesMap() {
		return Collections.unmodifiableMap(subtypeOfMap);
	}
	
	public void addSubtypeOf(String subtype, String type) throws Exception{
		if(!(subtypeOfMap.containsKey(type)||type.equals("Entity"))){
			throw new Exception("Error at "+subtype+": The declared supertype is not a subtype of Entity");
		}
		if(subtypeOfMap.containsKey(subtype))
			throw new Exception("Error at "+subtype+": Multiple inheritance is not allowed.");
		else
			subtypeOfMap.put(subtype, type);
	}

	public Map<String, String> getInstanceOfMap() {
		return Collections.unmodifiableMap(instanceOfMap);
	}
	
	public void addInstanceOf(String instance, String type) throws Exception {
		if(subtypeOfMap.containsKey(instance)){
			throw new Exception("Error at "+instance+": It is instance and type at the same time.");
		}
		if(instance.equals("Entity")){
			throw new Exception("Error at "+instance+": The root type `Entity' cannot be instantiated.");
		}
		if(!subtypeOfMap.containsKey(type)||type.equals("Entity")){
			throw new Exception("Error at "+instance+": The instantiated type is not (transitive) subtype of Entity.");
		}
		if(instanceOfMap.containsKey(instance)){
			throw new Exception("Error at "+instance+": Multiple types cannot be assigned to the same instance.");
		}
		instanceOfMap.put(instance, type);
	}

	public Map<String, Set<Relation>> getRelationshipDeclarationMap() {
		return Collections.unmodifiableMap(relationDeclarationMap);
	}
	
	/**
	 * Adds a declaration for the relationname with given types to the
	 * relationDeclarationMap
	 * @param name
	 * @param relationTypes
	 * @throws Exception 
	 */
	public void addRelationDeclaration(String name, String sType, String oType) throws Exception {
		if(!(subtypeOfMap.containsKey(sType)))
			throw new Exception("Error at declaration of "+name+": Its domain "+sType+" is not subtype of Entity.");
		if(!(subtypeOfMap.containsKey(oType)))
			throw new Exception("Error at declaration of "+name+": Its range "+oType+" is not subtype of Entity.");
		
		Set<Relation> set = new HashSet<>();
		if(relationDeclarationMap.containsKey(name))
			set = relationDeclarationMap.get(name);
		Relation decl = new Relation(sType,oType);
		
		if(set.contains(decl))
			throw new Exception("Error at declaration of "+name+": It is declared twice with the same types.");
		
		set.add(decl);
		relationDeclarationMap.put(name, set);
	}

	public Map<String, Set<Relation>> getRelationshipInstanceMap() {
		return Collections.unmodifiableMap(relationInstanceMap);
	}
	
	public void addRelationInstances(String name, String subject, String object) throws Exception {
		Set<Relation> set = new HashSet<>();
		if(relationInstanceMap.containsKey(name)){
			set = relationInstanceMap.get(name);
		}
		if(!instanceOfMap.containsKey(subject)){
			throw new Exception("Error at instance of "+name+": "+subject+" is not instantiated.");
		}
		if(!instanceOfMap.containsKey(object)){
			throw new Exception("Error at instance of "+name+": "+object+" is not instantiated.");
		}
		
		Relation i = new Relation(subject,object);
		if(set.contains(i)){
			throw new Exception("Error at instance of "+name+": '"+subject+" "+name+" "+object+"' already exists.");
		}
		checkRelationInstanceFits(name,subject,object);
		if(name.equals("elementOf"))
			elementOfMap.put(subject, object);
		else if(name.equals("subsetOf"))
			subsetOfMap.put(subject, object);
		set.add(i);
		relationInstanceMap.put(name, set);
	}

	private void checkRelationInstanceFits(String name, String subject, String object) throws Exception {
		Set<Relation> decls = relationDeclarationMap.get(name);
		
		//determine (super-)types of subject
		List<String> subjectTs = new ArrayList<>();
		String type = instanceOfMap.get(subject);
		while(!type.equals("Entity")){
			subjectTs.add(type);
			type = subtypeOfMap.get(type);
		}
		//determine (super-)types of object
		List<String> objectTs = new ArrayList<>();
		type = instanceOfMap.get(object);
		while(!type.equals("Entity")){
			objectTs.add(type);
			type = subtypeOfMap.get(type);
		}
		int count = 0;
		//build cross product and count fits to declarations
		for(String sT : subjectTs){
			for(String oT : objectTs){
				Relation r = new Relation(sT,oT);
				if(decls.contains(r))
					count++;
			}
		}
		if(count==0)
			throw new Exception("Error at instance of "+name+": '"+subject+" "+name+" "+object+"' does not fit any declaration.");
		if(count>1)
			throw new Exception("Error at instance of "+name+": '"+subject+" "+name+" "+object+"' fits multiple declarations.");
	}

	public Map<String, Function> getFunctionDeclarations() {
		return Collections.unmodifiableMap(functionDeclarations);
	}
	
	public void addFunctionDeclaration(String functionName, List<String> inputs, List<String> outputs) throws Exception {
		Function f = null;
		if(functionDeclarations.containsKey(functionName)){
			throw new Exception("Error: The function "+functionName+" has multiple declarations.");
		}
		f = new Function(inputs,outputs);
		for(String i : inputs){
			String type = instanceOfMap.get("i");
			if(type==null){
				throw new Exception("Error at "+functionName+"'s declaration"+": The language "+i+" was not declared.");
			}
			if(!isInstanceOf(type, "Language"))
				throw new Exception("Error at "+functionName+"'s declaration"+": "+i+" is not a language.");
		}
		for(String o : outputs){
			String type = instanceOfMap.get("i");
			if(type==null){
				throw new Exception("Error at "+functionName+"'s declaration"+": The language "+o+" was not declared.");
			}
			if(!isInstanceOf(type, "Language"))
				throw new Exception("Error at "+functionName+"'s declaration"+": "+o+" is not a language.");
		}
		functionDeclarations.put(functionName, f);
	}

	public Map<String, Set<Function>> getFunctionApplications() {
		return Collections.unmodifiableMap(functionInstances);
	}
	
	public void addFunctionApplication(String name, List<String> inputs, List<String> outputs) throws Exception {
		Function app = new Function(inputs,outputs);
		if(!functionDeclarations.containsKey(name)){
			throw new Exception("Error at application of "+name+": A declaration has to be stated beforehand.");
		}
		Set<Function> set = new HashSet<>();
		if(functionInstances.containsKey(name)){
			set = functionInstances.get(name);
		}
		if(set.contains(app)){
			throw new Exception("Error at application of "+name+": It already exists.");
		}
		checkFunctionInstanceFits(app,functionDeclarations.get(name));
		set.add(app);
		functionInstances.put(name, set);
	}

	private void checkFunctionInstanceFits(Function instance, Function function) throws Exception {
		List<String> dis = instance.getInputs();
		List<String> dls = function.getInputs();
		for(int i=0;i<dis.size();i++){
			if(!isElementOf(dis.get(i),dls.get(i))){
				throw new Exception("Error at application"+dis.get(i)+" is not element of "+dls.get(i)+".");
			}
		}
		
		List<String> ris = instance.getOutputs();
		List<String> rls = function.getOutputs();
		for(int i=0;i<ris.size();i++){
			if(!isElementOf(ris.get(i),rls.get(i))){
				throw new Exception("Error at application"+ris.get(i)+" is not element of "+rls.get(i)+".");
			}
		}
	}

	public Map<String,String> getElementOfMap(){
		return Collections.unmodifiableMap(elementOfMap);
	}

	public Map<String, List<String>> getLinkMap() {
		return Collections.unmodifiableMap(linkMap);
	}

	public void addLinks(String entity, List<String> links) throws Exception {
		if(!instanceOfMap.containsKey(entity))
			throw new Exception("Error at linking "+entity+". Declaration is missing.");
		linkMap.put(entity, links);
	}
	
	public void addSubstitutes(String by, String e) throws Exception{
		if(!instanceOfMap.containsKey(e)){
			throw new Exception("Unable to substitute : "+e+" does not exist.");
		}
		if(substMap.containsKey(e)){
			throw new Exception("Unable to substitute "+e+" by "+by+": "+e+" is already substituted elsewhere.");
		}
		substMap.put(e, by);
		
	}

	public boolean isInstanceOf(String entity, String type){
		if(!instanceOfMap.containsKey(entity))
			return false;
		String temp = instanceOfMap.get(entity);
		return temp.equals(type)? true : isSubtypeOf(temp,type);
	}
	
	public boolean isSubtypeOf(String subtype, String type){
		String temp = subtypeOfMap.get(subtype);
		while(!temp.equals(type)){
			if(temp.equals("Entity"))
				return false;
			temp = subtypeOfMap.get(temp);
		}
		return true;
	}
	
	private boolean isElementOf(String art, String lang) {
		String temp = elementOfMap.get(art);
		if(temp.equals(lang))
			return true;
		while(subsetOfMap.containsKey(temp)){
			temp = subsetOfMap.get(temp);
			if(temp.equals(lang))
				return true;
		}
		return false;
	}

	public List<String> getCriticalWarnings() {
		return criticalWarnings;
	}

	public void addWarning(String w) {
		criticalWarnings.add(w);
	}
	
}
