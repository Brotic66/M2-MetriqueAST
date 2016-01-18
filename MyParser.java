import Test.BMW;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;
import step2.MethodDeclarationVisitor;
import step2.MethodInvocationVisitor;
import step2.Parser;
import step2.VariableDeclarationFragmentVisitor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


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
        HashMap<String, ArrayList<String>> map = new HashMap<>();
        MyStruct struct = new MyStruct();

        for (File fileEntry : javaFiles) {
            String content = FileUtils.readFileToString(fileEntry);
            CompilationUnit parse = parse(content.toCharArray());

            /**
             * Fonction pour la métrique NOM
             */
            //classNOM(parse, true, struct);

            /**
             * Fonction pour la métrique NOM
             */
            //classNOA(parse, true, struct);

            /**
             * Fonction pour la métrique TCC
             */
            //cohesionTCC(parse);

            couplingCBO(parse, javaFiles, fileEntry.getName(), map);
        }

        /**
         * Métrique MHF (non réussis) + affichage
         */
        //System.out.println(infoHidingMHF(javaFiles));

        /**
         * Métrique DOOI
         */
        //inheritanceDOI(javaFiles);

        /**
         * Métrique NOC
         */
        // inheritanceNOC(javaFiles, true, struct);

        /**
         * Métrique NMO
         */
        //polymorphismNMO(javaFiles);

        /**
         * Affichage des résultats stocké dans la structure (si il y a lieu)
         */
        System.out.println("Minimum : " + struct.min);
        System.out.println("Maximum : " + struct.max);
        System.out.println("Somme : " + struct.som);
        System.out.println(struct.liste);
        System.out.println("Moyenne : " + (double)(struct.som)/struct.liste.size());

        /**
         * Affichage pour chaque classe de CBO
         */
        for (Map.Entry entry : map.entrySet()) {
            System.out.println("Class : " + entry.getKey() + " CBO : " + ((ArrayList)entry.getValue()).size());
        }

        /**
         * Moyenne de CBO
         */
        int totalCouplage = 0;
        for (Map.Entry entry : map.entrySet()) {
            totalCouplage += ((ArrayList)entry.getValue()).size();
        }
        System.out.println("Moyenne de couplage : " + (double) totalCouplage / (double) 34 );
    }

    /**
     * Fonction permettant de calculer la métrique TCC de cohésion.
     *
     * @param parse
     */
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

    /**
     * Fonction permettant de calculer la métrique de classe NOM.
     *
     * @param parse
     * @param init
     * @param struct
     */
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

    /**
     * Méthode permettant de calculer la métrique de classe NOA.
     *
     * @param parse
     * @param init
     * @param struct
     */
    private static void classNOA(CompilationUnit parse, boolean init, MyStruct struct) {
        VariableDeclarationFragmentVisitor visitor = new VariableDeclarationFragmentVisitor();
        parse.accept(visitor);

        int nbrMethod = 0;

        for (VariableDeclaration v : visitor.getVariables()) {
            if (v.resolveBinding().getDeclaringMethod() == null) {
                System.out.println(v);
                nbrMethod++;
            }
        }

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

    /**
     * Fonction permettant de calculer la métrique d'information cachée MHF.
     * Nous n'avons pas réussi à obtenir des résultats corrects avec cette fonctions.
     *
     * @param javaFiles
     * @return
     * @throws IOException
     */
    private static float infoHidingMHF(ArrayList<File> javaFiles) throws IOException {
        ArrayList<Integer> mvs = new ArrayList<>();
        HashMap<String, HashSet<String>> map = getMapChilds(javaFiles, true);
        int nbrMethod = 0;
        int nbrClasse = 0;

        for (File fileEntry : javaFiles) {
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
        }

        float som = 0;
        for (int i : mvs)
          som += ((float)i/(float)(nbrClasse - 1));


        return (((float)1 - som)/ (float)nbrMethod);
    }

    /**
     * Fonction permettant le calcul de la métrique d'héritage NOC.
     *
     * @param javaFiles
     * @param init
     * @param struct
     * @throws IOException
     */
    private static void inheritanceNOC(ArrayList<File> javaFiles, boolean init, MyStruct struct) throws IOException {
        ArrayList<Integer> mvs = new ArrayList<>();
        HashMap<String, HashSet<String>> map = getMapChilds(javaFiles, false);

        for (Map.Entry<String, HashSet<String>> entry : map.entrySet()) {
            if (!entry.getKey().equals("Object") && !entry.getKey().startsWith("J")) {
                boolean test = false;

                for (File s : javaFiles) {
                    System.out.println(s.getName());
                    String split[] = s.getName().split("\\.");

                    if (split[0].equals(entry.getKey()))
                        test = true;
                }

                if (test) {
                    struct.som += entry.getValue().size();
                    struct.liste.add(entry.getValue().size());

                    if (entry.getValue().size() > struct.max)
                        struct.max = entry.getValue().size();
                    if (init) {
                        init = false;
                        struct.min = entry.getValue().size();
                    } else if (struct.min > entry.getValue().size()) {
                        struct.min = entry.getValue().size();
                    }
                }
            }
        }
    }

    /**
     * Cette fonction crée une map avec clef = parent et valeur = liste d'enfants.
     * Si chain = true alors on liste les enfants indirect, sinon seulement les directs.
     *
     * @param javaFiles
     * @param chain
     * @return
     * @throws IOException
     */
    public static HashMap<String, HashSet<String>> getMapChilds(ArrayList<File> javaFiles, boolean chain) throws IOException {
        HashMap<String, HashSet<String>> map = new HashMap<>();

        for (File fileEntry : javaFiles) {
            System.out.println(fileEntry.getName());

            String content = FileUtils.readFileToString(fileEntry);
            CompilationUnit parse = parse(content.toCharArray());

            MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
            parse.accept(visitor);

            if (!visitor.getMethods().isEmpty()) {
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

        if (chain)
            ajouterEnfants("Object",map);

        System.out.println(map);
        return map;
    }

    /**
     * Fonction récursive qui ajoute les enfants indirect aux parents en utilisants le principe des liste chainée revisité.
     *
     * @param s
     * @param map
     */
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

    /**
     * Fonction pour la métrique d'héritage DOI.
     *
     * @param javaFiles
     * @throws IOException
     */
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

    /**
     * Fonction permettant le calcul de la métrique NMO
     *
     * @param javaFiles
     * @throws IOException
     */
    public static void polymorphismNMO(ArrayList<File> javaFiles) throws IOException {
        ArrayList<String> classList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        Map<String, Integer> listMethodOvByClass = new HashMap<>();

        for (File fileEntry : javaFiles) {
            String file = fileEntry.getName();
            file = file.substring(0,file.length()-5);
            classList.add(file);

        }

        for (File fileEntry : javaFiles) {
            String content = FileUtils.readFileToString(fileEntry);
            String fileName = fileEntry.getName();
            fileName = fileName.substring(0, fileName.length()-5);
            for (String name : classList)
                if(content.contains("public class "+fileName+" extends "+name))
                    map.put(fileName,name);
        }

        ArrayList<MethodDeclaration> listPmethod= new ArrayList<>();
        ArrayList<MethodDeclaration> listFmethod = new ArrayList<>();
        for(Map.Entry<String, String> entry : map.entrySet()){
            for(File fileEntry : javaFiles){
                String content = FileUtils.readFileToString(fileEntry);
                CompilationUnit parse = parse(content.toCharArray());
                if(fileEntry.getName().equals(entry.getKey()+".java"))
                    listFmethod = (ArrayList<MethodDeclaration>) getAllMethods(parse);
                if(fileEntry.getName().equals(entry.getValue()+".java"))
                    listPmethod = (ArrayList<MethodDeclaration>) getAllMethods(parse);
            }

            int overridedMethods = 0;
            for(MethodDeclaration pere : listPmethod) {
                String firstLine[] = pere.toString().split("\\{");
                for(MethodDeclaration fils : listFmethod) {
                    String line[] = fils.toString().split("\\{");
                    if(firstLine[0].equals(line[0]))
                        overridedMethods++;
                }
            }
            listMethodOvByClass.put(entry.getKey(),overridedMethods);
        }

        System.out.println("\n ========== nombre de classes : "+ classList.size());
        System.out.println("\n ========== Nombre de méthodes surchargées par sous-classe (NMO)");
        for(Map.Entry<String, Integer> entry : listMethodOvByClass.entrySet())
            System.out.println("Sous-classe : "+entry.getKey() + " -> NMO : " + entry.getValue());
    }

    /**
     * Récupèrent toutes les méthodes d'un fichier et les places dans un ArrayList
     *
     * @param parse
     * @return
     */
    public static List<MethodDeclaration> getAllMethods (CompilationUnit parse) {
        MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
        parse.accept(visitor);
        List<MethodDeclaration> allMethods = new ArrayList<>();

        allMethods.addAll(visitor.getMethods());
        return allMethods;
    }

    /**
     * Métrique de réuilisation
     *
     * @param javaFiles
     * @throws IOException
     */
    public static void ReuseRatio(ArrayList<File> javaFiles) throws IOException {
        Map<String, String> map = new HashMap<>();
        ArrayList<String> classList = new ArrayList<>();
        double nbrParent;
        double result;

        for (File fileEntry : javaFiles) {
            String file = fileEntry.getName();
            file = file.substring(0,file.length()-5);
            classList.add(file);

        }

        for (File fileEntry : javaFiles) {
            String content = FileUtils.readFileToString(fileEntry);
            String fileName = fileEntry.getName();
            fileName = fileName.substring(0, fileName.length()-5);
            for (String name : classList)
                if(content.contains("public class "+fileName+" extends "+name))
                    map.put(fileName,name);
        }

        List<String> list = new ArrayList<>();

        map.entrySet().stream().filter(entry -> !list.contains(entry.getValue())).forEach(entry -> list.add(entry.getValue()));

        nbrParent=list.size();
        result= nbrParent/ classList.size();

        System.out.println("Nombre total de Superclasses : " +nbrParent );
        System.out.println("Nombre total de classes : " +classList.size() );
        System.out.println("Résultat : " +result );
    }

    /**
     * Métrique de Reutilisation.
     *
     * @param javaFiles
     * @throws IOException
     */
    public static void reuseSpec(ArrayList<File> javaFiles) throws IOException {
        double nbrChild;
        double nbrParent;
        double result;

        Map<String, String> map = new HashMap<>();
        ArrayList<String> classList = new ArrayList<>();

        for (File fileEntry : javaFiles) {
            String file = fileEntry.getName();
            file = file.substring(0,file.length()-5);
            classList.add(file);

        }

        for (File fileEntry : javaFiles) {
            String content = FileUtils.readFileToString(fileEntry);
            String fileName = fileEntry.getName();
            fileName = fileName.substring(0, fileName.length()-5);
            for (String name : classList)
                if(content.contains("public class "+fileName+" extends "+name))
                    map.put(fileName,name);
        }

        List<String> list = new ArrayList<>();
        map.entrySet().stream().filter(entry -> !list.contains(entry.getValue())).forEach(entry -> list.add(entry.getValue()));
        nbrParent=list.size();
        nbrChild= map.size();
        result= nbrChild/ nbrParent;

        System.out.println("Nombre total de subclasses : " +nbrChild );
        System.out.println("Nombre total de Superclasses : " +nbrParent );
        System.out.println("Résultat : " +result );
    }

    public static void couplingCBO(CompilationUnit parse, ArrayList<File> javaFiles, String fileName, Map<String, ArrayList<String>> map) throws IOException {

        ArrayList<String> l = new ArrayList<String>();
        ArrayList<String> classList = new ArrayList<>();
        ArrayList<String> couplage = new ArrayList<>();

        for (File fileEntry : javaFiles) {
            String file = fileEntry.getName();
            file = file.substring(0,file.length()-5);
            classList.add(file);

        }

        VariableDeclarationFragmentVisitor visitor = new VariableDeclarationFragmentVisitor();
        parse.accept(visitor);

        for (VariableDeclarationFragment frag : visitor.getVariables()) {
            String s = frag.resolveBinding().getType().getName();

            while (s.contains("[]"))
                s = s.substring(0, s.length() - 2);
            if (!couplage.contains(s) && !s.equals("long")&& !s.equals("int")&& !s.equals("byte")&& !s.equals("short")
                    && !s.equals("float") && !s.equals("double") && !s.equals("char") && !s.equals("boolean") && !s.equals("String"))
                couplage.add(s);
        }

        l.addAll(couplage.stream().filter(classList::contains).collect(Collectors.toList()));
        map.put(fileName, l);
    }
}
