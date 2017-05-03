package org.java.megalib.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Block {

    private int id;
    private String text;

    private Map<String,Set<Relation>> relationDeclarationMap;
    private Map<String,Set<Relation>> relationshipMap;
    private Map<String,Set<Function>> functionDeclarations;
    private Map<String,Set<Function>> functionApplications;

    public Block(int id, String text){
        this.id = id;
        this.text = text;
        relationDeclarationMap = new HashMap<>();
        relationshipMap = new HashMap<>();
        functionDeclarations = new HashMap<>();
        functionApplications = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Map<String,Set<Relation>> getRelationshipDeclarationMap() {
        return Collections.unmodifiableMap(relationDeclarationMap);
    }

    public void addRelationDeclaration(String name, String subject, String object) {
        Set<Relation> set = new HashSet<>();
        if(relationDeclarationMap.containsKey(name)){
            set = relationDeclarationMap.get(name);
        }
        Relation decl = new Relation(subject, object);
        set.add(decl);
        relationDeclarationMap.put(name, set);
    }

    public Map<String,Set<Relation>> getRelationships() {
        return Collections.unmodifiableMap(relationshipMap);
    }

    public void addRelationInstance(String name, String subject, String object) {
        Set<Relation> set = new HashSet<>();
        if(relationshipMap.containsKey(name)){
            set = relationshipMap.get(name);
        }
        if(name.equals("~=") || name.equals("=")){
            object = object.replaceAll("\"", "");
        }
        Relation i = new Relation(subject, object);
        set.add(i);
        relationshipMap.put(name, set);
    }

    public Map<String,Set<Function>> getFunctionDeclarations() {
        return Collections.unmodifiableMap(functionDeclarations);
    }

    public void addFunctionDeclaration(String functionName, List<String> inputs, List<String> outputs) {
        Set<Function> declset = new HashSet<>();
        if(functionDeclarations.containsKey(functionName)){
            declset = functionDeclarations.get(functionName);
        }
        Function f = new Function(inputs, outputs);
        declset.add(f);
        functionDeclarations.put(functionName, declset);
    }

    public Map<String,Set<Function>> getFunctionApplications() {
        return Collections.unmodifiableMap(functionApplications);
    }

    public void addFunctionApplication(String name, List<String> inputs, List<String> outputs) {
        Function app = new Function(inputs, outputs);
        Set<Function> set = new HashSet<>();
        if(functionApplications.containsKey(name)){
            set = functionApplications.get(name);
        }
        set.add(app);
        functionApplications.put(name, set);
    }
}
