/**
 * 
 */
package org.java.megalib.checker.services;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import main.antlr.techdocgrammar.MegalibLexer;
import main.antlr.techdocgrammar.MegalibParser;

/**
 * @author mmay, aemmerichs
 *
 */
public class ParserGenerator {

	public MegalibParser generate(InputStream stream) throws IOException {
		ANTLRInputStream antlrStream = new ANTLRInputStream(stream);
		MegalibLexer lexer = new MegalibLexer(antlrStream);
	    CommonTokenStream token = new CommonTokenStream(lexer);	 

	    return new MegalibParser(token);
	}
}
