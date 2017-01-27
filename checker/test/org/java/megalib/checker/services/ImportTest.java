package org.java.megalib.checker.services;

import static org.junit.Assert.*;

import java.io.IOException;

import org.java.megalib.checker.services.ModelLoader;
import org.java.megalib.checker.services.WellformednessCheck;
import org.junit.Before;
import org.junit.Test;

public class ImportTest {

    private ModelLoader ml;

    @Before
    public void setUp() throws IOException {
        ml = new ModelLoader();
        ml.loadFile("testsample/ImportDemo/bcd/d/D.megal");
    }

    @Test
    public void testNoTypeErrors() {
        assertEquals(0, ml.getTypeErrors().size());
    }

    @Test
    public void testNoWarnings() {
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void testContent() {
        assertTrue(ml.getModel().isInstanceOf("?a1", "Artifact"));
        assertTrue(ml.getModel().isInstanceOf("?a2", "Artifact"));
        assertTrue(ml.getModel().isInstanceOf("HTML", "Language"));
        assertTrue(ml.getModel().isInstanceOf("f", "Function"));
    }

}
