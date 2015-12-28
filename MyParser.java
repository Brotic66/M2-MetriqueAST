import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import step2.MethodDeclarationVisitor;
import step2.MethodInvocationVisitor;
import step2.Parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * @author Brice VICO
 * @date 16/12/2015
 */
public class MyParser extends Parser {
    public static final String projectPath = "/home/brice/IdeaProjects/M2-MetriqueAST/";
    public static final String projectSourcePath = projectPath + "Code/";
    public static final String jrePath = "/opt/java/jdk1.8.0_66/jre/lib/rt.jar";


    public static void main(String[] args) throws IOException {

        // read java files
        final File folder = new File(projectSourcePath);
        ArrayList<File> javaFiles = listJavaFilesForFolder(folder);

        MyStruct struct = new MyStruct();

        for (File fileEntry : javaFiles) {
            String content = FileUtils.readFileToString(fileEntry);

            CompilationUnit parse = parse(content.toCharArray());

            System.out.println("========== Fichier : " + fileEntry.getName());

            //classNOM(parse, true, struct);
            cohesionICH(parse, true, struct);

        }

        System.out.println("Minimum : " + struct.min);
        System.out.println("Maximum : " + struct.max);
        System.out.println("Somme : " + struct.som);
        System.out.println(struct.liste);
        System.out.println("Moyenne : " + (double)(struct.som)/struct.liste.size());
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
}
