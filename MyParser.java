import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import scala.Array;
import scala.collection.mutable.StringBuilder;
import step2.MethodDeclarationVisitor;
import step2.Parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * @author Brice VICO
 * @date 16/12/2015
 */
public class MyParser extends Parser {
    public static final String projectPath = "/home/brice/IdeaProjects/ProjSeriai/";
    public static final String projectSourcePath = projectPath + "Code/";
    public static final String jrePath = "/usr/local/java/jdk1.8.0_40/jre/lib/rt.jar";


    public static void main(String[] args) throws IOException {

        // read java files
        final File folder = new File(projectSourcePath);
        ArrayList<File> javaFiles = listJavaFilesForFolder(folder);

        int min = 0;
        int max = 0;
        int som = 0;
        boolean init = true;
        ArrayList<Integer> liste = new ArrayList<>();


        for (File fileEntry : javaFiles) {
            String content = FileUtils.readFileToString(fileEntry);
            // System.out.println(content);

            CompilationUnit parse = parse(content.toCharArray());

            // print methods info
            //printMethodInfo(parse);
            int nbrMethod = nbrMethodInfo(parse);
            som += nbrMethod;
            liste.add(nbrMethod);

            if (nbrMethod > max)
                max = nbrMethod;
            if (init) {
                init = false;
                min = nbrMethod;
            } else if (min > nbrMethod) {
                min = nbrMethod;
            }


           // System.out.println(nbrMethod);

            // print variables info
            //printVariableInfo(parse);

            //print method invocations
            //printMethodInvocationInfo(parse);

        }

        System.out.println("Minimum : " + min);
        System.out.println("Maximum : " + max);
        System.out.println("Somme : " + som);
        System.out.println(liste);
        System.out.println("Moyenne : " + (double)som/liste.size());
    }

    public static int nbrMethodInfo(CompilationUnit parse) {
        MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
        parse.accept(visitor);

        return visitor.getMethods().size();
    }
}
