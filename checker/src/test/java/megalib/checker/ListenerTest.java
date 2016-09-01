package test.java.megalib.checker;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.java.megalib.checker.services.Listener;
import org.java.megalib.checker.services.MegaModelLoader;
import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;
import org.junit.Test;

/**
 * @author mmay, aemmerichs
 *
 */
public class ListenerTest {
	
	@Test
	public void enterEntityDeclarationFillsEntityDeclarations() {
		String input = "DerivedType < Type";
		Map<String, String> actual = new MegaModelLoader().createFromString(input).getSubtypesMap();
		
		assertTrue(actual.containsKey("DerivedType"));
		assertEquals(actual.get("DerivedType").toString(),"Type");
	}
	
	@Test
	public void enterEntityInstanceFillsEntityInstances(){
		String input = "Instance : Type";
		Map<String, String> actual = new MegaModelLoader().createFromString(input).getInstanceOfMap();
		
		assertTrue(actual.containsKey("Instance"));
		assertEquals(actual.get("Instance").toString(),"Type");
	}
	
	@Test
	public void enterRelationDeclarationFillsRelationDeclarationsWithRelationName()  {
		String input = "Relation < TypeOne # TypeTwo";
		Map<String, Set<List<String>>> actual = new MegaModelLoader().createFromString(input).getRelationDeclarationMap();
		
		assertTrue(actual.containsKey("Relation"));		
	}
	
	@Test
	public void enterRelationDeclarationFillsRelationDeclarationsWithTypes()  {
		String input = "Relation < TypeOne # TypeTwo";
		Map<String, Set<List<String>>> actual = new MegaModelLoader().createFromString(input).getRelationDeclarationMap();
		
		Set<List<String>> types = actual.get("Relation");
		String[] expected = {"TypeOne","TypeTwo"};
		
		assertTrue(types.contains(Arrays.asList(expected)));
	}
	
	@Test
	public void enterRelationDeclarationDoesNotOverideExistingDeclarations()  {
		String input = "Relation < TypeOne # TypeTwo\nRelation < TypeThree # TypeFour";
		Map<String, Set<List<String>>> actual = new MegaModelLoader().createFromString(input).getRelationDeclarationMap();
		
		Set<List<String>> types = actual.get("Relation");
		String[] expected1 = {"TypeOne","TypeTwo"};
		String[] expected2 = {"TypeThree","TypeFour"};
		
		assertTrue(types.contains(Arrays.asList(expected1)));
		assertTrue(types.contains(Arrays.asList(expected2)));
	}
	
	@Test
	public void enterRelationInstanceFillsRelationInstancesWithRelationName()  {
		String input = "ObjectOne Relation ObjectTwo";
		Map<String, Set<List<String>>> actual = new MegaModelLoader().createFromString(input).getRelationshipInstanceMap();
		
		assertTrue(actual.containsKey("Relation"));
	}
	
	@Test
	public void enterRelationInstanceFillsRelationInstancesWithObjects()  {
		String input = "ObjectOne Relation ObjectTwo";
		Map<String, Set<List<String>>> actual = new MegaModelLoader().createFromString(input).getRelationshipInstanceMap();
		
		Set<List<String>> types = actual.get("Relation");
		String[] expected = {"ObjectOne","ObjectTwo"};
		
		assertTrue(types.contains(Arrays.asList(expected)));
	}
	
	@Test
	public void enterRelationInstanceDoesNotOverideExistingInstances()  {
		String input = "ObjectOne Relation ObjectTwo\nObjectThree Relation ObjectFour";
		Map<String, Set<List<String>>> actual = new MegaModelLoader().createFromString(input).getRelationshipInstanceMap();
		
		Set<List<String>> types = actual.get("Relation");
		String[] expected1 = {"ObjectOne","ObjectTwo"};
		String[] expected2 = {"ObjectThree","ObjectFour"};
		
		assertTrue(types.contains(Arrays.asList(expected1)));
		assertTrue(types.contains(Arrays.asList(expected2)));
	}
	
	@Test
	public void enterFunctionDeclarationFillsFunctionDeclarationsWithFunctionName()  {
		String input = "Function : TypeOne # TypeTwo -> ReturnType";
		Map<String, Set<Function>> actual = new MegaModelLoader().createFromString(input).getFunctionDeclarations();
		
		assertTrue(actual.containsKey("Function"));
	}
	
	@Test
	public void enterFunctionDeclarationFillsFunctionDeclarationsWithFunction()  {
		String input = "Function : TypeOne # TypeTwo -> ReturnType";
		Map<String, Set<Function>> actual = new MegaModelLoader().createFromString(input).getFunctionDeclarations();
		
		Set<Function> types = actual.get("Function");
		
		assertEquals(1,types.size());
	}
	
	@Test
	public void enterFunctionDeclarationDoesNotOverideExistingDeclarations()  {
		String input = "Function : TypeOne # TypeTwo -> ReturnTypeOne\nFunction : TypeThree # TypeFour -> ReturnTypeTwo";
		Map<String, Set<Function>> actual = new MegaModelLoader().createFromString(input).getFunctionDeclarations();
		
		Set<Function> types = actual.get("Function");
		
		assertEquals(2,types.size());
	}
	
	@Test
	public void enterFunctionInstanceFillsFunctionInstanceWithFunctionName()  {
		String input = "Function(ObjectOne , ObjectTwo) |-> Result";
		Map<String, Set<Function>> actual = new MegaModelLoader().createFromString(input).getFunctionInstances();
		
		assertTrue(actual.containsKey("Function"));
	}
	
	@Test
	public void enterFunctionInstanceFillsFunctionInstanceWithFunction()  {
		String input = "Function(ObjectOne , ObjectTwo) |-> Result";
		Map<String, Set<Function>> actual = new MegaModelLoader().createFromString(input).getFunctionInstances();
		
		Set<Function> functions = actual.get("Function");
		
		assertEquals(1,functions.size());
	}
	
	@Test
	public void enterFunctionInstanceDoesNotOverideExistingInstances()  {
		String input = "Function(ObjectOne , ObjectTwo) |-> Result\nFunction(ObjectThree , ObjectFour) |-> ResultTwo";
		Map<String, Set<Function>> actual = new MegaModelLoader().createFromString(input).getFunctionInstances();
		
		Collection<Function> types = actual.get("Function");
		
		assertEquals(2,types.size());
	}
	
	@Test
	public void enterLinkFillsLinksWithName()  {
		String input = "Name = \"test\"";
		Map<String, List<String>> actual = new MegaModelLoader().createFromString(input).getLinkMap();
		
		assertTrue(actual.containsKey("Name"));
	}
	
	@Test
	public void enterLinkFillsLinksWithLinkString()  {
		String input = "Name = \"test\"";
		Map<String, List<String>> actual = new MegaModelLoader().createFromString(input).getLinkMap();
		
		String link = actual.get("Name").get(0);
		assertEquals("test", link);
	}
	
	@Test
	public void enterLinkFillsLinksDoesNotOverride()  {
		String input = "Name = \"test\"\nName = \"testTwo\"";
		Map<String, List<String>> actual = new MegaModelLoader().createFromString(input).getLinkMap();
		
		assertEquals(2,actual.get("Name").size());
		String link = actual.get("Name").get(0);
		String link2 = actual.get("Name").get(1);
		
		assertEquals("test", link);
		assertEquals("testTwo", link2);
	}
	
	@Test
	public void getModelReturnsMegaModel() {
		Listener sut = new Listener();
		Object actual = sut.getModel();
		assertThat(actual, instanceOf(MegaModel.class));
	}
}
