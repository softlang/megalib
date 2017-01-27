package org.java.megalib.checker.services;

import java.util.Set;

import org.java.megalib.models.MegaModel;

public class Substitution {

    private MegaModel model;

    public Substitution(MegaModel model) {
        this.model = model;
    }

    public MegaModel substituteGroup(Set<String[]> substSet) {
        return model;
    }

    public MegaModel substitute(String abstractE, String by) {
        if (model.isInstanceOf(abstractE, "Language")) {
            substituteLanguage(abstractE, by);
        }else if(model.isInstanceOf(abstractE, "Artifact")){
            substituteArtifact(abstractE, by);
        }else if(model.isInstanceOf(abstractE, "Technology")){
            substituteTechnology(abstractE, by);
        }else if(model.isInstanceOf(abstractE, "System")){
            substituteSystem(abstractE, by);
        }

        return model;
    }

    private void substituteSystem(String abstractE, String by) {
        // TODO Auto-generated method stub

    }

    private void substituteTechnology(String abstractE, String by) {
        // TODO Auto-generated method stub

    }

    private void substituteArtifact(String abstractE, String by) {
        // TODO Auto-generated method stub

    }

    private void substituteLanguage(String abstractE, String by) {
        // TODO Auto-generated method stub

    }


}
