package GUI.Widget;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Author: Matt
 * Date: 2/22/14
 * This class is designed to be a menuBar that is specially formatted to allow for
 * the easy compiling of various programs.
 */
public class CompilerBar extends MenuBar {

    /** Creates a new CompilerBar **/
    public CompilerBar(){
        super();

        //embellish
        configureFileOption();
        configureCompilationOption();
    }

    private void configureFileOption(){
        Menu menuFile = new Menu("File");
        MenuItem newProj = new MenuItem("New Project  | Ctrl-N");
        MenuItem loadProj = new MenuItem("Load Project  | Ctrl-L");
        MenuItem saveProj = new MenuItem("Save Project  | Ctrl-S");
        MenuItem saveProjAs = new MenuItem("Save Project As  | Ctrl-F1");
        menuFile.getItems().addAll(newProj,loadProj,saveProj,saveProjAs);
        getMenus().addAll(menuFile);
    }

    private void configureCompilationOption(){
        //FIXME: add ability to dynamically manipulate grammars
        Menu menuFile = new Menu("Program");
        MenuItem runProj = new MenuItem("Run Code  | Ctrl-R");

        Menu setGrammar = new Menu("Set Grammar");
        MenuItem bantamG = new MenuItem("Bantam");
        MenuItem java7G = new MenuItem("Java 7");
        setGrammar.getItems().addAll(bantamG,java7G);

        Menu settings = new Menu("Settings");
        MenuItem prefs = new MenuItem("Preferences");
        settings.getItems().add(prefs);

        menuFile.getItems().addAll(runProj, setGrammar);
        getMenus().addAll(menuFile,settings);
    }

}
