package org.java.megalib.checker.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.java.megalib.models.MegaModel;

public class SubstitutionCheck {

    private List<String> errors;


    public SubstitutionCheck(){
        errors = new ArrayList<>();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    private boolean substitutes(String by, String e, MegaModel m, Map<String,Set<String>> substByGroup) {
        if(!errors.isEmpty())
            return false;
        if(!m.getInstanceOfMap().containsKey(e)){
            errors.add("Unable to substitute : " + e + " does not exist.");
        }
        if(!m.isInstanceOf(e, "Language") && !m.isInstanceOf(e, "Artifact") && !m.isInstanceOf(e, "System")
           && !m.isInstanceOf(e, "Technology")){
            errors.add("Unable to substitute " + e + " by " + by
                       + ". Only instances of Language, Artifact, System and Technology can be substituted.");
        }
        // if substituting is substituted -> error
        if(substByGroup.containsKey(by)){
            errors.add("Unable to substitute by " + by + " since " + by
                       + " is also substituted. No such chains are allowed in the same group.");
        }

        if(m.getInstanceOfMap().containsKey(by)){
            if(!m.isSubtypeOf(m.getInstanceOfMap().get(by), m.getInstanceOfMap().get(e))){
                errors.add("Unable to substitute " + e + " by " + by + ":" + by
                           + "already exists and has a type that is not subtype of " + e + "'s type.");
            }
            // manifestations have to be equal, but because of file+, identifier
            // containment is enough
            if(m.isInstanceOf(by, "Artifact")){
                String manifestBy = m.getRelationshipInstanceMap().get("manifestsAs").parallelStream()
                                     .filter(r -> r.getSubject().equals(by)).map(r -> r.getObject())
                                     .collect(Collectors.toList()).get(0);
                String manifeste = m.getRelationshipInstanceMap().get("manifestsAs").parallelStream()
                                    .filter(r -> r.getSubject().equals(e)).map(r -> r.getObject())
                                    .collect(Collectors.toList()).get(0);
                if(!manifeste.contains(manifestBy)){
                    errors.add("Unable to substitute " + e + " by " + by + ":" + by + " has an unfit manifestation.");
                }

                // languages have to be equal or subset
                String langBy = m.getElementOfMap().get(by);
                String langE = m.getElementOfMap().get(e);

                // if langE is substituted replace by substituting
                if(substByGroup.containsKey(langE)){
                    assert (!substByGroup.get(langE).isEmpty());
                    if(substByGroup.get(langE).size() > 1){
                        errors.add("Unable to substitute " + e + " by " + by + ": " + e
                                   + " is the language of an artifact and is substituted more than once!");
                    }
                    langE = substByGroup.get(langE).iterator().next();
                }

                if(!m.isSubsetOf(langBy, langE)){
                    errors.add("Unable to substitute " + e + " by " + by + ": " + by + " has an unfit language."
                               + langBy + langE);
                }
            }
        }
        return errors.isEmpty();
    }

    public boolean substituteGroup(Map<String,Set<String>> substByGroup, MegaModel model) {
        substByGroup.forEach((k, v) -> v.forEach(by -> substitutes(by, k, model, substByGroup)));
        return errors.isEmpty();
    }
}
