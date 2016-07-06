/**
 * 
 */
package org.java.megalib.checker.tests;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;

import org.java.megalib.antlr.MegalibParser;
import org.java.megalib.checker.services.FileLoader;
import org.java.megalib.checker.services.IFileLoader;
import org.java.megalib.checker.services.IParserGenerator;
import org.java.megalib.checker.services.ParserGenerator;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mmay, aemmerichs
 *
 */
public class ParserGeneratorTests {
	private IParserGenerator sut;
	private String filepath;
	private IFileLoader fileLoaderService;
	private FileInputStream stream;
	
	@Before
	public void setUp() throws Exception {
		sut = new ParserGenerator();
		filepath = "./TestFiles/prelude.megal";
		fileLoaderService = new FileLoader();
		stream = fileLoaderService.load(filepath);
	}

	@Test
	public void ItReturnsMegalibParser() throws IOException{
		Object actual = sut.generate(stream);
		assertThat(actual, instanceOf(MegalibParser.class));
	}	
	
	@Test(expected=IOException.class)
	public void ThrowsFileNotFound() throws IOException{
		sut.generate(new FileInputStream(""));
	}
}