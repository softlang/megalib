package org.java.megalib.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Block {

    private int id;
    private String text;
    private String module;

    private Map<String,Set<Relation>> relationDeclarationMap;
    private Map<String,Set<Relation>> relationshipMap;
    private Map<String,Set<Function>> functionDeclarations;
    private Map<String,Set<Function>> functionApplications;
    private Map<String, String> instanceOfMap;
	private MegaModel model;

    public Block(int id, String text, String module, MegaModel model){
        this.id = id;
        this.text = text;
        this.module = module;
        this.model = model;
        relationDeclarationMap = new HashMap<>();
        relationshipMap = new HashMap<>();
        functionDeclarations = new HashMap<>();
        functionApplications = new HashMap<>();
        instanceOfMap = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getModule(){
    	return module;
    }

    public Map<String,Set<Relation>> getRelationshipDeclarationMap() {
        return Collections.unmodifiableMap(relationDeclarationMap);
    }

    public void addRelationDeclaration(String name, Relation decl) {
        Set<Relation> set = new HashSet<>();
        if(relationDeclarationMap.containsKey(name)){
            set = relationDeclarationMap.get(name);
        }
        set.add(decl);
        relationDeclarationMap.put(name, set);

        decl.setBlock(this);
    }

    public Map<String,Set<Relation>> getRelationships() {
        return Collections.unmodifiableMap(relationshipMap);
    }

    public void addRelationInstance(String name, Relation i) {
    	if(model.getSubtypesMap().containsKey(i.getSubject())
    			|| model.getRelationshipDeclarationMap().containsKey(i.getSubject())) {
			return;
		}
    	instanceOfMap.put(i.getSubject(), model.getType(i.getSubject()));
    	instanceOfMap.put(i.getObject(), model.getType(i.getObject()));
        Set<Relation> set = new HashSet<>();
        if(relationshipMap.containsKey(name)){
            set = relationshipMap.get(name);
        }
        set.add(i);
        relationshipMap.put(name, set);

        i.setBlock(this);
    }

    public Map<String,Set<Function>> getFunctionDeclarations() {
        return Collections.unmodifiableMap(functionDeclarations);
    }

    public void addFunctionDeclaration(String functionName, Function f) {
    	f.getInputs().forEach(l -> instanceOfMap.put(l, model.getType(l)));
    	f.getOutputs().forEach(l -> instanceOfMap.put(l, model.getType(l)));

        Set<Function> declset = new HashSet<>();
        if(functionDeclarations.containsKey(functionName)){
            declset = functionDeclarations.get(functionName);
        }
        declset.add(f);
        functionDeclarations.put(functionName, declset);

        f.setBlock(this);
    }

    public Map<String,Set<Function>> getFunctionApplications() {
        return Collections.unmodifiableMap(functionApplications);
    }

    public void addFunctionApplication(String name, Function app) {
    	app.getInputs().forEach(a -> instanceOfMap.put(a, model.getType(a)));
    	app.getOutputs().forEach(a -> instanceOfMap.put(a, model.getType(a)));
        Set<Function> set = new HashSet<>();
        if(functionApplications.containsKey(name)){
            set = functionApplications.get(name);
        }
        set.add(app);
        functionApplications.put(name, set);

        app.setBlock(this);
    }

    public Map<String, String> getInstanceOfMap(){
    	return Collections.unmodifiableMap(instanceOfMap);
    }

	public void addInstanceOf(String instance, String type) {
		instanceOfMap.put(instance, type);
	}
}
