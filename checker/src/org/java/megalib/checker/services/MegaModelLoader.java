/**
 * 
 */
package org.java.megalib.checker.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.FileUtils;
import org.java.megalib.models.MegaModel;

import main.antlr.techdocgrammar.MegalibParser;

/**
 * @author mmay, aemmerichs
 *
 */
public class MegaModelLoader {
	
	public static MegaModel createFromFile(String filepath) throws FileNotFoundException, IOException {
		return createFromString(FileUtils.readFileToString(new File(filepath)));
	}
	
	public static MegaModel createFromString(String data) throws IOException{
		
		ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
		return getListener(input).getModel();
	}
	
	public static Listener getListener(InputStream stream) throws IOException{
		MegalibParser parser = new ParserGenerator().generate(stream);
		ParseTreeWalker treeWalker = new ParseTreeWalker();
		Listener listener = new Listener();
		treeWalker.walk(listener, parser.declaration());
		return listener;
	}
	
}
