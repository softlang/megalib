package org.java.megalib.checker.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;

import main.antlr.techdocgrammar.MegalibBaseListener;
import main.antlr.techdocgrammar.MegalibParser.EntityDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.EntityInstanceContext;
import main.antlr.techdocgrammar.MegalibParser.FunctionDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.FunctionInstanceContext;
import main.antlr.techdocgrammar.MegalibParser.ImportsContext;
import main.antlr.techdocgrammar.MegalibParser.LinkContext;
import main.antlr.techdocgrammar.MegalibParser.RelationDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.RelationInstanceContext;

public class Listener extends MegalibBaseListener {
	private MegaModel model;
	
	public Listener(){
		model = new MegaModel();
	}
	
	@Override
	public void enterImports(ImportsContext context) {
		String name = context.getChild(1).getText();
		Checker checker = new Checker();
		try {
			String workingDir = Paths.get("").toAbsolutePath().normalize().toString();
			String filepath = workingDir.concat(File.separator + "TestFiles" + File.separator + name + ".megal");
			
			MegaModel importModel = checker.checkFile(filepath);
			importModel.getSubtypesMap().forEach((k,v)-> model.addSubtypeOf(k, v));
			importModel.getInstanceOfMap().forEach((k,v)->model.addInstanceOf(k, v));
			importModel.getRelationInstanceMap().forEach((k,v)->model.addRelationInstances(k, v));
			importModel.getRelationDeclarationMap().forEach((k,v)->model.addRelationDeclaration(k, v));
		} catch (IOException e) {
			System.out.println("Can not find import file: "+name);
		}
		
	}
	
	@Override
	public void enterEntityDeclaration(EntityDeclarationContext context) {
		String derivedType = context.getChild(0).getText();
		String superType = context.getChild(2).getText();
		
		model.addSubtypeOf(derivedType, superType);
	}
	
	@Override
	public void enterEntityInstance(EntityInstanceContext context) {
		String instance = context.getChild(0).getText();
		String type = context.getChild(2).getText();
		
		model.addInstanceOf(instance, type);
	}
	
	@Override
	public void enterRelationDeclaration(RelationDeclarationContext context) {
		String relation = context.getChild(0).getText();
		String type1 = context.getChild(2).getText();
		String type2 =  context.getChild(4).getText();
		Map<Integer, List<String>> relationTypes;
		
		LinkedList<String> typeList = createList(type1, type2);
		
		if(model.getRelationDeclarationMap().containsKey(relation)){
			relationTypes = model.getRelationDeclarationMap().get(relation);
			relationTypes.put(typeList.hashCode(), typeList);
		} else {	
			relationTypes = createMap(typeList);
		}
				
		model.addRelationDeclaration(relation, relationTypes);
	}

	private Map<Integer, List<String>> createMap(LinkedList<String> typeList) {
		Map<Integer, List<String>> listMap = new HashMap<>();
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
		Map<Integer, List<String>> relationObjects;
		
		LinkedList<String> objectList = createList(object1, object2);
		
		if(model.getRelationInstanceMap().containsKey(relation)){
			relationObjects = model.getRelationInstanceMap().get(relation);
			relationObjects.put(objectList.hashCode(), objectList);
		} else {	
			relationObjects = createMap(objectList);
		}
				
		model.addRelationInstances(relation, relationObjects);
	}
	
	@Override
	public void enterFunctionDeclaration(FunctionDeclarationContext context) {
		String functionName = context.getChild(0).getText();
		LinkedList<String> parameterTypes = new LinkedList<String>();
		LinkedList<String> returnTypes = new LinkedList<String>();
		
		boolean parameter = true;
		for (int childIndex = 2; childIndex < context.getChildCount(); childIndex++) {
			if(!context.getChild(childIndex).getText().equals("#") && parameter == false 
					&& !context.getChild(childIndex).getText().equals("->"))
			returnTypes.add(context.getChild(childIndex).getText());
			
			if(!context.getChild(childIndex).getText().equals("#") && parameter == true 
					&& !context.getChild(childIndex).getText().equals("->"))
			parameterTypes.add(context.getChild(childIndex).getText());
			
			if(context.getChild(childIndex).getText().equals("->"))
				parameter = false;
			
		}
		
		Map<Integer, Function> functionObjects;
		
		Function function = new Function();
		function.setParameterTypes(parameterTypes);
		function.setReturnTypes(returnTypes);
		
		if(model.getFunctionDeclarations().containsKey(functionName)){
			functionObjects = model.getFunctionDeclarations().get(functionName);
			functionObjects.put(function.hashCode(), function);
		} else {	
			functionObjects = createMap(function);
		}	
		model.addFunctionDeclarations(functionName, functionObjects);
	}
	
	@Override
	public void enterFunctionInstance(FunctionInstanceContext context) {
		String functionName = context.getChild(0).getText();
		LinkedList<String> returnObject = new LinkedList<String>();
		LinkedList<String> parameters = new LinkedList<String>();
		
		boolean parameter = true;
		for (int childIndex = 2; childIndex < context.getChildCount(); childIndex++) {
			if(!context.getChild(childIndex).getText().equals(",") && parameter == false 
					&& !context.getChild(childIndex).getText().equals("|->")
					&& !context.getChild(childIndex).getText().equals("(")
					&& !context.getChild(childIndex).getText().equals(")") )
				returnObject.add(context.getChild(childIndex).getText());
			
			if(!context.getChild(childIndex).getText().equals(",") && parameter == true 
					&& !context.getChild(childIndex).getText().equals("|->") 
					&& !context.getChild(childIndex).getText().equals("(")
					&& !context.getChild(childIndex).getText().equals(")") )
				parameters.add(context.getChild(childIndex).getText());
			
			if(context.getChild(childIndex).getText().equals("|->"))
				parameter = false;
		}
		Map<Integer, Function> functionObjects;
		
		Function function = new Function();
		function.setParameterTypes(parameters);
		function.setReturnTypes(returnObject);
	
		if(model.getFunctionInstances().containsKey(functionName)){
			functionObjects = model.getFunctionInstances().get(functionName);
			functionObjects.put(function.hashCode(), function);
		} else {	
			functionObjects = createMap(function);
		}
				
		model.addFunctionInstance(functionName, functionObjects);
	}
	
	@Override
	public void enterLink(LinkContext context) {
		String object = context.getChild(0).getText();
		String link = context.getChild(2).getText();
		List<String> links;
		
		if(model.getLinkMap().containsKey(object)){
			links = model.getLinkMap().get(object);
		} else {	
			links = new LinkedList<String>();
		}
		links.add(link);
		model.addLinks(object, links);		
	}
	
	public MegaModel getModel() {
		return this.model;
	}
}