package GUI.Window;

import Compiler.Parser.CFG.ContextFreeGrammar;
import GUI.Widget.OutputGUI;
import GUI.Widget.RichTextArea;
import GUI.Widget.SmartRichTextArea;
import GUI.Widget.CompilerBar;
import GUI.Widget.DirectoryPanel;
import GUI.Window.Utility.FinderDialog;
import Util.FunctionMapProxy;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Author: Matt
 * Date: 2/21/14
 * This class is designed to be the toplevel window for the GUI
 */
public class GuiWindow{

    private BorderPane root;
    private RichTextArea textArea;
    private final int width,length;
    private MenuBar menuBar;
    private final ContextMenu popup;
    private final Label lineNumbers = new Label("\n\n\n\n0");
    private final Label lineNumberHighlight = new Label("\n\n\n\n0");

    private double mouseDrag;
    private boolean mouseDragFired;
    private FinderDialog finderDialog;

    private boolean parse = true;

    private FunctionMapProxy<String> functionMapProxy;

    /** Contstructs a new GUIWindow of the given dimensionality
     *
     * @param width The width of the window
     * @param length The length of the window
     */
    public GuiWindow(int width, int length){
        this.width = width; this.length = length;
        //format popup menu
        popup = new ContextMenu();
        configurePopupMenu();
    }

    /** Launches a new GUI window  **/
    public void show(Stage primaryStage){
        //add a title
        primaryStage.setTitle("Cerulean");
        //construct the main window
        root = new BorderPane();
//        primaryStage.setScene(new Scene(new Group(root)));
        primaryStage.setScene(new Scene(root,width,length));

        //embellish
        buildTextArea();
        buildBottomPanel();
        setCloseConditions();
        configureColorLinkage();

        //build menus
        buildMenus();
        setAccelerators(primaryStage);

        //build the finder dialog
        finderDialog = new FinderDialog(textArea);
        finderDialog.initOwner(primaryStage);
        finderDialog.focusInput();

        //show the window
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    /** Adds menus to the window **/
    private void buildMenus(){
        //main menus
        menuBar = new CompilerBar();

        //an offset
        HBox offset = new HBox();
        HBox.setHgrow(menuBar, Priority.ALWAYS);
        MenuBar colLabel = new MenuBar();
        final Menu colLabelMenu = new Menu("Col:      ");
        colLabelMenu.setDisable(true);
        colLabel.getMenus().add(colLabelMenu);
        offset.getChildren().addAll(menuBar,colLabel);

        //update column when relevant, requires ugly delay to wait for listener threads
        textArea.caretPositionProperty().addListener((observableValue, number, number2) -> {
            Timeline updateColsLine = new Timeline(new KeyFrame(Duration.millis(5),
                    event -> colLabelMenu.setText("Col:    " + textArea.caret.get())));
            updateColsLine.setCycleCount(1);
            updateColsLine.play();
        });

        //Add menus to bar
        root.setTop(offset);
    }

    /** Returns the menu bar **/
    public MenuBar getMenu(){ return menuBar; }

    /** Adds a text editor to the GUI **/
    private void buildTextArea(){
        textArea = new SmartRichTextArea();
        BorderPane centerFrame = new BorderPane();
        TabPane mainFrame = new TabPane();
        Tab tab = new Tab("        ");
        mainFrame.getTabs().add(tab);
        centerFrame.setCenter(mainFrame);

        buildLineNumberBar(centerFrame);

        textArea.addToTab(tab);
        root.setCenter(centerFrame);
        configureTab(tab,textArea);
    }

    /** Constructs the line number bar **/
    private void buildLineNumberBar(BorderPane centerFrame){
        lineNumbers.setStyle("-fx-background-color: \"#383838\";");
        lineNumbers.setFont(Font.font("Courier New", 11.5));
        lineNumbers.setTextFill(Color.WHITE);
        lineNumberHighlight.setStyle("-fx-background-color: rgba(0, 100, 100, 0.0);");
        lineNumberHighlight.setFont(Font.font("Courier New", 11.5));
        lineNumberHighlight.setTextFill(Color.HOTPINK);
        lineNumberHighlight.setTooltip(new Tooltip("Parser Err:"));
        lineNumberHighlight.getTooltip().setStyle("-fx-background-radius: 0 0 0 ;");
        lineNumberHighlight.getTooltip().setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");

        //show popup
        lineNumberHighlight.setOnMouseEntered(mouseEvent -> {
            if (textArea.getLineOfBadToken() == -1 || lineNumberHighlight.getTooltip().isShowing()) return;
            lineNumberHighlight.getTooltip().setText(textArea.getMessageOfBadToken());
            Point2D p = lineNumberHighlight.localToScene(0.0,0.0);
            lineNumberHighlight.getTooltip().show(lineNumberHighlight,
                    p.getX() + lineNumberHighlight.getScene().getX() +
                            lineNumberHighlight.getScene().getWindow().getX(),
                    lineNumberHighlight.getScene().getWindow().getY() +
                            lineNumberHighlight.getLayoutY() + lineNumberHighlight.getHeight());
        });
        //hide popup
        lineNumberHighlight.getTooltip().setHideOnEscape(true);

        StackPane lineNumberPane = new StackPane();
        lineNumberPane.setAlignment(Pos.TOP_CENTER);
        lineNumberPane.getChildren().addAll(lineNumbers, lineNumberHighlight);
        centerFrame.setLeft(lineNumberPane);

        lineNumbers.setTranslateY(-10);
        lineNumberHighlight.setTranslateY(-10);
    }

    /** Builds the directory panel on the left side of the window from a
     * given starting directory. Should not be called twice conseccutively
     * without unexpected behavior.
      * @param defaultDirectory The starting directory of the panel.
     */
    public void buildDirectoryPanel(String defaultDirectory){
        final StackPane directoryArea = new StackPane();
        directoryArea.setPrefWidth(300);
        root.setLeft(directoryArea);
        DirectoryPanel directory = new DirectoryPanel(defaultDirectory);
        directoryArea.getChildren().add(directory);


        // Enable minimization by double-click
        directoryArea.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2)
                if (directoryArea.getOpacity() != 0.01){
                    directoryArea.setPrefWidth(20);
                    directoryArea.setOpacity(0.01);
                }else{
                    directoryArea.setPrefWidth(300);
                    directoryArea.setOpacity(1);
                }
        });
    }

    /** Builds the bottom panel **/
    private void buildBottomPanel(){
        root.setBottom(new OutputGUI());

        // Enable minimization by double-click
        root.getBottom().setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2)
                if (root.getBottom().getOpacity() != 0.01){
                    ((OutputGUI)root.getBottom()).setPrefHeight(10);
                    root.getBottom().setOpacity(0.01);
                }else{
                    ((OutputGUI)root.getBottom()).setPrefHeight(100);
                    root.getBottom().setOpacity(1);
                }
        });
    }

    /** Links the output with a string property
     * @param stringProperty The property to link to
     * @param severityProperty The severity of the text
     */
    public void linkOutput(SimpleStringProperty stringProperty, SimpleStringProperty severityProperty){
        ((OutputGUI) root.getBottom()).linkText( stringProperty, severityProperty);
    }

    /** Clears the output screen **/
    public void clearOutput(){((OutputGUI)root.getBottom()).clearOutput();}

    /**Configures the popup menu**/
    private void configurePopupMenu(){
        popup.setStyle("-fx-background-color: grey;");
        popup.setOpacity(.85);
        //function map for popup items
        functionMapProxy = new FunctionMapProxy<String>(){{
            put("Close",new Executable(){
                @Override public Object run(Object... args){
                    if (((TabPane)((BorderPane) root.getCenter()).getCenter()
                    ).getTabs().size() == 1) return null;
                    Tab curTab = getCurTab();
                    clear();
                    if (isEmpty())
                        ((TabPane)((BorderPane) root.getCenter()).getCenter()
                        ).getTabs().remove(curTab);
                    return null;
                }
            });
            put("New",new Executable(){
                @Override public Object run(Object... args){ addTab(); return null; }
            });
            put("Find",new Executable(){
                public Object run(Object... args){ finderDialog.show(); return null; }
            });
        }};

    }

    /** Configures the drag action of the tabs
     * @param dragAction The new action for tabs
     */
    public void configDrag(EventHandler<MouseEvent> dragAction){
        final TabPane tabPane = ((TabPane)((BorderPane) root.getCenter()).getCenter());
        tabPane.setOnMouseDragged(dragAction);
    }

    /** Returns the mouse drag quality for tabs.
     * @return The mouse drag quality for tabs.
     */
    public double getMouseDrag(){ return mouseDrag; }

    /** Returns the mouse drag fired quality for tabs.
     * @return The mouse drag fired quality for tabs.
     */
    public boolean getMouseFired(){ return mouseDragFired; }

    /** Sets the mouse drag fired quality for tabs.
     * @param flag The new mouse drag fired quality for tabs.
     */
    public void setMouseFired(boolean flag){ mouseDragFired = flag; }


    /** Sets the title of the current tab to the given string
     * @param title The new title of the tab
     */
    public void setTabTitle(String title){ getCurTab().setText(title); }

    /** Returns the title of the current tab
     * @return The title of the current tab
     */
    public String getTabID(){ return getCurTab().getId(); }

    /** Returns the text in the main area of the window
     * @return The text from the main TextArea of the window
     */
    public String getText(){ return textArea.getText(); }

    /** Attempts to set the window's text to the current text, enforces no time
     * restraint or guarantee of execution or formatting.
     * @param text The new text for the window
     */
    public void updateText(String text){
        textArea.setText(text);
        textArea.updateFormatting();
    }

    /** Forces the text of the window to be updated (NOT RECOMMENDED FOR
     * CASUAL USE). */
    public void forceUpdateText(){
        if (parse && textArea.isFocused()) textArea.forceUpdate();
    }

    /** Sets whether the current text is saved
     * @param saved whether the current text is saved
     */
    public void setSaved(boolean saved){((SmartRichTextArea)textArea).setSaved(saved);}

    /** Clears the window; prompts if window is unsaved. Returns success. **/
    public void clear(){
        if (textArea.getText().equals("")) return;
        //prompt if unsaved
        if (!((SmartRichTextArea)textArea).isSaved()){

            //FIXME: use UtilWindow abstract
            final Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            String message = "Warning: this action might result in loss of unsaved data.";
            Button cont = new Button("Continue");
            Button canc = new Button("Cancel");
            BorderPane pane = new BorderPane();
            pane.setLeft(cont);
            pane.setRight(canc);
            cont.setOnAction(actionEvent -> {
                textArea.clear();
                ((SmartRichTextArea) textArea).setSaved(false);
                dialogStage.close();
            });
            canc.setOnAction(actionEvent -> {
                dialogStage.close();
            });
            //FIXME: depricated code
            dialogStage.setScene(new Scene(VBoxBuilder.create().
                    children(new Text(message), pane).
                    alignment(Pos.CENTER).padding(new Insets(8)).build()));
            dialogStage.setTitle("Warning");
            dialogStage.showAndWait();
        }else{
            textArea.clear();
            ((SmartRichTextArea)textArea).setSaved(false);
        }
    }

    /** Adds a tab to the GUI **/
    public void addTab(){
        final TabPane tabPane = ((TabPane)((BorderPane) root.getCenter()).getCenter());
        //create tab
        Tab tab = new Tab("        ");
        tabPane.getTabs().add(tab);

        //add text area
        SmartRichTextArea newTextArea = new SmartRichTextArea();
        newTextArea.addToTab(tab);

        configureTab(tab, newTextArea);
    }

    /** Configures a given tab with proper conditional actions and adds to
     * it a given textArea.
     * @param tab The tab to configure
     * @param newTextArea The RichTextArea to add to it
     */
    private void configureTab(Tab tab, RichTextArea newTextArea){
        final TabPane tabPane =
                ((TabPane)((BorderPane) root.getCenter()).getCenter());

        tabPane.getSelectionModel().select(tab);
        newTextArea.requestFocus();

        //switch to proper textarea when tab is selected
        tab.setOnSelectionChanged(event -> {
            if ( tab.isSelected() ){
                textArea = newTextArea;
                newTextArea.requestFocus();
                updateLineNumberLabel();
            }
        });
        textArea = newTextArea;

        //watch for line number updates
        textArea.getNumLinesProperty().addListener((observableValue, oldValue, newValue) ->
                updateLineNumberLabel());
        textArea.scrollTopProperty().addListener((observableValue, number, number2) -> {
            updateLineNumberLabel();
        });
        updateLineNumberLabel();
        tab.setId(String.valueOf(Math.random()));
    }

    /** Subroutine for updating line numbers **/
    private void updateLineNumberLabel(){
        StringBuilder s = new StringBuilder("\n\n\n\n");
        StringBuilder t = new StringBuilder("\n\n\n\n");
        int i  = (int) ((textArea.getScrollTop() / 15) );

        for (; i < textArea.getNumLinesProperty().get(); i++ ){
            if (i+1 != textArea.getLineOfBadToken()) {
                s.append(i+1);
            }
            else {
                s.append(" ");
                t.append(i+1);
            }
            if (i+1 <= textArea.getLineOfBadToken())
                t.append("\n");
            s.append("\n");
        }

        if (textArea.getBaselineOffset() == -1) lineNumberHighlight.getTooltip().hide();

        lineNumbers.setText(s.toString());
        lineNumberHighlight.setText(t.toString());

    }

    /** Enables accelerators for menu items **/
    private void setAccelerators(Stage stage){
        final String[] actionIDs = {"Close","New","Find"};
        KeyCode[] hotkeys = {KeyCode.W,KeyCode.M,KeyCode.F};
        for ( int i = 0; i < hotkeys.length ; i++ ){
            final int j = i;
            stage.getScene().getAccelerators().put(
                    new KeyCodeCombination(hotkeys[i], KeyCombination.CONTROL_DOWN ),
                    () -> processMenuItemOnPopup(actionIDs[j])
            );
        }
    }


    /** Sets close conditions for the given stage **/
    private void setCloseConditions(){
        // --- Each Tab
        ((TabPane)((BorderPane) root.getCenter()).getCenter()).setTabClosingPolicy(
                TabPane.TabClosingPolicy.UNAVAILABLE);
        ((BorderPane) root.getCenter()).getCenter().setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                MenuItem[] optionItems = {new MenuItem("Close"), new MenuItem("New")};
                for (MenuItem menuItem : optionItems) {
                    menuItem.setOnAction(actionEvent ->
                            processMenuItemOnPopup(menuItem.getText()));
                }

                popup.getItems().clear();
                popup.getItems().addAll(optionItems);
                popup.show(((BorderPane) root.getCenter()).getCenter(),
                        Side.TOP, 0, 80);
            } else {
                popup.hide();
                mouseDrag = mouseEvent.getX();
                mouseDragFired = true;
            }
        });
    }

    /** Subtroutine for popup menu items of a given id **/
    private void processMenuItemOnPopup(String id){
        if (functionMapProxy.containsKey(id))
            functionMapProxy.run(id);
        else throw new RuntimeException("Error: Proxy ID not found");
    }

    /** Returns true iff the text in the window is empty **/
    public boolean isEmpty(){return textArea.isEmpty();}

    /** Returns the number of tabs in the window
     *  @return the number of tabs in the window
     */
    public int getNumTabs(){
        return ((TabPane)((BorderPane) root.getCenter()).getCenter()).getTabs().size();
    }

    /** Closes the current tab, does not prompt for saving **/
    public void closeTab(){
        Tab curTab = getCurTab();
        ((TabPane)((BorderPane) root.getCenter()).getCenter()
            ).getTabs().remove(curTab);
    }

    /** Returns the current tab (internal use only) **/
    private Tab getCurTab(){
        return  ((TabPane)((BorderPane) root.getCenter()
        ).getCenter()).getSelectionModel().getSelectedItem();
    }

    /** Sets the loading module of the directory panel. For more details, see
     * DirectoryPanel.setLoadModule.
     * @param loadable A class with loading capacities as specified by the Loadable
     *                 interface
     */
    public void setDirectoryLoadModule(DirectoryPanel.Loadable loadable){
        ((DirectoryPanel)((StackPane) root.getLeft()).getChildren().get(0)
                    ).setLoadModule(loadable);
    }

    /** Returns the directory panel's current directory
     * @return The directory panel's current directory
     */
    public String getCurrentDirectory(){
        return ((DirectoryPanel)((StackPane) root.getLeft()).getChildren().get(0)
                ).getCurrentDirectory();
    }

    /** Sets the grammar of the current context
     * @param grammar the new grammar
     */
    public void setGrammar(ContextFreeGrammar grammar){
        textArea.grammar = grammar;
    }

   public final SimpleStringProperty foregroundColor = new SimpleStringProperty();
   public final SimpleStringProperty backgroundColor = new SimpleStringProperty();

    /** Configures color liknkage **/
    private void configureColorLinkage(){
        foregroundColor.addListener((observableValue, s, s2) -> {
            textArea.setForegroundColor(foregroundColor.getValue());
        });
        backgroundColor.addListener((observableValue, s, s2) -> {
            textArea.setBackgroundColor(backgroundColor.getValue());
        });
    }

    /** Sets whether parsing should be enabled for the window
     * @param flag true if parsing is enabled (true by default)
     */
    public void setParse(boolean flag){
        parse = flag;
    }

}
