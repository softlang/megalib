package org.softlang.java.parts;

import java.io.File;

import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;

@SuppressWarnings("restriction")
public class GraphView extends ZestFxUiView implements IShowInTarget{

	@Override
	public boolean show(ShowInContext cxt) {
			Object in = cxt.getInput();

			if (in instanceof File) {
				File f = (File) in;
				Graph g = createGraphFromJson(f);
				setGraph(g);
				return true;
			} 
			return false;
		}
	
	private Graph createGraphFromJson(File f) {
		//TODO: Parse JSON
		//Create Graph
		//Return it
		return null;
	}



}