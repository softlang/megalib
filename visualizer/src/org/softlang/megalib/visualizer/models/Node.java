/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.models;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class Node {

    private String type;

    private String name;

    private String link;

    private LinkedList<String> instanceHierarchy = new LinkedList<>();

    private Map<String, Set<Edge>> edges = new LinkedHashMap<>();

    public Node(String type, String name, String link) {
        this.type = type;
        this.name = name;
        this.link = link;
    }

    protected Node(String type, String name, String link, LinkedList<String> instanceHierarchy, Map<String, Set<Edge>> edges) {
        this.type = type;
        this.name = name;
        this.link = link;
        this.instanceHierarchy = instanceHierarchy;
        this.edges = edges;
    }

    public void forEachEdge(Consumer<? super Edge> consumer) {
        edges.values().forEach(set -> set.forEach(consumer));
    }

    /**
     * Creates a connection between this node and the destination node as an edge between these nodes.
     *
     * @param relation the relation in which the two nodes are standing
     * @param destination the destination node on which the edge will point
     * @return the current node
     */
    public Node connect(String relation, Node destination) {
    	Set<Edge> set = new HashSet<Edge>();
    	if(edges.containsKey(relation))
    		set = edges.get(relation);
    	set.add(new Edge(this, destination, relation));
        edges.put(relation, set);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the name of this node.
     *
     * @return the name of this node
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the link of this node.
     *
     * @return the link of this node if present, or <tt>null</tt> otherwise
     */
    public String getLink() {
        return link;
    }

    public String getType() {
        return type;
    }

    public LinkedList<String> getInstanceHierarchy() {
        return instanceHierarchy;
    }

    public Map<String, Set<Edge>> getEdges() {
        return edges;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(name);
        hash = 83 * hash + Objects.hashCode(link);
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
        final Node other = (Node) obj;
        if (!Objects.equals(name, other.name))
            return false;
        if (!Objects.equals(link, other.link))
            return false;
        return true;
    }

    public boolean isInstanceOf(String type) {
        return this.type.equals(type);
    }

}
