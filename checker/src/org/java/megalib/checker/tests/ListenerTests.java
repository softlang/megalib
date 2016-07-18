package org.java.megalib.checker.tests;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.java.megalib.checker.services.Checker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mmay, aemmerichs
 *
 */
public class ListenerTests {
	ByteArrayOutputStream outContent = new ByteArrayOutputStream();

	@Before
	public void setUp() throws Exception {
		outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
	}

	@Test
	public void testEntityNameUsedProblem() {
		Checker checker = new Checker();
		try {
			checker.doCheck("./TestFiles/EntityTests/TestA.megal");
			assertEquals("Error at: 'System<Entity'! Name:System is already used before",
					outContent.toString().substring(0, outContent.toString().length() - 2));
		} catch (FileNotFoundException ex) {
			System.out.println("The File wasn't found on y" + "our harddrive!");
		} catch (IOException ex) {
			System.out.println("Something went wrong with your file. Is it possibly emtpy?");
		}
	}

	@Test
	public void testEntityTypeUnknownSubtypeProblem() {
		Checker checker = new Checker();
		try {
			checker.doCheck("./TestFiles/EntityTests/TestB.megal");
			assertEquals("Error at:Fragment<Artifact! Entity Type is unkown",
					outContent.toString().substring(0, outContent.toString().length() - 2));
		} catch (FileNotFoundException ex) {
			System.out.println("The File wasn't found on y" + "our harddrive!");
		} catch (IOException ex) {
			System.out.println("Something went wrong with your file. Is it possibly emtpy?");
		}
	}

	@Test
	public void testEntityTypeUnkownTypeProblem() {
		Checker checker = new Checker();
		try {
			checker.doCheck("./TestFiles/EntityTests/TestC.megal");
			assertEquals("Error at:aFile:File! Entity Type is unkown",
					outContent.toString().substring(0, outContent.toString().length() - 2));
		} catch (FileNotFoundException ex) {
			System.out.println("The File wasn't found on y" + "our harddrive!");
		} catch (IOException ex) {
			System.out.println("Something went wrong with your file. Is it possibly emtpy?");
		}
	}

	@Test
	public void testEntityNameUsedObjectProblem() {
		Checker checker = new Checker();
		try {
			checker.doCheck("./TestFiles/EntityTests/TestD.megal");
			assertEquals("Error at: 'aFile:System'! Name:aFile is already used before",
					outContent.toString().substring(0, outContent.toString().length() - 2));
		} catch (FileNotFoundException ex) {
			System.out.println("The File wasn't found on y" + "our harddrive!");
		} catch (IOException ex) {
			System.out.println("Something went wrong with your file. Is it possibly emtpy?");
		}
	}

	@Test
	public void testRelationUnkownEntityType() {
		Checker checker = new Checker();
		try {
			checker.doCheck("./TestFiles/RelationTests/TestRelationA.megal");
			assertEquals("Error at:relation<System#System! Entity Type is unkown",
					outContent.toString().substring(0, outContent.toString().length() - 2));
		} catch (FileNotFoundException ex) {
			System.out.println("The File wasn't found on y" + "our harddrive!");
		} catch (IOException ex) {
			System.out.println("Something went wrong with your file. Is it possibly emtpy?");
		}
	}
	
	@Test
	public void testRelationDuplicateRule() {
		Checker checker = new Checker();
		try {
			checker.doCheck("./TestFiles/RelationTests/TestRelationB.megal");
			assertEquals("Error at: 'relationArtifact<Artifact#Artifact'! Rule already exists for this relationship",
					outContent.toString().substring(0, outContent.toString().length() - 2));
		} catch (FileNotFoundException ex) {
			System.out.println("The File wasn't found on y" + "our harddrive!");
		} catch (IOException ex) {
			System.out.println("Something went wrong with your file. Is it possibly emtpy?");
		}
	}
	
	@Test
	public void testRelationUnkownRelation() {
		Checker checker = new Checker();
		try {
			checker.doCheck("./TestFiles/RelationTests/TestRelationC.megal");
			assertEquals("Error at: 'FileA partOf FileB' unknown relationsymbol 'partOf' used",
					outContent.toString().substring(0, outContent.toString().length() - 2));
		} catch (FileNotFoundException ex) {
			System.out.println("The File wasn't found on y" + "our harddrive!");
		} catch (IOException ex) {
			System.out.println("Something went wrong with your file. Is it possibly emtpy?");
		}
	}
	
	@Test
	public void testRelationUndefinedObjects() {
		Checker checker = new Checker();
		try {
			checker.doCheck("./TestFiles/RelationTests/TestRelationD.megal");
			assertEquals("Error at: 'FileC relationArtifact FileB' undefined object(s) were used",
					outContent.toString().substring(0, outContent.toString().length() - 2));
		} catch (FileNotFoundException ex) {
			System.out.println("The File wasn't found on y" + "our harddrive!");
		} catch (IOException ex) {
			System.out.println("Something went wrong with your file. Is it possibly emtpy?");
		}
	}

	@Test
	public void testRelationWrongObject() {
		Checker checker = new Checker();
		try {
			checker.doCheck("./TestFiles/RelationTests/TestRelationE.megal");
			assertEquals("Error at: 'FileA relationArtifact SystemA'Types of objects are not allowed in this relation",
					outContent.toString().substring(0, outContent.toString().length() - 2));
		} catch (FileNotFoundException ex) {
			System.out.println("The File wasn't found on y" + "our harddrive!");
		} catch (IOException ex) {
			System.out.println("Something went wrong with your file. Is it possibly emtpy?");
		}
	}
	
	@Test
	public void testEnterDescription_Context() {
		Checker checker = new Checker();
		try {
			checker.doCheck("./TestFiles/DescriptionTests/TestDescriptionA.megal");
			assertEquals("Error at java = HelloWorldobject is unknown", outContent.toString().substring(0, outContent.toString().length() - 2));
		} catch (FileNotFoundException ex) {
			System.out.println("The File wasn't found on your harddrive!");
		} catch (IOException ex) {
			System.out.println("Something went wrong with your file. Is it possibly emtpy?");
		}
	}

	@After
	public void cleanUp() {
		System.setOut(null);
	}

}
