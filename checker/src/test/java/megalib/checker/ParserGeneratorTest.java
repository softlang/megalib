/**
 * 
 */
package test.java.megalib.checker;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.java.megalib.checker.services.FileLoader;
import org.java.megalib.checker.services.IFileLoader;
import org.java.megalib.checker.services.IParserGenerator;
import org.java.megalib.checker.services.ParserGenerator;
import org.junit.Before;
import org.junit.Test;

import main.antlr.techdocgrammar.MegalibParser;

/**
 * @author mmay, aemmerichs
 *
 */
public class ParserGeneratorTest {
	private IParserGenerator sut;
	private String filepath;
	private IFileLoader fileLoaderService;
	private FileInputStream fileStream;
	private ByteArrayInputStream inputStream;
	
	@Before
	public void setUp() throws Exception {
		sut = new ParserGenerator();
		
		filepath = "./TestFiles/prelude.megal";
		fileLoaderService = new FileLoader();
		fileStream = fileLoaderService.load(filepath);
		
		String input = "test";
		inputStream = new ByteArrayInputStream(input.getBytes());
	}

	@Test
	public void ItReturnsMegalibParser() throws IOException{
		Object actual = sut.generate(fileStream);
		assertThat(actual, instanceOf(MegalibParser.class));
	}	
	
	@Test(expected=IOException.class)
	public void ThrowsFileNotFound() throws IOException{
		sut.generate(new FileInputStream(""));
	}
	
	@Test
	public void ItReturnsMegalibParserForByteArrayInputStream() throws IOException{
		Object actual = sut.generate(inputStream);
		assertThat(actual, instanceOf(MegalibParser.class));
	}	
}