/**
 * 
 */
package org.java.megalib.checker.services;

import java.io.FileInputStream;
import java.io.IOException;

import org.java.megalib.antlr.MegalibParser;

/**
 * @author mmay, aemmerichs
 *
 */
public interface IParserGenerator {
	public MegalibParser generate(FileInputStream stream) throws IOException;
}
