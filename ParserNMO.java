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

public class ParserNMO {
	
	public static final String projectPath = "/Users/eymard/Documents/workspace/Ast";
	public static final String projectSourcePath = projectPath + "/Code/jadvisor";
	public static final String jrePath = "/Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk/Contents/Home/jre/lib/rt.jar";

	public static void main(String[] args) throws IOException {

		// read java files
		final File folder = new File(projectSourcePath);
		ArrayList<File> javaFiles = listJavaFilesForFolder(folder);
		
		
		Map<String, String> map = new HashMap<String,String>();
		Map<String, Integer> overMethByClass = new HashMap<String,Integer>();
		
		
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
			listOfClasses.add(file); //on ajoute le nom dans la liste des classes 

		}
		
		//on recupere toute les classes qui extends une autre classe
		for (File fileEntry : javaFiles) {
			String content = FileUtils.readFileToString(fileEntry);
			String fileName = fileEntry.getName();
			fileName = fileName.substring(0, fileName.length()-5); //suppression de l'extension ".java"
			for (String name : listOfClasses){ //parcours de la listes des classes
					if(content.contains("public class "+fileName+" extends "+name)){
						map.put(fileName,name); //on ajoute dans la map de sorte que ça fasse (classe fille, classe pere (celle étendu)) 
					}	
			}
		}
		
		//on compare les signatures des classes pere et fils
		ArrayList<MethodDeclaration> listPmethod= new ArrayList<MethodDeclaration>();
		ArrayList<MethodDeclaration> listFmethod = new ArrayList<MethodDeclaration>();
		for(Map.Entry<String, String> entry : map.entrySet()){
			 System.out.println("Sous-classe : "+entry.getKey() + " / Classe : " + entry.getValue());
			for(File fileEntry : javaFiles){
				String content = FileUtils.readFileToString(fileEntry);
				CompilationUnit parse = parse(content.toCharArray());
				if(fileEntry.getName().equals(entry.getKey()+".java")){ //on récupère la liste des méthodes du fils avec un visiteur
				
					listFmethod = (ArrayList<MethodDeclaration>) getAllMethods(parse);
				}
				if(fileEntry.getName().equals(entry.getValue()+".java")) {//on récupère la liste des méthodes du pere avec un visiteur
					
					listPmethod = (ArrayList<MethodDeclaration>) getAllMethods(parse); 
				}
			}
			//comparaison  1 à 1 les méthodes pour voir si overriding,
			int overridedMethods = 0;
			for(MethodDeclaration pere : listPmethod) {
				String firstLine[] = pere.toString().split("\\{");//Pour avoir simplement la signature
				for(MethodDeclaration fils : listFmethod) {
					String line[] = fils.toString().split("\\{");//Pour avoir simplement la signature
					//System.out.println("methodes peres : "+ firstLine[0]);
					//System.out.println("methodes fils : "+ line[0]);
					if(firstLine[0].equals(line[0])){
						overridedMethods++;
					}
				}
			}
			//on ajoute dans la map final le resultat 
			overMethByClass.put(entry.getKey(),overridedMethods);
			
		}
		
		System.out.println("\n **** nombre de classes : "+ listOfClasses.size())
		;
		System.out.println("\n **** Nombre de méthodes surchargées par sous-classe (NMO)");
		for(Map.Entry<String, Integer> entry : overMethByClass.entrySet()){
			 System.out.println("Sous-classe : "+entry.getKey() + " -> NMO : " + entry.getValue());
		}
		//System.out.println("Nombre total de méthodes surchargées : " + nbTotOfOverridingMethods);
		
		
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
			//System.out.println(" All methods : "+ allMethods);
			return allMethods;
		}

}
