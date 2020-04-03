package org.java.megalib.checker.services;

import static org.junit.Assert.*;

import org.java.megalib.models.Relation;
import org.junit.Before;
import org.junit.Test;

public class SubstitutionChainTest {

    private ModelLoader ml;

    @Before
    public void setUp() throws Exception {
        ml = new ModelLoader();
        ml.loadFile("testsample/SubstitutionChainDemo/demo/Usage1.megal");
        ml.getTypeErrors().forEach(System.out::println);
        assertEquals(0, ml.getTypeErrors().size());
    }

    @Test
    public void testInstancesOfModel() {
        assertFalse(ml.getModel().getRelationships().get("conformsTo")
                      .contains(new Relation("umlInstanceModel", "?model1")));
        assertTrue(ml.getModel().getRelationships().get("conformsTo")
                     .contains(new Relation("umlInstanceModel", "umlModel")));
    }
}