/**
 *
 */
package org.java.megalib.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MegaModel {
    private Map<String, String> subtypeOfMap;
    private Map<String, String> instanceOfMap;
    private Map<String, String> subsetOfMap;
    private Map<String, Set<Relation>> relationDeclarationMap;
    private Map<String,Set<Relation>> relationshipMap;
    private Map<String,Set<Function>> functionDeclarations;
    private Map<String, Set<Function>> functionApplications;

    private Map<String,String> namespaceMap;
    private Set<String> substitutedLanguages;

    private List<Block> blocks;
    private List<Relation> importGraph;

    public MegaModel() {
        subtypeOfMap = new HashMap<>();
        instanceOfMap = new HashMap<>();
        subsetOfMap = new HashMap<>();
        relationDeclarationMap = new HashMap<>();
        relationshipMap = new HashMap<>();
        functionDeclarations = new HashMap<>();
        functionApplications = new HashMap<>();
        substitutedLanguages = new HashSet<>();
        namespaceMap = new HashMap<>();
        blocks = new ArrayList<>();
    }

    public void addBlock(Block m) {
        blocks.add(m);
    }

    public List<Block> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    public Map<String, String> getSubtypesMap() {
        return Collections.unmodifiableMap(subtypeOfMap);
    }

    public void addSubtypeOf(String subtype, String type) {
        subtypeOfMap.put(subtype, type);
    }

    public Map<String, String> getInstanceOfMap() {
        return Collections.unmodifiableMap(instanceOfMap);
    }

    public void addInstanceOf(String instance, String type, Block block) {
        instanceOfMap.put(instance, type);
        block.addInstanceOf(instance,type);
    }

    public Map<String, Set<Relation>> getRelationshipDeclarationMap() {
        return Collections.unmodifiableMap(relationDeclarationMap);
    }

    public void addRelationDeclaration(String name, String subject, String object, Block block) {
        Set<Relation> set = new HashSet<>();
        if (relationDeclarationMap.containsKey(name)) {
            set = relationDeclarationMap.get(name);
        }
        Relation decl = new Relation(subject, object);
        block.addRelationDeclaration(name, decl);
        set.add(decl);
        relationDeclarationMap.put(name, set);
    }

    public Map<String, Set<Relation>> getRelationships() {
        return Collections.unmodifiableMap(relationshipMap);
    }

    public void addRelationInstance(String name, String subject, String object, Block block) {
        Set<Relation> set = new HashSet<>();
        if(relationshipMap.containsKey(name)){
            set = relationshipMap.get(name);
        }
        if(name.equals("~=") || name.equals("=")){
            object = object.replaceAll("\"", "");
            addInstanceOf(object, "Link",block);
        }
        Relation i = new Relation(subject, object);
        if(name.equals("subsetOf")){
            subsetOfMap.put(subject, object);
        }
        set.add(i);
        relationshipMap.put(name, set);

        block.addRelationInstance(name, i);
    }

    public Map<String,Set<Function>> getFunctionDeclarations() {
        return Collections.unmodifiableMap(functionDeclarations);
    }

    public void addFunctionDeclaration(String functionName, List<String> inputs, List<String> outputs, Block block) {
        instanceOfMap.put(functionName, "Function");
        Set<Function> declset = new HashSet<>();
        if(functionDeclarations.containsKey(functionName)){
            declset = functionDeclarations.get(functionName);
        }
        Function f = new Function(inputs, outputs,true);
        declset.add(f);
        functionDeclarations.put(functionName, declset);

        block.addFunctionDeclaration(functionName, f);
    }

    public Map<String, Set<Function>> getFunctionApplications() {
        return Collections.unmodifiableMap(functionApplications);
    }

    public void addFunctionApplication(String name, List<String> inputs, List<String> outputs, Block block) {
        Function app = new Function(inputs, outputs,false);
        Set<Function> set = new HashSet<>();
        if (functionApplications.containsKey(name)) {
            set = functionApplications.get(name);
        }
        set.add(app);
        functionApplications.put(name, set);

        block.addFunctionApplication(name, app);
    }

    public String getType(String entity) {
		return instanceOfMap.get(entity);
	}

    public boolean isInstanceOf(String entity, String type) {
        if (!instanceOfMap.containsKey(entity))
            return false;
        String temp = instanceOfMap.get(entity);
        return temp.equals(type) ? true : isSubtypeOf(temp, type);
    }

    public boolean isSubtypeOf(String subtype, String type) {
        String temp = subtype;
        while (!temp.equals(type)) {
            if(temp.equals("Entity") || !subtypeOfMap.containsKey(temp))
                return false;
            temp = subtypeOfMap.get(temp);
        }
        return true;
    }

    public boolean isElementOf(String art, String lang) {
        return relationshipMap.get("elementOf").parallelStream().filter(r -> r.getSubject().equals(art))
                              .map(r -> r.getObject()).anyMatch(l -> l.equals(lang) || isSubsetOf(l, lang));
    }

    public boolean isSubsetOf(String subset, String superset) {
        String temp = subset;
        while(!temp.equals(superset)){
            if(!subsetOfMap.containsKey(temp))
                return false;
            temp = subsetOfMap.get(temp);
        }
        return true;
    }

    public Set<String> getSubstitutedLanguages() {
        return Collections.unmodifiableSet(substitutedLanguages);
    }

    public Set<String> getLinks(String name) {
        return relationshipMap.get("=").parallelStream().map(r -> r.getSubject()).filter(s -> s.equals(name))
                                  .collect(Collectors.toSet());
    }

    public Map<String,Set<String>> getLinkMap() {
        Map<String,Set<String>> linkMap = new HashMap<>();



        return linkMap;
    }

    public void addNamespace(String name, String link) {
        namespaceMap.put(name, link);
    }

    public String getNamespace(String name) {
        return namespaceMap.get(name);
    }

    public boolean containsRelationship(String source, String predicate, String target) {
        if(!relationshipMap.containsKey(predicate))
            return false;
        return relationshipMap.get(predicate).parallelStream()
                              .anyMatch(r -> r.getSubject().equals(source) && r.getObject().equals(target));
    }

    public Set<String> getEntitiesByType(String type){
    	return getInstanceOfMap().entrySet()
		   .parallelStream()
		   .filter(e -> isInstanceOf(e.getKey(), type))
		   .map(e -> e.getKey())
		   .collect(Collectors.toSet());
    }

    public List<Relation> getImportGraph() {
        return importGraph;
    }

    public void setImportGraph(List<Relation> importGraph) {
        this.importGraph = importGraph;
    }

}
