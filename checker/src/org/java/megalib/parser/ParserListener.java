package org.java.megalib.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;
import org.java.megalib.checker.services.Substitution;
import org.java.megalib.checker.services.TypeCheck;
import org.java.megalib.models.MegaModel;

import main.antlr.techdocgrammar.MegalibBaseListener;
import main.antlr.techdocgrammar.MegalibParser.FunctionDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.FunctionInstanceContext;
import main.antlr.techdocgrammar.MegalibParser.InstanceDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.LinkContext;
import main.antlr.techdocgrammar.MegalibParser.RelationDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.RelationInstanceContext;
import main.antlr.techdocgrammar.MegalibParser.SubstitutionContext;
import main.antlr.techdocgrammar.MegalibParser.SubstitutionGroupContext;
import main.antlr.techdocgrammar.MegalibParser.SubtypeDeclarationContext;

public class ParserListener extends MegalibBaseListener {
    private MegaModel model;
    private TypeCheck typeCheck;
    private Set<String[]> substSet;

    public ParserListener(MegaModel m) {
        model = m;
        typeCheck = new TypeCheck();
    }

    @Override
    public void enterSubstitutionGroup(SubstitutionGroupContext ctx) {
        substSet = new HashSet<>();
    }

    @Override
    public void exitSubstitutionGroup(SubstitutionGroupContext ctx) {
        new Substitution(model).substituteGroup(substSet);
        substSet = null;
    }

    @Override
    public void enterSubstitution(SubstitutionContext ctx) {
        String subject = ctx.getChild(0).getText();
        String object = ctx.getChild(2).getText();
        String[] s = {subject, object};
        substSet.add(s);
    }

    @Override
    public void enterSubtypeDeclaration(SubtypeDeclarationContext context) {
        String derivedType = context.getChild(0).getText();
        String superType = context.getChild(2).getText();
        if (typeCheck.addSubtypeOf(derivedType, superType, model)) {
            model.addSubtypeOf(derivedType, superType);
        }

        String link = context.getChild(5).getText();
        if (typeCheck.addLink(derivedType, link, model)) {
            model.addLink(derivedType, link.substring(1, link.length() - 1));

        }
    }

    @Override
    public void enterInstanceDeclaration(InstanceDeclarationContext context) {
        Iterator<ParseTree> it = context.children.iterator();
        String instance = it.next().getText();
        it.next(); // skip colon
        String type = it.next().getText();
        if (typeCheck.addInstanceOf(instance, type, model)) {
            model.addInstanceOf(instance, type);
        }
        while(it.next().getText().equals(";")){
            String relation = it.next().getText();
            String object = it.next().getText();
            if (relation.equals("=")) {
                String link = object.substring(1, object.length() - 1);
                if (typeCheck.addLink(instance, link, model)) {
                    model.addLink(instance, link);
                }
            } else {
                if (typeCheck.addRelationInstance(relation, instance, object, model)) {
                    model.addRelationInstances(relation, instance, object);
                }
            }
        }
    }

    @Override
    public void enterRelationDeclaration(RelationDeclarationContext context) {
        Iterator<ParseTree> it = context.children.iterator();
        String relation = it.next().getText();
        it.next(); // skip <
        String type1 = it.next().getText();
        it.next(); // skip #
        String type2 = it.next().getText();
        if (typeCheck.addRelationDeclaration(relation, type1, type2, model)) {
            model.addRelationDeclaration(relation, type1, type2);
        }
        while(it.next().getText().equals(";")){
            it.next(); // skip =
            String link = it.next().getText();
            if(typeCheck.addLink(relation, link, model)){
                model.addLink(relation, link);
            }
        }
    }

    @Override
    public void enterRelationInstance(RelationInstanceContext context) {
        Iterator<ParseTree> it = context.children.iterator();
        String subject = it.next().getText();
        String relation = it.next().getText();
        String object = it.next().getText();
        if (typeCheck.addRelationInstance(relation, subject, object, model)) {
            model.addRelationInstances(relation, subject, object);
        }

        while(it.next().getText().equals(";")){
            relation = it.next().getText();
            object = it.next().getText();
            if (relation.equals("=")) {
                String link = object.substring(1, object.length() - 1);
                if (typeCheck.addLink(subject, link, model)) {
                    model.addLink(subject, link);
                }
            } else {
                if (typeCheck.addRelationInstance(relation, subject, object, model)) {
                    model.addRelationInstances(relation, subject, object);
                }
            }
        }
    }

    @Override
    public void enterFunctionDeclaration(FunctionDeclarationContext context) {
        String functionName = context.getChild(0).getText();
        List<String> parameterTypes = new ArrayList<>();
        List<String> returnTypes = new ArrayList<>();

        boolean parameter = true;
        for (int childIndex = 2; childIndex < context.getChildCount(); childIndex++) {
            if (!context.getChild(childIndex).getText().equals("#") && parameter == false
                    && !context.getChild(childIndex).getText().equals("->")) {
                returnTypes.add(context.getChild(childIndex).getText());
            }

            if (!context.getChild(childIndex).getText().equals("#") && parameter == true
                    && !context.getChild(childIndex).getText().equals("->")) {
                parameterTypes.add(context.getChild(childIndex).getText());
            }

            if (context.getChild(childIndex).getText().equals("->")) {
                parameter = false;
            }
        }

        if (typeCheck.addFunctionDeclaration(functionName, parameterTypes, returnTypes, model)) {
            model.addFunctionDeclaration(functionName, parameterTypes, returnTypes);
        }
    }

    @Override
    public void enterFunctionInstance(FunctionInstanceContext context) {
        String functionName = context.getChild(0).getText();
        List<String> outputs = new ArrayList<>();
        List<String> inputs = new ArrayList<>();

        boolean parameter = true;
        for (int childIndex = 2; childIndex < context.getChildCount(); childIndex++) {
            if (!context.getChild(childIndex).getText().equals(",") && parameter == false
                    && !context.getChild(childIndex).getText().equals("|->")
                    && !context.getChild(childIndex).getText().equals("(")
                    && !context.getChild(childIndex).getText().equals(")")) {
                outputs.add(context.getChild(childIndex).getText());
            }

            if (!context.getChild(childIndex).getText().equals(",") && parameter == true
                    && !context.getChild(childIndex).getText().equals("|->")
                    && !context.getChild(childIndex).getText().equals("(")
                    && !context.getChild(childIndex).getText().equals(")")) {
                inputs.add(context.getChild(childIndex).getText());
            }

            if (context.getChild(childIndex).getText().equals("|->")) {
                parameter = false;
            }
        }

        if (typeCheck.addFunctionApplication(functionName, inputs, outputs, model)) {
            model.addFunctionApplication(functionName, inputs, outputs);
        }
    }

    @Override
    public void enterLink(LinkContext context) {
        Iterator<ParseTree> it = context.children.iterator();
        String subject = it.next().getText();
        it.next(); // skip =
        String link = it.next().getText();
        link = link.substring(1, link.length() - 1);
        if(typeCheck.addLink(subject, link, model)){
            model.addLink(subject, link);
        }
        while(it.next().getText().equals(";")){
            String relation = it.next().getText();
            String object = it.next().getText();
            if(relation.equals("=")){
                link = object.substring(1, object.length() - 1);
                if(typeCheck.addLink(subject, link, model)){
                    model.addLink(subject, link);
                }
            }else{
                if(typeCheck.addRelationInstance(relation, subject, object, model)){
                    model.addRelationInstances(relation, subject, object);
                }
            }
        }
    }

    public MegaModel getModel() {
        return model;
    }

    public List<String> getTypeErrors() {
        return typeCheck.getErrors();
    }
}