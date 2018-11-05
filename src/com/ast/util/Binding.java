package com.ast.util;

import org.eclipse.jdt.core.dom.IBinding;

public class Binding {
	private IBinding binding;
	private String label;
	
	public Binding(IBinding binding, String label) {
		this.binding = binding;
		this.label = label;
	}
	public static String getBindingLabel(IBinding binding) {
		String label;
		if (binding == null) {
			label= "null binding";
		} else {
			switch (binding.getKind()) {
				case IBinding.VARIABLE:
					label= "variable binding"; 
					break;
				case IBinding.TYPE:
					label= "type binding"; 
					break;
				case IBinding.METHOD:
					label= "method binding";
					break;
				case IBinding.PACKAGE:
					label= "package binding";
					break;
				case IBinding.ANNOTATION:
					label= "annotation binding";
					break;
				case IBinding.MEMBER_VALUE_PAIR:
					label= "member value pair binding";
					break;
				case IBinding.MODULE:
					label= "module binding";
					break;
				default:
					label= "unknown binding";
			}
		}
		return label;

	}
	public String getLabel() {
		return label;
	}
	public String getName() {
		return binding == null? "null": binding.getName();
	}
}
