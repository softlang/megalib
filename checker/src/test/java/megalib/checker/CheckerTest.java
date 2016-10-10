package test.java.megalib.checker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.java.megalib.checker.services.Checker;
import org.java.megalib.checker.services.MegaModelLoader;
import org.java.megalib.models.MegaModel;
import org.junit.Test;


/**
 * 
 * @author heinz
 *
 * This is a suite of test cases reassuring working checks. 
 * 
 * At every test case the Prelude model is part of the tested model.
 * 
 */
public class CheckerTest {
	
	@Test
	public void testLinkEntityNotExists() {
		String data = "entity = \"http://softlang.org/\"";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at Link of 'entity' the entity does not exist"));
	}
	
	@Test
	public void testLinkMalformed() {
		String data = "?l : Language \nentity : Artifact<?l> \nentity = \"nowebsitehere\"";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at Link to 'nowebsitehere' : The URL is malformed!"));
	}
	
	@Test
	public void testLinkDead() {
		String data = "?l : Language "
				+ "entity : Artifact<?l> "
				+ "entity = \"http://www.nowebsitehere.de/\"";
		MegaModel model = new MegaModelLoader().createFromString(data);
		assertEquals(7,model.getInstanceOfMap().size());
		assertEquals(6,model.getLinkMap().size());
		Checker resultChecker = new Checker(model);
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at Link to 'http://www.nowebsitehere.de/' : Connection failed!"));
	}
	
	@Test
	public void testLinkWorking() {
		String data = "?l : Language"
				+ "\nentity : Artifact<?l> "
				+ "\nentity = \"http://softlang.org/\"";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	
	@Test
	public void testEntityDeclaration() {
		String data = "SubSubEntity < SubEntity";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertTrue(resultChecker.getWarnings().contains("Error at subtype declaration of 'SubSubEntity'. The supertype 'SubEntity' is not declared."));
	}
	
	@Test
	public void testSubtypeWorking(){
		String data = "SubEntity < Entity\nSubSubEntity < SubEntity";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testSubtypeWorkingEntity(){
		String data = "SubEntity < Entity\nSubSubEntity < SubEntity";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testInstanceTypeNotDeclared() {
		String data = ("?Haskell : Language "
				+ "?Java : Lang");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at entity declaration of '?Java'. The type 'Lang' is not declared."));
	}
	
	@Test
	public void testInstanceTypeWorking(){
		String data = ("?Haskell : Language ");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testInstanceTypeUnderspecified(){
		String data = ("Haskell : Entity");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at entity declaration of 'Haskell'. The type is underspecified."));
	}
	
	@Test
	public void testInstanceIsAType(){
		String data = ("Language : Artifact");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at entity declaration 'Language'. It is defined as a type and instance at the same time."));
	}
	
	@Test
	public void testInstanceIsEntity(){
		String data = "Entity : Artifact";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at entity declaration 'Entity'. It is defined as a type and instance at the same time."));
		
	}
	
	@Test
	public void testRelationDeclarationFailLeft() {
		String data = ("mypartOf < Art # Language");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at relationship declaration of 'mypartOf'. The type 'Art'"
				+ "is not declared!"));
	}
	
	@Test
	public void testRelationDeclarationFailRight() {
		String data = ("partOf < Language # Art ");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at relationship declaration of 'partOf'. The type 'Art'"
				+ "is not declared!"));
	}
	
	@Test
	public void testRelationDeclarationWorkingSubtypeLeft(){
		String data = "Sub < Entity\nRelation < Sub # Entity";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testRelationDeclarationWorkingSubtypeRight(){
		String data = "Sub < Entity\nRelation < Entity # Sub";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testRelationDeclarationWorkingEntity(){
		String data = "Relation < Entity # Entity";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testRelationshipInstanceNotDeclared(){
		String data = "?l : Language"
				+ "\n?a : Artifact<?l>"
				+ "\n?b : Artifact<?l>"
				+ "\n?a Relation ?b";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at relation 'Relation'. It has not been declared!"));
	}
	
	@Test
	public void testRelationshipInstanceEntitiesNotDeclared(){
		String data = ("?l : Language"
				+ "\n?a : Artifact<?l> "
				+ "\n?a partOf ?b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(2,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at relationship instance '?a partOf ?b'! The instance"
				+ " does not fit any declaration."));
		assertTrue(resultChecker.getWarnings().contains("The entity '?b' is unknown!"));
	}
	
	@Test
	public void testRelationshipInstanceSingleDeclarationUnfit(){
		String data = ("rel < Artifact # Artifact "
				+ "\n?a : Artifact<?b> "
				+ "\n?b : Language "
				+ "\n?a rel ?b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at relationship instance '?a rel ?b'! The instance"
				+ " does not fit any declaration."));
	}
	
	@Test
	public void testRelationshipInstanceMultiDeclarationUnfit(){
		String data = ("Artifact < Entity "
				+ "\nLanguage < Entity "
				+ "\npartOf < Language # Language "
				+ "\npartOf < Artifact # Artifact "
				+ "\n?a : Artifact<?b> "
				+ "\n?b : Language "
				+ "\n?a partOf ?b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at relationship instance '?a partOf ?b'! The instance"
				+ " does not fit any declaration."));
	}
	
	@Test
	public void testRelationshipInstanceSingleDeclarationFit(){
		String data = ("?l : Language "
				+ "\nrel < Artifact # Artifact "
				+ "\n?a : Artifact<?l> "
				+ "\n?b : Artifact<?l> "
				+ "\n?a rel ?b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testRelationshipInstanceMultiDeclarationFirstFit(){
		String data = ("?l : Language"
				+ "\nrel < Artifact # Artifact "
				+ "\nrel < Language # Language"
				+ "\n?a : Artifact<?l> "
				+ "\n?b : Artifact<?l> "
				+ "\n?a partOf ?b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testRelationshipInstanceMultiDeclarationSecondFit(){
		String data = ("?l : Language "
				+ "?a : Artifact<?l> "
				+ "?b : Artifact<?l> "
				+ "?a partOf ?b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testRelationshipInstanceTransitivityWorking(){
		String data = "?l : Language "
				+ "XArtifact < Artifact "
				+ "ExArtifact < XArtifact "
				+ "?a : ExArtifact<?l> "
				+ "id < Artifact # Artifact "
				+ "?a id ?a";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testFunctionDeclarationDomainUnknown(){
		String data = ("?Haskell : Language "
				+ "merge : ?A -> ?Haskell");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at function declaration of 'merge' : '?A' is unknown."));
	}
	
	@Test
	public void testFunctionDeclarationSingleDomainNotALanguage() {
		String data = ("?Haskell : Language "
				+ "?a : Artifact<?Haskell> "
				+ "merge : ?a -> ?Haskell ");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at function declaration of 'merge' : '?a' is not an instance of Language"));
	}
	
	@Test
	public void testFunctionDeclarationSingleRangeNotALanguage() {
		String data = ("?Haskell : Language "
				+ "?a : Artifact<?Haskell> "
				+ "merge : ?Haskell -> ?a ");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at function declaration of 'merge' : '?a' is not an instance of Language"));
	}
	
	@Test
	public void testFunctionDeclarationMultiDomainNotALanguage(){
		String data = ("?Haskell : Language "
				+ "?a : Artifact<?Haskell> "
				+ "merge : ?Haskell # ?a -> ?Haskell ");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at function declaration of 'merge' : '?a' is not an instance of Language"));
	}
	
	@Test
	public void testFunctionDeclarationMultiRangeNotALanguage(){
		String data = ("?Haskell : Language "
				+ "?a : Artifact<?Haskell> "
				+ "merge : ?Haskell # ?Haskell -> ?Haskell # ?a ");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at function declaration of 'merge' : '?a' is not an instance of Language"));
	}
	
	@Test
	public void testFunctionDeclarationSingleDomainRange(){
		String data = ("?Haskell : Language "
				+ "merge : ?Haskell -> ?Haskell ");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testFunctionDeclarationMultiDomainRange(){
		String data = ("?Haskell : Language "
				+ "?Java : Language "
				+ "merge : ?Haskell # ?Java -> ?Haskell # ?Java ");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testFunctionApplicationNotInitialzedInputSingle(){
		String data = ("?Haskell : Language "
				+ "merge : ?Haskell -> ?Haskell "
				+ "?b : Artifact "
				+ "?b elementOf ?Haskell "
				+ "merge(?a) |-> ?b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(2,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at function application of 'merge'! The input '?a' has not been declared!"));
		assertTrue(resultChecker.getWarnings().contains("Error at function application of 'merge' with input [?a] and output [?b]! The input/output does not match any function declaration!"));
	}
	
	@Test
	public void testFunctionApplicationNotInitialzedOutputSingle(){
		String data = ("?Haskell : Language "
				+ "merge : ?Haskell -> ?Haskell "
				+ "?a : Artifact "
				+ "?a elementOf ?Haskell "
				+ "merge(?a) |-> ?b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(2,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at function application of 'merge'! The output '?b' has not been declared!"));
		assertTrue(resultChecker.getWarnings().contains("Error at function application of 'merge' with input [?a] and output [?b]! The input/output does not match any function declaration!"));
	}
	
	@Test
	public void testFunctionApplicationNotInitialzedInputMulti(){
		String data = ("?Haskell : Language "
				+ "merge : ?Haskell # ?Haskell -> ?Haskell # ?Haskell "
				+ "?a : Artifact "
				+ "?a elementOf ?Haskell "
				+ "?c : Artifact "
				+ "?c elementOf ?Haskell "
				+ "?d : Artifact "
				+ "?d elementOf ?Haskell "
				+ "merge(?a,?b) |-> (?c,?d)");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(2,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at function application of 'merge'! "
				+ "The input '?b' has not been declared!"));
		assertTrue(resultChecker.getWarnings().contains("Error at function application of 'merge' "
				+ "with input [?a, ?b] and output [?c, ?d]! The input/output does not match any function declaration!"));
	}
	
	@Test
	public void testFunctionApplicationNotInitialzedOutputMulti(){
		String data = ("?Haskell : Language "
				+ "merge : ?Haskell # ?Haskell -> ?Haskell # ?Haskell "
				+ "?a : Artifact "
				+ "?a elementOf ?Haskell "
				+ "?b : Artifact "
				+ "?b elementOf ?Haskell "
				+ "?c : Artifact "
				+ "?c elementOf ?Haskell "
				+ "merge(?a,?b) |-> (?c,?d)");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(2,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at function application of 'merge'! "
				+ "The output '?d' has not been declared!"));
		assertTrue(resultChecker.getWarnings().contains("Error at function application of 'merge' "
				+ "with input [?a, ?b] and output [?c, ?d]! The input/output does not match any function declaration!"));
	}
	
	@Test
	public void testFunctionApplicationWrongDomain(){
		String data = "?A : Language "
				+ "?B : Language "
				+ "?a : Artifact "
				+ "?b : Artifact "
				+ "?a elementOf ?B "
				+ "?b elementOf ?A "
				+ "id : ?A -> ?A "
				+ "id(?a) |-> ?b ";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testFunctionApplicationWrongRange(){
		String data = "?A : Language "
				+ "?B : Language "
				+ "?a : Artifact "
				+ "?b : Artifact "
				+ "?b elementOf ?B "
				+ "?a elementOf ?A "
				+ "id : ?A -> ?A "
				+ "id(?a) |-> ?b ";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testFunctionApplicationSubsetWorking(){
		String data = "?A : Language "
				+ "?B : Language "
				+ "?C : Language "
				+ "?C subsetOf ?B "
				+ "?B subsetOf ?A "
				+ "?a : Artifact "
				+ "?a elementOf ?C "
				+ "id : ?A -> ?A "
				+ "id(?a) |-> ?a";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testFunctionApplicationWorkingSingle(){
		String data = ("?Haskell : Language "
				+ "?merge : ?Haskell -> ?Haskell "
				+ "?a : Artifact "
				+ "?a elementOf ?Haskell "
				+ "?b : Artifact "
				+ "?b elementOf ?Haskell "
				+ "?merge(?a) |-> ?b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testFunctionApplicationWorkingMulti(){
		String data = ("?Haskell : Language "
				+ "merge : ?Haskell # ?Haskell -> ?Haskell # ?Haskell "
				+ "?a : Artifact "
				+ "?a elementOf ?Haskell "
				+ "?b : Artifact "
				+ "?b elementOf ?Haskell "
				+ "?c : Artifact "
				+ "?c elementOf ?Haskell "
				+ "?d : Artifact "
				+ "?d elementOf ?Haskell "
				+ "merge(?a,?b) |-> (?c,?d)");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testFunctionApplicationFailOverloaded(){
		String data = "?A : Language "
				+ "?B : Language "
				+ "?a : Artifact "
				+ "?a elementOf ?A "
				+ "?b : Artifact "
				+ "?b elementOf ?B "
				+ "id : ?A -> ?A "
				+ "id : ?B -> ?B "
				+ "id(?a) |-> ?b";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at function application of 'id' with input [?a] "
				+ "and output [?b]! The input/output does not match any function declaration!"));
	}
	
	@Test
	public void testFunctionApplicationWorkingOverloaded(){
		String data = "?A : Language "
				+ "?B : Language "
				+ "?a : Artifact "
				+ "?a elementOf ?A "
				+ "?b : Artifact "
				+ "?b elementOf ?B "
				+ "id : ?A -> ?A "
				+ "id : ?B -> ?B "
				+ "id(?b) |-> ?b";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testFunctionApplicationWorkingOverloadedMultiSubsets(){
		String data = "?A : Language "
				+ "?B : Language "
				+ "?C : Language "
				+ "?C subsetOf ?A "
				+ "?a : Artifact "
				+ "?a elementOf ?A "
				+ "?b : Artifact "
				+ "?b elementOf ?B "
				+ "?c : Artifact "
				+ "?c elementOf ?C "
				+ "id : ?A # ?B # ?A -> ?B # ?A "
				+ "id : ?B # ?A -> ?A # ?B # ?A "
				+ "id(?b,?c) |-> (?a,?b,?c)";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testFunctionRelationAtOnce(){
		String data = "?A : Language "
				+ "id : ?A -> ?A "
				+ "?a : Artifact "
				+ "?a elementOf ?A "
				+ "id(?a) |-> ?a "
				+ "id < Language # Language "
				+ "?A id ?A ";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
}
