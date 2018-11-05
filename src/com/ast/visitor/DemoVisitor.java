package com.ast.visitor;
import org.eclipse.jdt.core.dom.ASTNode;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;


public class DemoVisitor extends ASTVisitor {  
  
    @Override  
    public boolean visit(FieldDeclaration node) {  
        for (Object obj: node.fragments()) {  
            VariableDeclarationFragment v = (VariableDeclarationFragment)obj;  
            System.out.println("Field:\t" +node.getType()+ "\t"+v.getName());  
        }  
          
        return true;  
    }  
    @Override  
    public boolean visit(ImportDeclaration node) {  
        System.out.println("Import:\t" + node.getName());  
        return true;  
    }  
    @Override  
    public boolean visit(MethodDeclaration node) {  
        
        String cls="None";
    	IMethodBinding mb=node.resolveBinding();
    	if (mb!=null) {
    		
    		ITypeBinding tb=mb.getDeclaringClass();
    		if(tb!=null) {
    			cls=tb.getName();
    			
    		}
    		else {
    			IMethodBinding nmb=mb.getMethodDeclaration();
    			if(nmb!=null) {
    				cls=nmb.getName();
    			}
    		}
    		
    	}
    	
    	System.out.println("MethodDeclaration:\t" + node.getName()+"\t"+cls);  
        return true;  
    }  
    @Override  
    public boolean visit(MethodInvocation node) {  
    	String cls="None";
    	IMethodBinding mb=node.resolveMethodBinding();
    	if (mb!=null) {
    		
    		ITypeBinding tb=mb.getDeclaringClass();
    		if(tb!=null) {
    			//System.out.println("type1");
    			cls=tb.getName();
    			
    		}
    		else {
    			
    			IMethodBinding nmb=mb.getMethodDeclaration();
    			if(nmb!=null) {
    				//System.out.println("type2");
    				cls=nmb.getName();
    			}
    		}
    		
    	}
    	else {
    		Expression exp=node.getExpression();
    		if(exp!=null) {
    			
    			ITypeBinding tp=exp.resolveTypeBinding();
    			if(tp!=null) {
    				//System.out.println("type3");
    				cls=tp.getName();
    			}
    			else {
    				
        			String exptype=exp.getClass().toString();
        			//System.out.println(exptype);
        			if(exptype.equals("class org.eclipse.jdt.core.dom.MethodInvocation")) {
        				//System.out.println("type4");
        				MethodInvocation mi=(MethodInvocation) exp;
        				cls=mi.getName().toString();
        			}
        			else if(exptype.equals("class org.eclipse.jdt.core.dom.SimpleName")) {
        				SimpleName sn=(SimpleName) exp;
        				//System.out.println("type5");
        				cls=sn.toString();
        			}
        			else {
        				//System.out.println("type6");
        				cls=exp.toString()+exptype;
        			}
        		}
    		}
    		
    	}
    	
    	System.out.println("MethodInvocation:\t" + node.getName()+"\t"+cls);  
        return true;  
    }
    
    @Override  
    public boolean visit(TypeDeclaration node) {  
        System.out.println("Class:\t" + node.getName());  
        return true;  
    }  
    @Override  
    public boolean visit(ClassInstanceCreation node) {  
        System.out.println("ClassInstanceCreation:\t" + node.getType());  
        return true;  
    }
}