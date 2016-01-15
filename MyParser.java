import Test.BMW;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;
import step2.MethodDeclarationVisitor;
import step2.MethodInvocationVisitor;
import step2.Parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 * @author Brice VICO
 * @date 16/12/2015
 */
public class MyParser extends Parser {
    public static final String projectPath = "/home/brice/IdeaProjects/M2-MetriqueAST/";
    public static final String projectSourcePath = projectPath + "Code/jadvisor/";
    public static final String jrePath = "/opt/java/jdk1.8.0_66/jre/lib/rt.jar";


    public static void main(String[] args) throws IOException {

        // read java files
        final File folder = new File(projectSourcePath);
        ArrayList<File> javaFiles = listJavaFilesForFolder(folder);

        MyStruct struct = new MyStruct();

        BMW voiture = new BMW();
        voiture.roule();

       /* for (File fileEntry : javaFiles) {
            String content = FileUtils.readFileToString(fileEntry);

            CompilationUnit parse = parse(content.toCharArray());

            //System.out.println("========== Fichier : " + fileEntry.getName());

            //classNOM(parse, true, struct);
            //cohesionICH(parse, true, struct);
        }*/

        //System.out.println(infoHidingMHF(javaFiles));
        //System.out.println(getMapChilds(javaFiles));
        inheritanceDOI(javaFiles);

        /*System.out.println("Minimum : " + struct.min);
        System.out.println("Maximum : " + struct.max);
        System.out.println("Somme : " + struct.som);
        System.out.println(struct.liste);
        System.out.println("Moyenne : " + (double)(struct.som)/struct.liste.size());*/
    }

    private static void cohesionICH(CompilationUnit parse, boolean init, MyStruct struct) {
        int nbr = 0;
        MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
        parse.accept(visitor1);

        for (MethodDeclaration method : visitor1.getMethods()) {
            MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
            method.accept(visitor2);

            for (MethodInvocation methodInvocation : visitor2.getMethods()) {
                String callerClass = method.resolveBinding().getDeclaringClass().getName();
                String calledClass = methodInvocation.resolveMethodBinding().getDeclaringClass().getName();

                if (calledClass.equals(callerClass))
                    nbr++;
            }
        }

        struct.som += nbr;
        struct.liste.add(nbr);
        if (nbr > struct.max)
            struct.max = nbr;
        if (init) {
            init = false;
            struct.min = nbr;
        } else if (struct.min > nbr) {
            struct.min = nbr;
        }
    }
    
	public static void cohesionTCC(CompilationUnit parse){
		 MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
	     parse.accept(visitor1);
		VariableDeclarationFragmentVisitor visitor = new VariableDeclarationFragmentVisitor();
		parse.accept(visitor);
		
		ArrayList<String> listeAttributs = new ArrayList<String>();
		int nbPair = 0;
		int nbCohesion = 0;
		
	     if(visitor1.getMethods().size() > 2){
		     IBinding classe = visitor1.getMethods().get(0).resolveBinding().getDeclaringClass();
		     System.out.println("Classe : "+ classe.getName());
		     for(VariableDeclarationFragment a : visitor.getVariables()){
		    	 listeAttributs.add(a.getName().toString());
		     }
		     for (int i = 0; i < visitor1.getMethods().size(); i++) {
		    	 for(int j = i+1; j < visitor1.getMethods().size(); j++){		    		 
		    		 for(String nomAtt : listeAttributs){
		    			 if(visitor1.getMethods().get(j).getBody() != null && visitor1.getMethods().get(i).getBody() != null && visitor1.getMethods().get(i).getBody().toString().contains(nomAtt) && visitor1.getMethods().get(j).getBody().toString().contains(nomAtt)){
		    				 nbCohesion++;
		    			 }
		    			 nbPair++;
		    		 }
		    	 }
		     }
		     if(nbPair != 0)
		    	 System.out.println("TCC = " + (float)((float)nbCohesion/(float)nbPair)*(float)100 + "%");
		     else
		    	 System.out.println("TCC nulle car 0 méthodes");
	     }
	     
	}

    private static void classNOM(CompilationUnit parse, boolean init, MyStruct struct) {
        MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
        parse.accept(visitor);

        int nbrMethod = visitor.getMethods().size();
        struct.som += nbrMethod;
        struct.liste.add(nbrMethod);

        if (nbrMethod > struct.max)
            struct.max = nbrMethod;
        if (init) {
            init = false;
            struct.min = nbrMethod;
        } else if (struct.min > nbrMethod) {
            struct.min = nbrMethod;
        }
    }

    private static float infoHidingMHF(ArrayList<File> javaFiles) throws IOException {
        ArrayList<Integer> mvs = new ArrayList<>();
        HashMap<String, HashSet<String>> map = getMapChilds(javaFiles);
        int nbrMethod = 0;
        int nbrClasse = 0;

        for (File fileEntry : javaFiles) {
            System.out.println(fileEntry.getName());

            String content = FileUtils.readFileToString(fileEntry);
            CompilationUnit parse = parse(content.toCharArray());

            nbrClasse++;

            MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
            parse.accept(visitor);

            ITypeBinding type = null;
            int cptMethodPubProt = 0;

            for (MethodDeclaration m : visitor.getMethods()) {
                String split[] = m.resolveBinding().toString().split(" ");
                type = m.resolveBinding().getDeclaringClass();
                cptMethodPubProt = 0;
                nbrMethod++;

                if (split[0].equals("public") || split[0].equals("protected")) {
                    cptMethodPubProt++;
                }

                String className = m.resolveBinding().getDeclaringClass().getName();

                if (map.containsKey(className))
                mvs.add(map.get(className).size() * cptMethodPubProt);
            }

            System.out.println(mvs);
        }

        float som = 0;
        for (int i : mvs)
          som += ((float)i/(float)(nbrClasse - 1));


        return (((float)1 - som)/ (float)nbrMethod);
    }

    public static HashMap<String, HashSet<String>> getMapChilds(ArrayList<File> javaFiles) throws IOException {
        HashMap<String, HashSet<String>> map = new HashMap<>();

        for (File fileEntry : javaFiles) {
            System.out.println(fileEntry.getName());

            String content = FileUtils.readFileToString(fileEntry);
            CompilationUnit parse = parse(content.toCharArray());

            MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
            parse.accept(visitor);

            if (visitor.getMethods().isEmpty())
                System.out.println("========== Fuck ==========");
            else {
                ITypeBinding typeEnfant = visitor.getMethods().get(0).resolveBinding().getDeclaringClass();
                String enfant = typeEnfant.getName();
                ITypeBinding[] inter = typeEnfant.getInterfaces();
                ITypeBinding typeParent = typeEnfant.getSuperclass();
                String parent = "";
                if (typeParent != null)
                    parent = typeParent.getName();

                ArrayList<String> listEnfants = new ArrayList<>();
                while (typeParent != null) {
                    if (!map.containsKey(parent))
                        map.put(parent, new HashSet<>());

                    if (!map.containsKey(enfant))
                        map.put(enfant, new HashSet<>());

                        listEnfants.add(enfant);
                        System.out.println("Enfant : " + enfant + "=== Parent : " + parent);

                        map.get(parent).addAll(listEnfants);

                    for (ITypeBinding i : inter) {
                        if (!map.containsKey(i.getName()))
                            map.put(i.getName(), new HashSet<>());

                        map.get(i.getName()).add(enfant);

                        if (!map.containsKey("Object"))
                            map.put("Object", new HashSet<>());

                        map.get("Object").add(i.getName());
                    }

                        if (!map.get(parent).contains(enfant))
                            map.get(parent).add(enfant);

                    typeEnfant = typeParent;
                    inter = typeEnfant.getInterfaces();
                    typeParent = typeParent.getSuperclass();

                    if (typeParent != null) {
                        enfant = typeEnfant.getName();
                        parent = typeParent.getName();
                    }
                }
            }
        }

        System.out.println(map);

        ajouterEnfants("Object",map);

        System.out.println(map);
        return map;
    }


    public static void ajouterEnfants(String s, HashMap<String, HashSet<String>> map)
    {
        if (map.containsKey(s))
            for (String ss : map.get(s))
            {
                System.out.println("Classe : " + ss);
                ajouterEnfants(ss, map);
                map.get(s).addAll(map.get(ss));

            }
    }

    public static void inheritanceDOI(ArrayList<File> javaFiles) throws IOException {
        HashMap<String, HashSet<String>> map = new HashMap<>();

        int profondeurMax = 0;
        for (File fileEntry : javaFiles) {
            System.out.println(fileEntry.getName());

            String content = FileUtils.readFileToString(fileEntry);
            CompilationUnit parse = parse(content.toCharArray());

            MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
            parse.accept(visitor);

            if (visitor.getMethods().isEmpty())
                System.out.println("========== Fuck ==========");
            else {
                ITypeBinding typeEnfant = visitor.getMethods().get(0).resolveBinding().getDeclaringClass();
                String enfant = typeEnfant.getName();
                ITypeBinding typeParent = typeEnfant.getSuperclass();
                String parent = "";
                if (typeParent != null)
                    parent = typeParent.getName();

                int profondeur = 0;
                while (typeParent != null && !typeParent.toString().contains("java.")) {

                    System.out.println(typeParent.toString());
                    typeEnfant = typeParent;
                    typeParent = typeParent.getSuperclass();

                    if (typeParent != null) {
                        enfant = typeEnfant.getName();
                        parent = typeParent.getName();
                    }

                    profondeur++;
                }

                if (profondeurMax < profondeur)
                    profondeurMax = profondeur;
            }
        }

        System.out.println(profondeurMax +1);
    }
}
