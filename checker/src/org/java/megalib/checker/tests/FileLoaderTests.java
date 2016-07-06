package org.java.megalib.checker.tests;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.hamcrest.CoreMatchers.instanceOf;

import org.java.megalib.checker.services.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mmay, aemmerichs
 *
 */
public class FileLoaderTests {
	private IFileLoader sut;
	private String filepath;
	
	@Before
	public void setUp() throws Exception {
		sut = new FileLoader();
		filepath = "./TestFiles/prelude.megal";
	}

	@Test
	public void ItReturnsFileInputStream() throws FileNotFoundException{
		Object actual = sut.load(filepath);
		assertThat(actual, instanceOf(FileInputStream.class));
	}
	
	@Test
	public void ReturnedStreamNotEmptyForNotEmptyFile() throws FileNotFoundException{
		FileInputStream actual = sut.load(filepath);
		assertNotEquals("", actual.toString());
	}
	
	@Test(expected=FileNotFoundException.class)
	public void ThrowsFileNotFound() throws FileNotFoundException{
		sut.load("");
	}
}
