package com.ast.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodReference;
import org.eclipse.jdt.core.dom.ModuleDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import com.ast.util.Binding;
import com.ast.util.MyTreeNode;

public class NodeVisitor extends ASTVisitor {

	MyTreeNode root = null;
	int curId = 0;
	@Override
	// visit the given java file
	public boolean visit(CompilationUnit node) {
		// the whole java file
		root = new MyTreeNode(node, curId++);
		buildTree(root);
		return true;
	}
	// return the root of AST
	public MyTreeNode getRoot() {
		return root;
	}
	// build the AST recursively
	private void buildTree(MyTreeNode node) {
		
		setProperties(node);
		setBindings(node);
		for (MyTreeNode child: node.getChildren()) {
			buildTree(child);
		}	
	}
	// get children of given AST node
	private void setProperties(MyTreeNode node) {
		ArrayList<MyTreeNode> children = new ArrayList<>();
		
		ASTNode astnode = node.getNode();
		List<StructuralPropertyDescriptor> list = astnode.structuralPropertiesForType();
		for (StructuralPropertyDescriptor property: list) {
			String name = getPropertyName(property);
			// SimplePropertyDescriptor - the value of the given simple property, or null if none; primitive values are "boxed"
			if (property.isSimpleProperty()) {
				Object simpleproperty = astnode.getStructuralProperty(property);
				if (simpleproperty != null) {
					node.insertAttribute(name, simpleproperty.toString());
				}
				else
					node.insertAttribute(name, "null");
			}
			// ChildListPropertyDescriptor - the list (element type: ASTNode)
			else if (property.isChildListProperty()) {
				List<ASTNode> child = (List<ASTNode>) astnode.getStructuralProperty(property);
				for (ASTNode ch: child) {
					children.add(new MyTreeNode(ch, name, curId++));
				}
				// node.insertAttribute(name, String.valueOf(child.size()));
			}
			// ChildPropertyDescriptor - the child node (type ASTNode), or null if none
			else {
				ASTNode child = (ASTNode) astnode.getStructuralProperty(property);
				if (child != null) {
					children.add(new MyTreeNode(child, name, curId++));
				// 	node.insertAttribute(name, "yes");
				}
				// else
				//	node.insertAttribute(name, "null");
			}
		}
		for (MyTreeNode child: children) {
			node.insertChild(child);
		}

	}
	private void setBindings(MyTreeNode tnode) {
		ASTNode node = tnode.getNode();
		
		if (node instanceof Expression) {
			Expression expression= (Expression) node;
			ITypeBinding expressionTypeBinding= expression.resolveTypeBinding();
			tnode.setBinding(createExpressionTypeBinding(expressionTypeBinding));
			
			// expressions:
			if (expression instanceof Name) {
				IBinding binding= ((Name) expression).resolveBinding();
				if (binding != null && binding != expressionTypeBinding)
					tnode.setBinding(createBinding(binding));
			} else if (expression instanceof MethodInvocation) {
				MethodInvocation methodInvocation= (MethodInvocation) expression;
				IMethodBinding binding= methodInvocation.resolveMethodBinding();
				tnode.setBinding(createBinding(binding));
			} else if (expression instanceof SuperMethodInvocation) {
				SuperMethodInvocation superMethodInvocation= (SuperMethodInvocation) expression;
				IMethodBinding binding= superMethodInvocation.resolveMethodBinding();
				tnode.setBinding(createBinding(binding));
			} else if (expression instanceof ClassInstanceCreation) {
				ClassInstanceCreation classInstanceCreation= (ClassInstanceCreation) expression;
				IMethodBinding binding= classInstanceCreation.resolveConstructorBinding();
				tnode.setBinding(createBinding(binding));
			} else if (expression instanceof FieldAccess) {
				IVariableBinding binding= ((FieldAccess) expression).resolveFieldBinding();
				tnode.setBinding(createBinding(binding));
			} else if (expression instanceof SuperFieldAccess) {
				IVariableBinding binding= ((SuperFieldAccess) expression).resolveFieldBinding();
				tnode.setBinding(createBinding(binding));
			} else if (expression instanceof Annotation) {
				IAnnotationBinding binding= ((Annotation) expression).resolveAnnotationBinding();
				tnode.setBinding(createBinding(binding));
			} else if (expression instanceof LambdaExpression) {
				Binding lambdaBinding = null;
				try {
					IMethodBinding binding= ((LambdaExpression) expression).resolveMethodBinding();
					lambdaBinding = createBinding(binding);
				} catch (RuntimeException e) {
					System.out.println(e.getMessage());
					System.out.println("Exception thrown in LambdaExpression#resolveMethodBinding() for \"" + expression + "\"");
				}
				tnode.setBinding(lambdaBinding);
			} else if (expression instanceof MethodReference) {
				IMethodBinding binding= ((MethodReference) expression).resolveMethodBinding();
				tnode.setBinding(createBinding(binding));
			}	
			
		// references:
		} else if (node instanceof ConstructorInvocation) {
			IMethodBinding binding= ((ConstructorInvocation) node).resolveConstructorBinding();
			tnode.setBinding(createBinding(binding));
		} else if (node instanceof SuperConstructorInvocation) {
			IMethodBinding binding= ((SuperConstructorInvocation) node).resolveConstructorBinding();
			tnode.setBinding(createBinding(binding));
		} else if (node instanceof MethodRef) {
			IBinding binding= ((MethodRef) node).resolveBinding();
			tnode.setBinding(createBinding(binding));
		} else if (node instanceof MemberRef) {
			IBinding binding= ((MemberRef) node).resolveBinding();
			tnode.setBinding(createBinding(binding));
		} else if (node instanceof Type) {
			IBinding binding= ((Type) node).resolveBinding();
			tnode.setBinding(createBinding(binding));
			
		// declarations:
		} else if (node instanceof AbstractTypeDeclaration) {
			IBinding binding= ((AbstractTypeDeclaration) node).resolveBinding();
			tnode.setBinding(createBinding(binding));
		} else if (node instanceof AnnotationTypeMemberDeclaration) {
			IBinding binding= ((AnnotationTypeMemberDeclaration) node).resolveBinding();
			tnode.setBinding(createBinding(binding));
		} else if (node instanceof EnumConstantDeclaration) {
			IBinding binding= ((EnumConstantDeclaration) node).resolveVariable();
			tnode.setBinding(createBinding(binding));
			IBinding binding2= ((EnumConstantDeclaration) node).resolveConstructorBinding();
			tnode.setBinding(createBinding(binding2));
		} else if (node instanceof MethodDeclaration) {
			IBinding binding= ((MethodDeclaration) node).resolveBinding();
			tnode.setBinding(createBinding(binding));
		} else if (node instanceof VariableDeclaration) {
			IBinding binding= ((VariableDeclaration) node).resolveBinding();
			tnode.setBinding(createBinding(binding));
		} else if (node instanceof AnonymousClassDeclaration) {
			IBinding binding= ((AnonymousClassDeclaration) node).resolveBinding();
			tnode.setBinding(createBinding(binding));
		} else if (node instanceof ImportDeclaration) {
			IBinding binding= ((ImportDeclaration) node).resolveBinding();
			tnode.setBinding(createBinding(binding));
		} else if (node instanceof PackageDeclaration) {
			IBinding binding= ((PackageDeclaration) node).resolveBinding();
			tnode.setBinding(createBinding(binding));
		} else if (node instanceof TypeParameter) {
			IBinding binding= ((TypeParameter) node).resolveBinding();
			tnode.setBinding(createBinding(binding));
		} else if (node instanceof MemberValuePair) {
			IBinding binding= ((MemberValuePair) node).resolveMemberValuePairBinding();
			tnode.setBinding(createBinding(binding));
		} else if (node instanceof ModuleDeclaration) {
			IBinding binding= ((ModuleDeclaration) node).resolveBinding();
			tnode.setBinding(createBinding(binding));
		}

	}
	private String getPropertyName(StructuralPropertyDescriptor property) {
		String string = property.getId();
		StringBuffer buf= new StringBuffer();
		for (int i= 0; i < string.length(); i++) {
			char ch= string.charAt(i);
			if (i != 0 && Character.isUpperCase(ch)) {
				buf.append('_');
			}
			buf.append(Character.toUpperCase(ch));
		}
		return buf.toString();
	}
	private Binding createExpressionTypeBinding(ITypeBinding binding) {
		if (binding == null)
			return null;
		String label= "(Expression) type binding";
		return new Binding(binding, label);
	}
	private Binding createBinding(IBinding binding) {
		if (binding == null)
			return null;
		String label = Binding.getBindingLabel(binding);
		return new Binding(binding, label);
	}
}
