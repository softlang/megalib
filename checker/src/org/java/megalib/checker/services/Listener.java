package org.java.megalib.checker.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.java.megalib.antlr.MegalibBaseListener;
import org.java.megalib.antlr.MegalibParser.EntityDeclarationContext;
import org.java.megalib.antlr.MegalibParser.EntityInstanceContext;
import org.java.megalib.antlr.MegalibParser.FunctionDeclarationContext;
import org.java.megalib.antlr.MegalibParser.FunctionInstanceContext;
import org.java.megalib.antlr.MegalibParser.ImportsContext;
import org.java.megalib.antlr.MegalibParser.LinkContext;
import org.java.megalib.antlr.MegalibParser.RelationDeclarationContext;
import org.java.megalib.antlr.MegalibParser.RelationInstanceContext;
import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;

public class Listener extends MegalibBaseListener {
	private MegaModel model;
	
	public Listener(){
		model = new MegaModel();
	}
	
	@Override
	public void enterImports(ImportsContext context) {
		//throw new NotImplementedException();
		//Added for tests
		String name = context.getChild(1).getText();
		Checker checker = new Checker();
		try {
			String workingDir = Paths.get("").toAbsolutePath().normalize().toString();
			String filepath = workingDir.concat(File.separator + "TestFiles" + File.separator + name + ".megal");
			
			MegaModel importModel = checker.doCheck(filepath);
			model.entityDeclarations.putAll(importModel.entityDeclarations);
			model.entityInstances.putAll(importModel.entityInstances);
			model.relationInstances.putAll(importModel.relationInstances);
			model.relationDeclarations.putAll(importModel.relationDeclarations);
		} catch (IOException e) {
			System.out.println("Can not find import file: "+name);
		}
		
	}
	
	@Override
	public void enterEntityDeclaration(EntityDeclarationContext context) {
		String derivedType = context.getChild(0).getText();
		String supperType = context.getChild(2).getText();
		
		model.entityDeclarations.put(derivedType, supperType);
	}
	
	@Override
	public void enterEntityInstance(EntityInstanceContext context) {
		String instance = context.getChild(0).getText();
		String type = context.getChild(2).getText();
		
		model.entityInstances.put(instance, type);
	}
	
	@Override
	public void enterRelationDeclaration(RelationDeclarationContext context) {
		String relation = context.getChild(0).getText();
		String type1 = context.getChild(2).getText();
		String type2 =  context.getChild(4).getText();
		Map<Integer, LinkedList<String>> relationTypes;
		
		LinkedList<String> typeList = createList(type1, type2);
		
		if(model.relationDeclarations.containsKey(relation)){
			relationTypes = model.relationDeclarations.get(relation);
			relationTypes.put(typeList.hashCode(), typeList);
		} else {	
			relationTypes = createMap(typeList);
		}
				
		model.relationDeclarations.put(relation, relationTypes);
	}

	private Map<Integer, LinkedList<String>> createMap(LinkedList<String> typeList) {
		Map<Integer,LinkedList<String>> listMap = new HashMap<Integer,LinkedList<String>>();
		listMap.put(typeList.hashCode(), typeList);
		return listMap;
	}

	private Map<Integer, Function> createMap(Function function) {
		Map<Integer,Function> map = new HashMap<Integer,Function>();
		map.put(function.hashCode(), function);
		return map;
	}
	
	private LinkedList<String> createList(String type1, String type2) {
		LinkedList<String> typeList = new LinkedList<String>();
		typeList.add(type1);
		typeList.add(type2);
		return typeList;
	}
	
	@Override
	public void enterRelationInstance(RelationInstanceContext context) {
		String relation = context.getChild(1).getText();
		String object1 = context.getChild(0).getText();
		String object2 =  context.getChild(2).getText();
		Map<Integer, LinkedList<String>> relationObjects;
		
		LinkedList<String> objectList = createList(object1, object2);
		
		if(model.relationInstances.containsKey(relation)){
			relationObjects = model.relationInstances.get(relation);
			relationObjects.put(objectList.hashCode(), objectList);
		} else {	
			relationObjects = createMap(objectList);
		}
				
		model.relationInstances.put(relation, relationObjects);
	}
	
	@Override
	public void enterFunctionDeclaration(FunctionDeclarationContext context) {
		String functionName = context.getChild(0).getText();
		String returnType = context.getChild(context.getChildCount()-1).getText();
		LinkedList<String> parameterTypes = new LinkedList<String>();
		
		for (int childIndex = 2; childIndex < context.getChildCount() - 2; childIndex = childIndex + 2) {
			parameterTypes.add(context.getChild(childIndex).getText());
		}
		
		Map<Integer, Function> functionObjects;
		
		Function function = new Function();
		function.parameterTypes = parameterTypes;
		function.returnType = returnType;
		
		if(model.functionDeclarations.containsKey(functionName)){
			functionObjects = model.functionDeclarations.get(functionName);
			functionObjects.put(function.hashCode(), function);
		} else {	
			functionObjects = createMap(function);
		}
				
		model.functionDeclarations.put(functionName, functionObjects);
	}
	
	@Override
	public void enterFunctionInstance(FunctionInstanceContext context) {
		String functionName = context.getChild(0).getText();
		String returnObject = context.getChild(context.getChildCount()-1).getText();
		LinkedList<String> parameters = new LinkedList<String>();
		
		for (int childIndex = 2; childIndex < context.getChildCount() - 2; childIndex = childIndex + 2) {
			parameters.add(context.getChild(childIndex).getText());
		}
		
		Map<Integer, Function> functionObjects;
		
		Function function = new Function();
		function.parameterTypes = parameters;
		function.returnType = returnObject;
		
		if(model.functionInstances.containsKey(functionName)){
			functionObjects = model.functionInstances.get(functionName);
			functionObjects.put(function.hashCode(), function);
		} else {	
			functionObjects = createMap(function);
		}
				
		model.functionInstances.put(functionName, functionObjects);
	}
	
	@Override
	public void enterLink(LinkContext context) {
		String object = context.getChild(0).getText();
		String link = context.getChild(2).getText();
		LinkedList<String> links;
		
		if(model.links.containsKey(object)){
			links = model.links.get(object);
		} else {	
			links = new LinkedList<String>();
		}
		links.add(link);
		model.links.put(object, links);		
	}
	
	public MegaModel getModel() {
		return this.model;
	}
}