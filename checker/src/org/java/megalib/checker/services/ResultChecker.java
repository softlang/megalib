package org.java.megalib.checker.services;

import java.util.LinkedList;
import java.util.Map;

import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;

public class ResultChecker {

	private MegaModel model;
	public LinkedList<String> warnings = new LinkedList<String>();
	
	public ResultChecker(MegaModel model) {
		this.model = model;
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
		Map<String, String> entityDeclartations = model.entityDeclarations;
		for (Map.Entry<String, String> entry : entityDeclartations.entrySet()) {
			checkEntityDeclarationTypes(entityDeclartations, entry);
		}
	}

	public void checkEntityInstances() {
		Map<String, String> entityInstances = model.entityInstances;
		for (Map.Entry<String, String> entry : entityInstances.entrySet()) {
			checkEntityInstancesTypes(entry);
		}
	}

	public void checkRelationDeclaration() {
		Map<String, Map<Integer, LinkedList<String>>> relationDeclarations = model.relationDeclarations;
		for (Map.Entry<String, Map<Integer, LinkedList<String>>> entry : relationDeclarations.entrySet()) {
			checkRelationDeclarationTypes(entry);
		}
	}

	public void checkRelationInstances() {
		Map<String, Map<Integer, LinkedList<String>>> relationInstances = model.relationInstances;
		for (Map.Entry<String, Map<Integer, LinkedList<String>>> entry : relationInstances.entrySet()) {
			String name = entry.getKey();
			if (checkRelationInstancesRelationName(name, entry))
				checkRelationInstancesTypes(entry, name);
		}
	}

	public void checkDescription() {
		Map<String, LinkedList<String>> links = model.links;
		for (Map.Entry<String, LinkedList<String>> entry : links.entrySet()) {
			checkDescriptionName(entry);
		}
	}

	public void checkFunctionDeclarations() {
		Map<String, Map<Integer, Function>> functionDeclarations = model.functionDeclarations;
		for (Map.Entry<String, Map<Integer, Function>> entry : functionDeclarations.entrySet()) {
			String funtionName = entry.getKey();
			Map<Integer, Function> functions = entry.getValue();
			checkFunctionDeclarationReturnTypes(funtionName, functions);
			checkFunctionDeclarationParameter(funtionName, functions);
		}
	}

	public void checkFunctionInstances() {
		Map<String, Map<Integer, Function>> functionInstances = model.functionInstances;
		for (Map.Entry<String, Map<Integer, Function>> entry : functionInstances.entrySet()) {
			String name = entry.getKey();
			if (checkFunctionInstancesFunctionName(name, entry)) {
				Map<Integer, Function> declaration = model.functionDeclarations.get(name);
				Map<Integer, Function> functions = entry.getValue();
				checkFunctionInstancesReturnType(name, declaration, functions);
				checkFunctionInstancesParameterType(name, declaration, functions);
				checkFunctionInstancesInitialisedObjects(name, entry.getValue());
			}
		}

	}

	private boolean checkFunctionInstancesFunctionName(String name, Map.Entry<String, Map<Integer, Function>> entry) {
		if (!model.functionDeclarations.containsKey(name)) {
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
			Map<Integer, LinkedList<String>> relation = model.relationInstances.get("elementOf");

			LinkedList<String> returnTypes = newEntry.getValue().returnType;
			for (String e : returnTypes) {
				boolean checkReturnTypes = false;
				LinkedList<String> testReturnTypes = new LinkedList<String>();
				testReturnTypes.add(e);
				for (Map.Entry<Integer, Function> fktEntry : declaration.entrySet()) {
					LinkedList<String> fktParameterTypes = fktEntry.getValue().returnType;
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
			LinkedList<String> parameterTypes = newEntry.getValue().parameterTypes;
			for (String e : parameterTypes) {
				if(!model.entityInstances.containsKey(e)){
				String warning = ("Error at Function '" + name + "'! The object-type '" + e + "' has not been initialized before");
				warnings.add(warning);
				System.out.println(warning);
			}
			}
		}}
	
	

	private void checkFunctionInstancesParameterType(String name, Map<Integer, Function> declaration,
			Map<Integer, Function> functions) {
		for (Map.Entry<Integer, Function> newEntry : functions.entrySet()) {
			Map<Integer, LinkedList<String>> relation = model.relationInstances.get("elementOf");
			LinkedList<String> parameterTypes = newEntry.getValue().parameterTypes;
			for (String e : parameterTypes) {
				boolean checkParameterTypes = false;
				LinkedList<String> testParameterTypes = new LinkedList<String>();
				testParameterTypes.add(e);
				for (Map.Entry<Integer, Function> fktEntry : declaration.entrySet()) {
					LinkedList<String> fktParameterTypes = fktEntry.getValue().parameterTypes;
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

			LinkedList<String> returnTypes = newEntry.getValue().returnType;
			for (String returnType : returnTypes) {
				String returnShow = returnType; // used in print later
				returnType = model.entityInstances.get(returnType);
				while (model.entityDeclarations.get(returnType) != null
						&& model.entityDeclarations.get(returnType).equals("Language")) {
					returnType = model.entityDeclarations.get(returnType);
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
			LinkedList<String> paramaterTypes = newEntry.getValue().parameterTypes;
			for (String parameter : paramaterTypes) {
				String parameterShow = parameter; // used in print later
				parameter = model.entityInstances.get(parameter);
				while (model.entityDeclarations.get(parameter) != null
						&& model.entityDeclarations.get(parameter).equals("Language")) {
					parameter = model.entityDeclarations.get(parameter);
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
			Map.Entry<String, Map<Integer, LinkedList<String>>> entry) {
		if (!model.relationDeclarations.containsKey(name)) {
			String warning = ("Error at Relationtype '" + entry.getKey() + "' It has not been initialized");
			warnings.add(warning);
			System.out.println(warning);
			return false;
		} else
			return true;
	}

	private void checkRelationInstancesTypes(Map.Entry<String, Map<Integer, LinkedList<String>>> entry, String name) {
		Map<Integer, LinkedList<String>> actualRelations = model.relationDeclarations.get(name);

		Map<Integer, LinkedList<String>> type = entry.getValue();
		for (Map.Entry<Integer, LinkedList<String>> newEntry : type.entrySet()) {
			boolean succesfull = false;
			LinkedList<String> objects = newEntry.getValue();
			String leftObject = model.entityInstances.get(objects.get(0));
			String tempRightObject = model.entityInstances.get(objects.get(1));
			LinkedList<String> listToCheckTypes = new LinkedList<String>();

			while ("Entity" != leftObject && leftObject != null) {
				String rightObject = tempRightObject;
				while ("Entity" != rightObject && rightObject != null) {
					listToCheckTypes = new LinkedList<String>();
					listToCheckTypes.add(leftObject);
					listToCheckTypes.add(rightObject);
					if (actualRelations.containsKey(listToCheckTypes.hashCode()))
						succesfull = true;
					rightObject = model.entityDeclarations.get(rightObject);
				}
				leftObject = model.entityDeclarations.get(leftObject);
			}
			if (!succesfull) {
				String warning = ("Error at: '" + newEntry.getValue().get(0) + " " + entry.getKey() + " "
						+ newEntry.getValue().get(1) + "'! Wrong Entity(s) used");
				warnings.add(warning);
				System.out.println(warning);

			}
		}
	}

	private void checkRelationDeclarationTypes(Map.Entry<String, Map<Integer, LinkedList<String>>> entry) {
		Map<Integer, LinkedList<String>> type = entry.getValue();
		for (Map.Entry<Integer, LinkedList<String>> newEntry : type.entrySet()) {
			LinkedList<String> list = newEntry.getValue();
			for (String item : list) {
				if (!(model.entityDeclarations.containsKey(item) || item.equals("Entity"))) {
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
		if (!(model.entityDeclarations.containsKey(type) || type.equals("Entity"))) {
			String warning = ("Error at: " + derived + " : " + type + "! Entity Type '"+type+"' is unkown");
			warnings.add(warning);
			System.out.println(warning);
		}
	}

	private void checkDescriptionName(Map.Entry<String, LinkedList<String>> entry) {
		String name = entry.getKey();
		if (!(model.entityDeclarations.containsKey(name) || model.entityInstances.containsKey(name)
				|| model.relationDeclarations.containsKey(name) || model.relationInstances.containsKey(name))) {
			String warning = ("Error at Link of '" + entry.getKey() + "' the instance does not exist");
			warnings.add(warning);
			System.out.println(warning);
		}
	}
}