package model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.SpringLayout;

import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.HorizontalShiftAlgorithm;
import org.eclipse.gef.layout.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.SpaceTreeLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.SugiyamaLayoutAlgorithm;
import org.eclipse.gef.zest.fx.ZestProperties;

import javafx.scene.shape.Sphere;

import org.eclipse.gef.graph.Graph;

import org.softlang.megalib.visualizer.models.Node;
import org.softlang.megalib.visualizer.models.Edge;

public class GraphToGef {
			private static final String LABEL = ZestProperties.LABEL__NE;
		private Set<org.eclipse.gef.graph.Node> nodes = new HashSet();
		
		public org.eclipse.gef.graph.Graph createGraph(org.softlang.megalib.visualizer.models.Graph g){
			LinkedList<org.eclipse.gef.graph.Edge> edges = new LinkedList();
			g.forEachEdge(e-> edges.add(createEdge(e)));
			org.eclipse.gef.graph.Graph.Builder builder = new org.eclipse.gef.graph.Graph.Builder();
			SpringLayoutAlgorithm layout = new SpringLayoutAlgorithm();
			layout.setSpringLength(60);
			return builder.nodes(nodes).edges(edges).attr(ZestProperties.LAYOUT_ALGORITHM__G, layout).build();
		}
		
		public org.eclipse.gef.graph.Edge createEdge(Edge e){
			Node origin = e.getOrigin();
			Node destination = e.getDestination();
			org.eclipse.gef.graph.Node nOrigin = createNode(origin);
			org.eclipse.gef.graph.Node nDestination = createNode(destination);
			org.eclipse.gef.graph.Edge.Builder builder = new org.eclipse.gef.graph.Edge.Builder(nOrigin, nDestination).
					attr(LABEL, e.getLabel());
			return builder.buildEdge();
		}
		
		public org.eclipse.gef.graph.Node createNode(Node n){
			org.eclipse.gef.graph.Node.Builder builder = new org.eclipse.gef.graph.Node.Builder();
			builder.attr(LABEL, n.getName());
			builder.attr("link", n.getLink());
			org.eclipse.gef.graph.Node node = builder.buildNode();
			for(org.eclipse.gef.graph.Node n1: nodes){
				if(n1.attributesProperty().equals(node.attributesProperty())){
					node = n1;
					break;
				}
			}
			if (!nodes.contains(node)){
				nodes.add(node);
			}
			return node;
		}
		

}
