/**
 * 
 */
package org.java.megalib.checker.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.FileUtils;
import org.java.megalib.models.MegaModel;

import main.antlr.techdocgrammar.MegalibLexer;
import main.antlr.techdocgrammar.MegalibParser;

/**
 * @author mmay, aemmerichs
 *
 */
public class MegaModelLoader {
	
	/**
	 * A set containing the qualified names of all loaded models
	 */
	private Set<String> loadedModules;
	/**
	 * A growing megamodel that starts with the prelude statements
	 */
	private MegaModel initialModel;
	
	public MegaModelLoader(){
		loadedModules = new HashSet<String>();
		initialModel = new MegaModel();
		initialModel = createFromFile("Prelude.megal");
	}
	
	
	public MegaModel createFromFile(String filepath) {
		File f = null;
		String data = "";
		try{
			f = new File(filepath);
			data = FileUtils.readFileToString(f);
		}catch(IOException e){
			System.err.println(e.getMessage());
			return null;
		}
		String folder = "";
		if(f.getParent()!=null)
			folder = f.getParent();
		return createFromString(data,folder);
	}
	
	public MegaModel createFromString(String data){
		return createFromString(data,"");
	}

	
	private MegaModel createFromString(String data, String folder){
		
		ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
		try {
			MegaModel m = getListener(input).getModel();
			processImports(m);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return null;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
		Set<String> importPaths = initialModel.getImports();
		for(String p : importPaths)
			importModel(folder+p);
		return initialModel;
	}

	public MegalibListener getListener(InputStream stream) throws Exception{
		ANTLRInputStream antlrStream = new ANTLRInputStream(stream);
		MegalibLexer lexer = new MegalibLexer(antlrStream);
	    CommonTokenStream token = new CommonTokenStream(lexer);	 

	    MegalibParser parser = new MegalibParser(token);
	    parser.addErrorListener(new MegalibErrorListener());
		ParseTreeWalker treeWalker = new ParseTreeWalker();
		MegalibListener listener = new MegalibListener();
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
	
	private void importModel(String filepath) {
		if(loadedModules.contains(filepath)){
			return;
		}
		MegaModel importModel = createFromFile(filepath);
		processImports(importModel);
		loadedModules.add(filepath);
	}
	
	private void processImports(MegaModel importModel){
		importModel.getLinkMap().forEach((k,v)-> initialModel.addLinks(k, v));
		importModel.getSubtypesMap().forEach((k,v)-> initialModel.addSubtypeOf(k, v));
		importModel.getInstanceOfMap().forEach((k,v)->initialModel.addInstanceOf(k, v));
		importModel.getRelationshipInstanceMap()
			.forEach((n,set)->set
					.forEach(entry->initialModel.addRelationInstances(n, entry)));
		importModel.getRelationshipDeclarationMap()
			.forEach((n,set)->set
					.forEach(entry -> initialModel.addRelationDeclaration(n, entry)));
		importModel.getFunctionDeclarations()
			.forEach((n,set)->set
					.forEach(entry -> initialModel.addFunctionDeclaration(n, entry)));
		importModel.getFunctionInstances()
			.forEach((n,set)->set
					.forEach(entry -> initialModel.addFunctionInstance(n, entry)));
	}
	
}
