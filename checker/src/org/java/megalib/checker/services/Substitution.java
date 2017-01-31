package org.java.megalib.checker.services;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;

public class Substitution {

    private MegaModel model;
    private Map<String,Set<String>> substByGroup;

    public Substitution(MegaModel model, Map<String,Set<String>> substByGroup){
        this.model = model;
        this.substByGroup = substByGroup;
    }

    /**
     * Creates instances for all substituting entities.
     * For all existing relationships where the substituted entity is either subject or object:
     * If the target or source is not substituted, a new relationship is created that involves the other entity and the substituting entity.
     * If the target or source is substituted, a new relationship is created to the substituting entity
     *
     * @return
     */
    public MegaModel substituteGroup() {
        substByGroup.forEach((key,set) -> set.forEach(v -> model.addInstanceOf(v, model.getInstanceOfMap().get(key))));
        substByGroup.keySet().forEach(k -> substituteEntity(k));
        substituteInFunctionDecl();
        return model;
    }

    private void substituteEntity(String substituted) {
        for(String rname : model.getRelationshipInstanceMap().keySet()){
            Set<Relation> rstream = model.getRelationshipInstanceMap().get(rname).parallelStream()
                                        .filter(r -> r.getSubject().equals(substituted)||r.getObject().equals(substituted))
                                        .collect(Collectors.toSet());
            for(Relation r : rstream){
                if(r.getSubject().equals(substituted)){
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
                }else if(r.getObject().equals(substituted)){
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
        }
    }

    private void substituteInFunctionDecl() {
        Map<String,Set<Function>> map = model.getFunctionDeclarations();
        Set<Function> newSet = new HashSet<>();
        for(String name : map.keySet()){
            for(Function decl : map.get(name)){
                // crossproduct
            }
        }
    }

}
