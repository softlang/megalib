package model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.SpringLayout;

import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.HorizontalShiftAlgorithm;
import org.eclipse.gef.layout.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.SpaceTreeLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.SugiyamaLayoutAlgorithm;
import org.eclipse.gef.zest.fx.ZestProperties;

import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Sphere;

import org.eclipse.gef.graph.Graph;

import org.softlang.megalib.visualizer.models.Node;
import org.softlang.megalib.visualizer.models.transformation.ConfigItem;
import org.softlang.megalib.visualizer.models.transformation.TransformerConfiguration;
import org.softlang.megalib.visualizer.models.Edge;

import model.Triangle;

public class GraphToGef {
	
    	private static final ConfigItem<String, String> DEFAULT_CONFIG = new ConfigItem<String, String>()
            .put("color", "black")
            .put("shape", "oval");

        private TransformerConfiguration config = new GefConfigurationBuilder().buildConfiguration();
        
        private static Image image = new Image("/Images/Icon.png",50,50,false,false);	
	
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
            //TODO add ArrowHead to Edge
			builder.attr(ZestProperties.TARGET_DECORATION__E,new Triangle());
			return builder.buildEdge();
		}
		
		public org.eclipse.gef.graph.Node createNode(Node n){
			org.eclipse.gef.graph.Node.Builder builder = new org.eclipse.gef.graph.Node.Builder();
			builder.attr(LABEL, n.getName());
			builder.attr("link", n.getLink());
			builder.attr(ZestProperties.ICON__N, image);
			builder.attr(ZestProperties.INVISIBLE__NE, false);
			//TODO: add circle layout to nodes
			//builder.attr(ZestProperties.SHAPE__N, new  Circle(30));
			
			builder.attr(ZestProperties.SHAPE_CSS_STYLE__N, "-fx-fill: " + getConfigValue(n,"color"));
			boolean haslink = false;
			if(!n.getLink().equals("")) {
				haslink = true;
			}
			builder.attr(ZestProperties.LABEL_CSS_STYLE__NE,"-fx-fill: #000000;-fx-underline:" + haslink); 
			//Example:
			//builder.attr("element", getConfigValue(n, "key");
						
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
		
		
		
		
		private String getConfigValue(Node node, String attribute) {
	        return getConfigItem(node, attribute).get(attribute);
	    }

	    private ConfigItem<String, String> getConfigItem(Node node, String attribute) {
	        // Traverse the configuration hierarchy to determine if there is a configuration item present
	        // Hence: name -> type -> supertype (until finished) -> default configuration
	        for (String key : getKeyHierarchy(node)) {
	            if (config.contains(key) && config.get(key).contains(attribute)) {
					return config.get(key);
				}
	        }
	        System.out.println("Default for "+node.getName()+":"+node.getType()+" at "+attribute);
	        return DEFAULT_CONFIG;
	    }

	    private List<String> getKeyHierarchy(Node node) {
	        return Stream.concat(
	            Stream.of(node.getName(), node.getType()),
	            node.getInstanceHierarchy().stream()
	        ).collect(Collectors.toList());
	    }
}
