package org.java.megalib.checker.services;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.java.megalib.antlr.MegalibBaseListener;
import org.java.megalib.antlr.MegalibParser.EntityContext;
import org.java.megalib.antlr.MegalibParser.RelationContext;


public class Listener extends MegalibBaseListener {

	//Map with <EntityName, EntityType>
	public Map<String, String> entities = new HashMap<String, String>();
	//Map with <RelationName, <Key, [entity1,entity2,...]>>
	public Map<String, Map<Integer, LinkedList<String>>> relations = new HashMap<String, Map<Integer, LinkedList<String>>>();

	@Override
	public void enterEntity(EntityContext ctx) {
		//Case if childCount == 2 -> Entity is used in declaration
		//If store not contains entity, add to store
		if (ctx.getChildCount() == 2 && !entities.containsKey(ctx.getChild(0).getText())) {
			entities.put(ctx.getChild(0).getText(), "Entity");
			
		//Case if childCount > 2 -> A previous defined entity is used
		} else if (ctx.getChildCount() > 2) {
			//get name of entity behind the <
			String temp = ctx.getChild(2).getText();
			//check if temp exists in store
			if (entities.containsKey(temp)) {
				//add defined object to store
				entities.put(ctx.getChild(0).getText(), temp);
			} else
				//if entity behind < not kown print error
				System.out.println("At:" + ctx.getText() + " use of unknown entity");
		} else
			//if entity before < is always in store show error
			System.out.println("At: '" + ctx.getText() + "' is a double initialization of one entity");
	}
	
	@Override
	public void enterRelation(RelationContext ctx) {
		//get childCount of ctx to get border for for-loop
		int i = ctx.getChildCount();
		//set missingEntity to false
		boolean missingEntity = false;
		//create temp LinkedList, to add function elements
		LinkedList<String> temp = new LinkedList<String>();
		//iterate over function elements, if one entity is unknown abort and print error
		for (int j = 2; j < i; j = j + 2) {
			if (!entities.containsKey(ctx.getChild(j).getText())) {
				missingEntity = false;
				System.out.println("At: '" + ctx.getText() + "' an unknown entity is used");
				break;
			}
		}
		
		//if all entities in temp are known continue
		if (!missingEntity) {
			//add all entities to temp
			for (int j = 2; j < i; j = j + 2)
				temp.add(ctx.getChild(j).getText());
			//create new Map temp2
			Map<Integer, LinkedList<String>> temp2 = new HashMap<Integer, LinkedList<String>>();
			
			//assign temp2 value of existing Map if there exists one for this entity
			if (relations.containsKey(ctx.getChild(0).getText()))
				temp2 = relations.get(ctx.getChild(0).getText());
			
			//check if hashcode of list is already in Map as a key
			if (temp2.containsKey(temp.hashCode())) {
				//show error log
				System.out.println("At: '" + ctx.getText() + "' Rule already exists");
			} else {
				//if hashcode does not exists add enitity list temp to Map temp2 and add it to functionStore
				temp2.put(temp.hashCode(), temp);
				relations.put(ctx.getChild(0).getText(), temp2);
			}

		}
	}
}
