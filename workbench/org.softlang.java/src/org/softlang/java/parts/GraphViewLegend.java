package org.softlang.java.parts;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.ui.ZestFxUiModule;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.inject.Guice;
import com.google.inject.util.Modules;


@SuppressWarnings("restriction")
public class GraphViewLegend extends ZestFxUiView {

	
	String propertiesPath;
	String LABEL = ZestProperties.LABEL__NE;
	LinkedList<Node> nodeList = new LinkedList<Node>();
	Map<String,String> colour = new HashMap<String,String>();

	public GraphViewLegend() {
		super(Guice.createInjector(Modules.override(new ZestFxUiModule()).with(new ZestFxUiCustom())));
		IEclipsePreferences preferences = ConfigurationScope.INSTANCE
			    .getNode("org.softlang.java");
		propertiesPath = preferences.get("properties", "null");
		String dotExecutablePath = preferences.get("Graphvizz", "null");
		if(!propertiesPath.equals("null")) {
			if(dotExecutablePath.endsWith("dot.exe") || dotExecutablePath.endsWith("dot")) {
				File f = new File(propertiesPath);
				String json;
				try {
					json = FileUtils.readFileToString(f);
					JsonElement root = new JsonParser().parse(json);
					int size = root.getAsJsonObject().size();
					for(Entry<String, JsonElement> set: root.getAsJsonObject().entrySet()) {
						Node.Builder builder = new Node.Builder();
						builder.attr(LABEL, set.getKey());
						builder.attr("alllinks", new LinkedList<String>());
						builder.attr("bindings", new LinkedList<String>());
						builder.attr(ZestProperties.INVISIBLE__NE, false);			
						builder.attr("original_shape_color", "-fx-fill:" + lookupcolour(set.getKey()));
						builder.attr(ZestProperties.SHAPE_CSS_STYLE__N, "-fx-fill: " + lookupcolour(set.getKey()));
						builder.attr(ZestProperties.LABEL_CSS_STYLE__NE,"-fx-fill: #000000;-fx-underline:" + false); 
						nodeList.add(builder.buildNode());						
					}
					Graph.Builder gbuilder = new Graph.Builder();
					setGraph(gbuilder.nodes(nodeList).attr(ZestProperties.LAYOUT_ALGORITHM__G, new GridLayoutAlgorithm()).build());
					}
					catch(IOException e) {
						showErrorMessageNode("No properties file could be found on the given path. \n"
								+ "Please check your file path under: \n"
								+ "Window>Preferences>MegaLPreference");
					};
			}
			else {
				String message = "Please enter a valid path to your \n"
						+ "dot.exe (Windows) or dot-file (Linux/Mac) under: \n"
						+ "Window>Preferences>MegaLPreference";
				showErrorMessageNode(message);
				return;
			}	
		}
		else {
			String message = "Please enter a valid path to your \n"
					+ "properties file for MegaL under:\n"
					+ "Window>Preferences>MegaLPreference";
			showErrorMessageNode(message);
			return;
		}
		
	}
	
	private void showErrorMessageNode(String s) {
		nodeList = new LinkedList();
		nodeList.add(createNode(s, new LinkedList(), new LinkedList()));
		Graph.Builder gbuilder = new Graph.Builder();
		Graph g = gbuilder.nodes(nodeList).build();
		setGraph(g);
	}

	
	private Node createNode(String name, LinkedList<String> urls, LinkedList<String> bindings){
		Node.Builder builder = new Node.Builder();
		builder.attr(LABEL, name);
		builder.attr("alllinks", urls);
		builder.attr("bindings", bindings);
		builder.attr(ZestProperties.INVISIBLE__NE, false);			
		builder.attr("original_shape_color", "-fx-fill:" + lookupcolour(name));
		builder.attr(ZestProperties.SHAPE_CSS_STYLE__N, "-fx-fill: " + lookupcolour(name));
		boolean haslink = false;
		if(!urls.isEmpty()) {
			haslink = true;
		}
		builder.attr(ZestProperties.LABEL_CSS_STYLE__NE,"-fx-fill: #000000;-fx-underline:" + haslink); 
		Node node = builder.buildNode();
		return node;
	}
	
	private String lookupcolour (String key ) {
		File f = new File(propertiesPath);
		String json;
		try {
			json = FileUtils.readFileToString(f);
			JsonElement root = new JsonParser().parse(json);
			if(root.getAsJsonObject().has(key)) {
				return root.getAsJsonObject().get(key).getAsString();
			}
			while(colour.get(key) != null){
				key = colour.get(key);
				if(root.getAsJsonObject().has(key)) {
					return root.getAsJsonObject().get(key).getAsString();
				}
			}
		} catch (Exception e) {
			return "#FFFFFF";
		}
		return "#FFFFFF";
	}
	
}