package org.java.megalib.checker.tests;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.java.megalib.checker.services.Checker;
import org.java.megalib.checker.services.ResultChecker;
import org.junit.Before;
import org.junit.Test;


public class ResultCheckerTests {

	public Checker checker;
	
	@Before
	public void setUp(){
		checker = new Checker();
	}
	
	@Test
	public void testEntityDeclaration() throws IOException{
		String data = "SubSubEntity < SubEntity";
		ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
		ResultChecker resultChecker = new ResultChecker(checker.getListener(input).getModel());
		resultChecker.checkEntityDeclaration();
		assertTrue(resultChecker.warnings.contains("Error at: SubSubEntity < SubEntity! Entity Type is unkown"));
	}
	
	@Test
	public void testEntityInstance() throws IOException{
		String data = ("Language < Entity "
				+ "Haskell : Language "
				+ "Java : Lang");
		ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
		ResultChecker resultChecker = new ResultChecker(checker.getListener(input).getModel());
		resultChecker.checkEntityInstances();
		assertTrue(resultChecker.warnings.contains("Error at: Java : Lang! Entity Type is unkown"));
		assertTrue(!resultChecker.warnings.contains("Error at: Haskell : Language! Entity Type is unkown"));
	}
	
	@Test
	public void testRelationDeclaration() throws IOException{
		String data = ("Language < Entity "
				+ "Artifact < Entity "
				+ "SubArtifact < Artifact "
				+ "partOf < Artifact # Language "
				+ "partOf < System # Language");
		ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
		ResultChecker resultChecker = new ResultChecker(checker.getListener(input).getModel());
		resultChecker.checkRelationDeclaration();
		assertTrue(resultChecker.warnings.contains("Error at: 'partOf < System # Language'! Type of 'System' is unkown"));
	}
	
	@Test
	public void testRelationInstance() throws IOException{
		String data = ("Language < Entity "
				+ "Artifact < Entity "
				+ "SubArtifact < Artifact "
				+ "System < Entity "
				+ "partOf < Artifact # System "
				+ "File: Artifact "
				+ "Haskell: Language "
				+ "Windows: System "
				+ "File partOf Haskell "
				+ "File partOf Windows ");
		ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
		ResultChecker resultChecker = new ResultChecker(checker.getListener(input).getModel());
		resultChecker.checkDescription();
		resultChecker.checkEntityDeclaration();
		resultChecker.checkEntityInstances();
		resultChecker.checkRelationDeclaration();
		assertTrue(resultChecker.warnings.isEmpty());
		resultChecker.checkRelationInstances();
		assertTrue(resultChecker.warnings.contains("Error at: 'File partOf Haskell'! Wrong Entity used"));
	}
	
	@Test
	public void testFunctionDeclaration() throws IOException{
		String data = ("Language < Entity "
				+ "Artifact < Entity "
				+ "SubArtifact < Artifact "
				+ "System < Entity "
				+ "partOf < Artifact # System "
				+ "File: Artifact "
				+ "Haskell: Language "
				+ "Java: Language "
				+ "C: Artifact "
				+ "Prolog: Language "
				+ "merge: Java # Haskell -> C "
				+ "merge: Java -> Prolog "
				+ "merge: C # C -> Haskell");
		ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
		ResultChecker resultChecker = new ResultChecker(checker.getListener(input).getModel());
		resultChecker.checkFunctionDeclarations();
		assertTrue(resultChecker.warnings.contains("Error at function Declaration of 'merge'! The return Type 'C' is incorrect"));
		assertTrue(!resultChecker.warnings.contains("Error at function Declaration of 'merge'! The return Type 'Prolog' is incorrect"));
		assertTrue(resultChecker.warnings.contains("Error at function Declaration of 'merge' The parameter 'C' is incorrect"));
	}
	
	@Test
	public void testFunctionInstances() throws IOException{
		String data = ("Language < Entity "
				+ "Artifact < Entity "
				+ "SubArtifact < Artifact "
				+ "System < Entity "
				+ "partOf < Artifact # System "
				+ "File: Artifact "
				+ "Haskell: Language "
				+ "Java: Language "
				+ "JavaFile: Artifact "
				+ "JavaFile elementOf Java "
				+ "HaskellFile: Artifact "
				+ "WrongHaskellFile elementOf File "
				+ "RightHaskellFile elementOf Haskell "				
				+ "merge: Java -> Haskell "
				+ "merge(JavaFile) |-> RightHaskellFile "
				+ "merge(JavaFile) |-> WrongHaskellFile ");
		ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
		ResultChecker resultChecker = new ResultChecker(checker.getListener(input).getModel());
		resultChecker.checkFunctionInstances();
		assertTrue(resultChecker.warnings.contains("Error at Function 'merge'! The return-type 'WrongHaskellFile' is incorrect"));
		assertTrue(!resultChecker.warnings.contains("Error at Function 'merge'! The return-type 'RightHaskellFile' is incorrect"));
		
	}
}
