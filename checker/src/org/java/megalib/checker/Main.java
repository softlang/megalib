/**
 * 
 */
package org.java.megalib.checker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.java.megalib.antlr.MegalibParser;
import org.java.megalib.antlr.MegalibParser.DeclarationContext;
import org.java.megalib.checker.services.FileLoader;
import org.java.megalib.checker.services.Listener;
import org.java.megalib.checker.services.ParserGenerator;

/**
 * @author mmay@uni-koblenz.de, aemmerichs@uni-koblenz.de
 *
 */
public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			FileInputStream fileStream = new FileLoader().load("./TestFiles/testFile.megal");
			MegalibParser parser = new ParserGenerator().generate(fileStream);

		    Listener result = check(parser.declaration());
		    printResults(result);
		} 
		catch (FileNotFoundException ex) {
			
		} 
		catch (IOException ex) {
			
		}
	}

	private static Listener check(DeclarationContext ctx) {
		ParseTreeWalker treeWalker = new ParseTreeWalker();
		Listener listener = new Listener();
		treeWalker.walk(listener, ctx);
		return listener;
	}

	private static void printResults(Listener listener) {
		System.out.println("");
		System.out.println(listener.entities);
		System.out.println(listener.relations);
		System.out.println(listener.objects);
		System.out.println(listener.functions);
	}
	
}
