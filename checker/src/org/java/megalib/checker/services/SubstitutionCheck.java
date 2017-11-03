package org.java.megalib.checker.services;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.java.megalib.models.MegaModel;

public class SubstitutionCheck {

    private List<String> errors;


    public SubstitutionCheck(){
        errors = Collections.synchronizedList(new LinkedList<>());
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public boolean substituteGroup(Map<String,Set<String>> substByGroup, MegaModel model) {
        return substByGroup.entrySet().parallelStream()
                           .noneMatch(e -> e.getValue().parallelStream()
                                            .anyMatch(by -> !substitutes(by, e.getKey(), model, substByGroup)));
    }

    private boolean substitutes(String by, String e, MegaModel m, Map<String,Set<String>> substByGroup) {
        if(isCyclicSubst(by, substByGroup))
            return false;

        if(hasInvalidType(e, by, m))
            return false;

        if(isInvalidExistingSubstitute(by, e, m))
            return false;

        return true;
    }

    private boolean isCyclicSubst(String by, Map<String,Set<String>> substByGroup) {
        if(substByGroup.containsKey(by)){
            errors.add("Unable to substitute by " + by + " since " + by
                       + " is also substituted. No such chains are allowed in the same group.");
            return true;
        }
        return false;
    }

    private boolean hasInvalidType(String e, String by, MegaModel m) {
        if(!m.getInstanceOfMap().containsKey(e)){
            errors.add("Unable to substitute : " + e + " does not exist.");
            return true;
        }
        if(!m.isInstanceOf(e, "Language") && !m.isInstanceOf(e, "Artifact") && !m.isInstanceOf(e, "System")
           && !m.isInstanceOf(e, "Node")){
            errors.add("Unable to substitute " + e + " by " + by
                       + ". Only instances of Language, Artifact, System and Node can be substituted.");
            return true;
        }
        return false;
    }

    private boolean isInvalidExistingSubstitute(String by, String e, MegaModel m) {
        if(m.getInstanceOfMap().containsKey(by)){
            if(!m.getType(e).contains(m.getType(by)) && !m.isSubtypeOf(m.getType(by), m.getType(e))){
                errors.add("Unable to substitute " + e + " by " + by + " : " + by
                       + "already exists and has a type that is not subtype of " + e + "'s type.");
                return true;
            }
        }
        return false;
    }
}
