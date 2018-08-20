package module;

import java.util.List;

import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;

import javafx.scene.input.MouseEvent;

public class NodeHandler extends AbstractHandler implements IOnClickHandler{
	
	@Override
	public void click(MouseEvent e) {
		System.out.println("Node clicked");
		double x = e.getSceneX();
		double y = e.getSceneY();
		Graph g = (Graph) getHost().getAdaptable().getContents().get(0);
		System.out.println(g);
		Node n = getNodeAtPosition(x, y, g.getNodes());
		goToLink(n);
	}
	
	private Node getNodeAtPosition(double x, double y,List<Node> nodes){
		//TODO Implement method which returns Node at position
		return null;
	}
	
	private void goToLink(Node n){
		//TODO Implement Method which takes Link from Node and redirects user
		//TODO Add Link to node...
	}

}
