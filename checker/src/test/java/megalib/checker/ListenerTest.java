package test.java.megalib.checker;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.java.megalib.checker.services.Checker;
import org.java.megalib.checker.services.Listener;
import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mmay, aemmerichs
 *
 */
public class ListenerTest {
	private Listener sut;
	
	private Checker checker;

	@Before
	public void setUp() {
		checker = new Checker();
	}
	
	@Test
	public void enterEntityDeclarationFillsEntityDeclarations() throws IOException{
		String input = "DerivedType < Type";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, String> actual = sut.getModel().entityDeclarations;
		
		assertTrue(actual.containsKey("DerivedType"));
		assertEquals(actual.get("DerivedType").toString(),"Type");
	}
	
	@Test
	public void enterEntityInstanceFillsEntityInstances() throws IOException {
		String input = "Instance : Type";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, String> actual = sut.getModel().entityInstances;
		
		assertTrue(actual.containsKey("Instance"));
		assertEquals(actual.get("Instance").toString(),"Type");
	}
	
	@Test
	public void enterRelationDeclarationFillsRelationDeclarationsWithRelationName() throws IOException {
		String input = "Relation < TypeOne # TypeTwo";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, Map<Integer, LinkedList<String>>> actual = sut.getModel().relationDeclarations;
		
		assertTrue(actual.containsKey("Relation"));		
	}
	
	@Test
	public void enterRelationDeclarationFillsRelationDeclarationsWithTypes() throws IOException {
		String input = "Relation < TypeOne # TypeTwo";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, Map<Integer, LinkedList<String>>> actual = sut.getModel().relationDeclarations;
		
		Collection<LinkedList<String>> types = actual.get("Relation").values();
		LinkedList<String> expected = new LinkedList<>();
		expected.add("TypeOne");
		expected.add("TypeTwo");
		
		assertTrue(types.contains(expected));
	}
	
	@Test
	public void enterRelationDeclarationDoesNotOverideExistingDeclarations() throws IOException {
		String input = "Relation < TypeOne # TypeTwo\nRelation < TypeThree # TypeFour";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, Map<Integer, LinkedList<String>>> actual = sut.getModel().relationDeclarations;
		
		Collection<LinkedList<String>> types = actual.get("Relation").values();
		LinkedList<String> expected1 = new LinkedList<>();
		expected1.add("TypeOne");
		expected1.add("TypeTwo");
		LinkedList<String> expected2 = new LinkedList<>();
		expected2.add("TypeThree");
		expected2.add("TypeFour");
		
		assertTrue(types.contains(expected1));
		assertTrue(types.contains(expected2));
	}
	
	@Test
	public void enterRelationInstanceFillsRelationInstancesWithRelationName() throws IOException {
		String input = "ObjectOne Relation ObjectTwo";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, Map<Integer, LinkedList<String>>> actual = sut.getModel().relationInstances;
		
		assertTrue(actual.containsKey("Relation"));
	}
	
	@Test
	public void enterRelationInstanceFillsRelationInstancesWithObjects() throws IOException {
		String input = "ObjectOne Relation ObjectTwo";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, Map<Integer, LinkedList<String>>> actual = sut.getModel().relationInstances;
		
		Collection<LinkedList<String>> types = actual.get("Relation").values();
		LinkedList<String> expected = new LinkedList<>();
		expected.add("ObjectOne");
		expected.add("ObjectTwo");
		
		assertTrue(types.contains(expected));
	}
	
	@Test
	public void enterRelationInstanceDoesNotOverideExistingInstances() throws IOException {
		String input = "ObjectOne Relation ObjectTwo\nObjectThree Relation ObjectFour";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, Map<Integer, LinkedList<String>>> actual = sut.getModel().relationInstances;
		
		Collection<LinkedList<String>> types = actual.get("Relation").values();
		LinkedList<String> expected1 = new LinkedList<>();
		expected1.add("ObjectOne");
		expected1.add("ObjectTwo");
		LinkedList<String> expected2 = new LinkedList<>();
		expected2.add("ObjectThree");
		expected2.add("ObjectFour");
		
		assertTrue(types.contains(expected1));
		assertTrue(types.contains(expected2));
	}
	
	@Test
	public void enterFunctionDeclarationFillsFunctionDeclarationsWithFunctionName() throws IOException {
		String input = "Function : TypeOne # TypeTwo -> ReturnType";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, Map<Integer, Function>> actual = sut.getModel().functionDeclarations;
		
		assertTrue(actual.containsKey("Function"));
	}
	
	@Test
	public void enterFunctionDeclarationFillsFunctionDeclarationsWithFunction() throws IOException {
		String input = "Function : TypeOne # TypeTwo -> ReturnType";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, Map<Integer, Function>> actual = sut.getModel().functionDeclarations;
		
		Collection<Function> types = actual.get("Function").values();
		
		assertEquals(1,types.size());
	}
	
	@Test
	public void enterFunctionDeclarationDoesNotOverideExistingDeclarations() throws IOException {
		String input = "Function : TypeOne # TypeTwo -> ReturnTypeOne\nFunction : TypeThree # TypeFour -> ReturnTypeTwo";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, Map<Integer, Function>> actual = sut.getModel().functionDeclarations;
		
		Collection<Function> types = actual.get("Function").values();
		
		assertEquals(2,types.size());
	}
	
	@Test
	public void enterFunctionInstanceFillsFunctionInstanceWithFunctionName() throws IOException {
		String input = "Function(ObjectOne , ObjectTwo) |-> Result";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, Map<Integer, Function>> actual = sut.getModel().functionInstances;
		
		assertTrue(actual.containsKey("Function"));
	}
	
	@Test
	public void enterFunctionInstanceFillsFunctionInstanceWithFunction() throws IOException {
		String input = "Function(ObjectOne , ObjectTwo) |-> Result";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, Map<Integer, Function>> actual = sut.getModel().functionInstances;
		
		Collection<Function> types = actual.get("Function").values();
		
		assertEquals(1,types.size());
	}
	
	@Test
	public void enterFunctionInstanceDoesNotOverideExistingInstances() throws IOException {
		String input = "Function(ObjectOne , ObjectTwo) |-> Result\nFunction(ObjectThree , ObjectFour) |-> ResultTwo";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, Map<Integer, Function>> actual = sut.getModel().functionInstances;
		
		Collection<Function> types = actual.get("Function").values();
		
		assertEquals(2,types.size());
	}
	
	@Test
	public void enterLinkFillsLinksWithName() throws IOException {
		String input = "Name = test";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, LinkedList<String>> actual = sut.getModel().links;
		
		assertTrue(actual.containsKey("Name"));
	}
	
	@Test
	public void enterLinkFillsLinksWithLinkString() throws IOException {
		String input = "Name = test";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, LinkedList<String>> actual = sut.getModel().links;
		
		String link = actual.get("Name").getFirst();
		assertEquals("test", link);
	}
	
	@Test
	public void enterLinkFillsLinksDoesNotOverride() throws IOException {
		String input = "Name = test\nName = testTwo";
		ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());
		
		sut = checker.getListener(stream);
		Map<String, LinkedList<String>> actual = sut.getModel().links;
		
		String link = actual.get("Name").getFirst();
		String link2 = actual.get("Name").getLast();
		
		assertEquals("test", link);
		assertEquals("testTwo", link2);
	}
	
	@Test
	public void getModelReturnsMegaModel() {
		sut = new Listener();
		Object actual = sut.getModel();
		assertThat(actual, instanceOf(MegaModel.class));
	}
}
