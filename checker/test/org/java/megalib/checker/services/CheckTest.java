package org.java.megalib.checker.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.java.megalib.checker.services.WellformednessCheck;
import org.java.megalib.checker.services.ModelLoader;
import org.java.megalib.models.MegaModel;
import org.java.megalib.parser.ParserException;
import org.junit.Test;


public class CheckTest {

    @Test
    public void checkPrelude() {
        ModelLoader ml = new ModelLoader();
        assertEquals(0, ml.getTypeErrors().size());
        MegaModel m = ml.getModel();
        WellformednessCheck c = new WellformednessCheck(m);
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void checkInstanceOfTechnology() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/?T : Technology. " + "?L : ProgrammingLanguage. " + "?T uses ?L.";
        ml.loadString(input);
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void checkInstanceOfTechnologyUnderspec() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/T : Technology;=\"http://softlang.org/\". " + "?L : ProgrammingLanguage. " + "T uses ?L.";
        ml.loadString(input);
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(1, c.getWarnings().size());
        assertEquals("State a specific subtype of Technology for T.", c.getWarnings().get(0));
    }

    @Test
    public void checkInstanceOfLanguage() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/?T : Library. " + "?L : Language. " + "?T uses ?L.";
        ml.loadString(input);
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(0, ml.getTypeErrors().size());
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void checkInstanceOfLanguageUnderspec() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/?T : Library. " + "L : Language;=\"http://softlang.org/\";^uses ?T;^implements ?T.";
        ml.loadString(input);
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(0, ml.getTypeErrors().size());
        assertEquals(1, c.getWarnings().size());
        assertTrue(c.getWarnings()
                    .contains("State a specific subtype of Language for L."));
    }

    @Test
    public void checkLinkExistence() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/T : Library. " + "?L : ProgrammingLanguage. " + "T uses ?L.";
        ml.loadString(input);
        assertEquals(0, ml.getTypeErrors().size());
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);

        assertEquals(1, c.getWarnings().size());
        assertEquals("Link missing for entity T.", c.getWarnings().get(0));
    }

    @Test
    public void checkTechnologyUsesLanguage() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/T : Library; = \"http://softlang.wikidot.com/\".";
        ml.loadString(input);
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);

        assertEquals(0, ml.getTypeErrors().size());
        assertEquals(1, c.getWarnings().size());
        assertEquals("State a used language for T", c.getWarnings().get(0));
    }

    @Test
    public void checkFunctionImplementation() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/?L : ProgrammingLanguage. " + "f : ?L -> ?L. " + "?a : Artifact. " + "?a elementOf ?L. "
                       + "?a hasRole MvcModel." + "?a manifestsAs File. " + "f(?a)|->?a.";
        ml.loadString(input);
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);

        assertEquals(0, ml.getTypeErrors().size());
        assertEquals(1, c.getWarnings().size());
        assertTrue(c.getWarnings().contains("The function f is not implemented. Please state what implements it."));
    }

    @Test
    public void checkFunctionApplication() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/?L : ProgrammingLanguage. " + "f : ?L -> ?L. " + "?a : Artifact. " + "?a elementOf ?L. "
                       + "?a hasRole MvcModel. " + "?a manifestsAs File. " + "?T : Library. " + "?T uses ?L. "
                       + "?T implements f.";
        ml.loadString(input);
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(0, ml.getTypeErrors().size());
        assertEquals(1, c.getWarnings().size());
        assertTrue(c.getWarnings().contains("The function f is not applied yet. Please state an actual application."));
    }

    @Test
    public void checkArtifactElementOf() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/?a : Artifact. " + "?a hasRole MvcModel. " + "?a manifestsAs File.";
        ml.loadString(input);
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(0, ml.getTypeErrors().size());
        assertEquals(1, c.getWarnings().size());
        assertTrue(c.getWarnings().contains("Language missing for artifact ?a"));
    }

    @Test
    public void checkArtifactManifestsAs() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/?L : ProgrammingLanguage. " + "?a : Artifact. " + "?a elementOf ?L. "
                       + "?a hasRole MvcModel. ";
        ml.loadString(input);
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(0, ml.getTypeErrors().size());
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void checkAbstractArtifactHasRole() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/?L : ProgrammingLanguage. " + "?a : Artifact. " + "?a elementOf ?L. "
                       + "?a manifestsAs File.";
        ml.loadString(input);
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(0, ml.getTypeErrors().size());
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void checkArtifactHasRole() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/?L : ProgrammingLanguage. a : Artifact; ~= \"http://softlang.org/\". a elementOf ?L. a manifestsAs File.";
        ml.loadString(input);
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(0, ml.getTypeErrors().size());
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void checkCyclicSubsets() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/?L1 : ProgrammingLanguage. " + "?L2 : ProgrammingLanguage. " + "?L3 : ProgrammingLanguage. "
                       + "?L1 subsetOf ?L2. " + "?L2 subsetOf ?L3. " + "?L3 subsetOf ?L2.";
        ml.loadString(input);
        WellformednessCheck c = new WellformednessCheck(ml.getModel(), true);
        assertEquals(0, ml.getTypeErrors().size());
        assertEquals(1, c.getWarnings().size());
        assertTrue(c.getWarnings()
                    .contains("Cycles exist concerning the relationship subsetOf involving the following entities :[?L2, ?L3]"));
    }

    @Test
    public void checkLinkNotWorking() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/?L : ProgrammingLanguage; " + " = \"http://www.nowebsitehere.de\".";
        ml.loadString(input);
        WellformednessCheck c = new WellformednessCheck(ml.getModel());
        assertEquals(0, ml.getTypeErrors().size());
        assertEquals(1, c.getWarnings().size());
        assertEquals("Cannot resolve link to ?L: http://www.nowebsitehere.de", c.getWarnings().get(0));
    }

    @Test
    public void checkLinkIntoJar() throws ParserException, IOException{
    	ModelLoader ml = new ModelLoader();
        String input = "/**/a : Artifact; ~= \"file://checker.jar/org/main/antlr/techdocgrammar/Megalib.g4\"; elementOf Java; manifestsAs File.";
        ml.loadString(input);
        WellformednessCheck c = new WellformednessCheck(ml.getModel());
        assertEquals(0, ml.getTypeErrors().size());
        assertEquals(0, c.getWarnings().size());
    }

    @Test
    public void checkNoLinkTransient() throws ParserException, IOException {
        ModelLoader ml = new ModelLoader();
        String input = "/**/a : Artifact; elementOf Java; manifestsAs Transient.";
        ml.loadString(input);
        WellformednessCheck c = new WellformednessCheck(ml.getModel());
        assertEquals(0, ml.getTypeErrors().size());
        assertEquals(2, c.getWarnings().size());
        assertEquals("Binding missing for Artifact a.", c.getWarnings().get(0));
        assertEquals("The following transients are neither input nor output of a function application: [a]",
                     c.getWarnings().get(1));
    }

    @Test
    public void checkComplexFunApp() throws ParserException, IOException{
    	ModelLoader ml = new ModelLoader();
        String input = "/**/a : Artifact; ~= \"softlang.org\"; elementOf Java; manifestsAs Transient. f : Java # Java # Java -> Java # Java # Java. "
                       + "?T : Technology; implements f. f(a,a,a) |-> (a,a,a).";
        ml.loadString(input);
        WellformednessCheck c = new WellformednessCheck(ml.getModel(),true);
        assertEquals(0, ml.getTypeErrors().size());
        assertEquals(0, c.getWarnings().size());
    }
}
