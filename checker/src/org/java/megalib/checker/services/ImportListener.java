package org.java.megalib.checker.services;

import java.util.HashSet;
import java.util.Set;

import org.java.megalib.models.Relation;

import main.antlr.techdocgrammar.MegalibBaseListener;
import main.antlr.techdocgrammar.MegalibParser.ImportsContext;
import main.antlr.techdocgrammar.MegalibParser.ModuleContext;

public class ImportListener extends MegalibBaseListener{

	private Set<Relation> imports;
	private String name;
	
	public ImportListener(){
		imports = new HashSet<>();
		name = "";
	}
	
	@Override
	public void enterModule(ModuleContext ctx) {
		String mname = ctx.getChild(1).getText();
		name = mname;
	}
	
	@Override
	public void enterImports(ImportsContext context) {
		imports.add(new Relation(name,context.getChild(1).getText()));
	}
	
	public Set<Relation> getImports(){
		return imports;
	}
	
	public String getName(){
		return name;
	}
}
