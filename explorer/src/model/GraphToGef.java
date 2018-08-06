package model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.gef.layout.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.graph.Graph;

public class GraphToGef {
			private static final String LABEL = ZestProperties.LABEL__NE;
		private static Set<org.eclipse.gef.graph.Node> nodes = new HashSet();
		
		public static org.eclipse.gef.graph.Graph createGraph(model.Graph g){
			LinkedList<org.eclipse.gef.graph.Edge> edges = new LinkedList();
			g.forEachEdge(e-> edges.add(createEdge(e)));
			org.eclipse.gef.graph.Graph.Builder builder = new org.eclipse.gef.graph.Graph.Builder();
			return builder.nodes(nodes).edges(edges).attr(ZestProperties.LAYOUT_ALGORITHM__G, new RadialLayoutAlgorithm()).build();
		}
		
		public static org.eclipse.gef.graph.Edge createEdge(Edge e){
			Node origin = e.getOrigin();
			Node destination = e.getDestination();
			org.eclipse.gef.graph.Node nOrigin = createNode(origin);
			org.eclipse.gef.graph.Node nDestination = createNode(destination);
			org.eclipse.gef.graph.Edge.Builder builder = new org.eclipse.gef.graph.Edge.Builder(nOrigin, nDestination).
					attr(LABEL, e.getLabel());
			return builder.buildEdge();
		}
		
		public static org.eclipse.gef.graph.Node createNode(Node n){
			org.eclipse.gef.graph.Node.Builder builder = new org.eclipse.gef.graph.Node.Builder();
			builder.attr(LABEL, n.getName());
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
