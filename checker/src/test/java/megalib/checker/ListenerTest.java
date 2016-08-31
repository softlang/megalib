package test.java.megalib.checker;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
	public void enterEntityDeclarationFillsEntityDeclarations() throws IOException{
		String input = "DerivedType < Type";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, String> actual = sut.getModel().getSubtypesMap();
		
		assertTrue(actual.containsKey("DerivedType"));
		assertEquals(actual.get("DerivedType").toString(),"Type");
	}
	
	@Test
	public void enterEntityInstanceFillsEntityInstances() throws IOException {
		String input = "Instance : Type";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, String> actual = sut.getModel().getInstanceOfMap();
		
		assertTrue(actual.containsKey("Instance"));
		assertEquals(actual.get("Instance").toString(),"Type");
	}
	
	@Test
	public void enterRelationDeclarationFillsRelationDeclarationsWithRelationName() throws IOException {
		String input = "Relation < TypeOne # TypeTwo";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, Set<List<String>>> actual = sut.getModel().getRelationDeclarationMap();
		
		assertTrue(actual.containsKey("Relation"));		
	}
	
	@Test
	public void enterRelationDeclarationFillsRelationDeclarationsWithTypes() throws IOException {
		String input = "Relation < TypeOne # TypeTwo";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, Set<List<String>>> actual = sut.getModel().getRelationDeclarationMap();
		
		Set<List<String>> types = actual.get("Relation");
		String[] expected = {"TypeOne","TypeTwo"};
		
		assertTrue(types.contains(Arrays.asList(expected)));
	}
	
	@Test
	public void enterRelationDeclarationDoesNotOverideExistingDeclarations() throws IOException {
		String input = "Relation < TypeOne # TypeTwo\nRelation < TypeThree # TypeFour";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, Set<List<String>>> actual = sut.getModel().getRelationDeclarationMap();
		
		Set<List<String>> types = actual.get("Relation");
		String[] expected1 = {"TypeOne","TypeTwo"};
		String[] expected2 = {"TypeThree","TypeFour"};
		
		assertTrue(types.contains(Arrays.asList(expected1)));
		assertTrue(types.contains(Arrays.asList(expected2)));
	}
	
	@Test
	public void enterRelationInstanceFillsRelationInstancesWithRelationName() throws IOException {
		String input = "ObjectOne Relation ObjectTwo";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, Set<List<String>>> actual = sut.getModel().getRelationshipInstanceMap();
		
		assertTrue(actual.containsKey("Relation"));
	}
	
	@Test
	public void enterRelationInstanceFillsRelationInstancesWithObjects() throws IOException {
		String input = "ObjectOne Relation ObjectTwo";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, Set<List<String>>> actual = sut.getModel().getRelationshipInstanceMap();
		
		Set<List<String>> types = actual.get("Relation");
		String[] expected = {"ObjectOne","ObjectTwo"};
		
		assertTrue(types.contains(Arrays.asList(expected)));
	}
	
	@Test
	public void enterRelationInstanceDoesNotOverideExistingInstances() throws IOException {
		String input = "ObjectOne Relation ObjectTwo\nObjectThree Relation ObjectFour";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, Set<List<String>>> actual = sut.getModel().getRelationshipInstanceMap();
		
		Set<List<String>> types = actual.get("Relation");
		String[] expected1 = {"ObjectOne","ObjectTwo"};
		String[] expected2 = {"ObjectThree","ObjectFour"};
		
		assertTrue(types.contains(Arrays.asList(expected1)));
		assertTrue(types.contains(Arrays.asList(expected2)));
	}
	
	@Test
	public void enterFunctionDeclarationFillsFunctionDeclarationsWithFunctionName() throws IOException {
		String input = "Function : TypeOne # TypeTwo -> ReturnType";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, Set<Function>> actual = sut.getModel().getFunctionDeclarations();
		
		assertTrue(actual.containsKey("Function"));
	}
	
	@Test
	public void enterFunctionDeclarationFillsFunctionDeclarationsWithFunction() throws IOException {
		String input = "Function : TypeOne # TypeTwo -> ReturnType";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, Set<Function>> actual = sut.getModel().getFunctionDeclarations();
		
		Set<Function> types = actual.get("Function");
		
		assertEquals(1,types.size());
	}
	
	@Test
	public void enterFunctionDeclarationDoesNotOverideExistingDeclarations() throws IOException {
		String input = "Function : TypeOne # TypeTwo -> ReturnTypeOne\nFunction : TypeThree # TypeFour -> ReturnTypeTwo";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, Set<Function>> actual = sut.getModel().getFunctionDeclarations();
		
		Set<Function> types = actual.get("Function");
		
		assertEquals(2,types.size());
	}
	
	@Test
	public void enterFunctionInstanceFillsFunctionInstanceWithFunctionName() throws IOException {
		String input = "Function(ObjectOne , ObjectTwo) |-> Result";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, Set<Function>> actual = sut.getModel().getFunctionInstances();
		
		assertTrue(actual.containsKey("Function"));
	}
	
	@Test
	public void enterFunctionInstanceFillsFunctionInstanceWithFunction() throws IOException {
		String input = "Function(ObjectOne , ObjectTwo) |-> Result";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, Set<Function>> actual = sut.getModel().getFunctionInstances();
		
		Set<Function> functions = actual.get("Function");
		
		assertEquals(1,functions.size());
	}
	
	@Test
	public void enterFunctionInstanceDoesNotOverideExistingInstances() throws IOException {
		String input = "Function(ObjectOne , ObjectTwo) |-> Result\nFunction(ObjectThree , ObjectFour) |-> ResultTwo";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, Set<Function>> actual = sut.getModel().getFunctionInstances();
		
		Collection<Function> types = actual.get("Function");
		
		assertEquals(2,types.size());
	}
	
	@Test
	public void enterLinkFillsLinksWithName() throws IOException {
		String input = "Name = \"test\"";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, List<String>> actual = sut.getModel().getLinkMap();
		
		assertTrue(actual.containsKey("Name"));
	}
	
	@Test
	public void enterLinkFillsLinksWithLinkString() throws IOException {
		String input = "Name = \"test\"";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, List<String>> actual = sut.getModel().getLinkMap();
		
		String link = actual.get("Name").get(0);
		assertEquals("test", link);
	}
	
	@Test
	public void enterLinkFillsLinksDoesNotOverride() throws IOException {
		String input = "Name = \"test\"\nName = \"testTwo\"";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		Listener sut = MegaModelLoader.getListener(stream);
		Map<String, List<String>> actual = sut.getModel().getLinkMap();
		
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
