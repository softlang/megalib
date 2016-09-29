package org.java.megalib.checker.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;

public class Checker {

	private MegaModel model;
	private Set<String> warnings;
	
	public Checker(MegaModel model) {
		this.model = model;
	}
	
	public Set<String> getWarnings() {
		return warnings;
	}

	public void doChecks() {
		warnings = new HashSet<String>();
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
			checkLinkedEntityDeclared(name);
			links.get(name).forEach(l->checkLinkWorking(l));
		}
	}
	
	private void checkLinkedEntityDeclared(String name) {
		if (!(model.getSubtypesMap().containsKey(name) || model.getInstanceOfMap().containsKey(name)
				|| model.getRelationDeclarationMap().containsKey(name) || model.getRelationshipInstanceMap().containsKey(name))) {
			warnings.add("Error at Link of '" + name + "' the entity does not exist");
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
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		     public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		         java.security.cert.X509Certificate[] chck = null;
		         return chck;
		     }
		     public void checkClientTrusted(
		             java.security.cert.X509Certificate[] arg0, String arg1)
		                     throws java.security.cert.CertificateException {}
		     public void checkServerTrusted(
		             java.security.cert.X509Certificate[] arg0, String arg1)
		                     throws java.security.cert.CertificateException {}
		 } };

		 // Install the all-trusting trust manager
		 
		     SSLContext sc = null;
			try {
				sc = SSLContext.getInstance("TLS");
				sc.init(null, trustAllCerts, new SecureRandom());
			} catch (NoSuchAlgorithmException | KeyManagementException e1) {
				e1.printStackTrace();
			}
		     
		     HttpsURLConnection
		     .setDefaultSSLSocketFactory(sc.getSocketFactory());
		 
		HttpURLConnection huc;
		try {
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
		int tries = 5;
		while(tries>0){
			try {
				if(!(huc.getResponseCode()==HttpURLConnection.HTTP_OK)){
					warnings.add("Error at Link to '"+l+"' : Link not working "+huc.getResponseCode());
				}
				break;
			} catch (IOException e) {
				tries--;
			}
		}
		if(tries==0)
			warnings.add("Error at Link to '"+l+"' : Connection failed!");
		huc.disconnect();
		
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
		for(String entity : model.getInstanceOfMap().keySet()){
			if(model.getSubtypesMap().containsKey(entity) || entity.equals("Entity")){
				warnings.add("Error at entity declaration '"+entity+"'. It is defined as a type and instance at the same time.");
				continue;
			}
			String type = model.getInstanceOfMap().get(entity);
			if(type.equals("Entity")){
				warnings.add("Error at entity declaration of '"+entity+"'. The type is underspecified.");
				continue;
			}
			if(!model.getSubtypesMap().containsKey(type)){
				warnings.add("Error at entity declaration of '"+entity+"'. The type '"
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
	 * @param entity
	 * @param declarations
	 */
	private void checkRelationshipInstanceFitsDeclarations(String name, List<String> entity, Set<List<String>> declarations) {
		if(declarations.parallelStream()
				.filter(decl -> relationshipInstanceFitsDeclaration(entity,decl))
				.collect(Collectors.toList())
				.isEmpty())
			warnings.add("Error at relationship instance '"+entity.get(0)+" "+name+" "+entity.get(1)+"'! The instance"
					+ " does not fit any declaration.");
	}


	/**
	 * Determines all (upper) types of an entity and performs a containment test.
	 * @param entitys
	 * @param decl
	 * @return
	 */
	private boolean relationshipInstanceFitsDeclaration(List<String> entitys, List<String> decl) {
		if(entitys.size()!=decl.size()){
			return false;
		}
		for(int i = 0; i < entitys.size();i++){
			if(!isInstanceOf(entitys.get(i), decl.get(i)))
				return false;
		}
		return true;
	}
	
	private boolean isInstanceOf(String entity, String type){
		Set<String> types = new HashSet<>();
		if(!model.getInstanceOfMap().containsKey(entity)){
			warnings.add("The entity '"+entity+"' is unknown!");
			return false;
		}
		types.add(model.getInstanceOfMap().get(entity));
		while(true){
			int size = types.size();
			Set<String> newtypes = new HashSet<>();
			for(String t : types){
				if(model.getSubtypesMap().containsKey(t)){
					newtypes.add(model.getSubtypesMap().get(t));
				}
			}
			types.addAll(newtypes);
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
				fd.getParameterList().forEach(type -> checkIsLanguage(functionName,type));
				fd.getReturnList().forEach(type -> checkIsLanguage(functionName,type));
			}
		}
	}
	
	private void checkIsLanguage(String fname,String language) {
		String type = model.getInstanceOfMap().get(language);
		if(type==null){
			warnings.add("Error at function declaration of '"+fname+"' : '"+language+"' is unknown.");
			return;
		}
		while(!type.equals("Entity")){
			if(type.equals("Language"))
				return;
			type = model.getSubtypesMap().get(type);
		}
		warnings.add("Error at function declaration of '"+fname+"' : '"+language+"' is not an instance of Language");
	}
	
	public void checkFunctionApplications() {
		Map<String, Set<Function>> map = model.getFunctionInstances();
		for (String name : map.keySet()) {
			if (checkFunctionDeclarationExists(name)) {
				Set<Function> declarations = model.getFunctionDeclarations().get(name);
				Set<Function> entitys = map.get(name);
				checkFunctionApplicationInitialisedArtifacts(name,entitys);
				entitys.forEach(i->checkFunctionInstanceFitsDeclaration(name,i,declarations));
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
	
	private void checkFunctionApplicationInitialisedArtifacts(String name, Set<Function> entitys) {
		for (Function entity : entitys) {
			List<String> parameters = entity.getParameterList();
			for (String p : parameters) {
				if(!model.getInstanceOfMap().containsKey(p)){
					warnings.add("Error at function application of '" + name + "'! The input '" + p + "' has not been declared!");
				}
			}
			List<String> outputs = entity.getReturnList();
			for(String o : outputs){
				if(!model.getInstanceOfMap().containsKey(o)){
					warnings.add("Error at function application of '" + name + "'! The output '" + o + "' has not been declared!");
				}
			}
		}
	}

	/**
	 * Checks whether the function entity fits to any declaration
	 * @param name
	 * @param funapplication
	 * @param declarations
	 */
	private void checkFunctionInstanceFitsDeclaration(String name, Function funapplication, Set<Function> declarations) {
		if(declarations.parallelStream()
				.filter(d->checkInstanceElementOfDeclaration(funapplication,d))
				.collect(Collectors.toList())
				.isEmpty()){
			warnings.add("Error at function application of '"+name+"' with input "+funapplication.getParameterList().toString()
					+ " and output "+funapplication.getReturnList().toString()+"!"
					+ " The input/output does not match any function declaration!");
		}
	}
	
	private boolean checkInstanceElementOfDeclaration(Function funapplication, Function declaration) {
		List<String> parameter = funapplication.getParameterList();
		List<String> parameterTypes = declaration.getParameterList();
		if(parameter.size()!=parameterTypes.size())
			return false;
		for(int i = 0; i < parameter.size();i++){
			if(!isElementOf(parameter.get(i), parameterTypes.get(i)))
				return false;
		}
		List<String> returnValues = funapplication.getReturnList();
		List<String> returnTypes = declaration.getReturnList();
		if(returnValues.size()!=returnTypes.size())
			return false;
		for(int i = 0; i < returnValues.size(); i++ ){
			if(!isElementOf(returnValues.get(i),returnTypes.get(i)))
				return false;
		}
		return true;
	}
	
	/**
	 * Determines the artifact's language and its supersets and then checks, whether
	 * the language equals one of them.
	 * @param artifact
	 * @param language
	 * @return
	 */
	private boolean isElementOf(String artifact, String language){
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