package test.java.megalib.checker.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.java.megalib.checker.services.Check;
import org.java.megalib.checker.services.MegaModelLoader;
import org.java.megalib.models.MegaModel;
import org.junit.Test;


/**
 * 
 * @author heinz
 *
 * This test class assures the correctness of further checks.
 * 
 */
public class CheckTest {
	
	@Test
	public void checkInstanceOfTechnology() {
		String input = "?t : Technology "
				+ "?l : ProgrammingLanguage "
				+ "?t uses ?l";
		MegaModel m = new MegaModelLoader().loadString(input);
		Check c = new Check(m);
		
		assertEquals(0,m.getCriticalWarnings().size());
		assertEquals(1,c.getWarnings().size());
		assertTrue(c.getWarnings().contains("The entity ?t is underspecified. Please state a specific subtype of Technology."));
	}
	
	@Test
	public void checkInstanceOfLanguage(){
		String input = "?t : Library "
				+ "?l : Language "
				+ "?t uses ?l";
		MegaModel m = new MegaModelLoader().loadString(input);
		Check c = new Check(m);
		
		assertEquals(0,m.getCriticalWarnings().size());
		assertEquals(1,c.getWarnings().size());
		assertTrue(c.getWarnings().contains("The entity ?l is underspecified. Please state a specific subtype of Language."));
	}
	
	@Test
	public void checkLinkExistence(){
		String input = "t : Library "
				+ "l : ProgrammingLanguage "
				+ "t uses l";
		MegaModel m = new MegaModelLoader().loadString(input);
		Check c = new Check(m);
		
		assertEquals(0,m.getCriticalWarnings().size());
		assertEquals(2,c.getWarnings().size());
		assertTrue(c.getWarnings().contains("The entity l misses a Link for further reading."));
		assertTrue(c.getWarnings().contains("The entity t misses a Link for further reading."));
	}
	
	@Test
	public void checkTechnologyUsesLanguage(){
		String input = "?t : Library";
		MegaModel m = new MegaModelLoader().loadString(input);
		Check c = new Check(m);
		
		assertEquals(0,m.getCriticalWarnings().size());
		assertEquals(1,c.getWarnings().size());
		assertTrue(c.getWarnings().contains("The technology ?t does not use any language. Please state language usage."));
	}
	
	@Test
	public void checkFunctionImplementation(){
		String input = "?l : ProgrammingLanguage "
				+ "f : ?l -> ?l "
				+ "?a : Artifact<?l,MvcModel,File> "
				+ "f(?a)|->?a";
		MegaModel m = new MegaModelLoader().loadString(input);
		Check c = new Check(m);
		
		assertEquals(0,m.getCriticalWarnings().size());
		assertEquals(1,c.getWarnings().size());
		assertTrue(c.getWarnings().contains("The function f is not implemented. Please state what implements it."));
	}
	
	@Test
	public void checkFunctionApplication(){
		String input = "?l : ProgrammingLanguage "
				+ "f : ?l -> ?l "
				+ "?a : Artifact<?l,MvcModel,File> "
				+ "?t : Library "
				+ "?t uses ?l "
				+ "?t implements f";
		MegaModel m = new MegaModelLoader().loadString(input);
		Check c = new Check(m);
		assertEquals(0,m.getCriticalWarnings().size());
		assertEquals(1,c.getWarnings().size());
		assertTrue(c.getWarnings().contains("The function f is not applied yet. Please state an actual application."));
	}
	
	@Test
	public void checkArtifactElementOf(){
		String input = "?a : Artifact "
				+ "?a hasRole MvcModel "
				+ "?a manifestsAs File";
		MegaModel m = new MegaModelLoader().loadString(input);
		Check c = new Check(m);
		assertEquals(0,m.getCriticalWarnings().size());
		assertEquals(1,c.getWarnings().size());
		assertTrue(c.getWarnings().contains("Language missing for artifact ?a"));
	}
	
	@Test
	public void checkArtifactManifestsAs(){
		String input = "?l : ProgrammingLanguage "
				+ "?a : Artifact "
				+ "?a elementOf ?l "
				+ "?a hasRole MvcModel ";
		MegaModel m = new MegaModelLoader().loadString(input);
		Check c = new Check(m);
		assertEquals(0,m.getCriticalWarnings().size());
		assertEquals(1,c.getWarnings().size());
		assertTrue(c.getWarnings().contains("Manifestation misssing for ?a"));
	}
	
	@Test
	public void checkArtifactHasRole(){
		String input = "?l : ProgrammingLanguage "
				+ "?a : Artifact "
				+ "?a elementOf ?l "
				+ "?a manifestsAs File";
		MegaModel m = new MegaModelLoader().loadString(input);
		Check c = new Check(m);
		assertEquals(0,m.getCriticalWarnings().size());
		assertEquals(1,c.getWarnings().size());
		assertTrue(c.getWarnings().contains("Role misssing for ?a"));
	}
	
	@Test
	public void checkCyclicSubsets(){
		String input = "?l1 : ProgrammingLanguage "
				+ "?l2 : ProgrammingLanguage "
				+ "?l3 : ProgrammingLanguage "
				+ "?l1 subsetOf ?l2 "
				+ "?l2 subsetOf ?l3 "
				+ "?l3 subsetOf ?l2";
		MegaModel m = new MegaModelLoader().loadString(input);
		Check c = new Check(m);
		assertEquals(0,m.getCriticalWarnings().size());
		assertEquals(1,c.getWarnings().size());
		assertTrue(c.getWarnings().contains("Cycles exist concerning the relationship subsetOf involving the following entities :[?l2, ?l3]"));
	}
	
	@Test
	public void checkNotWellformedURI(){
		String input = "?l : ProgrammingLanguage "
				+ "?l = \"notauri\"";
		MegaModel m = new MegaModelLoader().loadString(input);
		Check c = new Check(m);
		assertEquals(0,m.getCriticalWarnings().size());
		assertEquals(1,c.getWarnings().size());
		assertTrue(c.getWarnings().contains("Error at Link to 'notauri' : The URL is malformed!"));
	}
	
	@Test
	public void checkLinkNotWorking(){
		String input = "?l : ProgrammingLanguage "
				+ "?l = \"http://www.nowebsitehere.de\"";
		MegaModel m = new MegaModelLoader().loadString(input);
		Check c = new Check(m);
		assertEquals(0,m.getCriticalWarnings().size());
		assertEquals(1,c.getWarnings().size());
		assertTrue(c.getWarnings().contains("Error at Link to 'http://www.nowebsitehere.de' : Connection failed!"));
	}
}