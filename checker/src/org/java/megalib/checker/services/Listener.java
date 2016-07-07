package org.java.megalib.checker.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.java.megalib.antlr.MegalibBaseListener;
import org.java.megalib.antlr.MegalibParser.EntityContext;
import org.java.megalib.antlr.MegalibParser.FunctionContext;
import org.java.megalib.antlr.MegalibParser.FunctionDeclarationContext;
import org.java.megalib.antlr.MegalibParser.RelationContext;
import org.java.megalib.antlr.MegalibParser.RelationDeclarationContext;
import org.java.megalib.antlr.MegalibParser.TypeDeclarationContext;

public class Listener extends MegalibBaseListener {

	// Map with <EntityName, EntityType>
	private Map<String, String> entities = new HashMap<String, String>();
	// Map with <ObjectName, EntityName>
	private Map<String, String> objects = new HashMap<String, String>();
	// Map with <RelationName, <Key, [entity1,entity2,...]>>
	private Map<String, Map<Integer, LinkedList<String>>> relations = new HashMap<String, Map<Integer, LinkedList<String>>>();
	//Map with <FunctionName, [FunctionObject1,FunctionObject,...,Output]
	private Map<String, LinkedList<String>> functions = new HashMap<String, LinkedList<String>>();
	
	@Override
	public void enterEntity(EntityContext ctx) {
		// Case if childCount == 2 -> Entity is used in declaration
		// If store not contains entity, add to store
		if (ctx.getChildCount() == 2 && !entities.containsKey(ctx.getChild(0).getText())) {
			entities.put(ctx.getChild(0).getText(), "Entity");

			// Case if childCount > 2 -> A previous defined entity is used
		} else if (ctx.getChildCount() > 2) {
			// get name of entity behind the <
			String temp = ctx.getChild(2).getText();
			// check if temp exists in store
			if (entities.containsKey(temp)) {
				// add defined object to store
				entities.put(ctx.getChild(0).getText(), temp);
			} else
				// if entity behind < not kown print error
				System.out.println("At:" + ctx.getText() + " use of unknown entity");
		} else
			// if entity before < is always in store show error
			System.out.println("At: '" + ctx.getText() + "' is a double initialization of one entity");
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
				System.out.println("At: '" + ctx.getText() + "' an unknown entity is used");
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
				System.out.println("At: '" + ctx.getText() + "' Rule already exists");
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
		if (entities.containsKey(ctx.getChild(2).getText()))
			// check for name errors with entites, objects and relations
			if (!entities.containsKey(ctx.getChild(0).getText()) && !objects.containsKey(ctx.getChild(0).getText())
					&& !relations.containsKey(ctx.getChild(0).getText()))
			objects.put(ctx.getChild(0).getText(), ctx.getChild(2).getText());
			else
			System.out.println("At: '" + ctx.getText() + "' Object name already used before!");
		else
			System.out.println("At: '" + ctx.getText() + "' unknown entity gets assigned!");
	}

	@Override
	public void enterRelation(RelationContext ctx) {
		// check if relation symbol exists
		if (relations.containsKey(ctx.getChild(2).getText())) {
			// get Map with all possibilites for relation symbol
			Map<Integer, LinkedList<String>> temp = relations.get(ctx.getChild(2).getText());

			// check that only instantiated objects get used
			if (objects.containsKey(ctx.getChild(0).getText()) && objects.containsKey(ctx.getChild(4).getText())) {
				// create List and get Hashcode to compare in Map
				String left = objects.get(ctx.getChild(0).getText());
				String right = objects.get(ctx.getChild(4).getText());
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
				if (!check)
					System.out.println("At: '" + ctx.getText() + "'Types of objects are not allowed in this relation");
			} else
				System.out.println("At: '" + ctx.getText() + "' undefined objects were used");
		} else
			System.out.println("At: '" + ctx.getText() + "' unknown relationsymbol used");
	}

	public void enterFunctionDeclaration(FunctionDeclarationContext ctx) {
		// create first temp and check variables
		LinkedList<String> temp = new LinkedList<String>();
		boolean missingEntity = false;
		// check that function name is not already use elsewhere (check in
		// functions list missing, see also TypeDeclaration
		if (!entities.containsKey(ctx.getChild(0).getText()) && !objects.containsKey(ctx.getChild(0).getText())
				&& !relations.containsKey(ctx.getChild(0).getText())) {
		} else {
			missingEntity = true;
			System.out.println("At: '" + ctx.getText() + "' function named used or another instance");
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
			//get actualy entity type of object
			String entity = objects.get(ctx.getChild(i).getText());
			check = false;
			//recursive raise from sub entity to their main entity and check if one belongs to the one in the list
			while (entity != "Entity") {
				//if one found set check to true and stop while loop
				if (entity.equals(temp.get((int) (i / 2) - 1))) {
					check = true;
					break;
				}
				entity = entities.get(entity);
			}
			//if no one found print error at which object
			if (!check)
			System.out.println("Error at " + ((i / 2)) + " object");
		}
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