package GUI.Util;

import Compiler.Parser.LanguageSource.BantamGrammarSource;
import Compiler.Parser.LanguageSource.JavaGrammar;
import GUI.DesktopController;
import GUI.Widget.DirectoryPanel;
import IO.IOManager;
import IO.JavaCompiler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

/**
 * Author: Matt
 * Date: 2/21/14
 * This class is designed to link classes in separate directories for the GUI elements
 * (e.g, IO) with the topmost GUI elements. This class instantiates no new fields, it
 * only manages connections between existing objects.
 */
public class GuiLinkageManager {
    private final DesktopController desktopController;

    /** Creates new GUI.Util.GuiLinkageManager with associated DesktopController
     * @param desktopController the associated DesktopController
     */
    public GuiLinkageManager(DesktopController desktopController, Stage stage){
        this.desktopController = desktopController;

        //link GUI to other items
        linkGuiToIO(stage);

        //redirect compiler output
        hijackCompilerOutput();

        //enable directory to perform loading tasks
        enableDirectoryLoading(stage);
    }

    /** Link the GUI items to IO items **/
    public void linkGuiToIO(Stage stage){

        final Stage finalStage = stage;
        final MenuBar menuBar = desktopController.getMenu();

        //set the menubar actions and shortcuts
        // --- Save File
        menuBar.getMenus().get(0).getItems().get(2).setOnAction(t -> {
            File saveFile = null;
            if (desktopController.getName() == null){
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Profile");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Source Code","*.java")
                );
                saveFile = fileChooser.showSaveDialog(finalStage);
            }
            else{
                saveFile = new File(desktopController.getName());
            }
            if (saveFile != null){
                IOManager.saveFile(saveFile,desktopController.getText());
                desktopController.setName(saveFile.getPath());
                desktopController.setTabTitle(saveFile.getName());
                desktopController.setSaved(true);
            }
        });

        // --- Save as
        menuBar.getMenus().get(0).getItems().get(3).setOnAction(t -> {
            desktopController.setName(null);
            menuBar.getMenus().get(0).getItems().get(2).fire();
        });


        // --- Load File
        menuBar.getMenus().get(0).getItems().get(1).setOnAction(t -> {
            //ask for file name
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Profile");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Source Code","*.java")
            );
            File loadFile = fileChooser.showOpenDialog(finalStage);
            if (loadFile != null){
                desktopController.addTab();
                desktopController.updateText(IOManager.loadFile(loadFile));
                finalStage.setTitle("Elope - " + loadFile.getName());
                desktopController.setName(loadFile.getPath());
                desktopController.setTabTitle(loadFile.getName());
            }
        });

        // --- New Project
        menuBar.getMenus().get(0).getItems().get(0).setOnAction(t -> {
            desktopController.clearWindow();
            desktopController.setTabTitle("        ");
            finalStage.setTitle(null);
        });

        // -- Run Program
        menuBar.getMenus().get(1).getItems().get(0).setOnAction(t -> {
            desktopController.clearOutput();
            String programName = desktopController.getName();
            JavaCompiler.report("Running "+programName+"...","PLAIN");
            JavaCompiler.runProgram(programName);
            JavaCompiler.report("\nProcess Terminated.","PLAIN");
        });

        // -- Set Grammar
        ((Menu)menuBar.getMenus().get(1).getItems().get(1)).getItems().get(0).setOnAction(t -> {
            desktopController.setGrammar(BantamGrammarSource.getBantamGrammar());
        });
        ((Menu)menuBar.getMenus().get(1).getItems().get(1)).getItems().get(1).setOnAction(t -> {
            desktopController.setGrammar(JavaGrammar.getJavaGrammar());
        });


        // -- Open Settings Dialog
        menuBar.getMenus().get(2).getItems().get(0).setOnAction(t -> {
            desktopController.showSettingsDialog();
        });

        // -- Clear Output
        JavaCompiler.outputToggler.addListener(
                (observableValue, aBoolean, aBoolean2) ->
                        desktopController.clearOutput());

        setAccelerators(stage);
    }

    /** Grabs the output from the compiler and exports it to the GUI window **/
    public void hijackCompilerOutput(){
        desktopController.linkOutput(JavaCompiler.output, JavaCompiler.severityProperty);
    }

    /** Enables accelerators for menu items **/
    private void setAccelerators(Stage stage){
        int numMenus = 2;
        KeyCode[][] hotkeys = {{KeyCode.N, KeyCode.L, KeyCode.S, KeyCode.F1},{KeyCode.R,KeyCode.F2}};
        for (int k = 0; k < numMenus; k++ ){
            for ( int i = 0; i < desktopController.getMenu().getMenus().get(k).getItems().size(); i++ ){
                final int j = i;
                final int q = k;
                stage.getScene().getAccelerators().put(
                  new KeyCodeCombination(hotkeys[k][i], KeyCombination.CONTROL_DOWN ),
                        () -> {
                            desktopController.getMenu().getMenus().get(q).getItems().get(j).fire();
                        }
                );
            }
        }
    }

    /** Enables directory loading. For more information, see DirectoryPanel.setLoadModule **/
    public void enableDirectoryLoading(Stage stage){
        desktopController.setDirectoryLoadModule(
                file -> {
                    desktopController.addTab();
                    desktopController.updateText(IOManager.loadFile(file));
                    stage.setTitle("Elope - " + file.getName());
                    desktopController.setName(file.getPath());
                    desktopController.setTabTitle(file.getName());
                }
        );
    }

}
