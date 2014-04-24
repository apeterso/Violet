package GUI;

import Compiler.Parser.CFG.ContextFreeGrammar;
import GUI.Widget.DirectoryPanel;
import GUI.Window.GuiWindow;
import GUI.Window.Utility.SettingsDialog;
import IO.IOManager;
import IO.JavaCompiler;
import IO.PreferenceManager;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;

/**
 * Author: Matt
 * Date: 2/21/14
 * This class is designed to control major GUI elements.
 */
public class DesktopController {

    public static enum STATE{PAUSED,ACTIVE,SECONDARY}

    private final GuiWindow window;
    private STATE state;
    private final HashMap<String,String> programNames;
    private final PreferenceManager preferences;
    private long lastTime = 0;
    /** The number of seconds in between background delay **/
    private long num_delay_seconds = 1;
    /** The preference window **/
    private final SettingsDialog settingsDialog;

    /**Constructs a new GUI.DesktopController. Defaults state to active.
     * @param stage The stage of the DesktopController
    **/
    public DesktopController(Stage stage){
        window = new GuiWindow(1200,750);
        window.show(stage);

        state = STATE.ACTIVE;
        initBackgroundTasks();
        programNames = new HashMap<>();

        //manage preferences
        preferences = new PreferenceManager();
        initializeBackup(stage);

        window.buildDirectoryPanel(preferences.loadDirectory());
        settingsDialog = new SettingsDialog(window);
        settingsDialog.initOwner(stage);
//        configDrag();
    }


    /** Sets the title of the current tab to the given string
     * @param title The new title for the tab
     */
    public void setTabTitle(String title){
        window.setTabTitle(title);
    }

    /** Returns the name of the currently executing program **/
    public String getName(){
        if (programNames.containsKey(window.getTabID()))
            return programNames.get(window.getTabID());
        return null;
    }

    /** Sets the name of the currently executing program **/
    public void setName(String name){programNames.put( window.getTabID(), name );}

    /** Initializes background tasks for the TextArea of the controller **/
    private void initBackgroundTasks(){
        new AnimationTimer(){
            @Override
            public void handle(long now){
                //every few seconds, force the parser to run on the grammar
                if ((now - lastTime)/num_delay_seconds >= 1000000000){
                    lastTime = now;
                    window.forceUpdateText();
                }
            }
        }.start();
    }

    /** Starts background tasks for the TextArea of the controller **/
    public void startBackgroundTasks(){
        state = STATE.ACTIVE;
    }

    /** Pauses background tasks for the TextArea of the controller **/
    public void stopBackgroundTasks(){
        state = STATE.PAUSED;
    }

    /** Returns the top menuBar
     * @return The top menubar
     */
    public MenuBar getMenu(){
        return window.getMenu();
    }

    /**
     * Returns the text currently underlying the window.
     * @return The raw window text
     */
    public String getText(){
        return window.getText();
    }

    /** Attempts to set the window's text to the current text, enforces no time
     * restraint or guarantee of execution or formatting.
     * @param text The new text for the window
     */
    public void updateText(String text){
        window.updateText(text);
    }

    /** Marks the windows text as saved or unsaved
     * @param saved whether the window should be marked as saved or unsaved
     */
    public void setSaved(boolean saved){ window.setSaved(saved);}

    /** Clears the window. Returns success. **/
    public void clearWindow(){  window.clear(); }

    /** Returns true iff the window is empty **/
    public boolean windowIsEmpty(){ return window.isEmpty(); }

    /** Constructs backup systems **/
    public final void initializeBackup(Stage stage){
        String backupLocation;
        int i = 0;
        while ((backupLocation = preferences.getBackupLocation(i)) != null){
            try{
                if (i > 0)
                    window.addTab();
                setName(backupLocation);
                setTabTitle(backupLocation.substring(backupLocation.lastIndexOf("\\")+1,
                        backupLocation.lastIndexOf(".")));
                updateText(IOManager.loadFile(new File(backupLocation)));
            }catch (NullPointerException ne){
                preferences.removeBackupEntries();
                JavaCompiler.report("Err: file failed to load; backups flushed for safety", "ERROR");
            }
            i++;
        }

        // --- Main Stage on Close
        stage.setOnCloseRequest(windowEvent -> {
            //clear existing backups
            preferences.removeBackupEntries();
            while( window.getNumTabs() > 0 ){
                //prompt to save if relevant
                clearWindow();
                if (!windowIsEmpty()){
                    windowEvent.consume();
                    return;
                }
                //save files
                if (getName() != null  && !getName().equals("        ")){
                    preferences.setBackupLocation(getName());
                }
                window.closeTab();
            }
            preferences.saveDirectory(window.getCurrentDirectory());
            try{
                //noinspection FinalizeCalledExplicitly
                preferences.finalize();
            }catch(Throwable fthrowable){
                System.out.println("Internal Err: unable to finalize preferences");
            }
        });

    }

    /**
     * Outputs information to the GUI (appends, not replaces)
     * @param simpleStringProperty the property to link to
     * @param severityProperty The severity of the text
     */
    public void linkOutput(SimpleStringProperty simpleStringProperty,
                           SimpleStringProperty severityProperty){
        window.linkOutput(simpleStringProperty,severityProperty);
    }

    /** Empties the output screen */
    public void clearOutput(){
        window.clearOutput();
     }

    /** Adds a tab to the main window on the GUI **/
    public void addTab(){window.addTab();}

    /** Sets the loading module of the directory panel. For more details, see
     * DirectoryPanel.setLoadModule.
     * @param loadable A class with loading capacities as specified by the Loadable
     *                 interface
     */
    public void setDirectoryLoadModule(DirectoryPanel.Loadable loadable){
        window.setDirectoryLoadModule(loadable);
    }

    /** Configures dragging on tabs to open new windows **/
    private void configDrag(){
        // --- drag out windows

        EventHandler<MouseEvent> onDragged = (mouseEvent -> {
            if (window.getMouseFired() && Math.abs(window.getMouseDrag()
                    - mouseEvent.getX()) > 60){

                //build and config new tab
                GuiWindow newWin = new GuiWindow(1200,750);
                Stage newStage = new Stage();
                File loadFile = new File(getName());
                newWin.show(newStage);
                newWin.updateText(IOManager.loadFile(loadFile));
                newStage.setTitle("Elope - " + loadFile.getName());
                setName(loadFile.getPath());
                newWin.setTabTitle(loadFile.getName());

                //handle old tab
                window.setMouseFired(false);
                window.closeTab();
            }
        });
        window.configDrag(onDragged);
    }

    /** Sets the CFG with which to parse content
     * @param grammar the new grammar
     */
    public void setGrammar(ContextFreeGrammar grammar){
        window.setGrammar(grammar);
    }

    /** Shows the settings dialog **/
    public void showSettingsDialog(){
        settingsDialog.show();
        settingsDialog.requestFocus();
    }

    /** Sets the minmum delay for the background refresh items
     * @param delay the minimum delay
     */
    public final void setBackgroundDelay(long delay){ num_delay_seconds = delay;}

}
