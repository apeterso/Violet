package GUI.Widget;

import IO.JavaCompiler;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

/**
 * Author: Matt
 * Date: 2/22/14
 * This class is designed to add a directory element to a FX GUI.
 */
public class DirectoryPanel extends ScrollPane {

    private ArrayList<Folder> folders;
    private String filterKey;
    private String currentDirectory;

    /** An interface an implementing class of which to be instantiated by an
     * external agent with access to the required resources to load a file.
     */
    public interface Loadable{
        public void loadFile(File file);
    }

    private Loadable loadModule;

    /** Sets the module that will be used for loading files selected from the directory
     * menu. Must be used by a higher level class, i.e one with access to the resources
     * required to perform IO operations and with access to GUI information.     *
     * @param loadModule The loadModule to be used internally
     */
    public void setLoadModule( Loadable loadModule ){
        this.loadModule = loadModule;
    }

    /** Construct a new DirectoryPanel and format it. **/
    public DirectoryPanel(String defaultDirectory){
        setStyle("-fx-background-color: #989898;");
        final String finalDefaultDirectory = defaultDirectory;
        folders = new ArrayList<Folder>(){{
            add( new Folder(finalDefaultDirectory) );
        }};
        build();
        setTitle("Directory");

        buildBottomPane();
        filterKey = "";
        currentDirectory = defaultDirectory;
    }

    /** Returns the current top-level directory.
     * @return The current top-level directory.
     */
    public String getCurrentDirectory(){
        return currentDirectory;
    }

    /**Reverts to default conditions**/
    private void reset(){
        setStyle("-fx-background-color: #989898;");
        folders = new ArrayList<Folder>(){{
            add( new Folder(System.getProperty("user.dir")) );
        }};
        build();
        setTitle("Directory");

        buildBottomPane();
    }

    /** Sets the top level directory to the given folder
     * @param directory The name of the new top directory.
     */
    public void setDirectory( String directory ){
        folders.clear();
        folders.add(new Folder(directory));
        build();
        buildBottomPane();
        currentDirectory = directory;
    }

    /** Sets the title for the panel **/
    public void setTitle(String newTitle){
        Label titularLabel = new Label(newTitle);
        titularLabel.setFont(Font.font("Courier New", 20));
        titularLabel.setStyle("-fx-font-weight: bold;");
        titularLabel.setStyle("-fx-text-fill: #333333;");
        titularLabel.setStyle("-fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 );");
        ((BorderPane)getContent()).setTop(titularLabel);
    }

    /** Constructs the GUI from the current directory **/
    public void build(){
        setContent(new BorderPane());
        getContent().setStyle("-fx-background-color: #989898;");
        if (folders.size() > 0)
            ((BorderPane)getContent()).setCenter(folders.get(0));
    }

    /** Builds the bottom options panel and adds to GUI **/
    private void buildBottomPane(){
        BorderPane bottomPane = new BorderPane();
        bottomPane.setPadding(new Insets(10, 0,0,0));

        // --- Browse button
        Button chooseDirectory = new Button("Choose Directory");
        chooseDirectory.setFont(Font.font("Courier New", 12));
        chooseDirectory.setOnMouseClicked(mouseEvent -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select Folder");
            File selectedDirectory = chooser.showDialog(new Stage());
            if (selectedDirectory != null)
                setDirectory(selectedDirectory.getPath());
        }
        );
        bottomPane.setLeft(chooseDirectory);

        // --- Filter Option
        BorderPane filterPane = new BorderPane();
        TextField filterField = new TextField();
        filterField.textProperty().addListener((observableValue, s, s2) ->
                setFilterKey(s2));
        filterPane.setLeft(new Label("Filter  "));
        filterPane.setRight(filterField);
        filterPane.setPadding(new Insets(0, 0,30,30));
        bottomPane.setRight(filterPane);

        //Add to scene
        bottomPane.setPrefHeight(555);
        ((BorderPane)getContent()).setBottom(bottomPane);
    }

    /** Sets the key to filter selections with. Filters current options.
     * @param key The new selection key
     */
    public void setFilterKey(String key ){
        filterKey = key;
        for (Folder folder : folders){
            folder.close();
            if (folder.isOpen())
                folder.traverse();
        }
    }

    /** Interface for files and folders **/
    private interface traversable{ abstract void traverse(); abstract void close(); }

    /** Class for storing/wrapping local directory information **/
    private class Folder extends BorderPane implements traversable {
        private final String name;
        private final File data;
        private final ArrayList<traversable> files;
        boolean traversed, overflow;
        public final int overflowSize = 100;

        /** Construct a new folder with given name
         * @param name The name of the folder
         */
        public Folder( String name ){
            super();
            this.name = name;
            data = new File(name);
            files = new ArrayList<>();
            traversed = overflow = false;

            //add to GUI
            setTop(new Label(data.getName()));
            setBottom(new BorderPane());
            format();

            //format opening and closing of folder
            setOnMouseClicked(mouseEvent -> {
                if (overflow) return;
                if (!traversed) traverse();
                else close();
                traversed = !traversed;
                mouseEvent.consume();
            });
        }

        /** Returns true iff the folder is open
         * @return Whether the folder is open
         */
        public boolean isOpen(){
            return traversed;
        }

        public void format(){
            try{
                ((Label)getTop()).setGraphic(new ImageView(new Image(getClass().getResourceAsStream(
                        "../images/Folder.png"))));
            }catch(NullPointerException npe){/*FIXME: bad runtime exception catch; only issue on webb*/}
        }

        /** Traverses the folder and populates its file
         * information.
         */
        public void traverse(){
            if (overflow) return;
            ((Label)getTop()).setGraphic(new ImageView(new Image(getClass().getResourceAsStream(
                    "../images/FolderOpen.png"))));
            File dir = data;
            File[] fileNames = dir.listFiles();//get list of files/folders

            //we'll see how this goes, would throw nullpointer anway so...
            assert fileNames != null;
            if (fileNames.length > overflowSize){ //abort if too large
                JavaCompiler.report("Directory \"" + name + "\" too large to display","WARNING");
                ((Label)getTop()).setGraphic(new ImageView(new Image(getClass().getResourceAsStream(
                        "../images/FolderBroken.png"))));
                overflow = true;
                return;
            }

            //loop through directory
            traversable newItem = null;
            for (File file : fileNames){
                BorderPane newPane = new BorderPane();
                //file or folder?
                if (file.isFile() && !file.isDirectory()){
                    if (filterKey.equals("") || file.getName().contains(filterKey)){
                        newItem = new LocalFile(file);
                        newPane.setTop((LocalFile)newItem);
                    }
                }
                else if (file.isDirectory()){
                    //make new folder and recurse
                    newItem  = new Folder(file.getPath());
                    newPane.setTop((Folder)newItem);
                }
                //store, add to GUI
                files.add(newItem);
                getBottomPane().setBottom(newPane);

            }
        }

        /** Closes the folder on the GUI, does not free
         * resources necessarily.
         */
        public void close(){
            //JVM will free resources right?
            setBottom(new BorderPane());
            format();
        }

        /** Returns the bottom-most pane accessible from this folder **/
        private BorderPane getBottomPane(){
            BorderPane pane = (BorderPane) getBottom();
            while ( pane.getBottom() != null )
                pane = (BorderPane) pane.getBottom();
            return pane;
        }

        /** Class for storing/wrapping local file information **/
        private class LocalFile extends Label implements traversable{
            private final File data;
            public final SimpleBooleanProperty isClosed;

            /** Constructs a new LocalFile out of a given File.
             * @param file The file for constuctions
             */
            public LocalFile( File file ){
                super(file.getName());
                format();
                data = file;

                //open file on click
                setOnMouseClicked(mouseEvent -> {
                    traverse();
                    mouseEvent.consume();
                });
                isClosed = new SimpleBooleanProperty(false);
                setGraphic(new ImageView(new Image(getClass().getResourceAsStream(
                        "../images/File.png"))));
            }

            /** Formats the label **/
            private void format(){
                setFont(Font.font("Courier New", 12));
                setStyle("-fx-font-color: white;");
            }

            /** Opens the file and parses data appropriately **/
            public void traverse(){
                loadModule.loadFile(data);
            }

            /** Marks the file as closed **/
            public void close(){
                isClosed.set(true);
            }
        }
    }

}
