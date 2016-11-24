package test.java.megalib.checker.services;

import static org.junit.Assert.*;

import org.java.megalib.checker.services.MegaModelLoader;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;
import org.junit.Test;

/**
 * 
 * @author heinz
 *
 * TODO : One could use mocking for this test class to raise test coverage and improve the test approach
 * 
 */
public class MegalibImportListenerTest {

	
	@Test
	public void complexTest(){
		MegaModelLoader ml = new MegaModelLoader();
		ml.loadFile("testsample/bcd/d/D.megal");
		MegaModel m = ml.getModel();
		assertEquals("Role",m.getInstanceOfMap().get("Information"));
		assertEquals("Artifact",m.getInstanceOfMap().get("softlangpage1"));
		assertEquals("Artifact",m.getInstanceOfMap().get("softlangpage2"));
		assertEquals("MarkupLanguage",m.getInstanceOfMap().get("HTML"));
		assertEquals("http://www.softlang.org/",m.getLinkMap().get("softlangpage1").get(0));
		assertEquals("http://www.softlang.org/",m.getLinkMap().get("softlangpage2").get(0));
		assertEquals(1,m.getRelationshipInstanceMap().get("correspondsTo").size());
		assertEquals(new Relation("softlangpage1","softlangpage2"),m.getRelationshipInstanceMap().get("correspondsTo").iterator().next());
	}
	
}
