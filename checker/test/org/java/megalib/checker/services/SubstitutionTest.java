package org.java.megalib.checker.services;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.java.megalib.checker.services.ModelLoader;
import org.java.megalib.checker.services.WellformednessCheck;
import org.junit.Test;

/**
 * @author heinz
 */
public class SubstitutionTest {

    @Test
    public void testAbstract() throws IOException {
        ModelLoader ml = new ModelLoader();
        ml.loadFile("testsample/SubstitutionDemo/Abstract.megal");
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void testTechnology() throws IOException {
        ModelLoader ml = new ModelLoader();
        ml.loadFile("testsample/SubstitutionDemo/Technology.megal");
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        c.getWarnings().forEach(w -> System.out.println(w));
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void testApp() throws IOException {
        ModelLoader ml = new ModelLoader();
        ml.loadFile("testsample/SubstitutionDemo/App.megal");
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(0, c.getWarnings().size());
    }

}
