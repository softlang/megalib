package org.java.megalib.checker.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;

public class ResultChecker {

	private MegaModel model;
	private List<String> warnings;
	
	public ResultChecker(MegaModel model) {
		this.model = model;
		warnings = new LinkedList<String>();
	}
	
	public List<String> getWarnings() {
		return warnings;
	}

	public void doChecks() {
		this.checkLinks();
		this.checkSubtypeDeclarations();
		this.checkInstanceDeclarations();
		this.checkRelationshipTypes();
		this.checkRelationshipInstances();
		this.checkFunctionDomainRange();
		this.checkFunctionApplications();
	}
	
	public void checkLinks() {
		Map<String, List<String>> links = model.getLinkMap();
		for (String name : links.keySet()) {
			checkLinkName(name);
			links.get(name).forEach(l->checkLinkWorking(l));
		}
	}
	
	private void checkLinkName(String name) {
		if (!(model.getSubtypesMap().containsKey(name) || model.getInstanceOfMap().containsKey(name)
				|| model.getRelationDeclarationMap().containsKey(name) || model.getRelationshipInstanceMap().containsKey(name))) {
			warnings.add("Error at Link of '" + name + "' the instance does not exist");
		}
	}
	
	private void checkLinkWorking(String l) {
		URL u ;
		try {
			u = new URL(l);
		} catch (MalformedURLException e) {
			warnings.add("Error at Link to '"+l+"' : The URL is malformed!");
			return;
		}
		HttpURLConnection huc;
		try {
			HttpURLConnection.setFollowRedirects(false);
			huc = (HttpURLConnection) u.openConnection();
		} catch (IOException e) {
			warnings.add("Error at Link to '"+l+"' : The URL connection failed!");
			return;
		}
		try {
			huc.setRequestMethod("HEAD");
		} catch (ProtocolException e) {
			warnings.add("Error at Link to '"+l+"' : ProtocolException!");
			return;
		}
		try {
			if(!(huc.getResponseCode()==HttpURLConnection.HTTP_OK)){
				warnings.add("Error at Link to '"+l+"' : Link not working");
			}
		} catch (IOException e) {
			warnings.add("Error at Link to '"+l+"' : getResponseCode() failed!");
		}
		
	}
	
	public void checkSubtypeDeclarations() {
		for(String subtype : model.getSubtypesMap().keySet()){
			String type = model.getSubtypesMap().get(subtype);
			if(!(type.equals("Entity")||model.getSubtypesMap().containsKey(type))){
				warnings.add("Error at subtype declaration of '"+subtype+"'. The supertype '"
						+ type + "' is not declared.");
			}
		}
	}
	
	public void checkInstanceDeclarations() {
		for(String instance : model.getInstanceOfMap().keySet()){
			String type = model.getInstanceOfMap().get(instance);
			if(!(type.equals("Entity")||model.getSubtypesMap().containsKey(type))){
				warnings.add("Error at instance declaration of '"+instance+"'. The type '"
						+ type + "' is not declared.");
			}
		}
	}
	
	public void checkRelationshipTypes() {
		model.getRelationDeclarationMap().forEach((name,declarations)->checkRelationDeclarationTypes(name,declarations));
	}
	
	private void checkRelationDeclarationTypes(String name, Set<List<String>> declarations) {
		for(List<String> declaration : declarations){
			for(String type : declaration){
				if(!(model.getSubtypesMap().containsKey(type)||type.equals("Entity"))){
					warnings.add("Error at relationship declaration of '"+name+"'. The type '"+type+"'"
							+ "is not declared!");
				}
			}
		}
	}
	
	public void checkRelationshipInstances() {
		Map<String, Set<List<String>>> relationshipInstances = model.getRelationshipInstanceMap();
		for (String name : relationshipInstances.keySet()) {
			if (checkRelationInstancesRelationName(name)){
				relationshipInstances.get(name).forEach(i -> 
					checkRelationshipInstanceFitsDeclarations(name,i,model.getRelationDeclarationMap().get(name)));
			}
		}
	}
	
	/**
	 * If the relation is not declared, issue a warning
	 * @param name
	 * @return
	 */
	public boolean checkRelationInstancesRelationName(String name) {
		if (!model.getRelationDeclarationMap().containsKey(name)) {
			warnings.add("Error at relation '" + name + "'. It has not been declared!");
			return false;
		}
		return true;
	}

	/**
	 * Checks if the entities involved in the relationship have fitting types concerning the relationship's declaration.
	 * @param name
	 * @param instance
	 * @param declarations
	 */
	private void checkRelationshipInstanceFitsDeclarations(String name, List<String> instance, Set<List<String>> declarations) {
		if(declarations.parallelStream()
				.filter(decl -> relationshipInstanceFitsDeclaration(instance,decl))
				.collect(Collectors.toList())
				.isEmpty())
			warnings.add("Error at relationship instance '"+instance.get(0)+" "+name+" "+instance.get(1)+"! The instance"
					+ "does not fit any declaration.");
	}


	/**
	 * Determines all (upper) types of an instance and performs a containment test.
	 * @param instances
	 * @param decl
	 * @return
	 */
	private boolean relationshipInstanceFitsDeclaration(List<String> instances, List<String> decl) {
		if(instances.size()!=decl.size()){
			return false;
		}
		for(int i = 0; i < instances.size();i++){
			if(!isInstanceOf(instances.get(i), decl.get(i)))
				return false;
		}
		return true;
	}
	
	private boolean isInstanceOf(String entity, String type){
		Set<String> types = new HashSet<>();
		types.add(model.getInstanceOfMap().get(entity));
		while(true){
			int size = types.size();
			for(String t : types){
				if(model.getSubtypesMap().containsKey(t)){
					types.add(model.getSubtypesMap().get(t));
				}
			}
			if(types.size()==size)
				break;
		}
		if(!types.contains(type))
			return false;
		return true;
	}

	public void checkFunctionDomainRange() {
		Map<String, Set<Function>> functionDeclarations = model.getFunctionDeclarations();
		for (String functionName : functionDeclarations.keySet()) {
			Set<Function> functiondeclarations = functionDeclarations.get(functionName);
			for(Function fd : functiondeclarations){
				fd.getParameterList().forEach(type -> checkIsLanguage(type));
				fd.getReturnList().forEach(type -> checkIsLanguage(type));
			}
		}
	}
	
	private void checkIsLanguage(String language) {
		String type = model.getInstanceOfMap().get(language);
		while(!type.equals("Entity")){
			if(type.equals("Language"))
				return;
			type = model.getSubtypesMap().get(type);
		}
		warnings.add("'"+language+"' is not an instance of Language");
	}
	
	public void checkFunctionApplications() {
		Map<String, Set<Function>> map = model.getFunctionInstances();
		for (String name : map.keySet()) {
			if (checkFunctionDeclarationExists(name)) {
				Set<Function> declarations = model.getFunctionDeclarations().get(name);
				Set<Function> instances = map.get(name);
				checkFunctionApplicationInitialisedArtifacts(name,instances);
				instances.forEach(i->checkFunctionInstanceFitsDeclaration(name,i,declarations));
			}
		}
	}
	
	private boolean checkFunctionDeclarationExists(String name) {
		if (!model.getFunctionDeclarations().containsKey(name)) {
			warnings.add("Error at function '" + name + "'! It has not been declared");
			return false;
		} else
			return true;
	}
	
	private void checkFunctionApplicationInitialisedArtifacts(String name, Set<Function> instances) {
		for (Function instance : instances) {
			List<String> parameters = instance.getParameterList();
			for (String p : parameters) {
				if(!model.getInstanceOfMap().containsKey(p)){
					warnings.add("Error at Function '" + name + "'! The parameter '" + p + "' has not been declared!");
				}
			}
			List<String> outputs = instance.getReturnList();
			for(String o : outputs){
				if(!model.getInstanceOfMap().containsKey(o)){
					warnings.add("Error at Function '" + name + "'! The output '" + o + "' has not been declared!");
				}
			}
		}
	}

	/**
	 * Checks whether the function instance fits to any declaration
	 * @param name
	 * @param instance
	 * @param declarations
	 */
	private void checkFunctionInstanceFitsDeclaration(String name, Function instance, Set<Function> declarations) {
		if(declarations.parallelStream()
				.filter(d->checkInstanceElementOfDeclaration(instance,d))
				.collect(Collectors.toList())
				.isEmpty()){
			warnings.add("Error at function application of '"+name+"' with parameters "+instance.getParameterList().toString()+" "
					+ "The instances do not fit any function declaration!");
		}
	}
	
	private boolean checkInstanceElementOfDeclaration(Function instance, Function declaration) {
		List<String> parameter = instance.getParameterList();
		List<String> parameterTypes = declaration.getParameterList();
		if(parameter.size()!=parameterTypes.size())
			return false;
		for(int i = 0; i < parameter.size();i++){
			if(!isElementOf(parameter.get(i), parameterTypes.get(i)))
				return false;
		}
		List<String> returnValues = instance.getReturnList();
		List<String> returnTypes = declaration.getReturnList();
		if(returnValues.size()!=returnTypes.size())
			return false;
		for(int i = 0; i < returnValues.size(); i++ ){
			if(!isElementOf(returnValues.get(i),returnTypes.get(i)))
				return false;
		}
		return true;
	}
	
	private boolean isElementOf(String artifact, String language){
		//determine the artifact's languages
		Set<String> langs = new HashSet<>();
		for(List<String> elemOf : model.getRelationshipInstanceMap().get("elementOf")){
			if(elemOf.get(0).equals(artifact)){
				langs.add(elemOf.get(1));
			}
		}
		while(true){
			int size = langs.size();
			if(!model.getRelationshipInstanceMap().containsKey("subsetOf"))
				break;
			for(List<String> subsetOf : model.getRelationshipInstanceMap().get("subsetOf")){
				if(langs.contains(subsetOf.get(0))){
					langs.add(subsetOf.get(1));
				}
			}
			if(size==langs.size())
				break;
		}
		return langs.contains(language);
	}
}