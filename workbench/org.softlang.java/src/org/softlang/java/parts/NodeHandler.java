package org.softlang.java.parts;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.core.internal.resources.Marker;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.parts.NodePart;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import javafx.scene.input.MouseEvent;

public class NodeHandler extends AbstractHandler implements IOnClickHandler {

	private Node current_main_node;
	
	@Override
	public void click(MouseEvent e) {	
		double x = e.getSceneX();
		double y = e.getSceneY();
		//get NodePart of selected Node
		NodePart nPart = (NodePart) getHost();
		//get selected Node by using NodePart.getContent()
		Node n = nPart.getContent();
		//get Graph of Node
		Graph g = n.getGraph();	
		
		if (e.isPrimaryButtonDown()) {
			goToLink(n);
		}
		if (e.isSecondaryButtonDown()) {
			//First of all reset all nodes to original color
			for(Node w: g.getNodes()){
				w.getAttributes().replace(ZestProperties.SHAPE_CSS_STYLE__N, w.getAttributes().get("original_shape_color"));
			}
			//if right click on already colored main node, undo previos coloring, by stopping here
			if (current_main_node != null && current_main_node.equals(n)){
				current_main_node = null;
				return;
			}
			//color selected node
			n.getAttributes().replace(ZestProperties.SHAPE_CSS_STYLE__N, "-fx-fill: #FFFF00; -fx-border-color:red");
			current_main_node = n;
			//iterative over edges and color neighbours of node n
			for (Edge r : g.getEdges()) {
				if (r.getSource().equals(n)) {
					r.getTarget().getAttributes().replace(ZestProperties.SHAPE_CSS_STYLE__N,
							"-fx-fill: #FFFFFF; -fx-border-color:red");
				} else if (r.getTarget().equals(n)) {
					r.getSource().getAttributes().replace(ZestProperties.SHAPE_CSS_STYLE__N,
							"-fx-fill: #FFFFFF; -fx-border-color:red");
				}
			}
			getHost().getRoot().refreshVisual();
		}
	}
	
	private void goToLink(Node n) {
		LinkedList<String> x = (LinkedList<String>) n.getAttributes().get("alllinks");
		if (!x.isEmpty()) {
			URI link;
			for (String s : (LinkedList<String>) n.getAttributes().get("alllinks")) {
				try {
					if (s.startsWith("http")){
					link = new URI(s);
					openWebpage(link);}
					else if (s.startsWith("file")){
					s = s.replaceFirst("file://", "");
					String[] splitted = s.split("#");
					int lineNumber = Integer.parseInt(splitted[splitted.length-1]);
					String path = "";
					for (int i = 0; i < splitted.length-1; i++) {
						path += splitted[i];
					}
					openFile(new Path(path), lineNumber);
					}
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void openFile(Path p, int lineNumber) {
		IPath pathToFile = p; 
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(pathToFile);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorDescriptor desc = PlatformUI.getWorkbench().
		        getEditorRegistry().getDefaultEditor(file.getName());
		try {
			IMarker marker = file.createMarker(Marker.TEXT);
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			IDE.openEditor(page, marker);
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static boolean openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean openWebpage(URL url) {
		try {
			return openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return false;
	}
}