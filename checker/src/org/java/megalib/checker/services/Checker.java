/**
 * 
 */
package org.java.megalib.checker.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.java.megalib.models.MegaModel;

import main.antlr.techdocgrammar.MegalibParser;
import main.antlr.techdocgrammar.MegalibParser.DeclarationContext;

/**
 * @author mmay, aemmerichs
 *
 */
public class Checker implements IChecker {
	
	ResultChecker checker;

	@Override
	public MegaModel checkFile(String filepath) throws FileNotFoundException, IOException {
		FileInputStream fileStream = new FileLoader().load(filepath);
		
		MegaModel result = getListener(fileStream).getModel();

		fileStream.close();
		checker = new ResultChecker(result);
		checker.doChecks();
		
		return result;
	}
	
	public Listener getListener(InputStream stream) throws IOException{
		MegalibParser parser = new ParserGenerator().generate(stream);
		return createListenerByTreeWalk(parser.declaration());
	}
	
	private static Listener createListenerByTreeWalk(DeclarationContext ctx) {
		ParseTreeWalker treeWalker = new ParseTreeWalker();
		Listener listener = new Listener();
		treeWalker.walk(listener, ctx);
		return listener;
	}
}
