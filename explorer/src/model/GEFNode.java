package model;

import java.util.LinkedList;

import org.softlang.megalib.visualizer.models.Node;

public class GEFNode extends Node{
	
	private LinkedList<String> allLinks;
	
	public GEFNode(Node original, LinkedList links) {
		super(original.getType(), original.getName(), original.getLink(), original.getInstanceHierarchy(), original.getEdges());
		this.allLinks = links;
		}
	
	public LinkedList<String> getallLinks(){
		return allLinks;
	}
	
}
