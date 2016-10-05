package org.java.megalib.checker.services;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;

import main.antlr.techdocgrammar.MegalibBaseListener;
import main.antlr.techdocgrammar.MegalibParser.FunctionDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.FunctionInstanceContext;
import main.antlr.techdocgrammar.MegalibParser.ImportsContext;
import main.antlr.techdocgrammar.MegalibParser.InstanceDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.LinkContext;
import main.antlr.techdocgrammar.MegalibParser.RelationDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.RelationInstanceContext;
import main.antlr.techdocgrammar.MegalibParser.SubtypeDeclarationContext;

public class Listener extends MegalibBaseListener {
	private MegaModel model;
	
	public Listener(){
		model = new MegaModel();
	}
	
	@Override
	public void enterImports(ImportsContext context) {
		String name = context.getChild(1).getText();
		String workingDir = Paths.get("").toAbsolutePath().normalize().toString();
		String filepath = workingDir.concat(File.separator + name + ".megal");
		model.addImport(filepath);
		
	}
	
	@Override
	public void enterSubtypeDeclaration(SubtypeDeclarationContext context) {
		String derivedType = context.getChild(0).getText();
		String superType = context.getChild(2).getText();
		model.addSubtypeOf(derivedType, superType);
	}
	
	@Override
	public void enterInstanceDeclaration(InstanceDeclarationContext context) {
		String instance = context.getChild(0).getText();
		String type = context.getChild(2).getText();
		model.addInstanceOf(instance, type);
		if(context.getChildCount() == 6){
			String language = context.getChild(4).getText();
			List<String> list = new ArrayList<>();
			list.add(instance);
			list.add(language);
			model.addRelationInstances("elementOf", list);
			
		}
	}
	
	@Override
	public void enterRelationDeclaration(RelationDeclarationContext context) {
		String relation = context.getChild(0).getText();
		String type1 = context.getChild(2).getText();
		String type2 =  context.getChild(4).getText();
		
		List<String> typeList = new LinkedList<String>();
		typeList.add(type1);
		typeList.add(type2);
		
		model.addRelationDeclaration(relation, typeList);
	}
	
	@Override
	public void enterRelationInstance(RelationInstanceContext context) {
		String relation = context.getChild(1).getText();
		String instance1 = context.getChild(0).getText();
		String instance2 =  context.getChild(2).getText();
		
		List<String> instances = new LinkedList<String>();
		instances.add(instance1);
		instances.add(instance2);
				
		model.addRelationInstances(relation, instances);
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
		
		Function function = new Function();
		
		function.setParameterList(parameterTypes);
		function.setReturnList(returnTypes);
		
		model.addFunctionDeclaration(functionName, function);
		model.addInstanceOf(functionName, "Function");
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
		Function function = new Function();
		function.setParameterList(parameters);
		function.setReturnList(returnObject);
	
		model.addFunctionInstance(functionName, function);
	}
	
	@Override
	public void enterLink(LinkContext context) {
		String entityname = context.getChild(0).getText();
		String link = context.getChild(2).getText();
		link = link.substring(1, link.length()-1);
		List<String> links;
		
		if(model.getLinkMap().containsKey(entityname)){
			links = model.getLinkMap().get(entityname);
		} else {	
			links = new LinkedList<String>();
		}
		links.add(link);
		model.addLinks(entityname, links);		
	}
	
	public MegaModel getModel() {
		return this.model;
	}
}