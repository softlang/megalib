package org.softlang.megalib.visualizer.cli;

import static org.junit.Assert.*;

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

public class SubstitutionDemoTest {

	private List<Graph> graphs;
	private Visualizer vis;
	
	@Before
	public void setUp() {
		TransformerRegistry.registerTransformer("dot", (VisualizerOptions options)
	   		     -> new DOTTransformer(options));
	    CommandLine cli = new CommandLine(Arrays.asList("graphviz", "dot"));

	    String data[] = {"-f", "../checker/testsample/SubstitutionDemo/App.megal", "-t", "dot"};
	    cli.parse(data);
	    VisualizerOptions options = VisualizerOptions.of(cli.getRequiredArguments());
	    vis = new Visualizer(options);
	    ModelToGraph mtg = new ModelToGraph(options);
	    assertTrue(mtg.loadModel());
	    graphs = mtg.createBlockGraphs();
	    graphs.forEach(vis::plotGraph);
	}

	@Test
	public void testGraph1() {
		Graph g1 = graphs.get(0);
		assertEquals("Abstract0",g1.getName());
		
		Map<String, Node> nmap = g1.getNodes();
		assertEquals(5,nmap.size());
		Set<Edge> edges = g1.getEdges();
		assertEquals(4,edges.size());
		assertTrue(edges.contains(new Edge(nmap.get("?Serializer"), nmap.get("?PL"), "uses")));
	}
	
	@Test
	public void testGraph2() {
		Graph g1 = graphs.get(1);
		assertEquals("Abstract1",g1.getName());
		
		Map<String, Node> nmap = g1.getNodes();
		assertEquals(7,nmap.size());
		Set<Edge> edges = g1.getEdges();
		assertEquals(6,edges.size());
		assertTrue(edges.contains(new Edge(nmap.get("?OL"),nmap.get("f"),  "domainOf_0")));
	}
	
	@Test
	public void testGraph4() {
		Graph g1 = graphs.get(3);
		assertEquals("Abstract3",g1.getName());
		
		Map<String, Node> nmap = g1.getNodes();
		assertEquals(8,nmap.size());
		Set<Edge> edges = g1.getEdges();
		assertEquals(8,edges.size());
		assertTrue(edges.contains(new Edge( nmap.get("?object"),nmap.get("f"), "inputOf_0")));
	}
	
	@Test
	public void testGraph5() {
		Graph g1 = graphs.get(4);
		assertEquals("BaseTechnology0",g1.getName());
		
		Map<String, Node> nmap = g1.getNodes();
		assertEquals(4,nmap.size());
		Set<Edge> edges = g1.getEdges();
		assertEquals(4,edges.size());
	}
	
	@Test
	public void testGraph6() {
		Graph g1 = graphs.get(5);
		assertEquals("Technology0",g1.getName());
		
		Map<String, Node> nmap = g1.getNodes();
		assertEquals(16,nmap.size());
		Set<Edge> edges = g1.getEdges();
		assertEquals(22,edges.size());
	}

}
