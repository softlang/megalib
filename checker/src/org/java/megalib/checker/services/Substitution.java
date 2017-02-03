package org.java.megalib.checker.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;

public class Substitution {

    private MegaModel model;
    private Map<String,Set<String>> substByGroup;
    private Map<String,Set<Function>> newFunctions;

    public Substitution(MegaModel model, Map<String,Set<String>> substByGroup){
        this.model = model;
        this.substByGroup = substByGroup;
        newFunctions = new ConcurrentHashMap<>();
    }

    /**
     * Creates instances for all substituting entities. For all existing
     * relationships where the substituted entity is either subject or object:
     * If the target or source is not substituted, a new relationship is created
     * that involves the other entity and the substituting entity. If the target
     * or source is substituted, a new relationship is created to the
     * substituting entity
     *
     * @return
     */
    public MegaModel substituteGroup() {
        substByGroup.forEach((key, set) -> set.forEach(v -> model.addInstanceOf(v, model.getInstanceOfMap().get(key))));
        substByGroup.keySet().forEach(k -> model.getRelationshipInstanceMap().keySet()
                                                .forEach(rname -> substituteEntityIn(k, rname)));

        model.getFunctionDeclarations().entrySet().parallelStream()
             .forEach(entry -> entry.getValue().forEach(f -> substituteFunction(entry.getKey(), f)));
        newFunctions.entrySet().stream()
                    .forEach(e -> e.getValue()
                                   .forEach(f -> model.addFunctionDeclaration(e.getKey(), f.getInputs(),
                                                                              f.getOutputs())));
        newFunctions.clear();
        model.getFunctionApplications().entrySet().parallelStream()
             .forEach(entry -> entry.getValue().forEach(f -> substituteFunction(entry.getKey(), f)));
        newFunctions.entrySet().stream()
                    .forEach(e -> e.getValue().forEach(f -> model.addFunctionApplication(e.getKey(), f.getInputs(),
                                                                                         f.getOutputs())));
        return model;
    }

    private void substituteEntityIn(String substituted, String rname) {
        Set<Relation> sstream = model.getRelationshipInstanceMap().get(rname).parallelStream()
                                     .filter(r -> r.getSubject().equals(substituted)).collect(Collectors.toSet());
        Set<Relation> ostream = model.getRelationshipInstanceMap().get(rname).parallelStream()
                                     .filter(r -> r.getObject().equals(substituted)).collect(Collectors.toSet());
        for(Relation r : sstream){
            if(substByGroup.containsKey(r.getObject())){
                for(String substingSubj : substByGroup.get(substituted)){
                    for(String substingObj : substByGroup.get(r.getObject())){
                        model.addRelationInstance(rname, substingSubj, substingObj);
                    }
                }
            }else{
                for(String substingSubj : substByGroup.get(substituted)){
                    model.addRelationInstance(rname, substingSubj, r.getObject());
                }
            }
        }
        for(Relation r : ostream){
            if(substByGroup.containsKey(r.getSubject())){
                for(String substingObj : substByGroup.get(substituted)){
                    for(String substingSubj : substByGroup.get(r.getSubject())){
                        model.addRelationInstance(rname, substingSubj, substingObj);
                    }
                }
            }else{
                for(String substingObj : substByGroup.get(substituted)){
                    model.addRelationInstance(rname, r.getSubject(), substingObj);
                }
            }
        }
    }

    private void substituteFunction(String name, Function pf) {
        Set<String> substituted = new HashSet<>();
        substituted.addAll(pf.getInputs().parallelStream().filter(l -> substByGroup.containsKey(l))
                             .collect(Collectors.toSet()));
        substituted.addAll(pf.getOutputs().parallelStream().filter(l -> substByGroup.containsKey(l))
                             .collect(Collectors.toSet()));
        List<Function> fset = new ArrayList<>();
        List<Function> newset = new ArrayList<>();
        fset.add(pf);
        for(String s : substituted){

            for(Function f : fset){
                for(String substing : substByGroup.get(s)){

                    List<String> inputs = f.getInputs().parallelStream().map(i -> i.equals(s) ? substing : i)
                                           .collect(Collectors.toList());
                    List<String> outputs = f.getOutputs().parallelStream().map(o -> o.equals(s) ? substing : o)
                                            .collect(Collectors.toList());
                    Function fnew = new Function(inputs, outputs);
                    newset.add(fnew);
                }
            }
            fset.clear();
            fset.addAll(newset);
        }
        if(newFunctions.containsKey(name)){
            fset.addAll(newFunctions.get(name));
        }
        newFunctions.put(name, fset.stream().collect(Collectors.toSet()));
    }

}
