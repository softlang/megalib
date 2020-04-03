package org.java.megalib.checker.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;

public class TypeErrorCheck {

    private List<String> errors;

    public TypeErrorCheck() {
        errors = new ArrayList<>();
    }

    public void addError(String e) {
        errors.add(e);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public boolean addNamespace(String name, String link, MegaModel m, String blockid) {
        if(!errors.isEmpty())
            return false;
        Optional<String> o = Optional.ofNullable(m.getNamespace(name));
        o.ifPresent(l -> errors.add(blockid+">> Error at namespace " + name + ":: \"" + link + "\": Already defined namespace."));
        return errors.isEmpty();
    }

    public boolean addSubtypeOf(String subtype, String type, MegaModel m, String blockid) {
        if(!errors.isEmpty())
            return false;
        if (subtype.equals("Entity")) {
            errors.add(blockid+">> Error at " + subtype + " < " + type + ": Entity is a MegaL keyword.");
        }
        if (!(m.getSubtypesMap().containsKey(type) || type.equals("Entity"))) {
            errors.add(blockid+">> Error at " + subtype + ": The declared supertype is not a subtype of Entity");
        }
        if (m.getSubtypesMap().containsKey(subtype)) {
            errors.add(blockid+">> Error at " + subtype + ": Multiple inheritance is not allowed.");
        }
        return errors.isEmpty();
    }

    public boolean addInstanceOf(String instance, String type, MegaModel m, String blockid) {
        if(!errors.isEmpty())
            return false;
        if (m.getSubtypesMap().containsKey(instance)) {
            errors.add(blockid+">> Error at " + instance + ": It is instance and type at the same time.");
        }
        if (instance.equals("Entity")) {
            errors.add(blockid+">> Error at " + instance + ": The root type `Entity' cannot be instantiated.");
        }
        if (!m.getSubtypesMap().containsKey(type) || type.equals("Entity")) {
            errors.add(blockid+">> Error at " + instance + ": The instantiated type is not (transitive) subtype of Entity.");
        }
        if (m.getInstanceOfMap().containsKey(instance)) {
        	if(!m.isSubtypeOf(type,m.getInstanceOfMap().get(instance))){
                errors.add(blockid+">> Error at " + instance + ": Multiple types cannot be assigned to the same instance.");
        	}
        }else if(m.getInstanceOfMap().keySet().parallelStream()
            .anyMatch(i -> i.toLowerCase().equals(instance.toLowerCase()))){
            errors.add(blockid+">> Error at "+ instance+ ": This ID is already given to another instance, possible with a different capitalization.");
        }
        return errors.isEmpty();
    }

    public boolean addRelationDeclaration(String name, String sType, String oType, MegaModel m, String blockid) {
        if(!errors.isEmpty())
            return false;
        if (!(m.getSubtypesMap().containsKey(sType))) {
            errors.add(blockid+">> Error at declaration of " + name + ": Its domain " + sType + " is not subtype of Entity.");
        }
        if (!(m.getSubtypesMap().containsKey(oType))) {
            errors.add(blockid+">> Error at declaration of " + name + ": Its range " + oType + " is not subtype of Entity.");
        }
        Relation decl = new Relation(sType, oType);
        if (m.getRelationshipDeclarationMap().containsKey(name)) {
            if (m.getRelationshipDeclarationMap().get(name).contains(decl)) {
                errors.add(blockid+">> Error at declaration of " + name + ": It is declared twice with the same types.");
            }
        }
        return errors.isEmpty();
    }

    public boolean addRelationInstance(String name, String subject, String object, MegaModel m, String blockid) {
        if(!errors.isEmpty())
            return false;

        if(name.equals("=") || name.equals("~="))
            return addLink(subject, object.replaceAll("\"", ""), m, blockid);

        if (!m.getInstanceOfMap().containsKey(subject)) {
            errors.add(blockid+">> Error at relationship '" + name + "': " + subject + " is not instantiated.");
        }

        if (!m.getInstanceOfMap().containsKey(object)) {
            errors.add(blockid+">> Error at relationship '" + name + "': " + object + " is not instantiated.");
        }
        if(name.startsWith("^")){
            name = name.substring(1);
            String temp = subject;
            subject = object;
            object = temp;
        }
        Relation i = new Relation(subject, object);
        if (m.getRelationships().containsKey(name)) {
            if (m.getRelationships().get(name).contains(i)) {
                errors.add(blockid+">> Error: '" + subject + " " + name + " " + object
                           + "' already exists.");
            }
        }

        if (!m.getRelationshipDeclarationMap().containsKey(name)) {
            errors.add(blockid+">> Error at instance of " + name + ": " + name + " is not declared.");
        }

        // Abort here, if things aren't instantiated
        if (!errors.isEmpty())
            return false;

        Set<Relation> decls = m.getRelationshipDeclarationMap().get(name);

        // determine (super-)types of subject
        List<String> subjectTs = new ArrayList<>();
        String type = m.getInstanceOfMap().get(subject);
        while (!type.equals("Entity")) {
            subjectTs.add(type);
            type = m.getSubtypesMap().get(type);
        }
        // determine (super-)types of object
        List<String> objectTs = new ArrayList<>();
        type = m.getInstanceOfMap().get(object);
        while (!type.equals("Entity")) {
            objectTs.add(type);
            type = m.getSubtypesMap().get(type);
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
        if (count == 0) {
            errors.add(blockid+">> Error at instance of " + name + ": '" + subject + " " + name + " " + object
                       + "' does not fit any declaration.");
        }
        if (count > 1) {
            errors.add(blockid+">> Error at instance of " + name + ": '" + subject + " " + name + " " + object
                       + "' fits multiple declarations.");
        }
        return errors.isEmpty();
    }

    public boolean addFunctionDeclaration(String functionName, List<String> inputs, List<String> outputs, MegaModel m, String blockid) {
        if(!errors.isEmpty())
            return false;
        for (String i : inputs) {
            String type = m.getInstanceOfMap().get(i);
            if(!m.isSubtypeOf(i, "Artifact")) {
            		if (type == null) {
            			errors.add(blockid+">> Error at " + functionName + "'s declaration" + ": The domain " + i + " was not declared.");
            			continue;
            		}
            		if (!m.isSubtypeOf(type, "Language")) {
            			errors.add(blockid+">> Error at " + functionName + "'s declaration" + ": " + i + " is neither language nor subtype of Artifact.");
            		}
            }
        }
        for (String o : outputs) {
            String type = m.getInstanceOfMap().get(o);
            if(!m.isSubtypeOf(o, "Artifact")) {
            		if (type == null) {
            			errors.add(blockid+">> Error at " + functionName + "'s declaration" + ": The range " + o + " was not declared.");
            			continue;
            		}
            		if (!m.isSubtypeOf(type, "Language")) {
            			errors.add(blockid+">> Error at " + functionName + "'s declaration" + ": " + o + " is neither language nor subtype of Artifact.");
            		}
            }
        }
        return errors.isEmpty();
    }

    public boolean addFunctionApplication(String name, List<String> inputs, List<String> outputs, MegaModel m, String blockid) {
        if(!errors.isEmpty())
            return false;
        if (!m.getFunctionDeclarations().containsKey(name)) {
            errors.add(blockid+">> Error at application of " + name + ": A declaration has to be stated beforehand.");
            return false;
        }
        for(String i : inputs){
            if(!m.isInstanceOf(i, "Artifact")){
                errors.add(blockid+">> Error at application of " + name + ": " + i + " is not instance of Artifact.");
            }
        }
        for(String o : outputs){
            if(!m.isInstanceOf(o, "Artifact")){
                errors.add(blockid+">> Error at application of " + name + ": " + o + " is not an instance of Artifact.");
            }
        }

        Set<Function> set = new HashSet<>();
        if (m.getFunctionApplications().containsKey(name)) {
            set = m.getFunctionApplications().get(name);
        }
        Function app = new Function(inputs, outputs,false);
        if (set.contains(app)) {
            errors.add(blockid+">> Error at application of " + name + " with inputs " + app.getInputs() + " " + "and outputs "
                    + app.getOutputs() + ": It already exists.");
        }

        // Stop if errors already occurred
        if(!errors.isEmpty())
            return false;

        // Check fit to declaration
        int fitcount = 0;
        for(Function decl : m.getFunctionDeclarations().get(name)){
            boolean fits = true;
            if(decl.getInputs().size() != inputs.size() 
            		|| decl.getOutputs().size() != outputs.size()){
                continue;
            }
            for(int i=0; i<inputs.size();i++){
                if(!m.isElementOf(inputs.get(i), decl.getInputs().get(i))
                		&& !m.isInstanceOf(inputs.get(i), decl.getInputs().get(i))){
                    fits = false;
                    break;
                }
            }
            if(!fits){
                continue;
            }
            for(int i = 0; i < outputs.size(); i++){
                if(!m.isElementOf(outputs.get(i), decl.getOutputs().get(i))
                		&& !m.isInstanceOf(outputs.get(i),decl.getOutputs().get(i))){
                    fits = false;
                    break;
                }
            }
            if(fits){
                fitcount++;
            }
        }
        if(fitcount == 0){
            errors.add(blockid+">> Error at application of " + name + " with inputs " + app.getInputs() + " " + "and outputs "
                    + app.getOutputs() + ": It does not fit any declaration");
        }
        return errors.isEmpty();
    }

    public boolean addLink(String subject, String link, MegaModel m, String blockid) {
        if(!errors.isEmpty())
            return false;
        if(!(m.getInstanceOfMap().containsKey(subject) || m.getSubtypesMap().containsKey(subject)
             || m.getRelationshipDeclarationMap().containsKey(subject))){
            errors.add(blockid+">> Error at linking " + subject + ". Declaration/Instantiation is missing.");
        }
        if(link.contains("::")){
            String ns = link.split("::")[0];
            Optional<String> o = Optional.ofNullable(m.getNamespace(ns));
            if(o.isPresent()){
                link = link.replace(ns + "::", o.get() + "/");
            }else{
                errors.add(blockid+">> Error at linking " + subject + ". Namespace '" + ns + "' does not exist.");
            };
        }
        Set<Relation> dlinks = m.getRelationships().get("=");
        Set<Relation> blinks = m.getRelationships().get("~=");
        if(dlinks != null && dlinks.contains(new Relation(subject, link))){
            errors.add(blockid+">> Error at linking " + subject + " to " + link
                       + ". This informative link has already been assigned");
        }
        if(blinks != null && blinks.contains(new Relation(subject, link))){
            errors.add(blockid+">> Error at linking " + subject + " to " + link + ". This binding has already been assigned");
        }
        return errors.isEmpty();
    }

}
