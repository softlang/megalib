package org.java.megalib.checker.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.java.megalib.models.Block;
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
        ml.loadFile("testsample/SubstitutionDemo/App.megal");
        ml.getTypeErrors().forEach(w-> System.out.println(w));
        assertEquals(0,ml.getTypeErrors().size());
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
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void testTechnology() throws IOException {
        ModelLoader ml = new ModelLoader();
        assertTrue(ml.loadFile("testsample/SubstitutionDemo/Technology.megal"));
        assertEquals(0, ml.getTypeErrors().size());
        assertTrue(ml.getModel().getInstanceOfMap().containsKey("?PLProject"));
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void testApp() throws IOException {
        ml = new ModelLoader();
        ml.loadFile("testsample/SubstitutionDemo/App.megal");
        ml.getTypeErrors().forEach(w -> System.out.println(w));
        assertTrue(ml.getTypeErrors().isEmpty());
        assertTrue(ml.getModel().getInstanceOfMap().containsKey("MyProject1"));
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
        assertTrue(ml.getModel().getRelationships().get("uses").contains(new Relation("?Serializer", "?PL")));
        assertFalse(ml.getModel().getRelationships().get("uses")
                      .contains(new Relation("?Serializer", "MyPL")));
        assertTrue(ml.getModel().getRelationships().get("uses")
                     .contains(new Relation("MySerializer", "MyPL")));
        assertFalse(ml.getModel().getRelationships().get("uses")
                      .contains(new Relation("MySerializer", "?PL")));
        assertTrue(ml.getModel().getRelationships().get("implements")
                     .contains(new Relation("?Serializer", "?OL")));
        assertTrue(ml.getModel().getRelationships().get("implements")
                     .contains(new Relation("MySerializer", "MyOL")));
    }

    @Test
    public void testPLInstance() {
        assertEquals("ProgrammingLanguage", ml.getModel().getInstanceOfMap().get("?PL"));
        assertEquals("ProgrammingLanguage", ml.getModel().getInstanceOfMap().get("MyPL"));
    }

    @Test
    public void testOLInstance() {
        assertEquals("ValueLanguage", ml.getModel().getInstanceOfMap().get("?OL"));
        assertEquals("ValueLanguage", ml.getModel().getInstanceOfMap().get("MyOL"));
    }

    @Test
    public void testProjectInstance() {
        assertEquals("Application", ml.getModel().getInstanceOfMap().get("?Project"));
        assertEquals("Application", ml.getModel().getInstanceOfMap().get("?PLProject"));
        assertEquals("Application", ml.getModel().getInstanceOfMap().get("MyProject1"));
        assertEquals("Application", ml.getModel().getInstanceOfMap().get("MyProject2"));
    }

    @Test
    public void testProgramInstance() {
        assertEquals("File+", ml.getModel().getInstanceOfMap().get("?program"));
        assertEquals("File+", ml.getModel().getInstanceOfMap().get("?myplprogram"));
        assertEquals("File", ml.getModel().getInstanceOfMap().get("myProgram1"));
        assertEquals("File+", ml.getModel().getInstanceOfMap().get("myProgram2"));
    }

    @Test
    public void testProgram1Relations() {
        assertEquals("File",ml.getModel().getType("myProgram1"));
        Set<Relation> rs = ml.getModel().getRelationships().get("elementOf");
        rs = rs.parallelStream().filter(t -> t.getSubject().equals("myProgram1")).collect(Collectors.toSet());
        assertEquals(1, rs.size());
        Relation r = new Relation("myProgram1", "MyPL");
        assertTrue(ml.getModel().getRelationships().get("elementOf").contains(r));
    }

    @Test
    public void testObjectInstance() {
        assertEquals("Transient", ml.getModel().getInstanceOfMap().get("?object"));
        assertEquals("Transient", ml.getModel().getInstanceOfMap().get("?myolobject"));
        assertEquals("Transient", ml.getModel().getInstanceOfMap().get("myObject11"));
        assertEquals("Transient", ml.getModel().getInstanceOfMap().get("myObject12"));
        assertEquals("Transient", ml.getModel().getInstanceOfMap().get("myObject21"));
        assertEquals("Transient", ml.getModel().getInstanceOfMap().get("myObject22"));
    }

    @Test
    public void testFDeclAbstract() {
        assertEquals("Function", ml.getModel().getInstanceOfMap().get("f"));
        Set<Function> decls = ml.getModel().getFunctionDeclarations().get("f");
        assertEquals(3, decls.size());
        List<String> inputs = new ArrayList<>();
        List<String> outputs = new ArrayList<>();
        inputs.add("?OL");
        outputs.add("?D");
        Function f = new Function(inputs, outputs,true);
        assertTrue(decls.contains(f));
    }

    @Test
    public void testFDecl1App() {
        assertEquals("Function", ml.getModel().getInstanceOfMap().get("f"));
        Set<Function> decls = ml.getModel().getFunctionDeclarations().get("f");
        assertEquals(3, decls.size());
        List<String> inputs = new ArrayList<>();
        List<String> outputs = new ArrayList<>();
        inputs.add("MyOL");
        outputs.add("MyD1");
        Function f = new Function(inputs, outputs,true);
        assertTrue(decls.contains(f));
    }

    @Test
    public void testFDecl2App() {
        assertEquals("Function", ml.getModel().getInstanceOfMap().get("f"));
        Set<Function> decls = ml.getModel().getFunctionDeclarations().get("f");
        assertEquals(3, decls.size());
        List<String> inputs = new ArrayList<>();
        List<String> outputs = new ArrayList<>();
        inputs.add("MyOL");
        outputs.add("MyD2");
        Function f = new Function(inputs, outputs,true);
        assertTrue(decls.contains(f));
    }
    
    @Test
    public void testAbstractDeleteApp0Correspondences() {
    	assertFalse(ml.getModel().getRelationships().get("correspondsTo").contains(new Relation("mydata11","?object")));
    }
    
    @Test
    public void testAbstractDeleteApp0FunApp() {
    	Set<Function> set = ml.getModel().getFunctionApplications().get("f");
    	assertEquals(7,set.size());
    }
    
    @Test
	public void substitutionBlockTest() {
		Object[] blocks = ml.getModel().getBlocks().parallelStream().filter(b -> !b.getModule().startsWith("common.")).toArray();
		Block b = (Block) blocks[5];
		assertEquals("Technology",b.getModule());
		assertEquals(0,b.getId());
	}

}
