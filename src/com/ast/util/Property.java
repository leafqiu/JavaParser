package com.ast.util;

import org.eclipse.jdt.core.dom.SimplePropertyDescriptor;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

// Not used now
// maybe can use it to substitute the children&attributes fields
public class Property {
	private StructuralPropertyDescriptor property;
	Property(StructuralPropertyDescriptor p) {
		property = p;
	}
	public boolean isChildListProperty() {
		return property.isChildListProperty();
	}
	public boolean isChildProperty() {
		return property.isChildProperty();
	}
	public boolean isSimpleProperty() {
		return property.isSimpleProperty();
	}
	
}