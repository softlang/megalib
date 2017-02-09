package org.java.megalib.checker.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.java.megalib.models.Function;
import org.java.megalib.models.Relation;
import org.junit.Before;
import org.junit.Test;

/**
 * @author heinz
 */
public class SubstitutionTest {

    private ModelLoader ml;

    @Before
    public void setUp() throws IOException {
        ml = new ModelLoader();
        assertTrue(ml.loadFile("testsample/SubstitutionDemo/App.megal"));
    }

    @Test
    public void testAbstract() throws IOException {
        ModelLoader ml = new ModelLoader();
        assertTrue(ml.loadFile("testsample/SubstitutionDemo/Abstract.megal"));
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void testBaseTechnology() throws IOException {
        ModelLoader ml = new ModelLoader();
        assertTrue(ml.loadFile("testsample/SubstitutionDemo/BaseTechnology.megal"));
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        c.getWarnings().forEach(w -> System.out.println(w));
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void testTechnology() throws IOException {
        ModelLoader ml = new ModelLoader();
        assertTrue(ml.loadFile("testsample/SubstitutionDemo/Technology.megal"));
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        c.getWarnings().forEach(w -> System.out.println(w));
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void testApp() throws IOException {
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        c.getWarnings().forEach(w -> System.out.println(w));
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void testSerializerInstance() {
        assertEquals("Library", ml.getModel().getInstanceOfMap().get("?Serializer"));
        assertEquals("Library", ml.getModel().getInstanceOfMap().get("MySerializer"));
    }

    @Test
    public void testSerializerRel() {
        assertTrue(ml.getModel().getRelationshipInstanceMap().get("uses").contains(new Relation("?Serializer", "?PL")));
        assertFalse(ml.getModel().getRelationshipInstanceMap().get("uses")
                      .contains(new Relation("?Serializer", "MyPL")));
        assertTrue(ml.getModel().getRelationshipInstanceMap().get("uses")
                     .contains(new Relation("MySerializer", "MyPL")));
        assertFalse(ml.getModel().getRelationshipInstanceMap().get("uses")
                      .contains(new Relation("MySerializer", "?PL")));
        assertTrue(ml.getModel().getRelationshipInstanceMap().get("implements")
                     .contains(new Relation("?Serializer", "?PL")));
        assertTrue(ml.getModel().getRelationshipInstanceMap().get("implements")
                     .contains(new Relation("MySerializer", "MyPL")));
    }

    @Test
    public void testPLInstance() {
        assertEquals("ProgrammingLanguage", ml.getModel().getInstanceOfMap().get("?PL"));
        assertEquals("ProgrammingLanguage", ml.getModel().getInstanceOfMap().get("MyPL"));
    }

    @Test
    public void testOLInstance() {
        assertEquals("ObjectGraph", ml.getModel().getInstanceOfMap().get("?OL"));
        assertEquals("ObjectGraph", ml.getModel().getInstanceOfMap().get("MyOL"));
    }

    @Test
    public void testProjectInstance() {
        assertEquals("Application", ml.getModel().getInstanceOfMap().get("?project"));
        assertEquals("Application", ml.getModel().getInstanceOfMap().get("?plproject"));
        assertEquals("Application", ml.getModel().getInstanceOfMap().get("myProject1"));
        assertEquals("Application", ml.getModel().getInstanceOfMap().get("myProject2"));
    }

    @Test
    public void testProgramInstance() {
        assertEquals("Artifact", ml.getModel().getInstanceOfMap().get("?program"));
        assertEquals("Artifact", ml.getModel().getInstanceOfMap().get("?myplprogram"));
        assertEquals("Artifact", ml.getModel().getInstanceOfMap().get("myProgram1"));
        assertEquals("Artifact", ml.getModel().getInstanceOfMap().get("myProgram2"));
    }

    @Test
    public void testProgram1Relations() {
        Relation r = new Relation("myProgram1", "File");
        assertTrue(ml.getModel().getRelationshipInstanceMap().get("manifestsAs").contains(r));

        Set<Relation> rs = ml.getModel().getRelationshipInstanceMap().get("elementOf");
        rs = rs.parallelStream().filter(t -> t.getSubject().equals("myProgram1")).collect(Collectors.toSet());
        assertEquals(1, rs.size());
        r = new Relation("myProgram1", "MyPL");
        assertTrue(ml.getModel().getRelationshipInstanceMap().get("elementOf").contains(r));
    }

    @Test
    public void testObjectInstance() {
        assertEquals("Artifact", ml.getModel().getInstanceOfMap().get("?object"));
        assertEquals("Artifact", ml.getModel().getInstanceOfMap().get("?myolobject"));
        assertEquals("Artifact", ml.getModel().getInstanceOfMap().get("myObject11"));
        assertEquals("Artifact", ml.getModel().getInstanceOfMap().get("myObject12"));
        assertEquals("Artifact", ml.getModel().getInstanceOfMap().get("myObject21"));
        assertEquals("Artifact", ml.getModel().getInstanceOfMap().get("myObject22"));
    }

    @Test
    public void testFDeclAbstract() {
        assertEquals("Function", ml.getModel().getInstanceOfMap().get("f"));
        Set<Function> decls = ml.getModel().getFunctionDeclarations().get("f");
        assertEquals(6, decls.size());
        List<String> inputs = new ArrayList<>();
        List<String> outputs = new ArrayList<>();
        inputs.add("?OL");
        outputs.add("?D");
        Function f = new Function(inputs, outputs);
        assertTrue(decls.contains(f));
    }

    @Test
    public void testFDeclTechnology() {
        assertEquals("Function", ml.getModel().getInstanceOfMap().get("f"));
        Set<Function> decls = ml.getModel().getFunctionDeclarations().get("f");
        assertEquals(6, decls.size());
        List<String> inputs = new ArrayList<>();
        List<String> outputs = new ArrayList<>();
        outputs.add("?D");
        inputs.add("MyOL");
        Function f = new Function(inputs, outputs);
        assertTrue(decls.contains(f));
    }

    @Test
    public void testFDecl1App() {
        assertEquals("Function", ml.getModel().getInstanceOfMap().get("f"));
        Set<Function> decls = ml.getModel().getFunctionDeclarations().get("f");
        assertEquals(6, decls.size());
        List<String> inputs = new ArrayList<>();
        List<String> outputs = new ArrayList<>();
        inputs.add("MyOL");
        outputs.add("MyD1");
        Function f = new Function(inputs, outputs);
        assertTrue(decls.contains(f));
    }

    @Test
    public void testFDecl2App() {
        assertEquals("Function", ml.getModel().getInstanceOfMap().get("f"));
        Set<Function> decls = ml.getModel().getFunctionDeclarations().get("f");
        assertEquals(6, decls.size());
        List<String> inputs = new ArrayList<>();
        List<String> outputs = new ArrayList<>();
        inputs.add("MyOL");
        outputs.add("MyD2");
        Function f = new Function(inputs, outputs);
        assertTrue(decls.contains(f));
    }

}
