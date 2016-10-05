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

import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.FileUtils;
import org.java.megalib.models.MegaModel;

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
			e.printStackTrace();
			System.exit(1);
		}
		String folder = "";
		if(f.getParent()!=null)
			folder = f.getParent();
		MegaModel newModel = createFromString(data,folder);
		fuseModels(newModel);
		return initialModel;
	}
	
	public MegaModel createFromString(String data){
		MegaModel newModel = createFromString(data,"");
		fuseModels(newModel);
		return initialModel;
	}
	
	private MegaModel createFromString(String data, String folder){
		
		ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
		try {
			fuseModels(getListener(input).getModel());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		Set<String> importPaths = initialModel.getImports();
		for(String p : importPaths)
			importModel(folder+p);
		return initialModel;
	}

	public Listener getListener(InputStream stream) throws IOException{
		MegalibParser parser = new ParserGenerator().generate(stream);
		ParseTreeWalker treeWalker = new ParseTreeWalker();
		Listener listener = new Listener();
		treeWalker.walk(listener, parser.declaration());
		return listener;
	}
	
	private void importModel(String filepath) {
		if(loadedModules.contains(filepath)){
			return;
		}
		MegaModel importModel = createFromFile(filepath);
		fuseModels(importModel);
		loadedModules.add(filepath);
	}
	
	private void fuseModels(MegaModel importModel){
		importModel.getLinkMap().forEach((k,v)-> initialModel.addLinks(k, v));
		importModel.getSubtypesMap().forEach((k,v)-> initialModel.addSubtypeOf(k, v));
		importModel.getInstanceOfMap().forEach((k,v)->initialModel.addInstanceOf(k, v));
		importModel.getRelationshipInstanceMap()
			.forEach((n,set)->set
					.forEach(entry->initialModel.addRelationInstances(n, entry)));
		importModel.getRelationDeclarationMap()
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
