/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.transformation.dot;

import org.softlang.megalib.visualizer.models.Node;

/**
 * Create instances of DOTNode to enrich a simple Node using style sheet information
 * 
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class DOTNode extends Node {

    private String color;

    private String shape;

    public DOTNode(Node original, String color, String shape) {
        super(original.getType(), original.getName(), original.getLink(), original.getInstanceHierarchy(), original.getEdges());
        this.color = color;
        this.shape = shape;
    }

    public String getColor() {
        return color;
    }

    public String getShape() {
        return shape;
    }

}
