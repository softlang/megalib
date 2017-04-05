/**
 *
 */
package org.java.megalib.models;

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
    private Map<String, String> elementOfMap;
    private Map<String, String> subsetOfMap;
    private Map<String, Set<Relation>> relationDeclarationMap;
    private Map<String, Set<Relation>> relationInstanceMap;
    private Map<String,Set<Function>> functionDeclarations;
    private Map<String, Set<Function>> functionApplications;

    private Map<String,String> namespaceMap;
    private Set<String> substitutedLanguages;

    private Set<String> removableAbstract;

    public MegaModel() {
        subtypeOfMap = new HashMap<>();
        instanceOfMap = new HashMap<>();
        elementOfMap = new HashMap<>();
        subsetOfMap = new HashMap<>();
        relationDeclarationMap = new HashMap<>();
        relationInstanceMap = new HashMap<>();
        functionDeclarations = new HashMap<>();
        functionApplications = new HashMap<>();
        substitutedLanguages = new HashSet<>();
        removableAbstract = new HashSet<>();
        namespaceMap = new HashMap<>();
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

    public void addInstanceOf(String instance, String type) {
        if (instance.startsWith("?")) {
            removableAbstract.add(instance);
        }
        instanceOfMap.put(instance, type);
    }

    public Map<String, Set<Relation>> getRelationshipDeclarationMap() {
        return Collections.unmodifiableMap(relationDeclarationMap);
    }

    public void addRelationDeclaration(String name, String subject, String object) {
        Set<Relation> set = new HashSet<>();
        if (relationDeclarationMap.containsKey(name)) {
            set = relationDeclarationMap.get(name);
        }
        Relation decl = new Relation(subject, object);
        set.add(decl);
        relationDeclarationMap.put(name, set);
    }

    public Map<String, Set<Relation>> getRelationships() {
        return Collections.unmodifiableMap(relationInstanceMap);
    }

    public void addRelationInstance(String name, String subject, String object) {
        Set<Relation> set = new HashSet<>();
        if (relationInstanceMap.containsKey(name)) {
            set = relationInstanceMap.get(name);
        }
        if(name.equals("~=") || name.equals("=")){
            object = object.replaceAll("\"", "");
        }
        Relation i = new Relation(subject, object);
        if (name.equals("elementOf")) {
            elementOfMap.put(subject, object);
        }else if(name.equals("subsetOf")){
            subsetOfMap.put(subject, object);
        }
        set.add(i);
        relationInstanceMap.put(name, set);
    }

    public Map<String,Set<Function>> getFunctionDeclarations() {
        return Collections.unmodifiableMap(functionDeclarations);
    }

    public void addFunctionDeclaration(String functionName, List<String> inputs, List<String> outputs) {
        instanceOfMap.put(functionName, "Function");
        Set<Function> declset = new HashSet<>();
        if(functionDeclarations.containsKey(functionName)){
            declset = functionDeclarations.get(functionName);
        }
        Function f = new Function(inputs, outputs);
        declset.add(f);
        functionDeclarations.put(functionName, declset);
    }

    public Map<String, Set<Function>> getFunctionApplications() {
        return Collections.unmodifiableMap(functionApplications);
    }

    public void addFunctionApplication(String name, List<String> inputs, List<String> outputs) {
        Function app = new Function(inputs, outputs);
        Set<Function> set = new HashSet<>();
        if (functionApplications.containsKey(name)) {
            set = functionApplications.get(name);
        }
        set.add(app);
        functionApplications.put(name, set);
    }

    public Map<String,String> getElementOfMap() {
        return Collections.unmodifiableMap(elementOfMap);
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
        if (!elementOfMap.containsKey(art))
            return false;
        String temp = elementOfMap.get(art);
        if (temp.equals(lang) || isSubsetOf(temp, lang))
            return true;
        return false;
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

    public void cleanUpAbstraction() {
        for (String e : removableAbstract) {
            cleanUpAbstractInstance(e);
        }
    }

    private void cleanUpAbstractInstance(String e) {
        instanceOfMap.remove(e);
        elementOfMap.remove(e);
        subsetOfMap.remove(e);

        Map<String, Set<Relation>> relmap = new HashMap<>();
        for (String name : relationInstanceMap.keySet()) {
            Set<Relation> rels = relationInstanceMap.get(name).parallelStream()
                    .filter((r -> !r.getSubject().equals(e)
                            && !r.getObject().equals(e)))
                    .collect(Collectors.toSet());
            relmap.put(name, rels);
        }
        relationInstanceMap = relmap;

        Map<String, Set<Function>> fAppmap = new HashMap<>();
        for (String name : functionApplications.keySet()) {
            Set<Function> fs = functionApplications.get(name).parallelStream()
                    .filter(f -> !f.getInputs().contains(e) && !f.getOutputs().contains(e))
                    .collect(Collectors.toSet());
            fAppmap.put(name, fs);
        }
        functionApplications = fAppmap;
    }

    public Set<String> getLinks(String name) {
        return relationInstanceMap.get("=").parallelStream().map(r -> r.getSubject()).filter(s -> s.equals(name))
                                  .collect(Collectors.toSet());
    }

    public void addNamespace(String name, String link) {
        namespaceMap.put(name, link);
    }

    public String getNamespace(String name) {
        return namespaceMap.get(name);
    }

}
