package test.java.megalib.checker.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.java.megalib.checker.services.ModelLoader;
import org.java.megalib.checker.services.ParserException;
import org.java.megalib.models.Function;
import org.java.megalib.models.Relation;
import org.junit.Test;

/**
 * Tests the correct behavior for MegaModelLoader and MegaModel functionality.
 * Especially the constraints relevant for creation time are tested.
 *
 * @author heinz
 */
public class ParserListenerTest {

    @Test
    public void preludeIsParsed() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        assertEquals(0, ml.getTypeErrors().size());
    }

    @Test
    public void addSubtypeInvalidSupertype() throws ParserException, IOException {
        String input = "/**/DerivedType < Type";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, String> subtypes = ml.getModel().getSubtypesMap();

        assertFalse(subtypes.containsKey("DerivedType"));
        assertTrue(ml.getTypeErrors()
                     .contains("Error at DerivedType: The declared supertype is not a subtype of Entity"));
        assertEquals(1, ml.getTypeErrors().size());
    }

    @Test
    public void addSubtypeEntity() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/Type < Entity " + "Entity < Type";
        ml.loadString(input);
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at Entity < Type: Entity is a MegaL keyword."));
    }

    @Test
    public void addSubtypeValidSupertype() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/DerivedType < Artifact";
        ml.loadString(input);
        Map<String, String> subtypes = ml.getModel().getSubtypesMap();

        assertTrue(subtypes.containsKey("DerivedType"));
        assertEquals(0, ml.getTypeErrors().size());
        assertEquals("Artifact", subtypes.get("DerivedType"));
    }

    @Test
    public void addSubtypeOfEntity() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/DerivedType < Entity";
        ml.loadString(input);
        Map<String, String> subtypes = ml.getModel().getSubtypesMap();

        assertTrue(subtypes.containsKey("DerivedType"));
        assertEquals(0, ml.getTypeErrors().size());
        assertEquals("Entity", subtypes.get("DerivedType"));
    }

    @Test
    public void addSubtypeMultipleInh() throws ParserException, IOException {
        String input = "/**/DerivedType < Technology \n" + "DerivedType < System";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, String> subtypes = ml.getModel().getSubtypesMap();

        assertTrue(subtypes.containsKey("DerivedType"));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at DerivedType: Multiple inheritance is not allowed."));
        assertEquals("Technology", subtypes.get("DerivedType"));
    }

    @Test
    public void addEntityInstance() throws ParserException, IOException {
        String input = "/**/Entity : Artifact";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, String> imap = ml.getModel().getInstanceOfMap();
        assertFalse(imap.containsKey("Entity"));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at Entity: The root type `Entity' cannot be instantiated."));
    }

    @Test
    public void addInstanceOfUnknown() throws ParserException, IOException {
        String input = "/**/Instance : Type";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, String> imap = ml.getModel().getInstanceOfMap();

        assertFalse(imap.containsKey("Instance"));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors()
                     .contains("Error at Instance: The instantiated type is not (transitive) subtype of Entity."));
    }

    @Test
    public void addInstanceOfEntity() throws ParserException, IOException {
        String input = "/**/Artifact : Technology";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, String> imap = ml.getModel().getInstanceOfMap();

        assertFalse(imap.containsKey("Artifact"));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at Artifact: It is instance and type at the same time."));
    }

    @Test
    public void addInstanceOfMulitple() throws ParserException, IOException {
        String input = "/**/t : Technology " + "t : Artifact";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, String> imap = ml.getModel().getInstanceOfMap();

        assertEquals("Technology", imap.get("t"));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at t: Multiple types cannot be assigned to the same instance."));
    }

    @Test
    public void addInstanceOfProgrammingLanguage() throws ParserException, IOException {
        String input = "/**/Java : ProgrammingLanguage";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, String> imap = ml.getModel().getInstanceOfMap();
        assertEquals("ProgrammingLanguage", imap.get("Java"));
        assertEquals(0, ml.getTypeErrors().size());
    }

    @Test
    public void addInstanceOfArtifact() throws ParserException, IOException {
        String input = "/**/Python : ProgrammingLanguage " + "a : Artifact " + "a elementOf Python "
                       + "a hasRole MvcModel " + "a manifestsAs File";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, String> imap = ml.getModel().getInstanceOfMap();
        assertTrue(imap.containsKey("a"));
        assertEquals("Artifact", imap.get("a"));
        assertTrue(ml.getModel().getRelationshipInstanceMap().get("elementOf").contains(new Relation("a", "Python")));
        assertTrue(ml.getModel().getRelationshipInstanceMap().get("hasRole").contains(new Relation("a", "MvcModel")));
        assertTrue(ml.getModel().getRelationshipInstanceMap().get("manifestsAs").contains(new Relation("a", "File")));
    }

    @Test
    public void addRelationDeclarationUnknownDomain() throws ParserException, IOException {
        String input = "/**/Relation < TypeOne # Artifact";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Relation>> actual = ml.getModel().getRelationshipDeclarationMap();

        assertFalse(actual.containsKey("Relation"));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors()
                     .contains("Error at declaration of Relation: Its domain TypeOne is not subtype of Entity."));
    }

    @Test
    public void addRelationDeclarationUnknownRange() throws ParserException, IOException {
        String input = "/**/Relation < Artifact # TypeTwo";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Relation>> actual = ml.getModel().getRelationshipDeclarationMap();

        assertFalse(actual.containsKey("Relation"));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors()
                     .contains("Error at declaration of Relation: Its range TypeTwo is not subtype of Entity."));
    }

    @Test
    public void addRelationDeclarationDouble() throws ParserException, IOException {
        String input = "/**/Relation < Artifact # Artifact " + "Relation < Artifact # Artifact";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Relation>> actual = ml.getModel().getRelationshipDeclarationMap();

        assertTrue(actual.containsKey("Relation"));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors()
                     .contains("Error at declaration of Relation: It is declared twice with the same types."));
    }

    @Test
    public void addRelationDeclarationOverloaded() throws ParserException, IOException {
        String input = "/**/Relation < Artifact # Artifact\nRelation < Technology # Technology";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Relation>> actual = ml.getModel().getRelationshipDeclarationMap();

        assertTrue(actual.containsKey("Relation"));
        assertEquals(0, ml.getTypeErrors().size());
        assertTrue(actual.get("Relation").contains(new Relation("Artifact", "Artifact")));
        assertTrue(actual.get("Relation").contains(new Relation("Technology", "Technology")));
    }

    @Test
    public void addRelationInstanceUndeclared() throws ParserException, IOException {
        String input = "/**/a : ProgrammingLanguage " + "b : ProgrammingLanguage " + "a r b";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at instance of r: r is not declared."));
    }

    @Test
    public void addRelationInstanceDouble() throws ParserException, IOException {
        String input = "/**/a : ProgrammingLanguage " + "b : ProgrammingLanguage " + "a subsetOf b " + "a subsetOf b";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Relation>> actual = ml.getModel().getRelationshipInstanceMap();

        assertTrue(actual.containsKey("subsetOf"));
        assertTrue(actual.get("subsetOf").contains(new Relation("a", "b")));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at instance of subsetOf: 'a subsetOf b' already exists."));
    }

    @Test
    public void addRelationInstanceUnfitDomain() throws ParserException, IOException {
        String input = "/**/a : Framework " + "b : ProgrammingLanguage " + "a subsetOf b ";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Relation>> actual = ml.getModel().getRelationshipInstanceMap();

        assertFalse(actual.containsKey("subsetOf"));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors()
                     .contains("Error at instance of subsetOf: 'a subsetOf b' does not fit any declaration."));
    }

    @Test
    public void addRelationInstanceUnfitRange() throws ParserException, IOException {
        String input = "/**/a : Framework " + "b : ProgrammingLanguage " + "b subsetOf a ";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Relation>> actual = ml.getModel().getRelationshipInstanceMap();

        assertFalse(actual.containsKey("subsetOf"));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors()
                     .contains("Error at instance of subsetOf: 'b subsetOf a' does not fit any declaration."));
    }

    @Test
    public void addRelationInstanceMultiFit() throws ParserException, IOException {
        String input = "/**/subsetOf < ProgrammingLanguage # ProgrammingLanguage " + "a : ProgrammingLanguage "
                       + "b : ProgrammingLanguage " + "a subsetOf b ";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Relation>> actual = ml.getModel().getRelationshipInstanceMap();

        assertFalse(actual.containsKey("subsetOf"));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors()
                     .contains("Error at instance of subsetOf: 'a subsetOf b' fits multiple declarations."));
    }

    @Test
    public void addRelationInstanceDomainNotInstance() throws ParserException, IOException {
        String input = "/**/b : ProgrammingLanguage " + "a subsetOf b";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Relation>> actual = ml.getModel().getRelationshipInstanceMap();
        assertFalse(actual.containsKey("subsetOf"));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at instance of subsetOf: a is not instantiated."));
    }

    @Test
    public void addRelationInstanceRangeNotInstance() throws ParserException, IOException {
        String input = "/**/a : ProgrammingLanguage " + "a subsetOf b";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Relation>> actual = ml.getModel().getRelationshipInstanceMap();
        assertFalse(actual.containsKey("subsetOf"));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at instance of subsetOf: b is not instantiated."));
    }

    @Test
    public void addFunctionDeclarationOverloading() throws ParserException, IOException {
        String input = "/**/a : ProgrammingLanguage " + "b : ProgrammingLanguage " + "f : a -> a " + "f : b -> b";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Function> actual = ml.getModel().getFunctionDeclarations();
        assertTrue(actual.containsKey("f"));

        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at function declaration of f: It has multiple declarations."));
    }

    @Test
    public void addFunctionDeclarationDomainNotInstantiated() throws ParserException, IOException {
        String input = "/**/b : ProgrammingLanguage " + "f : a -> b ";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Function> actual = ml.getModel().getFunctionDeclarations();
        assertFalse(actual.containsKey("f"));

        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at f's declaration: The domain a was not declared."));
    }

    @Test
    public void addFunctionDeclarationRangeNotInstantiated() throws ParserException, IOException {
        String input = "/**/a : ProgrammingLanguage " + "f : a -> b ";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Function> actual = ml.getModel().getFunctionDeclarations();
        assertFalse(actual.containsKey("f"));

        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at f's declaration: The range b was not declared."));
    }

    @Test
    public void addFunctionDeclarationDomainMultipleNotInstantiated() throws ParserException, IOException {
        String input = "/**/b : ProgrammingLanguage " + "f : b # a # b -> b # b # a ";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Function> actual = ml.getModel().getFunctionDeclarations();
        assertFalse(actual.containsKey("f"));
        assertEquals(2, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at f's declaration: The domain a was not declared."));
        assertTrue(ml.getTypeErrors().contains("Error at f's declaration: The range a was not declared."));
    }

    @Test
    public void addFunctionDeclarationRangeMultipleNotInstantiated() throws ParserException, IOException {
        String input = "/**/b : ProgrammingLanguage " + "f : b  # b -> b # b # a ";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Function> actual = ml.getModel().getFunctionDeclarations();
        assertFalse(actual.containsKey("f"));
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at f's declaration: The range a was not declared."));
    }

    @Test
    public void addFunctionDeclarationDomainNotALanguage() throws ParserException, IOException {
        String input = "/**/b : ProgrammingLanguage " + "f : Grammar -> b ";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Function> actual = ml.getModel().getFunctionDeclarations();
        assertFalse(actual.containsKey("f"));

        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at f's declaration: Grammar is not a language."));
    }

    @Test
    public void addFunctionDeclarationRangeNotALanguage() throws ParserException, IOException {
        String input = "/**/b : ProgrammingLanguage " + "f : b -> Grammar ";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Function> actual = ml.getModel().getFunctionDeclarations();
        assertFalse(actual.containsKey("f"));

        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at f's declaration: Grammar is not a language."));
    }

    @Test
    public void addFunctionDeclaration() throws ParserException, IOException {
        String input = "/**/l : ProgrammingLanguage " + "f : l -> l";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Function> actual = ml.getModel().getFunctionDeclarations();
        assertEquals(0, ml.getTypeErrors().size());
        assertTrue(actual.containsKey("f"));
    }

    @Test
    public void addFunctionDeclarationMulti() throws ParserException, IOException {
        String input = "/**/a : ProgrammingLanguage " + "b : ProgrammingLanguage " + "f : b  # b -> b # b # a ";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Function> actual = ml.getModel().getFunctionDeclarations();
        assertTrue(actual.containsKey("f"));

        assertEquals(0, ml.getTypeErrors().size());
    }

    @Test
    public void addFunctionApplicationNotDeclared() throws ParserException, IOException {
        String input = "/**/f(a)|->a";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Function>> actual = ml.getModel().getFunctionApplications();

        assertTrue(actual.isEmpty());
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors()
                     .contains("Error at application of f: A declaration has to be stated beforehand."));
    }

    @Test
    public void addFunctionApplication() throws ParserException, IOException {
        String input = "/**/l : ProgrammingLanguage " + "f : l # l # l -> l # l " + "a : Artifact " + "a elementOf l "
                       + "b : Artifact " + "b elementOf l " + "f(a,b,a)|->(b,a)";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Function>> actual = ml.getModel().getFunctionApplications();
        assertEquals(1, actual.size());
        assertTrue(actual.containsKey("f"));
        assertEquals(1, actual.get("f").size());
        Function f = actual.get("f").iterator().next();
        assertEquals(3, f.getInputs().size());
        assertEquals("a", f.getInputs().get(0));
        assertEquals("b", f.getInputs().get(1));
        assertEquals("a", f.getInputs().get(2));
        assertEquals(2, f.getOutputs().size());
        assertEquals("b", f.getOutputs().get(0));
        assertEquals("a", f.getOutputs().get(1));
    }

    @Test
    public void addFunctionApplicationDuplicate() throws ParserException, IOException {
        String input = "/**/l : ProgrammingLanguage " + "f : l # l # l -> l # l " + "a : Artifact " + "a elementOf l "
                       + "b : Artifact " + "b elementOf l " + "c : Artifact " + "c elementOf l " + "f(a,b,c)|->(b,a) "
                       + "f(a,b,c)|->(b,a)";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Function>> actual = ml.getModel().getFunctionApplications();
        assertTrue(actual.containsKey("f"));
        assertEquals(1, actual.get("f").size());
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors()
                     .contains("Error at application of f with inputs [a, b, c] and outputs [b, a]: It already exists."));

    }

    @Test
    public void addFunctionApplicationNotInstantiatedInput() throws ParserException, IOException {
        String input = "/**/l : DataRepresentationLanguage " + "f : l -> l " + "b : Artifact " + "b elementOf l "
                       + "f(a)|->b";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Function>> actual = ml.getModel().getFunctionApplications();
        assertTrue(actual.isEmpty());
        assertEquals(1, ml.getTypeErrors().size());
        assertEquals("Error at application of f: a is not instance of Artifact.", ml.getTypeErrors().get(0));
    }

    @Test
    public void addFunctionApplicationNotInstantiatedOutput() throws ParserException, IOException {
        String input = "/**/l : DataRepresentationLanguage " + "f : l -> l " + "a : Artifact " + "a elementOf l "
                       + "f(a)|->b";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Function>> actual = ml.getModel().getFunctionApplications();
        assertTrue(actual.isEmpty());
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at application of f: b is not instance of Artifact."));
    }

    @Test
    public void addFunctionApplicationUnfitDomain() throws ParserException, IOException {
        String input = "/**/l1 : DataRepresentationLanguage " + "l2 : DataRepresentationLanguage "
                       + "f : l1 # l2 -> l2 " + "a1 : Artifact " + "a1 elementOf l1 " + "a1 hasRole MvcModel "
                       + "a1 manifestsAs File " + "a2 : Artifact " + "a2 elementOf l2 " + "f(a1,a1)|->a2";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Function>> actual = ml.getModel().getFunctionApplications();
        assertTrue(actual.isEmpty());
        assertEquals(1, ml.getTypeErrors().size());
        assertTrue(ml.getTypeErrors().contains("Error at application of f: a1 is not element of l2."));
    }

    @Test
    public void addFunctionApplicationUnfitRange() throws ParserException, IOException {
        String input = "/**/l1 : DataRepresentationLanguage " + "l2 : DataRepresentationLanguage "
                       + "f : l1 # l2 -> l2 " + "a1 : Artifact " + "a1 elementOf l1 " + "a2 : Artifact "
                       + "a2 elementOf l2 " + "f(a1,a2)|->a1";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Function>> actual = ml.getModel().getFunctionApplications();
        assertTrue(actual.isEmpty());
        assertEquals(1, ml.getTypeErrors().size());
        assertEquals("Error at application of f: a1 is not element of l2.", ml.getTypeErrors().get(0));
    }

    @Test
    public void addFunctionApplicationSubset() throws ParserException, IOException {
        String input = "/**/l1 : DataRepresentationLanguage " + "l2 : DataRepresentationLanguage " + "l2 subsetOf l1 "
                       + "f : l1 # l2 -> l2 " + "a1 : Artifact " + "a1 elementOf l1 " + "a2 : Artifact "
                       + "a2 elementOf l2 " + "f(a2,a2)|->a2";
        ModelLoader ml = new ModelLoader();
        ml.loadString(input);
        Map<String, Set<Function>> actual = ml.getModel().getFunctionApplications();
        assertTrue(actual.containsKey("f"));
        assertEquals(0, ml.getTypeErrors().size());
    }

    @Test(expected = FileNotFoundException.class)
    public void fileNotFoundReturnsCriticalError() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        ml.loadFile("");
    }

    @Test(expected = ParserException.class)
    public void testSyntacticallyInvalidString() throws ParserException, IOException {
        String input = "xy test";
        new ModelLoader().loadString(input);
    }

    @Test
    public void testTurtleSyntax() throws ParserException, IOException {
        String input = "/**/a : Artifact\n" + "    elementOf Java\n" + "    hasRole MvcModel";
        assertNotNull(new ModelLoader().loadString(input));
    }

    @Test
    public void testTurtleInstanceLink() throws ParserException, IOException {
        String input = "/**/Java : ProgrammingLanguage\n"
                       + "    = \"https://en.wikipedia.org/wiki/Java_(programming_language)\"";
        assertNotNull(new ModelLoader().loadString(input));
    }

    @Test
    public void testTurtleTypeLink() throws ParserException, IOException {
        String input = "/**/Language < Entity\n" + "    =\"https://en.wikipedia.org/wiki/Computer_language\"";
        assertNotNull(new ModelLoader().loadString(input));
    }

    @Test
    public void testCommentAfterStmt() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/a : ProgrammingLanguage // test hello world";

        ml.loadString(input);
        assertNotNull(ml.getModel());
    }
}
