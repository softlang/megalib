package org.java.megalib.checker.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.java.megalib.models.Block;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;
import org.junit.Before;
import org.junit.Test;

public class ImportTest {

    private ModelLoader ml;
    private MegaModel model;

    @Before
    public void setUp() throws IOException {
        ml = new ModelLoader();
        ml.loadFile("testsample/ImportDemo/bcd/d/D.megal");
        model = ml.getModel();
    }

    @Test
    public void testNoTypeErrors() {
        assertEquals(0, ml.getTypeErrors().size());
    }

    @Test
    public void testNoWarnings() {
        TypeWarningCheck c = new TypeWarningCheck(model, true);
        c.getWarnings().forEach(System.out::println);
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void testImportGraph() {
        List<Relation> ig = model.getImportGraph();
        assertFalse(ig.isEmpty());
        ig.contains(new Relation("bcd.d.D", "bcd.C"));
        ig.contains(new Relation("bcd.C", "A"));
        ig.contains(new Relation("bcd.C", "bcd.B"));
        ig.contains(new Relation("bcd.B", "A"));
    }

    @Test
    public void testContent() {
        assertTrue(model.isInstanceOf("?spec", "Artifact"));
        assertTrue(model.isInstanceOf("?a1", "Artifact"));
        assertTrue(model.isInstanceOf("?a2", "Artifact"));
        assertTrue(model.isInstanceOf("f", "Function"));
    }

    @Test
    public void testBlockWisdom() {
        // every model's relationship knows its block and every block knows
        // subject and object
        for(Set<Relation> rset : model.getRelationships().values()){
            for(Relation r : rset){
                if(model.getSubtypesMap().containsKey(r.getSubject())
                   || model.getRelationshipDeclarationMap().containsKey(r.getSubject())){
                    continue;
                }
                Block b = r.getBlock();
                assertNotNull(b);
                assertNotNull(b.getInstanceOfMap().get(r.getSubject()));
                assertNotNull(b.getInstanceOfMap().get(r.getObject()));
            }
        }

        for(Block b : model.getBlocks()){
            for(Set<Relation> rset : b.getRelationships().values()){
                for(Relation r : rset){
                    if(model.getSubtypesMap().containsKey(r.getSubject())){
                        System.err.println(r.getSubject() + "---" + r.getObject());
                    }
                    assertNotNull(b.getInstanceOfMap().get(r.getSubject()));
                    assertNotNull(b.getInstanceOfMap().get(r.getObject()));
                }
            }
        }
    }

    @Test
    public void testMissingModule() throws IOException {
        ml = new ModelLoader();
        ml.loadFile("testsample/ImportDemo/F.megal");
        assertEquals("Error Missing import target: from F to E", ml.getTypeErrors().get(0));
    }

}