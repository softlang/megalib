/**
 * 
 */
package org.java.megalib.checker.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

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
	
	
	private Queue<String> todos;
	private File root;
	
	private MegaModel model;
	
	
	public MegaModelLoader(){
		todos = new LinkedList<>();
		model = new MegaModel();
		loadPrelude();
		loadFile("Prelude.megal");
	}

	public MegaModel getModel(){
		return model;
	}
	
	private void loadPrelude() {
		File f = null;
		String data = "";
		try{
			f = new File("Prelude.megal");
			data = FileUtils.readFileToString(f);
		}catch(IOException e){
			System.err.println(e.getMessage());
			return;
		}
		model = loadString(data);
	}
	
	public void loadFile(String filepath) {
		File f = null;
		String data = "";
		try{
			f = new File(filepath);
			data = FileUtils.readFileToString(f);
		}catch(IOException e){
			model.addWarning(e.getMessage());
			return;
		}
		loadCompleteModelFrom(data,filepath);
	}
	
	public MegaModel loadString(String data){
	    try {
			return ((MegalibParserListener) parse(data,new MegalibParserListener(model))).getModel();
		} catch (WellFormednessException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	private void loadCompleteModelFrom(String data, String path){
		try {
			
			resolveImports(data,path);
			while(!todos.isEmpty()){
				String p = todos.poll();
				p = root.getAbsolutePath()+ p.replaceAll(".", "\\");
				String pdata = FileUtils.readFileToString(new File(p)); 
				model = ((MegalibParserListener) parse(pdata,new MegalibParserListener(model))).getModel();
				if(!model.getCriticalWarnings().isEmpty()){
					model.getCriticalWarnings().forEach(w -> System.err.println(w));
					throw new WellFormednessException("Resolve critical errors first"+(path.equals("")? " in "+path : ""));
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return;
		} catch (WellFormednessException e) {
			System.err.println(e.getMessage());
			return;
		}
	}

	public MegalibBaseListener parse(String data, MegalibBaseListener listener) throws WellFormednessException, IOException{
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
					throw new WellFormednessException("Syntactic errors exist : Fix them before further checks");
				}
			}
		}
		return listener;
	}
	
	private void resolveImports(String data, String path) throws WellFormednessException, IOException{
		List<Relation> imports = new LinkedList<>();
		Set<String> processed = new HashSet<>();
		Set<String> toProcess = new HashSet<>();
		
	    MegalibImportListener l = (MegalibImportListener) parse(data,new MegalibImportListener());
	    imports.addAll(l.getImports());
	    toProcess.addAll(l.getImports().parallelStream().map(r -> r.getObject()).collect(Collectors.toSet()));
	    toProcess.removeAll(processed);
	    processed.add(l.getName());
	    // Resolve module name to file path
	    int lvl = l.getName().split(".").length;
	    root = new File(path);
	    assert(root.exists());
	    // Get root folder
	    for(int i = 0; i<lvl; i++){
	    	root = root.getParentFile();
	    }      
	    while(!toProcess.isEmpty()){
	    	String p = toProcess.iterator().next();
	    	p = root.getAbsolutePath()+ p.replaceAll(".", "\\");
	    	String pdata = FileUtils.readFileToString(new File(p)); 
	    	l = (MegalibImportListener) parse(pdata,new MegalibImportListener());
	    	imports.addAll(l.getImports());
	    	toProcess.addAll(l.getImports().parallelStream().map(r -> r.getObject()).collect(Collectors.toSet()));
	    	toProcess.removeAll(processed);
	    	processed.add(l.getName());
	    }      
		
		while(!imports.isEmpty()){
			Set<String> subjects = imports.parallelStream().map(r -> r.getSubject()).collect(Collectors.toSet());
			Set<String> objects = imports.parallelStream().map(r -> r.getObject()).collect(Collectors.toSet());
			Set<String> diff = new HashSet<>(objects);
			diff.removeAll(subjects);
			todos.addAll(diff);
			imports.removeIf(r -> diff.contains(r.getObject()));
		}
		
	}
	
}
