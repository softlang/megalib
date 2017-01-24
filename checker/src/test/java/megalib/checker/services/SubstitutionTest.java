package test.java.megalib.checker.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.java.megalib.checker.services.ModelLoader;
import org.java.megalib.checker.services.WellformednessCheck;
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
        ml.loadFile("testsample/bcd/d/D.megal");
    }

    @Test
    public void loadATest() throws IOException {
        ModelLoader ml = new ModelLoader();
        ml.loadFile("testsample/A.megal");
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        System.out.println(c.getWarnings().get(0));
        assertEquals(1, c.getWarnings().size());
    }

    @Test
    public void loadBTest() throws IOException {
        ModelLoader ml = new ModelLoader();
        ml.loadFile("testsample/bcd/B.megal");
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        System.out.println(c.getWarnings().get(0));
        assertEquals(1, c.getWarnings().size());
    }

    @Test
    public void loadCTest() throws IOException {
        ModelLoader ml = new ModelLoader();
        ml.loadFile("testsample/bcd/C.megal");
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(1, c.getWarnings().size());
    }

    @Test
    public void loadDTest() {
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        c.getWarnings().forEach(w -> System.out.println(w));
        assertEquals(1, c.getWarnings().size());
    }

    @Test
    public void checkAbstractEntitiesExist() {
        assertTrue(ml.getModel().getInstanceOfMap().containsKey("?a1"));
        assertTrue(ml.getModel().getInstanceOfMap().containsKey("?a2"));
        assertTrue(ml.getModel().getInstanceOfMap().containsKey("?l"));
    }

    @Test
    public void complexTest() {
        assertEquals("Role", ml.getModel().getInstanceOfMap().get("WebResource"));
        assertEquals("Artifact", ml.getModel().getInstanceOfMap().get("softlangpage1"));
        assertEquals("Artifact", ml.getModel().getInstanceOfMap().get("softlangpage2"));
        assertEquals("Artifact", ml.getModel().getInstanceOfMap().get("softlangpage3"));
        assertEquals("MarkupLanguage", ml.getModel().getInstanceOfMap().get("HTML"));
        assertEquals("http://www.softlang.org/", ml.getModel().getLinkMap().get("softlangpage1").iterator().next());
        assertEquals("http://www.softlang.org/", ml.getModel().getLinkMap().get("softlangpage2").iterator().next());
        assertEquals("http://www.softlang.org/", ml.getModel().getLinkMap().get("softlangpage3").iterator().next());
        assertEquals(1, ml.getModel().getRelationshipInstanceMap().get("correspondsTo").size());
        assertEquals(new Relation("softlangpage1", "softlangpage2"),
                     ml.getModel().getRelationshipInstanceMap().get("correspondsTo").iterator().next());
        int size = ml.getModel().getInstanceOfMap().size();
        ml.getModel().cleanUpAbstraction();
        assertEquals(size - 2, ml.getModel().getInstanceOfMap().size());
        for (String i : ml.getModel().getInstanceOfMap().keySet()) {
            assertFalse(i.startsWith("?"));
        }
    }

}
