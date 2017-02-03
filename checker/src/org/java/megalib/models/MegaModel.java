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
    private Map<String, Set<String>> linkMap;

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
        linkMap = new HashMap<>();
        substitutedLanguages = new HashSet<>();
        removableAbstract = new HashSet<>();
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

    public Map<String, Set<Relation>> getRelationshipInstanceMap() {
        return Collections.unmodifiableMap(relationInstanceMap);
    }

    public void addRelationInstance(String name, String subject, String object) {
        Set<Relation> set = new HashSet<>();
        if(name.startsWith("^")){
            name = name.substring(1);
            String temp = subject;
            subject = object;
            object = temp;
        }
        if (relationInstanceMap.containsKey(name)) {
            set = relationInstanceMap.get(name);
        }
        Relation i = new Relation(subject, object);
        if (name.equals("elementOf")) {
            elementOfMap.put(subject, object);
        } else
            if (name.equals("subsetOf")) {
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

    public Map<String, String> getElementOfMap() {
        return Collections.unmodifiableMap(elementOfMap);
    }

    public Map<String, Set<String>> getLinkMap() {
        return Collections.unmodifiableMap(linkMap);
    }

    public void addLink(String entity, String link) {
        Set<String> links = new HashSet<>();
        if (linkMap.containsKey(entity)) {
            links = linkMap.get(entity);
        }
        links.add(link);
        linkMap.put(entity, links);
    }

    public Map<String, String> getSubsetOfMap() {
        return Collections.unmodifiableMap(subsetOfMap);
    }

    public boolean isInstanceOf(String entity, String type) {
        if (!instanceOfMap.containsKey(entity))
            return false;
        String temp = instanceOfMap.get(entity);
        return temp.equals(type) ? true : isSubtypeOf(temp, type);
    }

    public boolean isSubtypeOf(String subtype, String type) {
        if (!subtypeOfMap.containsKey(subtype))
            return false;
        String temp = subtypeOfMap.get(subtype);
        while (!temp.equals(type)) {
            if (temp.equals("Entity"))
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

    public boolean isSubsetOf(String l1, String l2) {
        if (!subsetOfMap.containsKey(l1))
            return false;
        String temp = l1;
        while (subsetOfMap.containsKey(temp)) {
            if (subsetOfMap.get(temp).equals(l2))
                return true;
            temp = subsetOfMap.get(temp);
        }
        return false;
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
        linkMap.remove(e);

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

}
