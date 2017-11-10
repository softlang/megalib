package org.java.megalib.checker.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.java.megalib.models.Block;
import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;

public class Substitution {

    private MegaModel model;
    private Block block;
    private Map<String,Set<String>> substByGroup;

    public Substitution(MegaModel model, Map<String,Set<String>> substByGroup, Block block){
        this.model = model;
        this.block = block;
        this.substByGroup = substByGroup;
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
        copyReplaceInstances();
        copyReplaceRelNetwork();
        copyReplaceFunNetwork(true);
        copyReplaceFunNetwork(false);
        return model;
    }

	private void copyReplaceInstances() {
		substByGroup.forEach((key, set) -> set.forEach(v -> {
            if(!model.getInstanceOfMap().containsKey(v)){
                model.addInstanceOf(v, model.getInstanceOfMap().get(key),block);
            }
        }));
	}

    private void copyReplaceRelNetwork() {
    	HashMap<String,Set<Relation>> resultMap = new HashMap<>();
		for(String name : model.getRelationships().keySet()) {
			Set<Relation> resultRelations = new HashSet<>();
			for(Relation r : model.getRelationships().get(name)) {
				if(substByGroup.containsKey(r.getSubject())) {
					Set<Relation> tempresult = new HashSet<>();
					for(String by : substByGroup.get(r.getSubject())) {
						tempresult.add(new Relation(by, r.getObject()));
					}
					
					if(substByGroup.containsKey(r.getObject())) {
						//crossproduct
						for(Relation tr : tempresult) {
							for(String by : substByGroup.get(r.getObject())) {
								resultRelations.add(new Relation(tr.getSubject(), by));
							}
						}
					}else {
						resultRelations.addAll(tempresult);
					}
				}
				if(!substByGroup.containsKey(r.getSubject())&&substByGroup.containsKey(r.getObject())) {
					for(String by : substByGroup.get(r.getObject())) {
						resultRelations.add(new Relation(r.getSubject(), by));
					}
				}
			}
			resultMap.put(name, resultRelations);
		}
		for(String name : resultMap.keySet()) {
			for(Relation r : resultMap.get(name)) {
				model.addRelationInstance(name, r.getSubject(), r.getObject(), block);
			}
		}
	}
    
    private void copyReplaceFunNetwork(boolean isDec) {
    	HashMap<String,Set<Function>> resultMap = new HashMap<>();
    	
    	Map<String, Set<Function>> initMap = new HashMap<>();
		if(isDec) {
			model.getFunctionDeclarations().entrySet().stream().forEach(entry-> 
				initMap.put(entry.getKey(), entry.getValue().stream()
    					        .filter(f -> {
    					        	return new ArrayList<>(f.getInputs()).removeAll(substByGroup.keySet()) || new ArrayList<>(f.getOutputs()).removeAll(substByGroup.keySet());
    					        })
    					        .collect(Collectors.toSet())));
    	}else {
    		model.getFunctionApplications().entrySet().stream().forEach(entry-> 
				initMap.put(entry.getKey(), entry.getValue().stream()
					        .filter(f -> {
					        	return new ArrayList<>(f.getInputs()).removeAll(substByGroup.keySet()) || new ArrayList<>(f.getOutputs()).removeAll(substByGroup.keySet());
					        })
					        .collect(Collectors.toSet())));
    	}
		
    	for(String name : initMap.keySet()) {
    		Set<Function> resultFun = new HashSet<>();
    		for(Function f : initMap.get(name)) {
    			Set<Function> ftempFun = new HashSet<>();
    			ftempFun.add(f);
    			for(int i=0; i<f.getInputs().size();i++) {
    				String subst = f.getInputs().get(i);
    				if(substByGroup.containsKey(subst)) {
    					Set<Function> newTempFun = new HashSet<>();
    					for(String by : substByGroup.get(subst)) {
    						for(Function ftemp : ftempFun) {
    							List<String> tempInputs = new ArrayList<>(ftemp.getInputs());
    							tempInputs.set(i, by);
    							Function newftemp = new Function(tempInputs, ftemp.getOutputs(), isDec);
    							newTempFun.add(newftemp);
    						}
    					}
    					ftempFun.clear();
    					ftempFun.addAll(newTempFun);
    				}
    			}
    			for(int i=0; i<f.getOutputs().size();i++) {
    				String subst = f.getOutputs().get(i);
    				if(substByGroup.containsKey(subst)) {
    					Set<Function> newTempFun = new HashSet<>();
    					for(String by : substByGroup.get(subst)) {
    						for(Function ftemp : ftempFun) {
    							List<String> tempOutputs = new ArrayList<>(ftemp.getOutputs());
    							tempOutputs.set(i, by);
    							Function newftemp = new Function(ftemp.getInputs(), tempOutputs, isDec);
    							newTempFun.add(newftemp);
    						}
    					}
    					ftempFun.clear();
    					ftempFun.addAll(newTempFun);
    				}
    			}
    			resultFun.addAll(ftempFun);
    		}
    		resultMap.put(name, resultFun);
    		if(isDec)
    			resultMap.forEach((fname,set)-> set.forEach(f -> model.addFunctionDeclaration(fname, f.getInputs(), f.getOutputs(), block)));
    		else
    			resultMap.forEach((fname,set)-> set.forEach(f -> model.addFunctionApplication(fname, f.getInputs(), f.getOutputs(), block)));
    	}
    }

}
