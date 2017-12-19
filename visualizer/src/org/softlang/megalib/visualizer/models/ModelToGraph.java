/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.models;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.java.megalib.checker.services.ModelLoader;
import org.java.megalib.models.Block;
import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;
import org.softlang.megalib.visualizer.VisualizerOptions;

public class ModelToGraph {

	private MegaModel model;
	private VisualizerOptions options;
	private ModelLoader loader;

	public ModelToGraph(VisualizerOptions options) {
		this.options = options;
	}

	public boolean loadModel() {
		loader = new ModelLoader();
		try {
			model = loader.getModel();
			return loader.loadFile(options.getFilePath().toAbsolutePath().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Graph createGraph() {
		Graph graph = new Graph(options.getModelName() + "Complete",
				"The complete Megamodel for " + options.getModelName());
		model.getInstanceOfMap().entrySet().stream().filter(entry -> !entry.getValue().equals("Link"))
				.map(entry -> createNode(entry.getKey(), entry.getValue(), model)).forEach(graph::add);
		model.getFunctionDeclarations().forEach((name, funcs) -> graph.add(createNode(name, "Function", model)));
		model.getFunctionApplications().forEach((name, funcs) -> graph.add(createNode(name, "Function", model)));
		model.getRelationships().entrySet().stream().filter(e -> !e.getKey().equals("=") && !e.getKey().equals("~="))
				.forEach(e -> createEdgesByRelations(graph, e.getKey(), e.getValue()));
		model.getFunctionDeclarations().forEach((name, functions) -> createEdgesByFunction(graph, name, functions));
		model.getFunctionApplications().forEach((name, functions) -> createEdgesByFunction(graph, name, functions));

		return graph;
	}

	public List<Graph> createBlockGraphs() {
		List<Graph> graphs = new LinkedList<>();
		for (Block b : model.getBlocks()) {
			if (b.getModule().startsWith("common.")) {
				continue;
			}
			Graph graph = new Graph(b.getModule() + b.getId(), b.getText());
			// instance nodes
			b.getInstanceOfMap().entrySet().stream().filter(entry -> !entry.getValue().equals("Link"))
					.map(entry -> createNode(entry.getKey(), entry.getValue(), model)).forEach(graph::add);
			b.getFunctionDeclarations().forEach((name, funcs) -> graph.add(createNode(name, "Function", model)));
			b.getFunctionApplications().forEach((name, funcs) -> graph.add(createNode(name, "Function", model)));
			b.getRelationships().entrySet().stream().filter(e -> !e.getKey().equals("=") && !e.getKey().equals("~="))
					.forEach(e -> createEdgesByRelations(graph, e.getKey(), e.getValue()));
			b.getFunctionDeclarations().forEach((name, functions) -> createEdgesByFunction(graph, name, functions));
			b.getFunctionApplications().forEach((name, functions) -> createEdgesByFunction(graph, name, functions));
			graphs.add(graph);
		}

		return graphs;
	}

	private Node createNode(String name, String type, MegaModel model) {
		Node result = new Node(type, name, getFirstInstanceLink(model, name));
		applyInstanceHierarchy(result);
		return result;
	}

	private void applyInstanceHierarchy(Node node) {
		String type = node.getType();
		while (type != null && !type.isEmpty()) {
			node.getInstanceHierarchy().add(type);
			type = model.getSubtypesMap().get(type);
		}
	}

	private String getFirstInstanceLink(MegaModel model, String name) {
		return Optional.ofNullable(model.getLinks(name)).filter(set -> !set.isEmpty()).map(set -> set.iterator().next())
				.orElse("");
	}

	private void createEdgesByFunction(Graph graph, String functionName, Set<Function> funcs) {
		Iterator<Function> it = funcs.iterator();
		for (int i = 0; it.hasNext(); i++) {
			createEdgesByFunction(graph, functionName, it.next(), i);
		}
	}

	private void createEdgesByFunction(Graph graph, String functionName, Function f, int i) {
		if (f.isDecl) {
			f.getInputs().forEach(input -> createEdge(graph, input, functionName, "domainOf_" + i));
			f.getOutputs().forEach(output -> createEdge(graph, functionName, output, "hasRange_" + i));
		} else {
			f.getInputs().forEach(input -> createEdge(graph, input, functionName, "inputOf_" + i));
			f.getOutputs().forEach(output -> createEdge(graph, functionName, output, "hasOutput_" + i));
		}
	}

	private void createEdgesByRelations(Graph graph, String relationName, Set<Relation> relations) {
		relations.stream()
				.forEach(relation -> createEdge(graph, relation.getSubject(), relation.getObject(), relationName));
	}

	private void createEdge(Graph graph, String from, String to, String relation) {
		Node fromNode = graph.get(from);
		Node toNode = graph.get(to);
		fromNode.connect(relation, toNode);
	}

	public List<String> getTypeErrors() {
		return loader.getTypeErrors();
	}
}
