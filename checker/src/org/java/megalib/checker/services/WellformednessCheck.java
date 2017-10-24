package org.java.megalib.checker.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;

public class WellformednessCheck {

    private MegaModel model;
    private List<String> warnings;

    public WellformednessCheck(MegaModel model){
        doChecks(model, false);
    }

    public WellformednessCheck(MegaModel model, boolean nocon){
        doChecks(model, nocon);
    }

    /**
     *
     * @param model: a MegaModel AST
     * @param nocon: Assign true to disable link validation.
     */
    private void doChecks(MegaModel model, boolean nocon) {
        this.model = model;
        warnings = Collections.synchronizedList(new LinkedList<>());
        checkNamingConventions();
        instanceChecks();
        subtypeChecks();
        partOfCheck();
        fragmentPartOfCheck();
        partOfFragmentCheck();
        transientIsInputOrOutput();
        cyclicSubtypingChecks();
        cyclicRelationChecks("subsetOf");
        cyclicRelationChecks("partOf");
        cyclicRelationChecks("conformsTo");
        model.getFunctionDeclarations().keySet().forEach(f -> checkFunction(f));
        if(!nocon){
            checkLinks();
        }
    }

    private void checkNamingConventions() {
        model.getSubtypesMap().keySet().parallelStream()
             .filter(name -> name.substring(0, 1).equals(name.substring(0, 1).toLowerCase()))
             .forEach(name -> warnings.add("The Type ID " + name + " must begin with an uppercase letter"));

        model.getInstanceOfMap().entrySet().parallelStream().forEach(entry -> {
            String name = entry.getKey().replace("?", "");
            if(model.isInstanceOf(entry.getKey(), "Artifact") || entry.getValue().equals("Function")){
                if(name.substring(0, 1).equals(name.substring(0, 1).toUpperCase())){
                    warnings.add("The " + entry.getValue() + " ID " + entry.getKey()
                                 + " must begin with a lower case letter.");
                }
            }else{
                if(!entry.getValue().equals("Link")
                		&& name.substring(0, 1).equals(name.substring(0, 1).toLowerCase())){
                    warnings.add("The " + entry.getValue() + " ID " + name
                                 + " must begin with an upper case letter.");
                }
            }
        });

    }

    private void instanceChecks() {
        Map<String,String> map = model.getInstanceOfMap();

        map.keySet().stream().filter(e -> !e.startsWith("?")).forEach(e -> concreteInstanceChecks(e));
        map.keySet().stream().filter(e -> model.isInstanceOf(e, "Artifact")).forEach(e -> {
            Optional<Boolean> o = Optional.ofNullable(model.getRelationships().get("elementOf"))
                                          .map(s -> s.parallelStream().noneMatch(r -> r.getSubject().equals(e)));
            Optional<Boolean> o2 = Optional.ofNullable(model.getRelationships().get("manifestsAs"))
                    .map(s -> s.parallelStream().noneMatch(r -> r.getSubject().equals(e) && r.getObject().equals("Folder")));
            if(o.orElse(true) && o2.orElse(true)){
                warnings.add("Language missing for artifact " + e);
            }
        });
    }

    private void concreteInstanceChecks(String e) {
    	if(model.isInstanceOf(e, "Link")) {
			return;
		}
        Map<String,String> map = model.getInstanceOfMap();
        if(map.get(e).equals("Technology")){
            warnings.add("State a specific subtype of Technology for " + e + ".");
        }
        if(model.isInstanceOf(e, "Technology")){
            Optional<?> o = Optional.ofNullable(model.getRelationships().get("uses"))
                    .filter(set -> set.parallelStream()
                                      .anyMatch(r -> r.getSubject().equals(e)
                                                      && model.isInstanceOf(r.getObject(), "Language")));
            if(!o.isPresent())
               warnings.add("State a used language for " + e);
        }
        if(map.get(e).equals("Language")){
            warnings.add("State a specific subtype of Language for " + e + ".");
        }

        if(model.isInstanceOf(e, "Artifact")){
                long bindcount = Optional.ofNullable(model.getRelationships().get("~="))
                                         .map(set -> set.parallelStream().filter(r -> r.getSubject().equals(e)).count())
                                         .orElse(Integer.toUnsignedLong(0));
                if(bindcount < 1){
                    warnings.add("Binding missing for Artifact " + e + ".");
                }
        }else{
            Optional<Set<Relation>> o = Optional.ofNullable(model.getRelationships().get("="))
                                                .filter(set -> set.parallelStream()
                                                                  .anyMatch(r -> r.getSubject().equals(e)
                                                                                 || model.isInstanceOf(e, "Function")));
            if(!o.isPresent()){
                warnings.add("Link missing for entity " + e + ".");
            }
            o = Optional.ofNullable(model.getRelationships().get("~="))
                        .filter(set -> set.parallelStream().anyMatch(r -> r.getSubject().equals(e)));
            if(o.isPresent()){
                warnings.add("Do not bind entities other than artifacts. See " + e + ".");
            }
        }
    }

    private void subtypeChecks() {
        model.getSubtypesMap().forEach((k, v) -> {
            if(model.getRelationships().get("=").parallelStream().noneMatch(r -> r.getSubject().equals(k))){
                warnings.add("Link missing for subtype " + k);
            }
        });
    }

    /**
     * Every subject of a part-hood relationship cannot be subject of another
     * part-hood relationship.
     */
    private void partOfCheck() {
        if(!model.getRelationships().containsKey("partOf")) {
			return;
		}
        Set<Relation> partOfs = model.getRelationships().get("partOf");
        Set<String> subjects = new HashSet<>();
        for(Relation p : partOfs){
            if(!subjects.contains(p.getSubject())){
                subjects.add(p.getSubject());
            }else{
                warnings.add("Error at " + p.getSubject() + " partOf " + p.getObject() + ": " + "" + p.getSubject()
                             + " is already part of another composite.");
            }
        }
    }

    /**
     * All artifacts that manifest as fragments are part of another artifact
     * that is not a fragment.
     */
    private void fragmentPartOfCheck() {
        if(!model.getRelationships().containsKey("manifestsAs")) {
			return;
		}
        List<String> fragments = model.getRelationships().get("manifestsAs").parallelStream()
                                      .filter(r -> r.getObject().equals("Fragment")).map(r -> r.getSubject())
                                      .collect(Collectors.toList());
        for(String f : fragments){
            if(model.getRelationships().get("partOf").parallelStream()
                    .noneMatch(r -> r.getSubject().equals(f))){
                warnings.add("Composite missing for fragment " + f);
            }
        }
    }

    /**
     * All composite fragments do not have any part that is not a fragment.
     */
    private void partOfFragmentCheck() {
        if(!model.getRelationships().containsKey("manifestsAs")) {
			return;
		}
        Set<String> fset = model.getRelationships().get("manifestsAs").parallelStream()
                                .filter(r -> r.getObject().equals("Fragment")).map(r -> r.getSubject())
                                .collect(Collectors.toSet());
        for(String f : fset){
            Set<String> parts = model.getRelationships().get("partOf").parallelStream()
                                     .filter(r -> r.getObject().equals(f)).map(r -> r.getSubject())
                                     .collect(Collectors.toSet());
            Set<String> diff = new HashSet<>();
            diff.addAll(parts);
            diff.removeAll(fset);
            if(!parts.isEmpty() && !diff.isEmpty()){
                warnings.add("The following parts of the fragment " + f + " are not fragments and are thus invalid:"
                             + diff.toString());
            }
        }
    }

    private void transientIsInputOrOutput() {
        if(!model.getRelationships().containsKey("manifestsAs")) {
			return;
		}

        Set<String> tset = model.getRelationships().get("manifestsAs").parallelStream()
                                .filter(r -> r.getObject().equals("Transient") && !isPart(r.getSubject()))
                                .map(r -> r.getSubject()).collect(Collectors.toSet());
        Set<String> iovalues = new HashSet<>();
        Set<Function> pairs = new HashSet<>();
        model.getFunctionApplications().forEach((k, v) -> pairs.addAll(v));
        for(Function p : pairs){
            iovalues.addAll(p.getInputs());
            iovalues.addAll(p.getOutputs());
        }
        tset.removeAll(iovalues);
        if(!tset.isEmpty()){
            warnings.add("The following transients are neither input nor output of a function application: "
                         + tset.toString());
        }
    }

    private boolean isPart(String t) {
        if(!model.getRelationships().containsKey("partOf")) {
			return false;
		}
        Set<Relation> partOfs = model.getRelationships().get("partOf");
        return !partOfs.parallelStream().noneMatch(r -> r.getSubject().equals(t));
    }

    private void cyclicSubtypingChecks() {
        Map<String,String> map = new HashMap<>();
        map.putAll(model.getSubtypesMap());
        while(true){
            Set<String> subtypeset = map.keySet();
            Set<String> typeset = map.values().stream().collect(Collectors.toSet());
            Set<String> diff = new HashSet<>(subtypeset);
            diff.removeAll(typeset);
            if(diff.isEmpty()){
                break;
            }
            for(String t : diff){
                map.remove(t);
            }
        }
        if(!map.isEmpty()){
            warnings.add("Cycles exist in the subtyping hierarchy within the following entries :" + map);
        }
    }

    private void cyclicRelationChecks(String name) {
        if(!model.getRelationships().containsKey(name)) {
			return;
		}
        Set<Relation> rels = model.getRelationships().get(name);
        Set<String> subjects;
        Set<String> objects;

        while(true){
            subjects = rels.parallelStream().map(r -> r.getSubject()).collect(Collectors.toSet());
            objects = rels.parallelStream().map(r -> r.getObject()).collect(Collectors.toSet());
            Set<String> diff = new HashSet<>(subjects);
            diff.removeAll(objects);
            if(diff.isEmpty()){
                break;
            }
            rels = rels.parallelStream().filter(r -> !diff.contains(r.getSubject())).collect(Collectors.toSet());
        }
        Set<String> result = new HashSet<>(objects);
        result.retainAll(subjects);
        if(!result.isEmpty()){
            warnings.add("Cycles exist concerning the relationship " + name + " involving the following entities :"
                         + result);
        }

    }

    private void checkFunction(String name) {
        // check implements existence
        Set<Relation> implementsSet = model.getRelationships().get("implements");
        if(null == implementsSet){
            warnings.add("The function " + name + " is not implemented. Please state what implements it.");
        }else{
            Set<Relation> fset = implementsSet.parallelStream().filter(r -> r.getObject().equals(name))
                                              .collect(Collectors.toSet());
            if(fset.isEmpty()){
                warnings.add("The function " + name + " is not implemented. Please state what implements it.");
            }
        }
        // check application existence
        if(!model.getFunctionApplications().containsKey(name)){
            warnings.add("The function " + name + " is not applied yet. Please state an actual application.");
        }
    }

    private void checkLinks() {
        Set<Relation> links = model.getRelationships().get("=");
        if(model.getRelationships().containsKey("~=")){
            links.addAll(model.getRelationships().get("~="));
        }
        links.parallelStream().forEach(r -> {
        	if(!Linker.isResolvable(r.getObject(), model)){
            	warnings.add("Cannot resolve link to "+r.getSubject()+": "+r.getObject());
            }
        });
    }



    public List<String> getWarnings() {
        return warnings;
    }

}