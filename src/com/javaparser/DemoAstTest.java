package com.javaparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

import com.ast.util.JdtAstUtil;
import com.ast.util.MyTreeNode;
import com.ast.visitor.NodeVisitor;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

public class DemoAstTest {

	// build the AST
	private static final String delimiter = "  ";
	public DemoAstTest(String path, String destpath) throws IOException {
		CompilationUnit comp = JdtAstUtil.getCompilationUnit(path); 
//		if (destpath != null) {
//			PrintStream ps=new PrintStream(new FileOutputStream(destpath));  
//			System.setOut(ps);
//		}
		NodeVisitor visitor = new NodeVisitor();
		comp.accept(visitor);

		MyTreeNode root = visitor.getRoot();
		traverse(root, "");
		toXML(path, destpath, root);
	}
	// traverse the AST by pre-order
	// print it to standard out
	private void traverse(MyTreeNode node, String tabs) {
		if (node == null) {
			System.out.println("Some node traverse wrong!");
			System.exit(2);
		}
		// print the information
		node.print(tabs);
		if (node.numOfChildren() == 0) {	// leaf node
			return;
		}
		tabs += delimiter;
		for (MyTreeNode child: node.getChildren()) {
			traverse(child, tabs);
		}
		
	}
	// output the AST as a XML file
	private void toXML(String filepath, String outpath, MyTreeNode root) throws IOException {
		Document doc = DocumentHelper.createDocument();
		Element rootele = doc.addElement(root.getNodeName())
				.addAttribute("id", String.valueOf(root.getId()))
				.addAttribute("src", filepath);				
		buildElement(rootele, root);
		if (outpath == null) {
			File file = new File(filepath);
			String path = file.getAbsolutePath();
			outpath = path.substring(0, path.lastIndexOf('.')) + "_AST.xml";
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new FileWriter(outpath), format);
		writer.write(doc);
		writer.close();
	}
	private void buildElement(Element ele, MyTreeNode node) {
		if (node.numOfChildren() == 0) {
//			for (Map.Entry<String, String> entry: node.getAttributes().entrySet()) {
//				Element e = ele.addElement(entry.getKey());
//				e.addText(entry.getValue());
//			}
			for (Map.Entry<String, String> entry: node.getAttributes().entrySet()) {
				ele.addText(entry.getValue());
			}
		}
		else {
			for (MyTreeNode child: node.getChildren()) {
				Element e = ele.addElement(child.getNodeName())
						.addAttribute("id", String.valueOf(child.getId()))
						.addAttribute("label", child.getLabel());
				buildElement(e, child);
			}
		}
	}
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Not assign a file to parse!");
			return;
		}
		String file = args[0];
		String destfile = null;
		if (args.length == 2)
			destfile = args[1];
		// run by: F:/workplace2/Test/src/Main.java
		new DemoAstTest(file, destfile); 
	}
}
