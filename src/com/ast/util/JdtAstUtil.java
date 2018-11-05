package com.ast.util;

import java.io.BufferedInputStream;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.IOException;  
  
import org.eclipse.jdt.core.dom.AST;  
import org.eclipse.jdt.core.dom.ASTParser;  
import org.eclipse.jdt.core.dom.CompilationUnit;
public class JdtAstUtil {  
    /** 
    * get compilation unit of source code 
    * @param javaFilePath  
    * @return CompilationUnit 
    */  
    public static CompilationUnit getCompilationUnit(String javaFilePath){  
        byte[] input = null;  
        try {  
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(javaFilePath));  
            input = new byte[bufferedInputStream.available()];  
                bufferedInputStream.read(input);  
                bufferedInputStream.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
          
        ASTParser astParser = ASTParser.newParser(AST.JLS9); 
    	astParser.setEnvironment(null, null, null, true);
    	String[] temp=javaFilePath.split("\\.");
    	
    	astParser.setUnitName(temp[0]);
    	astParser.setResolveBindings(true);
    	astParser.setBindingsRecovery(true);
        astParser.setSource(new String(input).toCharArray());  
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);  
       
        CompilationUnit result = (CompilationUnit) (astParser.createAST(null));  
          
        return result;  
    }  
}  