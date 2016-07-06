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
			String filepath = getFilepathOfArguments(args);
			doCheck(filepath);
		}
		catch (EmptyFileNameException ex) {
			System.out.println("No filepath specified!");
		}
		catch (FileNotFoundException ex) {
			System.out.println("The File wasn't found on your harddrive!");
		} 
		catch (IOException ex) {
			System.out.println("Something went wrong with your file. Is it possibly emtpy?");
		}
	}
	
	private static String getFilepathOfArguments(String[] arguments) throws EmptyFileNameException{
		if (argumentsExists(arguments)) {
			return arguments[2];
		}
		throw new EmptyFileNameException();
	}

	private static boolean argumentsExists(String[] arguments) {
		return arguments.length>0 && arguments[0].contains("-f") && !arguments[2].isEmpty();
	}

	private static void doCheck(String filepath) throws FileNotFoundException, IOException {
		FileInputStream fileStream = new FileLoader().load(filepath);
		MegalibParser parser = new ParserGenerator().generate(fileStream);

		Listener result = walkOverSyntaxTree(parser.declaration());
		printResults(result);
	}

	private static Listener walkOverSyntaxTree(DeclarationContext ctx) {
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
