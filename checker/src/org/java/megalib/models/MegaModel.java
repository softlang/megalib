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
    private Map<String, Function> functionDeclarations;
    private Map<String, Set<Function>> functionInstances;
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
        functionInstances = new HashMap<>();
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

    public void addRelationInstances(String name, String subject, String object) {
        Set<Relation> set = new HashSet<>();
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

    public Map<String, Function> getFunctionDeclarations() {
        return Collections.unmodifiableMap(functionDeclarations);
    }

    public void addFunctionDeclaration(String functionName, List<String> inputs, List<String> outputs) {
        instanceOfMap.put(functionName, "Function");
        Function f = new Function(inputs, outputs);
        functionDeclarations.put(functionName, f);
    }

    public Map<String, Set<Function>> getFunctionApplications() {
        return Collections.unmodifiableMap(functionInstances);
    }

    public void addFunctionApplication(String name, List<String> inputs, List<String> outputs) {
        Function app = new Function(inputs, outputs);
        Set<Function> set = new HashSet<>();
        if (functionInstances.containsKey(name)) {
            set = functionInstances.get(name);
        }
        set.add(app);
        functionInstances.put(name, set);
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

    public void substitutes(String by, String e) {
        System.out.println("Substituting " + e + " by " + by);
        String v = instanceOfMap.get(e);
        addInstanceOf(by, v);

        if (elementOfMap.containsKey(e)) {
            v = elementOfMap.get(e);
            addRelationInstances("elementOf", by, v);
        }

        if (subtypeOfMap.containsKey(e)) {
            v = subtypeOfMap.get(e);
            addRelationInstances("subsetOf", by, v);
        }

        if (linkMap.containsKey(e)) {
            Set<String> links = linkMap.get(e);
            for (String l : links) {
                addLink(by, l);
            }
        }

        Map<String, Set<Relation>> rmap = new HashMap<>();
        for (String name : relationInstanceMap.keySet()) {
            Set<Relation> rels = new HashSet<>();
            for (Relation r : relationInstanceMap.get(name)) {
                if (r.getSubject().equals(e)) {
                    if (r.getObject().equals(e)) {
                        rels.add(new Relation(by, by));
                    } else {
                        rels.add(new Relation(by, r.getObject()));
                    }
                } else {
                    if (r.getObject().equals(e)) {
                        rels.add(new Relation(r.getSubject(), by));
                    } else {
                        rels.add(r);
                    }
                }
            }
            rmap.put(name, rels);
        }
        relationInstanceMap = rmap;

        if (isInstanceOf(e, "Artifact")) {
            Map<String, Set<Function>> fdmap = new HashMap<>();
            for (String name : functionInstances.keySet()) {
                Set<Function> fs = new HashSet<>();
                for (Function f : functionInstances.get(name)) {
                    List<String> inputs = f.getInputs().stream().map(i -> i.equals(e) ? by : i)
                                           .collect(Collectors.toList());
                    List<String> outputs = f.getOutputs().stream().map(o -> o.equals(e) ? by : o)
                                            .collect(Collectors.toList());
                    fs.add(new Function(inputs, outputs));
                }
                fdmap.put(name, fs);
            }
            functionInstances = fdmap;
        }

        if (isInstanceOf(e, "Language")) {
            substitutedLanguages.add(e);
            Map<String, Function> newmap = new HashMap<>();
            for (String name : functionDeclarations.keySet()) {
                Function d = functionDeclarations.get(name);
                List<String> inputs = d.getInputs().stream().map(i -> i.equals(e) ? by : i)
                                       .collect(Collectors.toList());
                List<String> outputs = d.getOutputs().stream().map(o -> o.equals(e) ? by : o)
                                        .collect(Collectors.toList());
                newmap.put(name, new Function(inputs, outputs)); // overwritting
            }
            functionDeclarations = newmap;
        }

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
        for (String name : functionInstances.keySet()) {
            Set<Function> fs = functionInstances.get(name).parallelStream()
                                                .filter(f -> !f.getInputs().contains(e) && !f.getOutputs().contains(e))
                                                .collect(Collectors.toSet());
            fAppmap.put(name, fs);
        }
        functionInstances = fAppmap;
    }

}
