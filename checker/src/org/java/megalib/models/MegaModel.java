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

import org.java.megalib.checker.services.WellFormednessException;

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
    private Map<String, Set<String>> substMap;

    private List<String> criticalWarnings;

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
        substMap = new HashMap<>();
        criticalWarnings = new ArrayList<>();
    }

    public Map<String, String> getSubtypesMap() {
        return Collections.unmodifiableMap(subtypeOfMap);
    }

    public void addSubtypeOf(String subtype, String type) throws WellFormednessException {
        if (subtype.equals("Entity"))
            throw new WellFormednessException("Error at " + subtype + " < " + type + ": Entity is a MegaL keyword.");
        if (!(subtypeOfMap.containsKey(type) || type.equals("Entity")))
            throw new WellFormednessException("Error at " + subtype
                                              + ": The declared supertype is not a subtype of Entity");
        if (subtypeOfMap.containsKey(subtype))
            throw new WellFormednessException("Error at " + subtype + ": Multiple inheritance is not allowed.");
        else {
            subtypeOfMap.put(subtype, type);
        }
    }

    public Map<String, String> getInstanceOfMap() {
        return Collections.unmodifiableMap(instanceOfMap);
    }

    public void addInstanceOf(String instance, String type) throws WellFormednessException {
        if (subtypeOfMap.containsKey(instance))
            throw new WellFormednessException("Error at " + instance + ": It is instance and type at the same time.");
        if (instance.equals("Entity"))
            throw new WellFormednessException("Error at " + instance
                                              + ": The root type `Entity' cannot be instantiated.");
        if (!subtypeOfMap.containsKey(type) || type.equals("Entity"))
            throw new WellFormednessException("Error at " + instance
                                              + ": The instantiated type is not (transitive) subtype of Entity.");
        if (instanceOfMap.containsKey(instance))
            throw new WellFormednessException("Error at " + instance
                                              + ": Multiple types cannot be assigned to the same instance.");
        instanceOfMap.put(instance, type);
    }

    public Map<String, Set<Relation>> getRelationshipDeclarationMap() {
        return Collections.unmodifiableMap(relationDeclarationMap);
    }

    /**
     * Adds a declaration for the relationname with given types to the
     * relationDeclarationMap
     *
     * @param name
     * @param relationTypes
     * @throws Exception
     */
    public void addRelationDeclaration(String name, String sType, String oType) throws WellFormednessException {
        if (!(subtypeOfMap.containsKey(sType)))
            throw new WellFormednessException("Error at declaration of " + name + ": Its domain " + sType
                                              + " is not subtype of Entity.");
        if (!(subtypeOfMap.containsKey(oType)))
            throw new WellFormednessException("Error at declaration of " + name + ": Its range " + oType
                                              + " is not subtype of Entity.");

        Set<Relation> set = new HashSet<>();
        if (relationDeclarationMap.containsKey(name)) {
            set = relationDeclarationMap.get(name);
        }
        Relation decl = new Relation(sType, oType);

        if (set.contains(decl))
            throw new WellFormednessException("Error at declaration of " + name
                                              + ": It is declared twice with the same types.");

        set.add(decl);
        relationDeclarationMap.put(name, set);
    }

    public Map<String, Set<Relation>> getRelationshipInstanceMap() {
        return Collections.unmodifiableMap(relationInstanceMap);
    }

    public void addRelationInstances(String name, String subject, String object) throws WellFormednessException {
        Set<Relation> set = new HashSet<>();
        if (relationInstanceMap.containsKey(name)) {
            set = relationInstanceMap.get(name);
        }
        if (!instanceOfMap.containsKey(subject))
            throw new WellFormednessException("Error at instance of " + name + ": " + subject
                                              + " is not instantiated.");
        if (!instanceOfMap.containsKey(object))
            throw new WellFormednessException("Error at instance of " + name + ": " + object + " is not instantiated.");

        Relation i = new Relation(subject, object);
        if (set.contains(i))
            throw new WellFormednessException("Error at instance of " + name + ": '" + subject + " " + name + " "
                                              + object + "' already exists.");
        checkRelationInstanceFits(name, subject, object);
        if (name.equals("elementOf")) {
            elementOfMap.put(subject, object);
        } else
            if (name.equals("subsetOf")) {
                subsetOfMap.put(subject, object);
            }
        set.add(i);
        relationInstanceMap.put(name, set);
    }

    private void checkRelationInstanceFits(String name, String subject, String object) throws WellFormednessException {
        if (!relationDeclarationMap.containsKey(name))
            throw new WellFormednessException("Error at instance of " + name + ": '" + subject + " " + name + " "
                                              + object + "' does not fit any declaration.");
        Set<Relation> decls = relationDeclarationMap.get(name);

        // determine (super-)types of subject
        List<String> subjectTs = new ArrayList<>();
        String type = instanceOfMap.get(subject);
        while (!type.equals("Entity")) {
            subjectTs.add(type);
            type = subtypeOfMap.get(type);
        }
        // determine (super-)types of object
        List<String> objectTs = new ArrayList<>();
        type = instanceOfMap.get(object);
        while (!type.equals("Entity")) {
            objectTs.add(type);
            type = subtypeOfMap.get(type);
        }
        int count = 0;
        // build cross product and count fits to declarations
        for (String sT : subjectTs) {
            for (String oT : objectTs) {
                Relation r = new Relation(sT, oT);
                if (decls.contains(r)) {
                    count++;
                }
            }
        }
        if (count == 0)
            throw new WellFormednessException("Error at instance of " + name + ": '" + subject + " " + name + " "
                                              + object + "' does not fit any declaration.");
        if (count > 1)
            throw new WellFormednessException("Error at instance of " + name + ": '" + subject + " " + name + " "
                                              + object + "' fits multiple declarations.");
    }

    public Map<String, Function> getFunctionDeclarations() {
        return Collections.unmodifiableMap(functionDeclarations);
    }

    public void addFunctionDeclaration(String functionName, List<String> inputs,
                                       List<String> outputs) throws WellFormednessException {
        instanceOfMap.put(functionName, "Function");
        Function f = null;
        if (functionDeclarations.containsKey(functionName))
            throw new WellFormednessException("Error: The function " + functionName + " has multiple declarations.");
        f = new Function(inputs, outputs);
        for (String i : inputs) {
            String type = instanceOfMap.get(i);
            if (type == null)
                throw new WellFormednessException("Error at " + functionName + "'s declaration" + ": The language " + i
                                                  + " was not declared.");
            if (!isSubtypeOf(type, "Language"))
                throw new WellFormednessException("Error at " + functionName + "'s declaration" + ": " + i
                                                  + " is not a language.");
        }
        for (String o : outputs) {
            String type = instanceOfMap.get(o);
            if (type == null)
                throw new WellFormednessException("Error at " + functionName + "'s declaration" + ": The language " + o
                                                  + " was not declared.");
            if (!isSubtypeOf(type, "Language"))
                throw new WellFormednessException("Error at " + functionName + "'s declaration" + ": " + o
                                                  + " is not a language.");
        }
        functionDeclarations.put(functionName, f);
    }

    public Map<String, Set<Function>> getFunctionApplications() {
        return Collections.unmodifiableMap(functionInstances);
    }

    public void addFunctionApplication(String name, List<String> inputs,
                                       List<String> outputs) throws WellFormednessException {
        Function app = new Function(inputs, outputs);
        if (!functionDeclarations.containsKey(name))
            throw new WellFormednessException("Error at application of " + name
                                              + ": A declaration has to be stated beforehand.");
        Set<Function> set = new HashSet<>();
        if (functionInstances.containsKey(name)) {
            set = functionInstances.get(name);
        }
        if (set.contains(app))
            throw new WellFormednessException("Error at application of " + name + " with inputs " + app.getInputs()
                                              + " " + "and outputs " + app.getOutputs() + ": It already exists.");
        if (app.getInputs().size() != functionDeclarations.get(name).getInputs().size())
            throw new WellFormednessException("Error at application of " + name + " with inputs " + app.getInputs()
                                              + " " + "and outputs " + app.getOutputs()
                                              + ": It does not fit to the declaration.");
        if (app.getOutputs().size() != functionDeclarations.get(name).getOutputs().size())
            throw new WellFormednessException("Error at application of " + name + " with inputs " + app.getInputs()
                                              + " " + "and outputs " + app.getOutputs()
                                              + ": It does not fit to the declaration.");
        checkFunctionInstanceFits(app, functionDeclarations.get(name));
        set.add(app);
        functionInstances.put(name, set);
    }

    private void checkFunctionInstanceFits(Function instance, Function function) throws WellFormednessException {
        List<String> dis = instance.getInputs();
        List<String> dls = function.getInputs();

        for (int i = 0; i < dis.size(); i++) {
            if (!isInstanceOf(dis.get(i), "Artifact"))
                throw new WellFormednessException("Error at a function application: " + dis.get(i)
                                                  + " is not instance of Artifact.");
            if (!isElementOf(dis.get(i), dls.get(i)))
                throw new WellFormednessException("Error at a function application: " + dis.get(i)
                                                  + " is not element of " + dls.get(i) + ".");
        }

        List<String> ris = instance.getOutputs();
        List<String> rls = function.getOutputs();
        for (int i = 0; i < ris.size(); i++) {
            if (!isInstanceOf(ris.get(i), "Artifact"))
                throw new WellFormednessException("Error at a function application: " + ris.get(i)
                                                  + " is not instance of Artifact.");
            if (!isElementOf(ris.get(i), rls.get(i)))
                throw new WellFormednessException("Error at a function application: " + ris.get(i)
                                                  + " is not element of " + rls.get(i) + ".");
        }
    }

    public Map<String, String> getElementOfMap() {
        return Collections.unmodifiableMap(elementOfMap);
    }

    public Map<String, Set<String>> getLinkMap() {
        return Collections.unmodifiableMap(linkMap);
    }

    public void addLink(String entity, String link) throws WellFormednessException {
        if (!(instanceOfMap.containsKey(entity) || subtypeOfMap.containsKey(entity)))
            throw new WellFormednessException("Error at linking " + entity + ". Declaration is missing.");
        Set<String> links;
        if (!linkMap.containsKey(entity)) {
            links = new HashSet<>();
        } else {
            links = linkMap.get(entity);
        }
        if (!links.add(link))
            throw new WellFormednessException("Error at linking " + entity + " to " + link
                                              + ". This link has already been assigned");
        linkMap.put(entity, links);
    }

    public Map<String, String> getSubsetOfMap() {
        return Collections.unmodifiableMap(subsetOfMap);
    }

    public void addSubstitutes(String by, String e) throws WellFormednessException {
        if (!e.startsWith("?"))
            throw new WellFormednessException("Unable to substitute: " + e + " is not abstract.");
        if (!instanceOfMap.containsKey(e))
            throw new WellFormednessException("Unable to substitute : " + e + " does not exist.");
        if (instanceOfMap.containsKey(by))
            throw new WellFormednessException("Unable to substitute " + e + " by " + by + ". " + by
                                              + " already exists");
        Set<String> subst = substMap.get(e);
        if (null == subst) {
            subst = new HashSet<>();
        } else {
            if (subst.contains(by))
                throw new WellFormednessException(by + " is already substituting " + e + " elsewhere!");
        }
        subst.add(by);
        substMap.put(e, subst);

    }

    public boolean isInstanceOf(String entity, String type) {
        if (!instanceOfMap.containsKey(entity))
            return false;
        String temp = instanceOfMap.get(entity);
        return temp.equals(type) ? true : isSubtypeOf(temp, type);
    }

    public boolean isSubtypeOf(String subtype, String type) {
        String temp = subtypeOfMap.get(subtype);
        while (!temp.equals(type)) {
            if (temp.equals("Entity"))
                return false;
            temp = subtypeOfMap.get(temp);
        }
        return true;
    }

    private boolean isElementOf(String art, String lang) {
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

    public List<String> getCriticalWarnings() {
        return criticalWarnings;
    }

    public void addWarning(String w) {
        criticalWarnings.add(w);
    }

    public Map<String, Set<String>> getSubstitutions() {
        return Collections.unmodifiableMap(substMap);
    }

    /**
     * TODO : Removing the abstract entity should only happen after all modules
     * have been processed.
     * 
     * @throws WellFormednessException
     */
    public void resolveSubstitutions() throws WellFormednessException {
        for (String e : substMap.keySet()) {
            for (String by : substMap.get(e)) {
                // copy values from existing relationships with e
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
                    if (substMap.get(e).size() > 1)
                        throw new WellFormednessException("Error while trying to substitute " + e
                                                          + ": A language can only be substituted once since overloading functions is not allowed.");
                    Map<String, Function> newmap = new HashMap<>();
                    for (String name : functionDeclarations.keySet()) {
                        Function d = functionDeclarations.get(name);
                        List<String> inputs = d.getInputs().stream().map(i -> i.equals(e) ? by : i)
                                               .collect(Collectors.toList());
                        List<String> outputs = d.getOutputs().stream().map(o -> o.equals(e) ? by : o)
                                                .collect(Collectors.toList());
                        newmap.put(name, new Function(inputs, outputs));
                    }
                    functionDeclarations = newmap;
                }
            }

            instanceOfMap.remove(e);
            elementOfMap.remove(e);
            subsetOfMap.remove(e);
            linkMap.remove(e);

            Map<String, Set<Relation>> newmap = new HashMap<>();
            for (String name : relationInstanceMap.keySet()) {
                Set<Relation> rels = relationInstanceMap.get(name).parallelStream()
                                                        .filter((r -> !r.getSubject().equals(e)
                                                                      && !r.getObject().equals(e)))
                                                        .collect(Collectors.toSet());
                newmap.put(name, rels);
            }
            relationInstanceMap = newmap;

            Map<String, Set<Function>> newAppmap = new HashMap<>();
            for (String name : functionInstances.keySet()) {
                Set<Function> fs = functionInstances.get(name).parallelStream()
                                                    .filter(f -> !f.getInputs().contains(e)
                                                                 && !f.getOutputs().contains(e))
                                                    .collect(Collectors.toSet());
                newAppmap.put(name, fs);
            }
            functionInstances = newAppmap;
        }
        substMap = new HashMap<>();
    }

}
