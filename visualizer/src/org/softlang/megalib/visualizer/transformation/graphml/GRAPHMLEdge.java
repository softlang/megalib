/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.transformation.graphml;

import java.util.List;
import java.util.Objects;

import org.softlang.megalib.visualizer.models.Edge;
import org.softlang.megalib.visualizer.models.Node;

public class GRAPHMLEdge {

    private String origin;

    private String destination;

    private String label;
    
    private String id;

    public GRAPHMLEdge(Edge e, List<GRAPHMLNode> nodes, int id) {
        boolean destinationFound = false;
        boolean originFound = false;
        for(GRAPHMLNode node: nodes) {
        	if(node.getName().equals(e.getDestination().getName())) {
        		this.destination = node.getId();
        		destinationFound = true;
        	}
        	if(node.getName().equals(e.getOrigin().getName())) {
        		this.origin = node.getId();
        		originFound = true;
        	}
        	if(destinationFound && originFound)
        		break;
        }
        this.label = e.getLabel();
        this.id = Integer.toString(id);
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getLabel() {
        return label;
    }
    
    public String getId() {
    	return id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.origin);
        hash = 29 * hash + Objects.hashCode(this.destination);
        hash = 29 * hash + Objects.hashCode(this.label);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Edge other = (Edge) obj;
        if (!Objects.equals(this.label, other.getLabel())) {
            return false;
        }
        if (!Objects.equals(this.origin, other.getOrigin())) {
            return false;
        }
        if (!Objects.equals(this.destination, other.getDestination())) {
            return false;
        }
        return true;
    }

}
