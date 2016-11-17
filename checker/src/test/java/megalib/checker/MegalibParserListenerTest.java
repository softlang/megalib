package test.java.megalib.checker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.java.megalib.checker.services.MegaModelLoader;
import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;
import org.junit.Test;

/**
 * Tests the correct behavior for MegaModelLoader and MegaModel functionality.
 * Especially the constraints relevant for creation time are tested.
 * 
 * @author heinz
 *
 */
public class MegalibParserListenerTest {
	
	@Test
	public void preludeIsParsed(){
		MegaModelLoader ml = new MegaModelLoader();
		MegaModel model = ml.getModel();
		model.getCriticalWarnings().forEach(w->System.out.println(w));
		assertEquals(19,model.getInstanceOfMap().size());
		assertEquals(19,model.getLinkMap().size());
		assertEquals(28,model.getSubtypesMap().size());
		Map<String, Set<Relation>> rm = model.getRelationshipDeclarationMap();
		//the number of distinct relation ship names
		assertEquals(15,rm.size());
		int count = rm.values().stream().map(set -> set.size()).reduce(0, (a,b) -> a+b);
		assertEquals(41, count);
		assertEquals(0,model.getCriticalWarnings().size());
	}
	
	@Test
	public void addSubtypeInvalidSupertype() {
		String input = "DerivedType < Type";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, String> subtypes = m.getSubtypesMap();
		
		assertFalse(subtypes.containsKey("DerivedType"));
		assertTrue(m.getCriticalWarnings().contains("Error at DerivedType: The declared supertype is not a subtype of Entity"));
		assertEquals(1,m.getCriticalWarnings().size());
	}
	
	@Test
	public void addSubtypeValidSupertype() {
		String input = "DerivedType < Artifact";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, String> subtypes = m.getSubtypesMap();
		
		assertTrue(subtypes.containsKey("DerivedType"));
		assertEquals(0,m.getCriticalWarnings().size());
		assertEquals("Artifact",subtypes.get("DerivedType"));
	}
	
	@Test
	public void addSubtypeOfEntity() {
		String input = "DerivedType < Entity";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, String> subtypes = m.getSubtypesMap();
		
		assertTrue(subtypes.containsKey("DerivedType"));
		assertEquals(0,m.getCriticalWarnings().size());
		assertEquals("Entity",subtypes.get("DerivedType"));
	}
	
	@Test
	public void addSubtypeMultipleInh() {
		String input = "DerivedType < Technology \n"
				+ "DerivedType < System";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, String> subtypes = m.getSubtypesMap();
		
		assertTrue(subtypes.containsKey("DerivedType"));
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at DerivedType: Multiple inheritance is not allowed."));
		assertEquals("Technology",subtypes.get("DerivedType"));
	}
	
	@Test
	public void addEntityInstance(){
		String input = "Entity : Artifact";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, String> imap = m.getInstanceOfMap();
		assertFalse(imap.containsKey("Entity"));
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at Entity: The root type `Entity' cannot be instantiated."));
	}
	
	@Test
	public void addInstanceOfUnknown(){
		String input = "Instance : Type";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, String> imap = m.getInstanceOfMap();
		
		assertFalse(imap.containsKey("Instance"));
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at Instance: The instantiated type is not (transitive) subtype of Entity."));
	}
	
	@Test
	public void addInstanceOfEntity(){
		String input = "Artifact : Technology";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, String> imap = m.getInstanceOfMap();
		
		assertFalse(imap.containsKey("Artifact"));
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at Artifact: It is instance and type at the same time."));
	}
	
	@Test
	public void addInstanceOfMulitple(){
		String input = "t : Technology "
				+ "t : Artifact";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, String> imap = m.getInstanceOfMap();
		
		assertEquals("Technology",imap.get("t"));
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at t: Multiple types cannot be assigned to the same instance."));
	}
	
	@Test
	public void addInstanceOfProgrammingLanguage(){
		String input = "Java : ProgrammingLanguage";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, String> imap = m.getInstanceOfMap();
		assertEquals("ProgrammingLanguage",imap.get("Java"));
		assertEquals(0,m.getCriticalWarnings().size());
	}
	
	@Test
	public void addInstanceOfArtifact(){
		String input = "Python : ProgrammingLanguage "
				+ "a : Artifact<Python,MvcModel,File>";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, String> imap = m.getInstanceOfMap();
		assertEquals(21,imap.size());
		assertTrue(imap.containsKey("a"));
		assertEquals("Artifact",imap.get("a"));
		assertTrue(m.getRelationshipInstanceMap().get("elementOf").contains(new Relation("a","Python")));
		assertTrue(m.getRelationshipInstanceMap().get("hasRole").contains(new Relation("a","MvcModel")));
		assertTrue(m.getRelationshipInstanceMap().get("manifestsAs").contains(new Relation("a","File")));
	}
	
	@Test
	public void addRelationDeclarationUnknownDomain()  {
		String input = "Relation < TypeOne # Artifact";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Set<Relation>> actual = m.getRelationshipDeclarationMap();
		
		assertFalse(actual.containsKey("Relation"));		
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at declaration of Relation: Its domain TypeOne is not subtype of Entity."));
	}
	
	@Test
	public void addRelationDeclarationUnknownRange()  {
		String input = "Relation < Artifact # TypeTwo";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Set<Relation>> actual = m.getRelationshipDeclarationMap();
		
		assertFalse(actual.containsKey("Relation"));		
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at declaration of Relation: Its range TypeTwo is not subtype of Entity."));
	}
	
	@Test
	public void addRelationDeclarationDouble()  {
		String input = "Relation < Artifact # Artifact "
				+ "Relation < Artifact # Artifact";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Set<Relation>> actual = m.getRelationshipDeclarationMap();
		
		assertTrue(actual.containsKey("Relation"));		
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at declaration of Relation: It is declared twice with the same types."));
	}
	
	@Test
	public void addRelationDeclarationOverloaded()  {
		String input = "Relation < Artifact # Artifact\nRelation < Technology # Technology";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Set<Relation>> actual = m.getRelationshipDeclarationMap();
		
		assertTrue(actual.containsKey("Relation"));		
		assertEquals(0,m.getCriticalWarnings().size());
		assertTrue(actual.get("Relation").contains(new Relation("Artifact","Artifact")));
		assertTrue(actual.get("Relation").contains(new Relation("Technology","Technology")));
	}
	
	@Test
	public void addRelationInstanceDouble(){
		String input = "a : ProgrammingLanguage "
				+ "b : ProgrammingLanguage "
				+ "a subsetOf b "
				+ "a subsetOf b";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Set<Relation>> actual = m.getRelationshipInstanceMap();
		
		assertTrue(actual.containsKey("subsetOf"));
		assertTrue(actual.get("subsetOf").contains(new Relation("a","b")));
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at instance of subsetOf: 'a subsetOf b' already exists."));
	}
	
	@Test
	public void addRelationInstanceUnfitDomain(){
		String input = "a : Framework "
				+ "b : ProgrammingLanguage "
				+ "a subsetOf b ";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Set<Relation>> actual = m.getRelationshipInstanceMap();
		
		assertFalse(actual.containsKey("subsetOf"));
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at instance of subsetOf: 'a subsetOf b' does not fit any declaration."));
	}
	
	@Test
	public void addRelationInstanceUnfitRange(){
		String input = "a : Framework "
				+ "b : ProgrammingLanguage "
				+ "b subsetOf a ";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Set<Relation>> actual = m.getRelationshipInstanceMap();
		
		assertFalse(actual.containsKey("subsetOf"));
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at instance of subsetOf: 'b subsetOf a' does not fit any declaration."));
	}
	
	@Test
	public void addRelationInstanceMultiFit(){
		String input = "subsetOf < ProgrammingLanguage # ProgrammingLanguage "
				+ "a : ProgrammingLanguage "
				+ "b : ProgrammingLanguage "
				+ "a subsetOf b ";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Set<Relation>> actual = m.getRelationshipInstanceMap();
		
		assertFalse(actual.containsKey("subsetOf"));
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at instance of subsetOf: 'a subsetOf b' fits multiple declarations."));
	}
	
	@Test
	public void addRelationInstanceDomainNotInstance(){
		String input = "b : ProgrammingLanguage "
				+ "a subsetOf b";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Set<Relation>> actual = m.getRelationshipInstanceMap();
		assertFalse(actual.containsKey("subsetOf"));
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at instance of subsetOf: a is not instantiated."));
	}
	
	@Test
	public void addRelationInstanceRangeNotInstance(){
		String input = "a : ProgrammingLanguage "
				+ "a subsetOf b";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Set<Relation>> actual = m.getRelationshipInstanceMap();
		assertFalse(actual.containsKey("subsetOf"));
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at instance of subsetOf: b is not instantiated."));
	}
	
	@Test
	public void addFunctionDeclarationOverloading(){
		String input = "a : ProgrammingLanguage "
				+ "b : ProgrammingLanguage "
				+ "f : a -> a "
				+ "f : b -> b";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Function> actual = m.getFunctionDeclarations();
		assertTrue(actual.containsKey("f"));
		
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error: The function f has multiple declarations."));
	}
	
	@Test
	public void addFunctionDeclarationDomainNotInstantiated(){
		String input = "b : ProgrammingLanguage "
				+ "f : a -> b ";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Function> actual = m.getFunctionDeclarations();
		assertFalse(actual.containsKey("f"));
		
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at f's declaration: The language a was not declared."));
	}
	
	@Test
	public void addFunctionDeclarationRangeNotInstantiated(){
		String input = "a : ProgrammingLanguage "
				+ "f : a -> b ";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Function> actual = m.getFunctionDeclarations();
		assertFalse(actual.containsKey("f"));
		
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at f's declaration: The language b was not declared."));
	}
	
	@Test
	public void addFunctionDeclarationDomainMultipleNotInstantiated(){
		String input = "b : ProgrammingLanguage "
				+ "f : b # a # b -> b # b # a ";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Function> actual = m.getFunctionDeclarations();
		assertFalse(actual.containsKey("f"));
		
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at f's declaration: The language a was not declared."));
	}
	
	@Test
	public void addFunctionDeclarationRangeMultipleNotInstantiated(){
		String input = "b : ProgrammingLanguage "
				+ "f : b  # b -> b # b # a ";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Function> actual = m.getFunctionDeclarations();
		assertFalse(actual.containsKey("f"));
		
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at f's declaration: The language a was not declared."));
	}
	
	@Test
	public void addFunctionDeclarationDomainNotALanguage(){
		String input = "b : ProgrammingLanguage "
				+ "f : Grammar -> b ";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Function> actual = m.getFunctionDeclarations();
		assertFalse(actual.containsKey("f"));
		
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at f's declaration: Grammar is not a language."));
	}
	
	@Test
	public void addFunctionDeclarationRangeNotALanguage(){
		String input = "b : ProgrammingLanguage "
				+ "f : b -> Grammar ";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Function> actual = m.getFunctionDeclarations();
		assertFalse(actual.containsKey("f"));
		
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at f's declaration: Grammar is not a language."));
	}
	
	@Test
	public void addFunctionDeclaration(){
		String input = "l : ProgrammingLanguage "
				+ "f : l -> l";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Function> actual = m.getFunctionDeclarations();
		assertEquals(0,m.getCriticalWarnings().size());
		assertTrue(actual.containsKey("f"));
	}
	
	@Test
	public void addFunctionDeclarationMulti(){
		String input = "a : ProgrammingLanguage "
				+ "b : ProgrammingLanguage "
				+ "f : b  # b -> b # b # a ";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Function> actual = m.getFunctionDeclarations();
		assertTrue(actual.containsKey("f"));
		
		assertEquals(0,m.getCriticalWarnings().size());
	}
	
	@Test
	public void addFunctionApplicationNotDeclared(){
		String input = "f(a)|->a";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Set<Function>> actual = m.getFunctionApplications();
		
		assertTrue(actual.isEmpty());
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at application of f: A declaration has to be stated beforehand."));
	}
	
	@Test
	public void addFunctionApplication(){
		String input = "l : ProgrammingLanguage "
				+ "f : l # l # l -> l # l "
				+ "a : Artifact<l,MvcModel,File> "
				+ "b : Artifact<l,MvcView,File> "
				+ "f(a,b,a)|->(b,a)";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Set<Function>> actual = m.getFunctionApplications();
		assertEquals(1,actual.size());
		assertTrue(actual.containsKey("f"));
		assertEquals(1,actual.get("f").size());
		Function f = actual.get("f").iterator().next();
		assertEquals(3,f.getInputs().size());
		assertEquals("a",f.getInputs().get(0));
		assertEquals("b",f.getInputs().get(1));
		assertEquals("a",f.getInputs().get(2));
		assertEquals(2,f.getOutputs().size());
		assertEquals("b",f.getOutputs().get(0));
		assertEquals("a",f.getOutputs().get(1));
	}
	
	@Test
	public void addFunctionApplicationDuplicate(){
		String input = "l : ProgrammingLanguage "
				+ "f : l # l # l -> l # l "
				+ "a : Artifact<l,MvcModel,File> "
				+ "b : Artifact<l,MvcView,File> "
				+ "c : Artifact<l,MvcModel,File> "
				+ "f(a,b,c)|->(b,a) "
				+ "f(a,b,c)|->(b,a)";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Set<Function>> actual = m.getFunctionApplications();
		assertTrue(actual.containsKey("f"));
		assertEquals(1,actual.get("f").size());
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at application of f with inputs [a, b, c] and outputs [b, a]: It already exists."));
		
	}
	
	@Test
	public void addFunctionApplicationNotInstantiatedInput(){
		String input = "l : DataRepresentationLanguage "
				+ "f : l -> l "
				+ "b : Artifact<l,Grammar,File> "
				+ "f(a)|->b";
		MegaModel m = new MegaModelLoader().loadString(input);
		Map<String, Set<Function>> actual = m.getFunctionApplications();
		assertTrue(m.getFunctionDeclarations().containsKey("f"));
		assertTrue(actual.isEmpty());
		assertEquals(1,m.getCriticalWarnings().size());
		assertTrue(m.getCriticalWarnings().contains("Error at a function application: a is not instance of Artifact."));
	}
	
	
	
	@Test
	public void fileNotFoundReturnsNull(){
		MegaModelLoader ml = new MegaModelLoader();
		ml.loadFile("");
		assertEquals(1,ml.getModel().getCriticalWarnings().size());
		assertTrue(ml.getModel().getCriticalWarnings().contains("File '' does not exist"));
	}
	
	@Test
	public void testSyntacticallyInvalidString(){
		String input = "xy test";
		assertNull(new MegaModelLoader().loadString(input));
	}	
	
	@Test
	public void testComment() throws IOException{
		String input = "// test hello world";
		MegaModel actual = new MegaModelLoader().loadString(input);
		assertNotNull(actual);
	}
	
	@Test
	public void testCommentAfterStmt() throws IOException{
		String input = "a : ProgrammingLanguage // test hello world";
		MegaModel actual = new MegaModelLoader().loadString(input);
		assertNotNull(actual);
	}
}
