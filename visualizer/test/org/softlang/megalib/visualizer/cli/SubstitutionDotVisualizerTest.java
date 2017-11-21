package org.softlang.megalib.visualizer.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.softlang.megalib.visualizer.Visualizer;
import org.softlang.megalib.visualizer.VisualizerOptions;
import org.softlang.megalib.visualizer.models.Edge;
import org.softlang.megalib.visualizer.models.Graph;
import org.softlang.megalib.visualizer.models.ModelToGraph;
import org.softlang.megalib.visualizer.models.Node;
import org.softlang.megalib.visualizer.models.transformation.TransformerRegistry;
import org.softlang.megalib.visualizer.transformation.dot.DOTTransformer;

public class SubstitutionDotVisualizerTest {

	private List<Graph> graphs;
	private VisualizerOptions options;
	
	@Before
	public void setUp() {
		TransformerRegistry.registerTransformer("dot", (VisualizerOptions options)
				-> new DOTTransformer(options));
		CommandLine cli = new CommandLine(Arrays.asList("dot"));
		
		String data[] = {"-f", "../checker/testsample/SubstitutionDemo/App.megal", "-t", "dot"};
	    cli.parse(data);
	    options = VisualizerOptions.of(cli.getRequiredArguments());
	    ModelToGraph mtg = new ModelToGraph(options);
	    assertTrue(mtg.loadModel());
	    graphs = mtg.createBlockGraphs();
	    assertEquals(10,graphs.size());
	}
	
	@Test
	public void testFilesExist() {
		graphs.forEach(new Visualizer(options)::plotGraph);
		assertTrue(new File("../output/SubstitutionDemo/App0.dot").exists());
		assertTrue(new File("../output/SubstitutionDemo/App0.png").exists());
	}

	@Test
	public void testGraph0() {
		Graph g1 = graphs.get(0);
		assertEquals("SubstitutionDemo.BaseTechnology0",g1.getName());
		
		Map<String, Node> nmap = g1.getNodes();
		assertEquals(4,nmap.size());
		Set<Edge> edges = g1.getEdges();
		assertEquals(4,edges.size());
	}
	
	@Test
	public void testGraph1() {
		Graph g1 = graphs.get(1);
		assertEquals("SubstitutionDemo.Abstract0",g1.getName());
		
		Map<String, Node> nmap = g1.getNodes();
		assertEquals(5,nmap.size());
		Set<Edge> edges = g1.getEdges();
		assertEquals(4,edges.size());
		assertTrue(edges.contains(new Edge(nmap.get("?Serializer"), nmap.get("?PL"), "uses")));
	}
	
	@Test
	public void testGraph2() {
		Graph g1 = graphs.get(2);
		assertEquals("SubstitutionDemo.Abstract1",g1.getName());
		
		Map<String, Node> nmap = g1.getNodes();
		assertEquals(7,nmap.size());
		Set<Edge> edges = g1.getEdges();
		assertEquals(6,edges.size());
		assertTrue(edges.contains(new Edge(nmap.get("?OL"),nmap.get("f"),  "domainOf_0")));
	}
	
	@Test
	public void testGraph4() {
		Graph g1 = graphs.get(4);
		assertEquals("SubstitutionDemo.Abstract3",g1.getName());
		
		Map<String, Node> nmap = g1.getNodes();
		assertEquals(8,nmap.size());
		Set<Edge> edges = g1.getEdges();
		assertEquals(8,edges.size());
		assertTrue(edges.contains(new Edge( nmap.get("?object"),nmap.get("f"), "inputOf_0")));
	}
	
	@Test
	public void testGraph5() {
		Graph g1 = graphs.get(5);
		assertEquals("SubstitutionDemo.Technology0",g1.getName());
		
		Map<String, Node> nmap = g1.getNodes();
		assertEquals(15,nmap.size());
		Set<Edge> edges = g1.getEdges();
		assertEquals(21,edges.size());
	}
	
	@Test
	public void testGraph9() {
		Graph g = graphs.get(9);
		assertEquals("SubstitutionDemo.App2",g.getName());
	}

}
