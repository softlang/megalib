package module;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.zest.fx.behaviors.GraphLayoutBehavior;

public class CustomGraphLayoutBehavior extends GraphLayoutBehavior{
	
	protected Rectangle computeLayoutBounds() {			
		Rectangle newBounds;			
		Graph g = (Graph) getHost().getAdaptable().getContents().get(0);
		newBounds = new Rectangle(0, 0, g.getNodes().size()*100, g.getNodes().size()*80);
		return newBounds;
	}

}
