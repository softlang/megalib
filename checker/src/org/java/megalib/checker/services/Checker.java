/**
 * 
 */
package org.java.megalib.checker.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.java.megalib.antlr.MegalibParser;
import org.java.megalib.antlr.MegalibParser.DeclarationContext;
import org.java.megalib.models.MegaModel;

/**
 * @author mmay, aemmerichs
 *
 */
public class Checker implements IChecker {

	@Override
	public MegaModel doCheck(String filepath) throws FileNotFoundException, IOException {
		FileInputStream fileStream = new FileLoader().load(filepath);
		MegalibParser parser = new ParserGenerator().generate(fileStream);
		Listener result = walkOverSyntaxTree(parser.declaration());

		fileStream.close();
		return result.getModel();
	}
	
	private static Listener walkOverSyntaxTree(DeclarationContext ctx) {
		ParseTreeWalker treeWalker = new ParseTreeWalker();
		Listener listener = new Listener();
		treeWalker.walk(listener, ctx);
		return listener;
	}
}
