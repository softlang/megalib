package org.java.megalib.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;
import org.java.megalib.checker.services.Substitution;
import org.java.megalib.checker.services.SubstitutionCheck;
import org.java.megalib.checker.services.TypeCheck;
import org.java.megalib.models.MegaModel;

import main.antlr.techdocgrammar.MegalibBaseListener;
import main.antlr.techdocgrammar.MegalibParser.FunctionApplicationContext;
import main.antlr.techdocgrammar.MegalibParser.FunctionDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.InstanceDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.RelationDeclarationContext;
import main.antlr.techdocgrammar.MegalibParser.RelationInstanceContext;
import main.antlr.techdocgrammar.MegalibParser.SubstitutionContext;
import main.antlr.techdocgrammar.MegalibParser.SubstitutionGroupContext;
import main.antlr.techdocgrammar.MegalibParser.SubtypeDeclarationContext;

public class ParserListener extends MegalibBaseListener {
    private MegaModel model;
    private TypeCheck typeCheck;
    private Map<String,Set<String>> substByGroup;
    private SubstitutionCheck substCheck;

    public ParserListener(MegaModel m) {
        model = m;
        typeCheck = new TypeCheck();
        substCheck = new SubstitutionCheck();
        substByGroup = new HashMap<>();
    }

    @Override
    public void enterSubstitution(SubstitutionContext ctx) {
        String s = ctx.getChild(0).getText();
        String o = ctx.getChild(2).getText();
        Set<String> set;
        if(substByGroup.containsKey(o)){
            set = substByGroup.get(o);
        }else{
            set = new HashSet<>();
        }
        set.add(s);
        substByGroup.put(o, set);

    }

    @Override
    public void exitSubstitutionGroup(SubstitutionGroupContext ctx) {
        if(substCheck.substituteGroup(substByGroup, model)){
            model = new Substitution(model, substByGroup).substituteGroup();
        }
        substCheck.getErrors().forEach(e -> typeCheck.addError(e));
        substByGroup.clear();
    }

    /*
     * @Override public void enterNamespace(NamespaceContext ctx) {
     * System.out.println(ctx.getChild(0).getText() + ctx.getChild(1).getText()
     * + ctx.getChild(2).getText());
     * if(typeCheck.addNamespace(ctx.getChild(0).getText(),
     * ctx.getChild(3).getText(), model)){
     * model.addNamespace(ctx.getChild(0).getText(), ctx.getChild(3).getText());
     * } }
     */
    @Override
    public void enterSubtypeDeclaration(SubtypeDeclarationContext context) {
        Iterator<ParseTree> it = context.children.iterator();
        String sub = it.next().getText();
        it.next(); // skip '<'
        String t = it.next().getText();
        if (typeCheck.addSubtypeOf(sub, t, model)) {
            model.addSubtypeOf(sub, t);
        }
        while(it.next().getText().equals(";")){
            ParseTree r = it.next();
            assert (r.getChildCount() == 2);
            String p = r.getChild(0).getText();
            String l = r.getChild(1).getText();
            if(typeCheck.addRelationInstance(p, sub, l, model)){
                model.addRelationInstance(p, sub, l);
            }
        }
    }

    @Override
    public void enterInstanceDeclaration(InstanceDeclarationContext context) {
        Iterator<ParseTree> it = context.children.iterator();
        String s = it.next().getText();
        it.next(); // skip colon
        String t = it.next().getText();
        if(typeCheck.addInstanceOf(s, t, model)){
            model.addInstanceOf(s, t);
        }
        while(it.next().getText().equals(";")){
            ParseTree r = it.next();
            assert (r.getChildCount() == 2);
            String p = r.getChild(0).getText();
            String o = r.getChild(1).getText();
            if(p.startsWith("^")){
                p = p.substring(1);
                if(typeCheck.addRelationInstance(p, o, s, model)){
                    model.addRelationInstance(p, o, s);
                }
            }else{
                if(typeCheck.addRelationInstance(p, s, o, model)){
                    model.addRelationInstance(p, s, o);
                }
            }

        }
    }

    @Override
    public void enterRelationDeclaration(RelationDeclarationContext context) {
        Iterator<ParseTree> it = context.children.iterator();
        String r = it.next().getText();
        it.next(); // skip <
        String t1 = it.next().getText();
        it.next(); // skip #
        String t2 = it.next().getText();
        if (typeCheck.addRelationDeclaration(r, t1, t2, model)) {
            model.addRelationDeclaration(r, t1, t2);
        }
        while(it.next().getText().equals(";")){
            ParseTree rule = it.next();
            assert (rule.getChildCount() == 2);
            String sym = rule.getChild(0).getText();
            String l = rule.getChild(1).getText();
            if(typeCheck.addRelationInstance(sym, r, l, model)){
                model.addRelationInstance(sym, r, l);
            }
        }
    }

    @Override
    public void enterRelationInstance(RelationInstanceContext context) {
        Iterator<ParseTree> it = context.children.iterator();
        String s = it.next().getText();
        ParseTree rule = it.next();
        assert (rule.getChildCount() == 2);
        String p = rule.getChild(0).getText();
        String o = rule.getChild(1).getText();
        if(p.startsWith("^")){
            p = p.substring(1);
            if(typeCheck.addRelationInstance(p, o, s, model)){
                model.addRelationInstance(p, o, s);
            }
        }else{
            if(typeCheck.addRelationInstance(p, s, o, model)){
                model.addRelationInstance(p, s, o);
            }
        }
        while(it.next().getText().equals(";")){
            rule = it.next();
            assert (rule.getChildCount() == 2);
            p = rule.getChild(0).getText();
            o = rule.getChild(1).getText();
            if(p.startsWith("^")){
                p = p.substring(1);
                if(typeCheck.addRelationInstance(p, o, s, model)){
                    model.addRelationInstance(p, o, s);
                }
            }else{
                if(typeCheck.addRelationInstance(p, s, o, model)){
                    model.addRelationInstance(p, s, o);
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
        for(int childIndex = 2; childIndex < context.getChildCount() - 1; childIndex++){
            if(!context.getChild(childIndex).getText().equals("#") && parameter
                    && !context.getChild(childIndex).getText().equals("->")) {
                parameterTypes.add(context.getChild(childIndex).getText());
            }

            if (context.getChild(childIndex).getText().equals("->")) {
                parameter = false;
            }

            if(!context.getChild(childIndex).getText().equals("#") && !parameter
                    && !context.getChild(childIndex).getText().equals("->")){
                returnTypes.add(context.getChild(childIndex).getText());
            }
        }

        if (typeCheck.addFunctionDeclaration(functionName, parameterTypes, returnTypes, model)) {
            model.addFunctionDeclaration(functionName, parameterTypes, returnTypes);
        }
    }

    @Override
    public void enterFunctionApplication(FunctionApplicationContext context) {
        String functionName = context.getChild(0).getText();
        List<String> outputs = new ArrayList<>();
        List<String> inputs = new ArrayList<>();

        boolean parameter = true;
        for(int childIndex = 2; childIndex < context.getChildCount() - 1; childIndex++){
            if(!context.getChild(childIndex).getText().equals(",") && parameter
                    && !context.getChild(childIndex).getText().equals("|->")
                    && !context.getChild(childIndex).getText().equals("(")
                    && !context.getChild(childIndex).getText().equals(")")) {
                inputs.add(context.getChild(childIndex).getText());
            }

            if (context.getChild(childIndex).getText().equals("|->")) {
                parameter = false;
            }

            if(!context.getChild(childIndex).getText().equals(",") && !parameter
                    && !context.getChild(childIndex).getText().equals("|->")
                    && !context.getChild(childIndex).getText().equals("(")
                    && !context.getChild(childIndex).getText().equals(")")){
                outputs.add(context.getChild(childIndex).getText());
            }
        }

        if (typeCheck.addFunctionApplication(functionName, inputs, outputs, model)) {
            model.addFunctionApplication(functionName, inputs, outputs);
        }
    }

    public MegaModel getModel() {
        return model;
    }

    public List<String> getTypeErrors() {
        return typeCheck.getErrors();
    }
}