package org.java.megalib.checker.services;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;

public class ResultChecker {

	private MegaModel model;
	private LinkedList<String> warnings;
	
	public ResultChecker(MegaModel model) {
		this.model = model;
		warnings = new LinkedList<String>();
	}
	
	public LinkedList<String> getWarnings() {
		return warnings;
	}

	public void doChecks() {
		this.checkDescription();
		this.checkEntityDeclaration();
		this.checkEntityInstances();
		this.checkFunctionDeclarations();
		this.checkFunctionInstances();
		this.checkRelationDeclaration();
		this.checkRelationInstances();
	}

	public void checkEntityDeclaration() {
		Map<String, String> entityDeclartations = model.getSubtypesMap();
		for (Map.Entry<String, String> entry : entityDeclartations.entrySet()) {
			checkEntityDeclarationTypes(entityDeclartations, entry);
		}
	}

	public void checkEntityInstances() {
		Map<String, String> entityInstances = model.getInstanceOfMap();
		for (Map.Entry<String, String> entry : entityInstances.entrySet()) {
			checkEntityInstancesTypes(entry);
		}
	}

	public void checkRelationDeclaration() {
		Map<String, Map<Integer, List<String>>> relationDeclarations = model.getRelationDeclarationMap();
		for (Entry<String, Map<Integer, List<String>>> entry : relationDeclarations.entrySet()) {
			checkRelationDeclarationTypes(entry);
		}
	}

	public void checkRelationInstances() {
		Map<String, Map<Integer, List<String>>> relationInstances = model.getRelationInstanceMap();
		for (Entry<String, Map<Integer, List<String>>> entry : relationInstances.entrySet()) {
			String name = entry.getKey();
			if (checkRelationInstancesRelationName(name, entry))
				checkRelationInstancesTypes(entry, name);
		}
	}

	public void checkDescription() {
		Map<String, List<String>> links = model.getLinkMap();
		for (Entry<String, List<String>> entry : links.entrySet()) {
			checkDescriptionName(entry);
		}
	}

	public void checkFunctionDeclarations() {
		Map<String, Map<Integer, Function>> functionDeclarations = model.getFunctionDeclarations();
		for (Map.Entry<String, Map<Integer, Function>> entry : functionDeclarations.entrySet()) {
			String funtionName = entry.getKey();
			Map<Integer, Function> functions = entry.getValue();
			checkFunctionDeclarationReturnTypes(funtionName, functions);
			checkFunctionDeclarationParameter(funtionName, functions);
		}
	}

	public void checkFunctionInstances() {
		Map<String, Map<Integer, Function>> functionInstances = model.getFunctionInstances();
		for (Map.Entry<String, Map<Integer, Function>> entry : functionInstances.entrySet()) {
			String name = entry.getKey();
			if (checkFunctionInstancesFunctionName(name, entry)) {
				Map<Integer, Function> declaration = model.getFunctionDeclarations().get(name);
				Map<Integer, Function> functions = entry.getValue();
				checkFunctionInstancesReturnType(name, declaration, functions);
				checkFunctionInstancesParameterType(name, declaration, functions);
				checkFunctionInstancesInitialisedObjects(name, entry.getValue());
			}
		}

	}

	private boolean checkFunctionInstancesFunctionName(String name, Map.Entry<String, Map<Integer, Function>> entry) {
		if (!model.getFunctionDeclarations().containsKey(name)) {
			String warning = ("Error at function '" + entry.getKey() + "'! It has not been initialized");
			warnings.add(warning);
			System.out.println(warning);
			return false;
		} else
			return true;
	}

	private void checkFunctionInstancesReturnType(String name, Map<Integer, Function> declaration,
			Map<Integer, Function> functions) {
		for (Map.Entry<Integer, Function> newEntry : functions.entrySet()) {
			Map<Integer, List<String>> relation = model.getRelationInstanceMap().get("elementOf");

			List<String> returnTypes = newEntry.getValue().getReturnTypes();
			for (String e : returnTypes) {
				boolean checkReturnTypes = false;
				LinkedList<String> testReturnTypes = new LinkedList<>();
				testReturnTypes.add(e);
				for (Map.Entry<Integer, Function> fktEntry : declaration.entrySet()) {
					List<String> fktParameterTypes = fktEntry.getValue().getReturnTypes();
					for (String f : fktParameterTypes) {
						testReturnTypes.add(f);
						if (relation.containsKey(testReturnTypes.hashCode())) {
							checkReturnTypes = true;
							break;
						} else
							testReturnTypes.removeLast();
					}
				}
				if (!checkReturnTypes) {
					String warning = ("Error at Function '" + name + "'! The return-type '" + e + "' is incorrect");
					warnings.add(warning);
					System.out.println(warning);
				}
			}
		}
	}
	
	private void checkFunctionInstancesInitialisedObjects(String name, Map<Integer, Function> functions) {
		for (Map.Entry<Integer, Function> newEntry : functions.entrySet()) {
			List<String> parameterTypes = newEntry.getValue().getParameterTypes();
			for (String e : parameterTypes) {
				if(!model.getInstanceOfMap().containsKey(e)){
				String warning = ("Error at Function '" + name + "'! The object-type '" + e + "' has not been initialized before");
				warnings.add(warning);
				System.out.println(warning);
			}
			}
		}}
	
	

	private void checkFunctionInstancesParameterType(String name, Map<Integer, Function> declaration,
			Map<Integer, Function> functions) {
		for (Map.Entry<Integer, Function> newEntry : functions.entrySet()) {
			Map<Integer, List<String>> relation = model.getRelationInstanceMap().get("elementOf");
			List<String> parameterTypes = newEntry.getValue().getParameterTypes();
			for (String e : parameterTypes) {
				boolean checkParameterTypes = false;
				LinkedList<String> testParameterTypes = new LinkedList<String>();
				testParameterTypes.add(e);
				for (Map.Entry<Integer, Function> fktEntry : declaration.entrySet()) {
					List<String> fktParameterTypes = fktEntry.getValue().getParameterTypes();
					for (String f : fktParameterTypes) {
						testParameterTypes.add(f);
						if (relation.containsKey(testParameterTypes.hashCode())) {
							checkParameterTypes = true;
							break;
						} else
							testParameterTypes.removeLast();
					}
				}
				if (!checkParameterTypes) {
					String warning = ("Error at Function '" + name + "'. The parameter Type '" + e + "' is unknown");
					warnings.add(warning);
					System.out.println(warning);
				}
			}
		}
	}

	private void checkFunctionDeclarationReturnTypes(String funtionName, Map<Integer, Function> functions) {
		for (Map.Entry<Integer, Function> newEntry : functions.entrySet()) {

			List<String> returnTypes = newEntry.getValue().getReturnTypes();
			for (String returnType : returnTypes) {
				String returnShow = returnType; // used in print later
				returnType = model.getInstanceOfMap().get(returnType);
				while (model.getSubtypesMap().get(returnType) != null
						&& model.getSubtypesMap().get(returnType).equals("Language")) {
					returnType = model.getSubtypesMap().get(returnType);
				}
				if (returnType == null || !returnType.equals("Language")) {
					String warning = ("Error at function Declaration of '" + funtionName + "' The return Type '"
							+ returnShow + "' is incorrect");
					warnings.add(warning);
					System.out.println(warning);
				}
			}
		}
	}

	private void checkFunctionDeclarationParameter(String funtionName, Map<Integer, Function> functions) {
		for (Map.Entry<Integer, Function> newEntry : functions.entrySet()) {
			List<String> paramaterTypes = newEntry.getValue().getParameterTypes();
			for (String parameter : paramaterTypes) {
				String parameterShow = parameter; // used in print later
				parameter = model.getInstanceOfMap().get(parameter);
				while (model.getSubtypesMap().get(parameter) != null
						&& model.getSubtypesMap().get(parameter).equals("Language")) {
					parameter = model.getSubtypesMap().get(parameter);
				}
				if (parameter == null || !parameter.equals("Language")) {
					String warning = ("Error at function Declaration of '" + funtionName + "' The parameter '"
							+ parameterShow + "' is incorrect");
					warnings.add(warning);
					System.out.println(warning);
				}
			}
		}
	}

	public boolean checkRelationInstancesRelationName(String name,
			Entry<String, Map<Integer, List<String>>> entry) {
		if (!model.getRelationDeclarationMap().containsKey(name)) {
			String warning = ("Error at Relationtype '" + entry.getKey() + "' It has not been initialized");
			warnings.add(warning);
			System.out.println(warning);
			return false;
		} else
			return true;
	}

	private void checkRelationInstancesTypes(Entry<String, Map<Integer, List<String>>> entry, String name) {
		Map<Integer, List<String>> actualRelations = model.getRelationDeclarationMap().get(name);

		Map<Integer, List<String>> type = entry.getValue();
		for (Entry<Integer, List<String>> newEntry : type.entrySet()) {
			boolean succesfull = false;
			List<String> objects = newEntry.getValue();
			String leftObject = model.getInstanceOfMap().get(objects.get(0));
			String tempRightObject = model.getInstanceOfMap().get(objects.get(1));
			LinkedList<String> listToCheckTypes = new LinkedList<String>();

			while ("Entity" != leftObject && leftObject != null) {
				String rightObject = tempRightObject;
				while ("Entity" != rightObject && rightObject != null) {
					listToCheckTypes = new LinkedList<String>();
					listToCheckTypes.add(leftObject);
					listToCheckTypes.add(rightObject);
					if (actualRelations.containsKey(listToCheckTypes.hashCode()))
						succesfull = true;
					rightObject = model.getSubtypesMap().get(rightObject);
				}
				leftObject = model.getSubtypesMap().get(leftObject);
			}
			if (!succesfull) {
				String warning = ("Error at: '" + newEntry.getValue().get(0) + " " + entry.getKey() + " "
						+ newEntry.getValue().get(1) + "'! Wrong Entity(s) used");
				warnings.add(warning);
				System.out.println(warning);

			}
		}
	}

	private void checkRelationDeclarationTypes(Entry<String, Map<Integer, List<String>>> entry) {
		Map<Integer, List<String>> type = entry.getValue();
		for (Entry<Integer, List<String>> newEntry : type.entrySet()) {
			List<String> list = newEntry.getValue();
			for (String item : list) {
				if (!(model.getSubtypesMap().containsKey(item) || item.equals("Entity"))) {
					String warning = ("Error at: '" + entry.getKey() + " < " + list.get(0) + " # " + list.get(1)
							+ "'! Type of '" + item + "' is unkown");
					warnings.add(warning);
					System.out.println(warning);
				}
			}
		}
	}

	private void checkEntityDeclarationTypes(Map<String, String> entityDeclartations, Map.Entry<String, String> entry) {
		String derived = entry.getKey();
		String type = entry.getValue();
		if (!(entityDeclartations.containsKey(type) || type.equals("Entity"))) {
			String warning = ("Error at: " + derived + " < " + type + "! Entity Type '"+type+"' is unkown");
			warnings.add(warning);
			System.out.println(warning);
		}
	}

	private void checkEntityInstancesTypes(Map.Entry<String, String> entry) {
		String derived = entry.getKey();
		String type = entry.getValue();
		if (!(model.getSubtypesMap().containsKey(type) || type.equals("Entity"))) {
			String warning = ("Error at: " + derived + " : " + type + "! Entity Type '"+type+"' is unkown");
			warnings.add(warning);
			System.out.println(warning);
		}
	}

	private void checkDescriptionName(Entry<String, List<String>> entry) {
		String name = entry.getKey();
		if (!(model.getSubtypesMap().containsKey(name) || model.getInstanceOfMap().containsKey(name)
				|| model.getRelationDeclarationMap().containsKey(name) || model.getRelationInstanceMap().containsKey(name))) {
			String warning = ("Error at Link of '" + entry.getKey() + "' the instance does not exist");
			warnings.add(warning);
			System.out.println(warning);
		}
	}
}