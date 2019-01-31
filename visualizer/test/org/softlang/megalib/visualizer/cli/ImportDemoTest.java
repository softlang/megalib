package org.softlang.megalib.visualizer.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.softlang.megalib.visualizer.Visualizer;
import org.softlang.megalib.visualizer.VisualizerOptions;
import org.softlang.megalib.visualizer.models.Graph;
import org.softlang.megalib.visualizer.models.ModelToGraph;
import org.softlang.megalib.visualizer.models.Node;
import org.softlang.megalib.visualizer.models.transformation.TransformerRegistry;
import org.softlang.megalib.visualizer.transformation.dot.DOTTransformer;

public class ImportDemoTest {

	private List<Graph> graphs;
	private Visualizer vis;
	
	@Before
	public void setUp() {
		TransformerRegistry.registerTransformer("dot", "dot", (VisualizerOptions options)
	   		     -> new DOTTransformer(options));
	    CommandLine cli = new CommandLine(Arrays.asList("graphviz", "dot"));

	    String data[] = {"-f", "../checker/testsample/ImportDemo/bcd/d/D.megal", "-t", "dot"};
	    cli.parse(data);
	    VisualizerOptions options = VisualizerOptions.of(cli.getRequiredArguments());
	    vis = new Visualizer(options);
	    ModelToGraph mtg = new ModelToGraph(options);
	    assertTrue(mtg.loadModel());
	    graphs = mtg.createBlockGraphs();
	    graphs.forEach(vis::plotGraph);
	}
	
	@Test
	public void testSize() {
		assertEquals(4,graphs.size());
	}
	
	@Test
	public void testGraph1() {
		Graph g1 = graphs.get(0);
		Map<String, Node> nodes = g1.getNodes();
		
		assertTrue(nodes.containsKey("?Program"));
		assertTrue(nodes.containsKey("?spec"));
		assertTrue(nodes.containsKey("HTML5"));
		assertTrue(nodes.containsKey("Specification"));
		assertEquals(4,nodes.size());
		
		assertEquals(4,g1.getEdges().size());
	}
	
	@Test
	public void testGraph4() {
		Graph g4 = graphs.get(3);
		Map<String, Node> nodes = g4.getNodes();
		
		assertTrue(nodes.containsKey("f"));
		assertTrue(nodes.containsKey("HTML5"));
		assertTrue(nodes.containsKey("?a1"));
		assertTrue(nodes.containsKey("?a2"));
		assertTrue(nodes.containsKey("?Program"));
		assertEquals(5,nodes.size());
		assertEquals(5,g4.getEdges().size());
	}

}
