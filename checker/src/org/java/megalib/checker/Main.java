/**
 * 
 */
package org.java.megalib.checker;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.java.megalib.antlr.MegalibLexer;
import org.java.megalib.antlr.MegalibParser;
import org.java.megalib.antlr.MegalibParser.DeclarationContext;
import org.java.megalib.checker.services.Listener;

/**
 * @author mmay@uni-koblenz.de, aemmerichs@uni-koblenz.de
 *
 */
public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MegalibLexer lexer = new MegalibLexer(new ANTLRInputStream("abc<Entity;aaa<abc;of<abc#abc;karl:abc;joe:aaa;of<aaa#aaa;karl of joe;"));

	    CommonTokenStream token = new CommonTokenStream(lexer);	 

	    MegalibParser parser = new MegalibParser(token);
	 
	    DeclarationContext ctx = parser.declaration();

	    ParseTreeWalker treeWalker = new ParseTreeWalker();
	    Listener listener = new Listener();
	    treeWalker.walk(listener, ctx);
	    
	    System.out.println("");
	    System.out.println(listener.entities);
	    System.out.println(listener.relations);
	    System.out.println(listener.objects);
	}
	
}
