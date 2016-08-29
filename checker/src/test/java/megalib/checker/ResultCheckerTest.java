package test.java.megalib.checker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.java.megalib.checker.services.Checker;
import org.java.megalib.checker.services.ResultChecker;
import org.junit.Before;
import org.junit.Test;


public class ResultCheckerTest {

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
		resultChecker.doChecks();
		assertTrue(resultChecker.getWarnings().contains("Error at: SubSubEntity < SubEntity! Entity Type 'SubEntity' is unkown"));
	}
	
	@Test
	public void testEntityInstance() throws IOException{
		String data = ("Language < Entity "
				+ "Haskell : Language "
				+ "Java : Lang");
		ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
		ResultChecker resultChecker = new ResultChecker(checker.getListener(input).getModel());
		resultChecker.doChecks();
		assertTrue(resultChecker.getWarnings().size() == 1);
		assertTrue(resultChecker.getWarnings().contains("Error at: Java : Lang! Entity Type 'Lang' is unkown"));
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
		resultChecker.doChecks();
		assertTrue(resultChecker.getWarnings().size() == 1);
		assertTrue(resultChecker.getWarnings().contains("Error at: 'partOf < System # Language'! Type of 'System' is unkown"));
	}
	
	@Test
	public void testRelationInstance() throws IOException{
		String data = ("Language < Entity "
				+ "Artifact < Entity "
				+ "SubArtifact < Artifact "
				+ "System < Entity "
				+ "partOf < Artifact # System "
				+ "File: SubArtifact "
				+ "Haskell: Language "
				+ "Windows: System "
				+ "File partOf Haskell "
				+ "File partOf Windows ");
		ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
		ResultChecker resultChecker = new ResultChecker(checker.getListener(input).getModel());
		resultChecker.doChecks();
		assertTrue(resultChecker.getWarnings().size() == 1);
		assertTrue(resultChecker.getWarnings().contains("Error at: 'File partOf Haskell'! Wrong Entity(s) used"));
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
		resultChecker.doChecks();
		assertTrue(resultChecker.getWarnings().size() == 3);
		assertTrue(resultChecker.getWarnings().contains("Error at function Declaration of 'merge' The return Type 'C' is incorrect"));
		assertTrue(!resultChecker.getWarnings().contains("Error at function Declaration of 'merge' The return Type 'Prolog' is incorrect"));
		assertTrue(resultChecker.getWarnings().contains("Error at function Declaration of 'merge' The parameter 'C' is incorrect"));
	}
	
	@Test
	public void testFunctionInstances1() throws IOException{
		String data = ("Language < Entity "
				+ "Artifact < Entity "
				+ "SubArtifact < Artifact "
				+ "System < Entity "
				+ "partOf < Artifact # System "
				+ "elementOf < Artifact # Language "
				+ "elementOf < Language # Language "
				+ "elementOf < Artifact # Artifact "
				+ "File: Artifact "
				+ "Haskell: Language "
				+ "Java: Language "
				+ "JavaFile: Artifact "
				+ "JavaFile elementOf Java "
				+ "WrongHaskellFile: Artifact "
				+ "RightHaskellFile: Artifact "
				+ "WrongHaskellFile elementOf File "
				+ "RightHaskellFile elementOf Haskell "				
				+ "merge: Java -> Haskell "
				+ "merge(JavaFile) |-> RightHaskellFile "
				+ "merge(JavaFile) |-> WrongHaskellFile "
				+ "insert: Java#Java -> Java#Java "
				+ "insert(JavaFile,JavaFile) |-> (JavaFile,JavaFile) "
				+ "insert(JavaFile,JavaFile) |-> (JavaFile,HaskellFile)");
		ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
		ResultChecker resultChecker = new ResultChecker(checker.getListener(input).getModel());
		resultChecker.doChecks();
		assertTrue(resultChecker.getWarnings().size() == 2);
		assertTrue(resultChecker.getWarnings().contains("Error at Function 'merge'! The return-type 'WrongHaskellFile' is incorrect"));
		assertTrue(resultChecker.getWarnings().contains("Error at Function 'insert'! The return-type 'HaskellFile' is incorrect"));
	}
	
	
	@Test
	public void testFunctionInstances2() throws IOException{
		String data = ("Language < Entity "
				+ "Artifact < Entity "
				+ "elementOf < Artifact # Language "
				+ "Java: Language "
				+ "Haskell: Language "
				+ "JavaFile: Artifact "
				+ "JavaFile elementOf Java "
				+ "HaskellFile: Artifact "
				+ "HaskellFile elementOf Haskell "			
				+ "merge: Java -> Haskell "
				+ "insert(JavaFile) |-> HaskellFile ");
		ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
		ResultChecker resultChecker = new ResultChecker(checker.getListener(input).getModel());
		resultChecker.doChecks();
		assertTrue(resultChecker.getWarnings().size() == 1);
		assertTrue(resultChecker.getWarnings().contains("Error at function 'insert'! It has not been initialized"));
	}
	
	@Test
	public void testFunctionInstance3() throws IOException {
		String data = ("Language < Entity "
				+ "Java : Language "
				+ "f : Java -> Java "
				+ "Technology < Entity "
				+ "MyTech : Technology "
				+ "Function < Entity "
				+ "implements < Technology # Function "
				+ "MyTech implements f");
		ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
		ResultChecker resultChecker = new ResultChecker(checker.getListener(input).getModel());
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
	}
}
