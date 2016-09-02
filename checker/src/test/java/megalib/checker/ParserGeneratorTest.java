/**
 * 
 */
package test.java.megalib.checker;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.java.megalib.checker.services.ParserGenerator;
import org.junit.Before;
import org.junit.Test;

import main.antlr.techdocgrammar.MegalibParser;

/**
 * @author mmay, aemmerichs
 *
 */
public class ParserGeneratorTest {
	private ParserGenerator sut;
	private ByteArrayInputStream inputStream1;
	private ByteArrayInputStream inputStream2;
	
	@Before
	public void setUp() throws Exception {
		sut = new ParserGenerator();
		String filepath = "Prelude.megal";
		String data = FileUtils.readFileToString(new File(filepath));
		inputStream1 = new ByteArrayInputStream(data.getBytes());
		
		String input = "test";
		inputStream2 = new ByteArrayInputStream(input.getBytes());
	}

	@Test
	public void ItReturnsMegalibParser() throws IOException{
		Object actual = sut.generate(inputStream1);
		assertThat(actual, instanceOf(MegalibParser.class));
	}	
	
	@Test(expected=IOException.class)
	public void ThrowsFileNotFound() throws IOException{
		sut.generate(new FileInputStream(""));
	}
	
	@Test
	public void ItReturnsMegalibParserForByteArrayInputStream() throws IOException{
		Object actual = sut.generate(inputStream2);
		assertThat(actual, instanceOf(MegalibParser.class));
	}	
}