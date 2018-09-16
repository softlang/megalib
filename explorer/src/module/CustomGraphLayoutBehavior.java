package module;

import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.zest.fx.behaviors.GraphLayoutBehavior;

public class CustomGraphLayoutBehavior extends GraphLayoutBehavior{
	
	protected Rectangle computeLayoutBounds() {
		
		LayoutContext layoutContext = getLayoutContext();
				
		Rectangle newBounds = new Rectangle();			
			/* TODO:
			 * Find a way to calculate appropiate size
			 * according to graph's nodes and edges
			 * 
				x = layoutContext.getNodes();
				y = layoutContext.getEdges();
			*/
			newBounds = new Rectangle(0, 0, 1000, 1000);
		return newBounds;
	}

}
