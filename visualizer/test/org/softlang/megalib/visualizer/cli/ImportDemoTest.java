package org.softlang.megalib.visualizer.cli;

import org.java.megalib.parser.ParserException;
import org.junit.Test;
import org.softlang.megalib.visualizer.Main;

public class ImportDemoTest {

	@Test
	public void test() throws ParserException {
		String data[] = {"-f", "../checker/testsample/ImportDemo/bcd/d/D.megal", "-t", "dot"};
		Main.main(data) ;
	}

}
