package com.javaparser;

import org.eclipse.jdt.core.dom.CompilationUnit;  
  
import com.ast.util.JdtAstUtil;  
import com.ast.visitor.DemoVisitor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;  
import java.io.PrintStream;  
public class DemoVisitorTest {  
     
    public DemoVisitorTest(String path, String destpath) throws FileNotFoundException {  
        CompilationUnit comp = JdtAstUtil.getCompilationUnit(path);  
          
        DemoVisitor visitor = new DemoVisitor();  
        PrintStream ps=new PrintStream(new FileOutputStream(destpath));  
        System.setOut(ps);  
        comp.accept(visitor);  
    }  
    public static void main(String[] args) throws FileNotFoundException {
		String file=args[0];
		String destfile=args[1];
		//new DemoVisitorTest("D:/1.java"); 
		new DemoVisitorTest(file,destfile); 
    }
}  