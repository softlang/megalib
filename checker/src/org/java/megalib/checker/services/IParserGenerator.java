/**
 * 
 */
package org.java.megalib.checker.services;

import java.io.IOException;
import java.io.InputStream;

import org.java.megalib.antlr.MegalibParser;

/**
 * @author mmay, aemmerichs
 *
 */
public interface IParserGenerator {
	public MegalibParser generate(InputStream stream) throws IOException;
}
