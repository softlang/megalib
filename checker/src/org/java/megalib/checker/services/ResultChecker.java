package org.java.megalib.checker.services;

import java.util.LinkedList;
import java.util.Map;

import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;

public class ResultChecker {

	private MegaModel model;

	public ResultChecker(MegaModel model) {
		this.model = model;
	}

	public void checkEntityDeclaration() {
		Map<String, String> entityDeclartations = model.entityDeclarations;
		for (Map.Entry<String, String> entry : entityDeclartations.entrySet()) {
			String derived = entry.getKey();
			String type = entry.getValue();
			if (!(entityDeclartations.containsKey(type) || type.equals("Entity")))
				System.out.println("Error at:" + derived + " < " + type + "! Entity Type is unkown");
		}
	}

	public void checkEntityInstances() {
		Map<String, String> entityInstances = model.entityInstances;
		for (Map.Entry<String, String> entry : entityInstances.entrySet()) {
			String derived = entry.getKey();
			String type = entry.getValue();
			if (!(model.entityDeclarations.containsKey(type) || type.equals("Entity")))
				System.out.println("Error at:" + derived + " : " + type + " Entity Type is unkown");
		}
	}

	public void checkRelationDeclaration() {
		Map<String, Map<Integer, LinkedList<String>>> relationDeclarations = model.relationDeclarations;
		for (Map.Entry<String, Map<Integer, LinkedList<String>>> entry : relationDeclarations.entrySet()) {
			Map<Integer, LinkedList<String>> type = entry.getValue();
			for (Map.Entry<Integer, LinkedList<String>> newEntry : type.entrySet()) {
				LinkedList<String> list = newEntry.getValue();
				for (String item : list) {
					if (!(model.entityDeclarations.containsKey(item) || item.equals("Entity")))
						System.out.println("Error at: '" + entry.getKey() + " < " + list.get(0) + "#" + list.get(1)
								+ "'! Type of '" + item + "' is unkown");
				}
			}
		}
	}

	public void checkRelationInstances() {
		Map<String, Map<Integer, LinkedList<String>>> relationInstances = model.relationInstances;
		for (Map.Entry<String, Map<Integer, LinkedList<String>>> entry : relationInstances.entrySet()) {
			String name = entry.getKey();
			if (!model.relationDeclarations.containsKey(name))
				System.out.println("Error at Relationtype '" + entry.getKey() + "' has not been initialized");
			else {
				Map<Integer, LinkedList<String>> actualRelations = model.relationDeclarations.get(name);

				Map<Integer, LinkedList<String>> type = entry.getValue();
				for (Map.Entry<Integer, LinkedList<String>> newEntry : type.entrySet()) {
					boolean succesfull = false;
					LinkedList<String> objects = newEntry.getValue();
					String leftObject = model.entityInstances.get(objects.get(0));
					String rightObject = model.entityInstances.get(objects.get(1));
					LinkedList<String> listToCheckTypes = new LinkedList<String>();

					while ("Entity" != leftObject && leftObject != null) {
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
					if (!succesfull)
						System.out.println("Error at: '" + newEntry.getValue().get(0) + " " + entry.getKey() + " "
								+ newEntry.getValue().get(1) + "'! Wrong Entity used");

				}
			}
		}
	}

	public void checkDescription() {
		Map<String, LinkedList<String>> links = model.links;
		for (Map.Entry<String, LinkedList<String>> entry : links.entrySet()) {
			String name = entry.getKey();
			if (!(model.entityDeclarations.containsKey(name) || model.entityInstances.containsKey(name)
					|| model.relationDeclarations.containsKey(name) || model.relationInstances.containsKey(name)))
				System.out.println("Error at Link of '" + entry.getKey() + "' the instance does not exist");
		}
	}

	public void checkFunctionDeclarations() {
		Map<String, Map<Integer, Function>> functionDeclarations = model.functionDeclarations;
		for (Map.Entry<String, Map<Integer, Function>> entry : functionDeclarations.entrySet()) {
			String funtionName = entry.getKey();
			Map<Integer, Function> functions = entry.getValue();
			for (Map.Entry<Integer, Function> newEntry : functions.entrySet()) {

				String returnType = model.entityInstances.get(newEntry.getValue().returnType);
				while (returnType != null & !returnType.equals("Language")) {
					returnType = model.entityDeclarations.get(returnType);
				}
				if (returnType == null || !returnType.equals("Language"))
					System.out.println("Error at function Declaration of '" + funtionName + "'! The return Type '"
							+ newEntry.getValue().returnType + "' is incorrect");
				LinkedList<String> paramaterTypes = newEntry.getValue().parameterTypes;
				for (String parameter : paramaterTypes) {
					parameter = model.entityInstances.get(parameter);
					String parameterShow = parameter; // used in print later
					while (model.entityDeclarations.get(parameter) != null
							& model.entityDeclarations.get(parameter).equals("Language")) {
						parameter = model.entityDeclarations.get(returnType);
					}
					if (parameter == null | !parameter.equals("Language"))
						System.out.println("Error at function Declaration of '" + funtionName + "' The parameter '"
								+ parameterShow + "' is incorrect");
				}
			}
		}
	}

	public void checkFunctionInstances() {
		Map<String, Map<Integer, Function>> functionInstances = model.functionInstances;
		for (Map.Entry<String, Map<Integer, Function>> entry : functionInstances.entrySet()) {
			String name = entry.getKey();
			Map<Integer, Function> declaration = model.functionDeclarations.get(name);
			Map<Integer, Function> functions = entry.getValue();
			for (Map.Entry<Integer, Function> newEntry : functions.entrySet()) {
				Map<Integer, LinkedList<String>> relation = model.relationInstances.get("elementOf");

				// Check returnType
				String returnType = newEntry.getValue().returnType;
				LinkedList<String> testReturnType = new LinkedList<String>();
				testReturnType.add(returnType);
				boolean checkReturnType = false;
				for (Map.Entry<Integer, Function> fktEntryA : declaration.entrySet()) {
					String fktParameterTypesA = fktEntryA.getValue().returnType;
					testReturnType.add(fktParameterTypesA);
					if (relation.containsKey(testReturnType.hashCode())) {
						checkReturnType = true;
						break;
					}
					if (!checkReturnType)
						System.out.println("Error at Function '" + name + "'! The return-type '"
								+ newEntry.getValue().returnType + "' is unknown");
				}

				// Check parameter
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
					if (!checkParameterTypes)
						System.out
								.println("Error at Function '" + name + "'. The parameter Type '" + e + "' is unknown");
				}
			}

		}

	}
}