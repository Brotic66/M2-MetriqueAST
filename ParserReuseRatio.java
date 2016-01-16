package step2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class ParserReuseRatio {
	
	public static final String projectPath = "/Users/eymard/Documents/workspace/Ast";
	public static final String projectSourcePath = projectPath + "/Code/jadvisor";
	public static final String jrePath = "/Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk/Contents/Home/jre/lib/rt.jar";

	public static void main(String[] args) throws IOException {

		// read java files
		final File folder = new File(projectSourcePath);
		ArrayList<File> javaFiles = listJavaFilesForFolder(folder);
		double nbrSuper,total;
		
		Map<String, String> map = new HashMap<String,String>();
		
		ArrayList<String> listOfClasses = new ArrayList<String>();

		//On parcours une fois pour recuperer le nom des classes 
		for (File fileEntry : javaFiles) {
			/*String content = FileUtils.readFileToString(fileEntry);
			// System.out.println(content);
			CompilationUnit parse = parse(content.toCharArray());
			// print methods info
			printMethodInfo(parse);
			// print variables info
			printVariableInfo(parse);
			//print method invocations
			printMethodInvocationInfo(parse);*/
			
			String file = fileEntry.getName();
			file = file.substring(0,file.length()-5); //suppression de l'extension ".java"
			listOfClasses.add(file); //on ajoute dans la liste des classes 

		}
		
		//on recupere toutes les classes qui extends une autre classe
		for (File fileEntry : javaFiles) {
			String content = FileUtils.readFileToString(fileEntry);
			String fileName = fileEntry.getName();
			fileName = fileName.substring(0, fileName.length()-5); //suppression l'extension ".java"
			for (String name : listOfClasses){ //parcours de la iste des classes
					if(content.contains("public class "+fileName+" extends "+name)){
						
						map.put(fileName,name); //on ajoute dans la map de sorte que ça fasse (classe fils, classe pere (celle étendu)) 
					}	
			}
		}
		
		List<String> list = new ArrayList<String>();
		
		//on recupere les super classes
		for(Map.Entry<String, String> entry : map.entrySet()){
			 System.out.println("Classe enfant : "+entry.getKey() + " / Classe parent : " + entry.getValue());
			 if (!list.contains(entry.getValue())){
				 		list.add(entry.getValue());
					 }
			
		}
		nbrSuper=list.size();
		total= nbrSuper/ listOfClasses.size();
		

		System.out.println("\n *****  Reuse Ratio (U)");;
		System.out.println("Nombre total de Superclasses : " +nbrSuper );
		System.out.println("Nombre total de classes : " +listOfClasses.size() );
		System.out.println("Total U : " +total );
		
		
	}

	// read all java files from specific folder
	public static ArrayList<File> listJavaFilesForFolder(final File folder) {
		ArrayList<File> javaFiles = new ArrayList<File>();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				javaFiles.addAll(listJavaFilesForFolder(fileEntry));
			} else if (fileEntry.getName().contains(".java")) {
				// System.out.println(fileEntry.getName());
				javaFiles.add(fileEntry);
			}
		}

		return javaFiles;
	}

	// create AST
	private static CompilationUnit parse(char[] classSource) {
		ASTParser parser = ASTParser.newParser(AST.JLS4); // java +1.6
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
 
		parser.setBindingsRecovery(true);
 
		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);
 
		parser.setUnitName("");
 
		String[] sources = { projectSourcePath }; 
		String[] classpath = {jrePath};
 
		parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
		parser.setSource(classSource);
		
		return (CompilationUnit) parser.createAST(null); // create and parse
	}

	// navigate method information
	public static void printMethodInfo(CompilationUnit parse) {
		MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
		parse.accept(visitor);

		for (MethodDeclaration method : visitor.getMethods()) {
			System.out.println("Method name: " + method.getName()
					+ " Return type: " + method.getReturnType2());
		}

	}

	// navigate variables inside method
	public static void printVariableInfo(CompilationUnit parse) {

		MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
		parse.accept(visitor1);
		for (MethodDeclaration method : visitor1.getMethods()) {

			VariableDeclarationFragmentVisitor visitor2 = new VariableDeclarationFragmentVisitor();
			method.accept(visitor2);

			for (VariableDeclarationFragment variableDeclarationFragment : visitor2
					.getVariables()) {
				System.out.println("variable name: "
						+ variableDeclarationFragment.getName()
						+ " variable Initializer: "
						+ variableDeclarationFragment.getInitializer());
			}

		}
	}
	
	// navigate method invocations inside method
		public static void printMethodInvocationInfo(CompilationUnit parse) {

			MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
			parse.accept(visitor1);
			for (MethodDeclaration method : visitor1.getMethods()) {

				MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
				method.accept(visitor2);

				for (MethodInvocation methodInvocation : visitor2.getMethods()) {
					System.out.println("method " + method.getName() + " invoc method "
							+ methodInvocation.getName());
				}

			}
		}
		
		
		public static List<MethodDeclaration> getAllMethods (CompilationUnit parse) {
			MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
			parse.accept(visitor);
			List<MethodDeclaration> allMethods = new ArrayList<MethodDeclaration>();
			allMethods.addAll(visitor.getMethods());
			//allMethods.addAll(visitor.getStaticMethods());
			return allMethods;
		}

}


