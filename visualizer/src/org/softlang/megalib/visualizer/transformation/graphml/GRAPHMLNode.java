/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.transformation.graphml;

import org.softlang.megalib.visualizer.models.Node;

public class GRAPHMLNode extends Node {

    private String color;

    private String shape;
    
    private String id;
    
    private String width;
    
    private String icon;

    public GRAPHMLNode(Node original, String color, String shape, int id, String icon) {
        super(original.getType(), original.getName(), original.getLink(), original.getInstanceHierarchy(), original.getEdges());
        this.color = color;
        this.shape = shape;
        this.id = Integer.toString(id);
        this.width = Integer.toString(50 + this.getName().length() * 10);
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public String getShape() {
        return shape;
    }
    
    public String getId() {
    	return id;
    }
    
    public String getWidth() {
    	return width;
    }

    public String getIcon() {
    	return icon;
    }
}
