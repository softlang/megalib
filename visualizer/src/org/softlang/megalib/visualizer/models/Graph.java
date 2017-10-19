/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.models;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class Graph implements Iterable<Node> {

    private String name;
    private String text;

    private Map<String, Node> nodes = new TreeMap<>();

    Graph(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }
    
    public String getText() {
		// TODO Auto-generated method stub
		return text;
	}

    /**
     * Adds a node to the graph. If a node with the same name is present, the old one gets overwritten by the new node.
     *
     * @param n the node to be added
     * @return the node added to the graph
     */
    Node add(Node n) {
        return nodes.put(n.getName(), n);
    }

    /**
     * Gets a node by its name.
     *
     * @param name the name of the node
     * @return the node that is represented by its name or null if no node with the name is present
     */
    public Node get(String name) {
        return nodes.get(name);
    }

    public void remove(Node node) {
        nodes.remove(node.getName());
    }

    /**
     * Checks if a node with a certain name exists in the graph.
     *
     * @param name the name of the node
     * @return <tt>true</tt> if a node exists with the given name within this graph, <tt>false</tt> otherwise
     */
    public boolean has(String name) {
        return nodes.containsKey(name);
    }

    public void forEachNode(Consumer<? super Node> consumer) {
        nodes.values().forEach(consumer);
    }

    public void forEachEdge(Consumer<? super Edge> consumer) {
        nodes.values().forEach(n -> n.forEachEdge(consumer));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(nodes);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Graph other = (Graph) obj;
        if (!Objects.equals(nodes, other.nodes))
            return false;
        return true;
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public Set<Edge> getEdges() {
        Set<Edge> edges = new HashSet<>();
        forEachEdge(edges::add);

        return edges;
    }

    @Override
    public Iterator<Node> iterator() {
        return nodes.values().iterator();
    }

}
