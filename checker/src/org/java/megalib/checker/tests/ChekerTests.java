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
import org.junit.Before;
import org.junit.Test;

/**
 * @author mmay, aemmerichs
 *
 */

public class ChekerTests {
	IChecker sut;
	String filepath;
	
	@Before
	public void setUp() throws Exception {
		sut = new Checker();
		filepath = "./TestFiles/JavaC.megal";
	}
	
	@Test
	public void doCheckReturnsListener() throws FileNotFoundException, IOException {
		Object actual = sut.doCheck(filepath);
		assertThat(actual, instanceOf(Listener.class));
	}
	
	@Test
	public void doCheckReturnsExpectedEntities() throws FileNotFoundException, IOException {
		Map<String, String> expected = new HashMap<String, String>();
	}
	
	@Test
	public void doCheckReturnsExpectedObjects() throws FileNotFoundException, IOException {
		Map<String, String> expected = new HashMap<String, String>();
	}
	
	@Test
	public void doCheckReturnsExpectedRelations() throws FileNotFoundException, IOException {
		Map<String, Map<Integer, LinkedList<String>>> expected = new HashMap<String, Map<Integer, LinkedList<String>>>();
	}
	
	@Test
	public void doCheckReturnsExpectedFunctions() throws FileNotFoundException, IOException {
		Map<String, LinkedList<String>> expected = new HashMap<String, LinkedList<String>>();
	}
}
