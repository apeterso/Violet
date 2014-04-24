package GUI.Widget;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Author: Matt
 * Date: 2/22/14
 * This class is designed to show output in the main GUI.
 */
public class OutputGUI extends ScrollPane {

    final private static HashMap<String,Paint> paintMap = new HashMap<String,Paint>(){{
        put("NORMAL",Color.web("#66CCCC"));
        put("WARNING",Color.web("#FF6699"));
        put("ERROR",Color.web("#FF1919"));
        put("PLAIN",Color.web("#FFFFFF"));
    }};

    final private LinkedList<Label> outputLabels;

    /** Constructs and formats a new OutputGUI **/
    public OutputGUI(){
        super();
        setFitToWidth(true);
        setFitToWidth(true);

        //configure the first label
        outputLabels = new LinkedList<Label>(){{add(new Label(""));}};
        setTextFill(Color.web("#FF6699"));
        setFont(Font.font("Courier New", 11.5));
        setBackgroundFill("#303030");

        //configure the pane for holding the labels, add to scroll-pane
        setContent(new BorderPane());
        ((BorderPane)getContent()).setPrefHeight(100);

        getContent().setStyle("-fx-background-color: #303030;");
    }

    /** Sets the text fill of the bottom label.
     * @param color the new color for the fill
     * @return The label that was set, strictly for GUI coding convenience
     */
    public Label setTextFill(Paint color){
        outputLabels.peekLast().setTextFill(color);
        return outputLabels.peekLast();
    }

    /** Sets the text background fill of the bottom label.
     * @param hexColor the new color for the fill in hex-string format
     * @return The label that was set, strictly for GUI coding convenience
     */
    public Label setBackgroundFill(String hexColor){
        outputLabels.peekLast().setStyle("-fx-background-color: "+hexColor+";");
        return outputLabels.peekLast();
    }

    /** Sets the text font of the bottom label.
     * @param font the Font that will be set
     * @return The label that was set, strictly for GUI coding convenience
     */
    public Label setFont(Font font){
        outputLabels.peekLast().setFont(font);
        return outputLabels.peekLast();
    }

    /** Pushes the top label onto the pane. Adds a new label to the stack. **/
    public void publishLabel(){
        BorderPane pane = getPane();
        pane.setTop(outputLabels.peekLast());
        pane.setBottom(new BorderPane());
        outputLabels.add(new Label(""));
        //FIXME: want the label to fill the whole region... 2000 isn't the right way
        ((Label)pane.getTop()).setPrefWidth(2000);
    }

    /** Returns the bottommost borderpane for setting
     * @return The bottommost borderpane
     */
    private BorderPane getPane(){
        BorderPane pane = (BorderPane) getContent();
        while ( pane.getBottom() != null )
            pane = (BorderPane) pane.getBottom();
        return pane;
    }

    /** Sets the text of the top Label.
     * @param text The new text for the label
     * @return The label that was set, strictly for GUI coding convenience
     */
    public Label setText(String text){
        outputLabels.peekLast().setText(text);
        return outputLabels.peekLast();
    }

    /** Remove all labels **/
    public void clearOutput(){
        setContent(new BorderPane());
        outputLabels.clear();
        outputLabels.add(new Label());
    }


    /** Adds the information to it's window. If an unknown severity signal is passed, the text
     * might not display properly. **/
    public void linkText(SimpleStringProperty simpleStringProperty,
                         SimpleStringProperty colorProperty){
        final SimpleStringProperty finalColorProperty = colorProperty;
        simpleStringProperty.addListener((observableValue, s, s2) -> {
            //assume valid color option
            setFont(Font.font("Courier New", 11.5));
            setBackgroundFill("#303030");
            setTextFill(paintMap.get(finalColorProperty.get()));
            setText(s2);
            publishLabel();
        });
    }

}
