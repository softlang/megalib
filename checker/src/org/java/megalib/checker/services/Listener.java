package org.java.megalib.checker.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.antlr.v4.runtime.BaseErrorListener;
import org.java.megalib.antlr.MegalibBaseListener;
import org.java.megalib.antlr.MegalibParser.DescriptionContext;
import org.java.megalib.antlr.MegalibParser.EntityContext;
import org.java.megalib.antlr.MegalibParser.FunctionContext;
import org.java.megalib.antlr.MegalibParser.FunctionDeclarationContext;
import org.java.megalib.antlr.MegalibParser.ImportsContext;
import org.java.megalib.antlr.MegalibParser.RelationContext;
import org.java.megalib.antlr.MegalibParser.RelationDeclarationContext;
import org.java.megalib.antlr.MegalibParser.TypeDeclarationContext;

public class Listener extends MegalibBaseListener {
	private Map<String, String> entities;
	private Map<String, String> objects;
	private Map<String, Map<Integer, LinkedList<String>>> relations;
	private Map<String, LinkedList<String>> functions;
	
	public Listener(){
		entities = new HashMap<String,String>();
		objects = new HashMap<String, String>();
		relations = new HashMap<String, Map<Integer, LinkedList<String>>>();
		functions = new HashMap<String, LinkedList<String>>();
	}
	
	@Override
	public void enterEntity(EntityContext ctx) {
		String derived = ctx.getChild(0).getText();
		String type = ctx.getChild(2).getText();
		
		if (entityIsKnown(derived)) {
			System.out.println("Error at: '" + ctx.getText() + "'! Name:"+ derived +" is already used before");
			return;
		}		
		if (!entityIsKnown(type)) {
			System.out.println("Error at:" + ctx.getText() + "! Entity Type is unkown");
			return;
		}
		
		entities.put(derived, type);
	}

	private boolean entityIsKnown(String name) {
		return entities.containsKey(name) || name.equals("Entity");
	}
	
	private boolean objectIsKnown(String name) {
		return objects.containsKey(name);
	}
		
	private boolean relationIsKnown(String name) {
		return relations.containsKey(name);
	}

	private boolean functionIsKnown(String name) {
		return functions.containsKey(name);
	}
	
	private boolean isKnown(String name){
		return entityIsKnown(name) || objectIsKnown(name)
				|| relationIsKnown(name) || functionIsKnown(name);
	}

	@Override
	public void enterRelationDeclaration(RelationDeclarationContext ctx) {
		LinkedList<String> listOfTypes = new LinkedList<String>();
		Map<Integer, LinkedList<String>> actualRelationVarations = new HashMap<Integer, LinkedList<String>>();
				
		for (int i = 2; i < ctx.getChildCount(); i = i + 2) {			
			if (!entityIsKnown(ctx.getChild(i).getText())) {
				System.out.println("Error at:" + ctx.getText() + "! Entity Type is unkown");
				return;
			}
			listOfTypes.add(ctx.getChild(i).getText());
		}
		
		if (relations.containsKey(ctx.getChild(0).getText()))
			actualRelationVarations = relations.get(ctx.getChild(0).getText());
		if (actualRelationVarations.containsKey(listOfTypes.hashCode())) {
			System.out.println("Error at: '" + ctx.getText() + "'! Rule already exists for this relationship");
		} else {
			actualRelationVarations.put(listOfTypes.hashCode(), listOfTypes);
			relations.put(ctx.getChild(0).getText(), actualRelationVarations);
		}
	}

	@Override
	public void enterTypeDeclaration(TypeDeclarationContext ctx) {
		String object = ctx.getChild(0).getText();
		String type = ctx.getChild(2).getText();
		
		if (!entityIsKnown(type)) {
			System.out.println("Error at:" + ctx.getText() + "! Entity Type is unkown");
			return;
		} else if (isKnown(object)) {
			System.out.println("Error at: '" + ctx.getText() + "'! Name:"+ctx.getChild(0).getText()+" is already used before");
			return;
		}
		
		objects.put(ctx.getChild(0).getText(), ctx.getChild(2).getText());
				
	}
	
	@Override
	public void enterRelation(RelationContext ctx) {
		// check if relation symbol exists
		if (relations.containsKey(ctx.getChild(1).getText())) {
			// get Map with all possibilites for relation symbol
			Map<Integer, LinkedList<String>> temp = relations.get(ctx.getChild(1).getText());

			// check that only instantiated objects get used
			if (objects.containsKey(ctx.getChild(0).getText()) && objects.containsKey(ctx.getChild(2).getText())) {
				// create List and get Hashcode to compare in Map
				String left = objects.get(ctx.getChild(0).getText());
				String right = objects.get(ctx.getChild(2).getText());
				boolean check = false;

				LinkedList<String> tempList = new LinkedList<String>();
				// recursive check if a combination of top-types is in Map
				String tempLeft = left;
				while ("Entity" != tempLeft && !check) {
					String tempRight = right;
					while ("Entity" != tempRight && !check) {
						tempList = new LinkedList<String>();
						tempList.add(tempLeft);
						tempList.add(tempRight);
						if (temp.containsKey(tempList.hashCode()))
							check = true;
						tempRight = entities.get(tempRight);
					}
					tempLeft = entities.get(tempLeft);
				}
				//TODO change prints, WS problem in message
				if (!check)
					System.out.println("Error at: '" + ctxToString(ctx) + "'Types of objects are not allowed in this relation");
			} else
				System.out.println("Error at: '" + ctxToString(ctx) + "' undefined object(s) were used");
		} else
			System.out.println("Error at: '" + ctxToString(ctx) + "' unknown relationsymbol '"+ctx.getChild(1).getText()+"' used");
	}

	@Override
	public void enterFunctionDeclaration(FunctionDeclarationContext ctx) {
		LinkedList<String> functionTypes = new LinkedList<String>();
		
		if (isKnown(ctx.getChild(0).getText())) {
			System.out.println("Error at: '" + ctx.getText() + "'! Name:"+ctx.getChild(0).getText()+" is already used before");
			return;
		}

		for (int i = 2; i < ctx.getChildCount(); i = i + 2) {
			//TODO: maybe we have to check more at this point (e.g. !functionIsKnown(...))
			if (!objectIsKnown(ctx.getChild(i).getText())) {
				System.out.println("Error at:'" + ctx.getText() + "' undefined entity:'" + ctx.getChild(i).getText() + "'");
			}
			functionTypes.add(ctx.getChild(i).getText());
		}
		
		functions.put(ctx.getChild(0).getText(), functionTypes);

	}

	@Override
	public void enterFunction(FunctionContext ctx) {
		// create first temp and check variables
		LinkedList<String> functionTypes = new LinkedList<String>();
		boolean check = true;
		// check that function name is in function Map
		if (!functionIsKnown(ctx.getChild(0).getText())) {
			System.out.println("Error at: '" + ctxToString(ctx) + "' function name '"+ctx.getChild(0).getText()+"' unknown");
			return;
		}

		functionTypes = functions.get(ctx.getChild(0).getText());
		
		for (int i = 2; i < ctx.getChildCount(); i = i + 2) {
			// get actual entity type of object
			String entity = objects.get(ctx.getChild(i).getText());
			String functionType = functionTypes.get(i/2 - 1);
			check = validateEntity(functionType, entity);
			// if no one found print error at which object
			if (!check)
				System.out.println("Error at " + ctxToString(ctx) + " at the " + ((i / 2)) + "object.)");
		}
	}

	private boolean validateEntity(String functionType, String entity) {
		while (entity != null) {
			if (entity.equals(functionType)) {
				return true;
			}
			entity = entities.get(entity);
		}
		return false;
	}
	
	@Override
	public void enterDescription(DescriptionContext ctx){
		if(!isKnown(ctx.getChild(0).getText()))
			System.out.println("Error at " + ctxToString(ctx) + "object is unknown");
	}
	
	@Override
	public void enterImports(ImportsContext ctx){
		String name = ctx.getChild(1).getText();
		Checker checker = new Checker();
		try {
			String workingDir = Paths.get("").toAbsolutePath().normalize().toString();
			String filepath = workingDir.concat(File.separator + "TestFiles" + File.separator + name + ".megal");
			
			Listener listener = checker.doCheck(filepath);
			entities.putAll(listener.getEntities());
			objects.putAll(listener.getObjects());
			functions.putAll(listener.getFunctions());
			relations.putAll(listener.getRelations()); 
		} catch (IOException e) {
			System.out.println("Can not find import file: "+name);
		}
	}
	
	public String ctxToString(RelationContext ctx){
		String text = "";
		for(int i = 0; i<ctx.getChildCount();i++){
			if(i<ctx.getChildCount()-1)
			text = text.concat(ctx.getChild(i).getText()+" ");
			else
			text = text.concat(ctx.getChild(i).getText());
		}
		return text;
	}
	
	public String ctxToString(FunctionContext ctx){
		String text = "";
		for(int i = 0; i<ctx.getChildCount();i++){
			if(i<ctx.getChildCount()-1)
			text = text.concat(ctx.getChild(i).getText()+" ");
			else
			text = text.concat(ctx.getChild(i).getText());
		}
		return text;
	}
	
	public String ctxToString(DescriptionContext ctx){
		String text = "";
		for(int i = 0; i<ctx.getChildCount();i++){
			if(i<ctx.getChildCount()-1)
			text = text.concat(ctx.getChild(i).getText()+" ");
			else
			text = text.concat(ctx.getChild(i).getText());
		}
		return text;
	}
	
	public Map<String, String> getEntities() {
		return entities;
	}

	public Map<String, String> getObjects() {
		return objects;
	}

	public Map<String, Map<Integer, LinkedList<String>>> getRelations() {
		return relations;
	}

	public Map<String, LinkedList<String>> getFunctions() {
		return functions;
	}
}