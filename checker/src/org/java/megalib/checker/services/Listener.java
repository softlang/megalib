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
		if (!typeIsKnown(type)) {
			System.out.println("Error at:" + ctx.getText() + "! Entity Type is unkown");
			return;
		}
		
		entities.put(derived, type);
	}

	private boolean entityIsKnown(String derived) {
		return entities.containsKey(derived);
	}
	
	private boolean typeIsKnown(String type){
		return entityIsKnown(type) || type.equals("Entity");
	}

	@Override
	public void enterRelationDeclaration(RelationDeclarationContext ctx) {
		// get childCount of ctx to get border for for-loop
		int i = ctx.getChildCount();
		// set missingEntity to false
		boolean missingEntity = false;
		// create temp LinkedList, to add function elements
		LinkedList<String> temp = new LinkedList<String>();
		// iterate over function elements, if one entity is unknown abort and
		// print error
		for (int j = 2; j < i; j = j + 2) {
			if (!entities.containsKey(ctx.getChild(j).getText())) {
				missingEntity = false;
				System.out.println("Error at:" + ctx.getText() + "! Entity Type is unkown");
				break;
			}
		}

		// if all entities in temp are known continue
		if (!missingEntity) {
			// add all entities to temp
			for (int j = 2; j < i; j = j + 2)
				temp.add(ctx.getChild(j).getText());
			// create new Map temp2
			Map<Integer, LinkedList<String>> temp2 = new HashMap<Integer, LinkedList<String>>();

			// assign temp2 value of existing Map if there exists one for this
			// entity
			if (relations.containsKey(ctx.getChild(0).getText()))
				temp2 = relations.get(ctx.getChild(0).getText());

			// check if hashcode of list is already in Map as a key
			if (temp2.containsKey(temp.hashCode())) {
				// show error log
				System.out.println("Error at: '" + ctx.getText() + "'! Rule already exists for this relationship");
			} else {
				// if hashcode does not exists add enitity list temp to Map
				// temp2 and add it to functionStore
				temp2.put(temp.hashCode(), temp);
				relations.put(ctx.getChild(0).getText(), temp2);
			}

		}
	}

	@Override
	public void enterTypeDeclaration(TypeDeclarationContext ctx) {
		// check if entity exists
		if (entities.containsKey(ctx.getChild(2).getText())) {
			// check for name errors with entites, objects and relations
			if (contains(ctx.getChild(0).getText()))
				objects.put(ctx.getChild(0).getText(), ctx.getChild(2).getText());
			else
				System.out.println("Error at: '" + ctx.getText() + "'! Name:"+ctx.getChild(0).getText()+" is already used before");
		} else
			System.out.println("Error at:" + ctx.getText() + "! Entity Type is unkown");
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
					System.out.println("At: '" + ctx.getText() + "'Types of objects are not allowed in this relation");
			} else
				System.out.println("At: '" + ctx.getText() + "' undefined objects were used");
		} else
			System.out.println("At: '" + ctx.getText() + "' unknown relationsymbol used");
	}

	@Override
	public void enterFunctionDeclaration(FunctionDeclarationContext ctx) {
		// create first temp and check variables
		LinkedList<String> temp = new LinkedList<String>();
		boolean missingEntity = false;
		// check that function name is not already use elsewhere (check in
		// functions list missing, see also TypeDeclaration
		if (contains(ctx.getChild(0).getText())) {
			missingEntity = true;
			System.out.println("Error at: '" + ctx.getText() + "'! Name:"+ctx.getChild(0).getText()+" is already used before");
		}

		// iterate over all entities and check that they are initialized
		for (int i = 2; i < ctx.getChildCount() && !missingEntity; i = i + 2) {
			if (!entities.containsKey(ctx.getChild(i).getText())) {
				System.out.println("At:'" + ctx.getText() + "' undefined entities were used");
				missingEntity = true;
			}
			// add entities to list
			temp.add(ctx.getChild(i).getText());
		}
		// if any entity had not existed stop else add to Map
		if (!missingEntity) {
			functions.put(ctx.getChild(0).getText(), temp);
		}

	}

	@Override
	public void enterFunction(FunctionContext ctx) {
		// create first temp and check variables
		LinkedList<String> temp = new LinkedList<String>();
		boolean missingEntity = false;
		boolean check = true;
		// check that function name is in function Map
		if (!functions.containsKey(ctx.getChild(0).getText())) {
			missingEntity = true;
			System.out.println("At: '" + ctx.getText() + "' function name unknown");
		}

		temp = functions.get(ctx.getChild(0).getText());
		// iterate over all entities and check that they or their top-types
		// correspond to them in the function list
		for (int i = 2; i < ctx.getChildCount() && !missingEntity; i = i + 2) {
			// get actualy entity type of object
			String entity = objects.get(ctx.getChild(i).getText());
			check = false;
			// recursive raise from sub entity to their main entity and check if
			// one belongs to the one in the list
			while (entity != "Entity" && entity != null) {
				// if one found set check to true and stop while loop
				if (entity.equals(temp.get((i / 2) - 1))) {
					check = true;
					break;
				}
				entity = entities.get(entity);
			}
			// if no one found print error at which object
			if (!check)
				System.out.println("Error at " + ctx.getText() + " at the " + ((i / 2)) + "object.)");
		}
	}
	
	@Override
	public void enterDescription(DescriptionContext ctx){
		if(!contains(ctx.getChild(0).getText()))
			System.out.println("Error at " + ctx.getText() + "object is unknown");
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
	
	public boolean contains(String name){
		return entities.containsKey(name) || objects.containsKey(name)
				|| relations.containsKey(name) || functions.containsKey(name);
		
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