package com.ast.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;

public class MyTreeNode {

	ASTNode node;	// can use instanceof or .getNodeType() to know the specific type
	String label;
	int id;
	List<MyTreeNode> children;
	Map<String, String> attributes;
	List<Binding> bindings;
	public MyTreeNode(ASTNode node, int id) {
		this.node = node;
		this.label = null;
		this.id = id;
		children = new ArrayList<>();
		attributes = new HashMap<>();
		bindings = new ArrayList<>(2);
	}
	public MyTreeNode(ASTNode node, String label, int id) {
		this.node = node;
		this.label = label;
		this.id = id;
		children = new ArrayList<>();
		attributes = new HashMap<>();
		bindings = new ArrayList<>(2);		
	}
	public String getLabel() {
		return this.label;
	}
	public ASTNode getNode() {
		return node;
	}
	public String getNodeName() {
		return node.getClass().getSimpleName();
	}
	public int getId() {
		return this.id;
	}
	public List<MyTreeNode> getChildren() {
		return children;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void printAttributes() {
		if (attributes.size() != 0) {
			for (Map.Entry<String, String> entry: attributes.entrySet()) {
				System.out.print(entry.getKey() + ": " + entry.getValue() + "; ");
			}
		}
	}
	public void insertChild(MyTreeNode child) {
		children.add(child);
	}
	public int numOfChildren() {
		return children.size();
	}
	public void insertAttribute(String key, String val) {
		attributes.put(key, val);
	}
	public void setBinding(Binding binding) {
		if (binding != null)
			bindings.add(binding);
	}
	public boolean hasBinding() {
		return (bindings.size() > 0);
	}
	public void printBindings() {
		if (hasBinding()) {
			for (Binding b: bindings) {
				System.out.print(b.getLabel() + ": " + b.getName() + "; ");
			}
		}
	}
	public void printId() {
		System.out.print("id: " + this.id + "; ");
	}
	public void print(String tabs) {
//		if (label != null)
//			System.out.print(tabs + label + ": " + getNodeName() + ": { ");
//		else
			System.out.print(tabs + getNodeName() + ": { ");
		printId();
		printBindings();
		printAttributes();
		System.out.println("}");
	}
	public void toXML() {
		
	}
}
