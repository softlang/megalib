package org.java.megalib.checker.services;

import java.util.ArrayList;
import java.util.List;

import org.java.megalib.models.MegaModel;

import main.antlr.techdocgrammar.MegalibBaseListener;
import main.antlr.techdocgrammar.MegalibParser.FunctionDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.FunctionInstanceContext;
import main.antlr.techdocgrammar.MegalibParser.InstanceDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.LinkContext;
import main.antlr.techdocgrammar.MegalibParser.RelationDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.RelationInstanceContext;
import main.antlr.techdocgrammar.MegalibParser.SubstitutionContext;
import main.antlr.techdocgrammar.MegalibParser.SubtypeDeclarationContext;

public class MegalibParserListener extends MegalibBaseListener {
	private MegaModel model;
	
	public MegalibParserListener(MegaModel m){
		model = m;
	}
	
	@Override
	public void enterSubstitution(SubstitutionContext ctx) {
		String subject = ctx.getChild(0).getText();
		String object = ctx.getChild(2).getText();
		try {
			model.addSubstitutes(subject, object);
		} catch (WellFormednessException e) {
			model.addWarning(e.getMessage());
		}
		super.enterSubstitution(ctx);
	}
	
	@Override
	public void enterSubtypeDeclaration(SubtypeDeclarationContext context) {
		String derivedType = context.getChild(0).getText();
		String superType = context.getChild(2).getText();
		try {
			model.addSubtypeOf(derivedType, superType);
		} catch (WellFormednessException e) {
			model.addWarning(e.getMessage());
		}
	}
	
	@Override
	public void enterInstanceDeclaration(InstanceDeclarationContext context) {
		String instance = context.getChild(0).getText();
		String type = context.getChild(2).getText();
		try {
			model.addInstanceOf(instance, type);
		} catch (WellFormednessException e) {
			model.addWarning(e.getMessage());
		}
	}
	
	@Override
	public void enterRelationDeclaration(RelationDeclarationContext context) {
		String relation = context.getChild(0).getText();
		String type1 = context.getChild(2).getText();
		String type2 =  context.getChild(4).getText();
		
		try {
			model.addRelationDeclaration(relation, type1,type2);
		} catch (WellFormednessException e) {
			model.addWarning(e.getMessage());
		}
	}
	
	@Override
	public void enterRelationInstance(RelationInstanceContext context) {
		String relation = context.getChild(1).getText();
		String instance1 = context.getChild(0).getText();
		String instance2 =  context.getChild(2).getText();
		
		List<String> instances = new ArrayList<String>();
		instances.add(instance1);
		instances.add(instance2);
		try {
			model.addRelationInstances(relation, instance1,instance2);
		} catch (WellFormednessException e) {
			model.addWarning(e.getMessage());
		}
	}
	
	@Override
	public void enterFunctionDeclaration(FunctionDeclarationContext context) {
		String functionName = context.getChild(0).getText();
		List<String> parameterTypes = new ArrayList<String>();
		List<String> returnTypes = new ArrayList<String>();
		
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
		
		try {
			model.addFunctionDeclaration(functionName, parameterTypes,returnTypes);
		} catch (WellFormednessException e) {
			model.addWarning(e.getMessage());
		}
	}
	
	@Override
	public void enterFunctionInstance(FunctionInstanceContext context) {
		String functionName = context.getChild(0).getText();
		List<String> outputs = new ArrayList<String>();
		List<String> inputs = new ArrayList<String>();
		
		boolean parameter = true;
		for (int childIndex = 2; childIndex < context.getChildCount(); childIndex++) {
			if(!context.getChild(childIndex).getText().equals(",") && parameter == false 
					&& !context.getChild(childIndex).getText().equals("|->")
					&& !context.getChild(childIndex).getText().equals("(")
					&& !context.getChild(childIndex).getText().equals(")") )
				outputs.add(context.getChild(childIndex).getText());
			
			if(!context.getChild(childIndex).getText().equals(",") && parameter == true 
					&& !context.getChild(childIndex).getText().equals("|->") 
					&& !context.getChild(childIndex).getText().equals("(")
					&& !context.getChild(childIndex).getText().equals(")") )
				inputs.add(context.getChild(childIndex).getText());
			
			if(context.getChild(childIndex).getText().equals("|->"))
				parameter = false;
		}
	
		try {
			model.addFunctionApplication(functionName, inputs,outputs);
		} catch (WellFormednessException e) {
			model.addWarning(e.getMessage());
		}
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
			links = new ArrayList<String>();
		}
		links.add(link);
		try {
			model.addLinks(entityname, links);
		} catch (WellFormednessException e) {
			model.addWarning(e.getMessage());
		}		
	}
	
	public MegaModel getModel() {
		return model;
	}
}