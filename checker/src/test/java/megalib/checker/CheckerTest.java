package test.java.megalib.checker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.java.megalib.checker.services.Checker;
import org.java.megalib.checker.services.MegaModelLoader;
import org.java.megalib.models.MegaModel;
import org.junit.Test;


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
		String data = "entity : Artifact \nentity = \"nowebsitehere\"";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at Link to 'nowebsitehere' : The URL is malformed!"));
	}
	
	@Test
	public void testLinkDead() {
		String data = "entity : Artifact "
				+ "entity = \"http://www.nowebsitehere.de/\"";
		MegaModel model = new MegaModelLoader().createFromString(data);
		assertEquals(1,model.getInstanceOfMap().size());
		assertEquals(1,model.getLinkMap().size());
		Checker resultChecker = new Checker(model);
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at Link to 'http://www.nowebsitehere.de/' : Connection failed!"));
	}
	
	@Test
	public void testLinkWorking() {
		String data = "entity : Artifact \nentity = \"http://softlang.org/\"";
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
		String data = ("Haskell : Language "
				+ "Java : Lang");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at entity declaration of 'Java'. The type 'Lang' is not declared."));
	}
	
	@Test
	public void testInstanceTypeWorking(){
		String data = ("Haskell : Language ");
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
		String data = ("File : Artifact");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at entity declaration 'File'. It is defined as a type and instance at the same time."));
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
		String data = "Artifact < Entity\na : Artifact\nb : Artifact\na Relation b";
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at relation 'Relation'. It has not been declared!"));
	}
	
	@Test
	public void testRelationshipInstanceEntitiesNotDeclared(){
		String data = ("a : Artifact "
				+ "\na partOf b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(2,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at relationship instance 'a partOf b'! The instance"
				+ " does not fit any declaration."));
		assertTrue(resultChecker.getWarnings().contains("The entity 'b' is unknown!"));
	}
	
	@Test
	public void testRelationshipInstanceSingleDeclarationUnfit(){
		String data = ("Artifact < Entity "
				+ "\nLanguage < Entity "
				+ "\npartOf < Artifact # Artifact "
				+ "\na : Artifact "
				+ "\nb : Language "
				+ "\na partOf b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at relationship instance 'a partOf b'! The instance"
				+ " does not fit any declaration."));
	}
	
	@Test
	public void testRelationshipInstanceMultiDeclarationUnfit(){
		String data = ("Artifact < Entity "
				+ "\nLanguage < Entity "
				+ "\npartOf < Language # Language "
				+ "\npartOf < Artifact # Artifact "
				+ "\na : Artifact "
				+ "\nb : Language "
				+ "\na partOf b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at relationship instance 'a partOf b'! The instance"
				+ " does not fit any declaration."));
	}
	
	@Test
	public void testRelationshipInstanceSingleDeclarationFit(){
		String data = ("Artifact < Entity "
				+ "\npartOf < Artifact # Artifact "
				+ "\na : Artifact "
				+ "\nb : Artifact "
				+ "\na partOf b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testRelationshipInstanceMultiDeclarationFirstFit(){
		String data = ("Artifact < Entity "
				+ "\nLanguage < Entity "
				+ "\npartOf < Artifact # Artifact "
				+ "\npartOf < Language # Language"
				+ "\na : Artifact "
				+ "\nb : Artifact "
				+ "\na partOf b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testRelationshipInstanceMultiDeclarationSecondFit(){
		String data = ("Artifact < Entity "
				+ "\nLanguage < Entity "
				+ "\npartOf < Language # Language"
				+ "\npartOf < Artifact # Artifact "
				+ "\na : Artifact "
				+ "\nb : Artifact "
				+ "\na partOf b");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(0,resultChecker.getWarnings().size());
	}
	
	@Test
	public void testFunctionDeclarationSingleDomainNotALanguage() {
		String data = ("Haskell : Language "
				+ "a : Artifact "
				+ "merge : a -> Haskell ");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at function declaration of 'merge' : 'a' is not an instance of Language"));
	}
	
	@Test
	public void testFunctionDeclarationSingleRangeNotALanguage() {
		String data = ("Haskell : Language "
				+ "a : Artifact "
				+ "merge : Haskell -> a ");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at function declaration of 'merge' : 'a' is not an instance of Language"));
	}
	
	@Test
	public void testFunctionDeclarationMultiDomainNotALanguage(){
		String data = ("Haskell : Language "
				+ "a : Artifact "
				+ "merge : Haskell # a -> Haskell ");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at function declaration of 'merge' : 'a' is not an instance of Language"));
	}
	
	@Test
	public void testFunctionDeclarationMultiRangeNotALanguage(){
		String data = ("Haskell : Language "
				+ "a : Artifact "
				+ "merge : Haskell # Haskell -> Haskell # a ");
		Checker resultChecker = new Checker(new MegaModelLoader().createFromString(data));
		resultChecker.doChecks();
		assertEquals(1,resultChecker.getWarnings().size());
		assertTrue(resultChecker.getWarnings().contains("Error at function declaration of 'merge' : 'a' is not an instance of Language"));
	}
	
	
	
}
