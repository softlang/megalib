/**
 * 
 */
package java.megalib.checker;

import java.io.IOException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

/**
 * @author mmay, aemmerichs
 *
 */
public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		try {
//			MegalibParser parser = initiateParser();
//		} catch (InputStreamError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private static MegalibParser initiateParser() throws InputStreamError {
		try {
			ANTLRInputStream input = new ANTLRInputStream(System.in);
			Lexer megalibLexer = new MegalibLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(megalibLexer);
			MegalibParser parser = new MegalibParser(tokens);
			return parser;
		} catch (IOException e) {
			throw new InputStreamError(e.getMessage());
		}
	}

}
