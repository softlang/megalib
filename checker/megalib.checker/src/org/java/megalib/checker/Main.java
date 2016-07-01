/**
 * 
 */
package org.java.megalib.checker;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.java.megalib.antlr.MegalibLexer;
import org.java.megalib.antlr.MegalibParser;
import org.java.megalib.antlr.MegalibParser.DeclartationContext;

/**
 * @author mmay, aemmerichs
 *
 */
public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MegalibLexer lexer = new MegalibLexer(new ANTLRInputStream("abc< Entity abcabc<abc#abc"));

	    CommonTokenStream token = new CommonTokenStream(lexer);	 

	    MegalibParser parser = new MegalibParser(token);
	 
	    DeclartationContext ctx = parser.declartation();

	    ParseTreeWalker treeWalker = new ParseTreeWalker();
	    Listener listener = new Listener();
	    treeWalker.walk(listener, ctx);
	    System.out.println("");
	    System.out.println(listener.store);
	    System.out.println(listener.functionStore);
	}
	
}
