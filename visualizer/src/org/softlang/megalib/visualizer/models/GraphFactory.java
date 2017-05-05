/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.models;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;
import org.softlang.megalib.visualizer.exceptions.MegaModelVisualizerException;
import org.softlang.megalib.visualizer.ModelReader;
import org.softlang.megalib.visualizer.VisualizerOptions;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class GraphFactory {

    private VisualizerOptions options;

    private ModelReader reader;

    private MegaModel baseModel;

    private MegaModel resolveableModel;

    public GraphFactory(VisualizerOptions options) {
        this.options = options;
        reader = new ModelReader(options);
    }

    public Graph create() throws MegaModelVisualizerException {
        baseModel = reader.read();
        resolveableModel = reader.readFull();

        Graph graph = createGraph(options.getModelName(), baseModel);
        return graph;
    }

    private Graph createGraph(String name, MegaModel model) {
        Graph graph = new Graph(name);
        createNodes(model, graph);
        createEdges(model, graph);

        return graph;
    }

    private void createNodes(MegaModel model, Graph graph) {
        createNodesByInstances(model, graph);
        createNodesByFunctions(model, graph);
    }

    private void createNodesByInstances(MegaModel model, Graph graph) {
        List<Node> nodes = model.getInstanceOfMap().entrySet().stream()
            .map(entry -> createNode(entry.getKey(), entry.getValue(), model))
            .collect(Collectors.toList());
        nodes.forEach(graph::add);
    }

    private void createNodesByFunctions(MegaModel model, Graph graph) {
        model.getFunctionDeclarations().forEach((name, actions) -> graph.add(createNode(name, "FunctionDeclaration", model)));
        model.getFunctionApplications().forEach((name, actions) -> graph.add(createNode(("#" + name), "FunctionApplication", model)));
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
            type = resolveableModel.getSubtypesMap().get(type);
        }
    }

    private String getFirstInstanceLink(MegaModel model, String name) {
        return Optional.ofNullable(model.getLinks(name)).filter(set -> !set.isEmpty()).map(set -> set.iterator().next())
                       .orElse("");
    }

    private void createEdges(MegaModel model, Graph graph) {
        model.getRelationships().entrySet().parallelStream()
             .filter(e -> !e.getKey().equals("=") && !e.getKey().equals("~="))
             .forEach(e -> createEdgesByRelations(graph, e.getKey(), e.getValue()));
        model.getFunctionDeclarations().forEach((name, functions) -> createEdgesByFunctionDeclarations(graph, name, functions));
        model.getFunctionApplications().forEach((name, functions) -> createEdgesByFunctionApplications(graph, name, functions));
    }

    private void createEdgesByFunctionDeclarations(Graph graph, String functionName, Set<Function> funcs) {
        funcs.forEach(f -> createEdgesByFunction(graph, functionName, f));
    }

    private void createEdgesByFunctionApplications(Graph graph, String functionName, Set<Function> funcs) {
        funcs.forEach(f -> {
            createEdgesByFunction(graph, ("#" + functionName), f);
            createEdge(graph, ("#" + functionName), functionName, "applicationOf");
        });
    }

    private void createEdgesByFunction(Graph graph, String functionName, Function f) {
        f.getInputs().forEach(input -> createEdge(graph, functionName, input, "functionInput"));
        f.getOutputs().forEach(output -> createEdge(graph, functionName, output, "functionOutput"));
    }

    private void createEdgesByRelations(Graph graph, String relationName, Set<Relation> relations) {
        relations.stream()
            .forEach(relation -> createEdge(graph, relation.getSubject(), relation.getObject(), relationName));
    }

    private void createEdge(Graph graph, String from, String to, String relation) {
        Node fromNode = graph.get(from);
        Node toNode = graph.get(to);

        if (toNode == null) {
            toNode = lazyCreateImportedNode(graph, to);
        }

        fromNode.connect(relation, toNode);
    }

    private Node lazyCreateImportedNode(Graph graph, String name) {
        // This method is executed when a destination node is not found in the simple model.
        // The resolveableModel is the complete megamodel instance and an import node is to be created
        // If there is still no node available, throw an exception
        if(!resolveableModel.getInstanceOfMap().containsKey(name))
            throw new MegaModelVisualizerException("Could not resolve node " + name);
        String type = resolveableModel.getInstanceOfMap().get(name);
        Node result = createNode(name, type, resolveableModel);
        graph.add(result);

        return result;
    }

}
