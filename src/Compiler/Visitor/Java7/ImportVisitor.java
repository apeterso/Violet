package Compiler.Visitor.Java7;

import Compiler.AbstractSyntaxTree.RawSyntaxTree;
import Compiler.Nodes.ASTNode;
import GUI.Window.Utility.UtilWindow;
import javafx.scene.text.Text;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Visitor gathers lists of classes from the import statements of
 * a program.
 * Created by Matt Levine on 4/6/14.
 */
public class ImportVisitor extends Java7Visitor{

    private LinkedList<String> classes;

    public String[] getImportedClassNames(RawSyntaxTree tree){
        classes = new LinkedList<>();
        try{
            classes.addAll(getLangLibrary());
        }catch(IOException e){
            throwError();
        }

        tree.getRoot().accept(this);
        return classes.toArray(new String[classes.size()]);
    }

    /** Returns the classes automatically imported by the lang library
     * @return the classes automatically imported by the lang library
     */
    private Set<String> getLangLibrary() throws IOException{

        //FIXME: this method is pretty inefficient

        Set<String> classes = new HashSet<>();

        //get the lang library this cool way that I came up with
        String langDir = String.class.getResource("String.class").toString();
        langDir = langDir.replaceAll("%20"," ");
        if (langDir == null ) reportMissingLangDir();
        String langDirAbr =
                langDir.substring(langDir.indexOf("C:"),langDir.indexOf("rt.jar"))+"classlist";
        File langDirFile = new File(langDirAbr);

        BufferedReader fileContent = new BufferedReader(new FileReader(langDirFile));
        String line;
        StringBuilder assembly = new StringBuilder();
        //twice as much work to use two regexes, but significantly simpler logic
        Pattern class_ = Pattern.compile("lang/.*");
        Pattern classEnd = Pattern.compile("lang/[a-zA-Z]*");

        while ((line = fileContent.readLine()) != null){
            for (int i = 0; i < line.length(); i++){
                assembly.append(line.charAt(i));
                Matcher m = class_.matcher(assembly);
                Matcher m2 = classEnd.matcher(assembly);
                if (m.matches() && !m2.matches()){
                    classes.add(assembly.subSequence(5,assembly.length()-1).toString().replaceAll("java",""));
                    assembly.delete(0, assembly.length() - 1);
                }if (!m.hitEnd() && assembly.length() > 0){
                    assembly.delete(0,assembly.length()-1);
                }

            }
        }

        return classes;
    }

    /** Reports a failure to load the java Lang directory **/
    private void reportMissingLangDir(){
        throw new RuntimeException("Import Err: Could not locate Lang directory");
    }

    @Override
    public Object visitImportDeclaration(ASTNode node){
        //FIXME: fine for some purposes but not quite right
        //FIXME: grab methods and fields from these classes
        classes.add(node.getChildren().get(node.getNumChildren()-1).getChildren(
                ).get(0).treeNode.getValue());
        System.out.println(classes);
        return null;
    }

    /** Displays a relevant error message **/
    private void throwError(){
        //FIXME: provide error messages too? Not sure if should or not
        new UtilWindow("Error",300,300) {
            @Override
            protected void setCloseConditions() {   return;  }
            @Override
            protected void addWidgets(){
                super.addWidgets();
                String err = "Failed to load java.lang class objects";
                root.setCenter(new Text(err));
            }
        }.show();
    }


}
