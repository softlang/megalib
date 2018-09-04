/*
 *  All rights reserved.
 */
package model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.softlang.megalib.visualizer.models.ModelToGraph;

import org.softlang.megalib.visualizer.models.Graph;
import org.softlang.megalib.visualizer.models.Node;
import org.softlang.megalib.visualizer.VisualizerOptions;
import org.softlang.megalib.visualizer.models.Edge;
import org.java.megalib.checker.services.ModelLoader;
import org.java.megalib.models.Block;
import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;

public class ModelDataToGraph extends ModelToGraph{

	private MegaModel model;
	private ModelLoader loader;
	private Graph graph;
	private String path;
	private String modelid;
	
	public ModelDataToGraph(VisualizerOptions options) {
		super(options);
	}
	
	public ModelDataToGraph() {
	}
	
	public List<Graph> createGraph(String path1, String path2) {
		Path p = Paths.get(path1, path2);
		modelid = path2.replace(".megal", "").replaceAll("/", "\\."); 
		loader = new ModelLoader();
		model = loader.getModel();
		try {
			loader.loadFile(p.toAbsolutePath().toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
		List<Graph> graphs = new LinkedList<>();
		String modelname = null;
		int id = 0;
		for (Block b : model.getBlocks()) {
			if (b.getModule().startsWith("common.")) {
				continue;
			}
			graph = new Graph(b.getModule() + id, b.getText());
			id++;
			// instance nodes
			b.getInstanceOfMap().entrySet().stream().filter(entry -> !entry.getValue().equals("Link"))
					.map(entry -> createNode(entry.getKey(), entry.getValue(), model)).forEach(graph::add);
			b.getFunctionDeclarations().forEach((name, funcs) -> graph.add(createNode(name, "Function", model)));
			b.getFunctionApplications().forEach((name, funcs) -> graph.add(createNode(name, "Function", model)));
			b.getRelationships().entrySet().stream().filter(e -> !e.getKey().equals("=") && !e.getKey().equals("~="))
					.forEach(e -> createEdgesByRelations(graph, e.getKey(), e.getValue()));
			b.getFunctionDeclarations().forEach((name, functions) -> createEdgesByFunction(graph, name, functions));
			b.getFunctionApplications().forEach((name, functions) -> createEdgesByFunction(graph, name, functions));
			if(b.getModule().equals(modelid)){
			graphs.add(graph);
			}
		}
		return graphs;
	}
	
	protected Node createNode(String name, String type, MegaModel model) {
		Node result = new Node(type, name, getFirstInstanceLink(model, name));
		applyInstanceHierarchy(result);
		return result;
	}


	protected void applyInstanceHierarchy(Node node) {
		String type = node.getType();
		while (type != null && !type.isEmpty()) {
			node.getInstanceHierarchy().add(type);
			type = model.getSubtypesMap().get(type);
		}
	}

	private String getFirstInstanceLink(MegaModel model, String name) {
		Set<Relation> r = new HashSet();
		if(model.getRelationships().get("=") != null) {
			r.addAll(model.getRelationships().get("="));
		}
		if(model.getRelationships().get("~=") !=null) {
			r.addAll(model.getRelationships().get("~="));
		}
		for(Relation x:r) {
			if(x.getSubject().equals(name)) {
				String link = x.getObject();
				if(link.contains("::")){
		            String ns = link.split("::")[0];
		            link = link.replace(ns + "::", model.getNamespace(ns) + "/");
		        }
				return link;
			}
		}
		return "";
	}




}



