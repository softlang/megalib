/**
 * 
 */
package org.java.megalib.checker.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.FileUtils;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;

import main.antlr.techdocgrammar.MegalibBaseListener;
import main.antlr.techdocgrammar.MegalibLexer;
import main.antlr.techdocgrammar.MegalibParser;

public class MegaModelLoader {
	
	/**
	 * Collections that contain qualified names for processed (done) or remaining (todos) modules 
	 * in the import process.
	 */
	private Set<String> dones;
	private List<String> todos;
	/**
	 * A growing megamodel that starts with the prelude statements
	 */
	private MegaModel model;
	
	
	public MegaModelLoader(){
		dones = new HashSet<>();
		todos = new LinkedList<>();
		model = new MegaModel();
		loadFile("Prelude.megal");
	}
	
	public MegaModel getModel(){
		return model;
	}
	
	public void loadFile(String filepath) {
		File f = null;
		String data = "";
		try{
			f = new File(filepath);
			data = FileUtils.readFileToString(f);
		}catch(IOException e){
			System.err.println(e.getMessage());
			return;
		}
		loadStringFrom(data,filepath);
	}
	
	public MegaModel loadString(String data){
	    loadStringFrom(data,"");
	    return model;
	}

	
	private void loadStringFrom(String data, String path){
		try {
			
			resolveImports(data,path);
			//work on the stack
			MegaModel m = ((MegalibListener) parse(data,new MegalibListener(model))).getModel();
			if(!m.getCriticalWarnings().isEmpty()){
				m.getCriticalWarnings().forEach(w -> System.err.println(w));
				throw new Exception("Resolve critical errors first"+(path.equals("")? " in "+path : ""));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}
	}

	public MegalibBaseListener parse(String data, MegalibBaseListener listener) throws Exception{
		ByteArrayInputStream stream = new ByteArrayInputStream(data.getBytes());
		ANTLRInputStream antlrStream = new ANTLRInputStream(stream);
		MegalibLexer lexer = new MegalibLexer(antlrStream);
	    CommonTokenStream token = new CommonTokenStream(lexer);	 

	    MegalibParser parser = new MegalibParser(token);
	    parser.addErrorListener(new MegalibErrorListener());
		ParseTreeWalker treeWalker = new ParseTreeWalker();
		treeWalker.walk(listener, parser.declaration());
		for(ANTLRErrorListener el : parser.getErrorListeners()){
			if(el instanceof MegalibErrorListener){
				if(((MegalibErrorListener) el).getCount()>0){
					throw new Exception("Syntactic errors exist : Fix them before further checks");
				}
			}
		}
		return listener;
	}

	public List<String> getTodos() {
		return Collections.unmodifiableList(todos);
	}

	public void addTodo(String todo) {
		if(!dones.contains(todo))
			todos.add(todo);
	}
	
	public Set<String> getDones(){
		return Collections.unmodifiableSet(dones);
	}
	
	public void addDone(String done){
		dones.add(done);
	}
	
	private void resolveImports(String data, String path){
		List<Relation> imports = new LinkedList<>();
		Set<String> processed = new HashSet<>();
		try {
			MegalibImportListener l = (MegalibImportListener) parse(data,new MegalibImportListener());
			imports.addAll(l.getImports());
			processed.add(l.getName());
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
}
