package org.java.megalib.checker.tests;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.java.megalib.checker.services.Checker;
import org.java.megalib.checker.services.IChecker;
import org.java.megalib.checker.services.Listener;
import org.java.megalib.models.MegaModel;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mmay, aemmerichs
 *
 */

public class CheckerTests {
	IChecker sut;
	String filepath;
	
	@Before
	public void setUp() throws Exception {
		sut = new Checker();
		filepath = "./TestFiles/Prelude.megal";
	}
	
	@Test
	public void doCheckReturnsMegaModel() throws FileNotFoundException, IOException {
		Object actual = sut.doCheck(filepath);
		assertThat(actual, instanceOf(MegaModel.class));
	}
}
