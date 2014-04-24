package GUI.Window.Utility;

import Compiler.Scanner.Rule.RuleAssembler;
import GUI.Window.GuiWindow;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.HashMap;

/**
 * Created by Matt Levine on 4/3/14.
 */

public class SettingsDialog extends UtilWindow {

    private GuiWindow win;
    private ChoiceBox colorBox;
    private TextField colorEntry;
    private HashMap<String,SimpleStringProperty> colorMap = new HashMap<>();

    private final SimpleStringProperty keyProperty = new SimpleStringProperty();
    private SimpleStringProperty numberProperty = new SimpleStringProperty();
    private SimpleStringProperty classProperty = new SimpleStringProperty();;

    public SettingsDialog(GuiWindow window){
        super("Preferences",500,500);
        win = window;
        hide();

        configMap();
    }

    @Override
    protected void addWidgets(){
        super.addWidgets();
        root.setStyle("-fx-background-color: #D6D6D6;");

        TabPane mainFrame = new TabPane();
        mainFrame.setPrefWidth(utilWidth-20);
        mainFrame.setPrefHeight(utilHeight-20);

        mainFrame.getTabs().addAll(getAsceticTab(),getInfoTab(),getPrefsTab());

        root.setCenter(mainFrame);
    }

    /** Returns and configures the info tab
     * @return the info tab
     */
    private Tab getInfoTab(){
        Tab infoTab = new Tab("Info");

        String information = "\nThis IDE is the result of an ongoing project for CS461, " +
                "a computer science course at Colby College instructed by Dale Skrien. It" +
                " is coded to follow Professor Skrien's rules of good Object Oriented design" +
                " as described in his textbook, \"Object Oriented Design Using Java\"." +
                "\n\nThe code is currently written by the author(s): Matthew Levine, last" +
                "  updated on 04/03/2014. \n\nThe underlying compiler is built for flexibility" +
                "and efficiency; the parser is chart-based following the Earley Parsing" +
                " algorithm; the context free grammars and semantic analyzers are all general," +
                " not refined to a particular grammar or language. ";
        Label text = getTitleLabel(information,11);
        text.setWrapText(true);

        infoTab.setContent(text);
        return infoTab;
    }

    /** Returns and configures the ascetics tab **/
    private Tab getAsceticTab(){
        Tab ascetic = new Tab("Ascetic");
        BorderPane main = new BorderPane();
        main.setStyle("-fx-background-color: #D6D6D6;");
        ascetic.setContent(main);
        main.setTop(getColorfulLabel("Ascetics",30));

        ObservableList<String> colorOptions = FXCollections.observableArrayList(
                            "No Option Selected","-----------------","Background Color",
                            "Foreground Color","-----------------",
                            "Keyword Color","Class Color","String Color","Number Color");
        colorBox = new ChoiceBox<String>(colorOptions);
        colorBox.getSelectionModel().selectFirst();
        colorEntry = new TextField();
        colorEntry.setMaxWidth(155);

        main.setCenter(getCenterOfAsceticTab(colorBox, colorEntry));

        main.setBottom(getTitleLabel("last update: 04/03/2014 by Matthew Levine",9));

        return ascetic;
    }

    private Tab getPrefsTab(){
        Tab prefs = new Tab("Preferences");
        BorderPane main = new BorderPane();
        main.setStyle("-fx-background-color: #D6D6D6;");
        prefs.setContent(main);

        final CheckBox parse = new CheckBox("Parse Code");
        parse.setSelected(true);
        main.setCenter(parse);
        parse.selectedProperty().addListener((
                observableValue, aBoolean, aBoolean2) -> {
            win.setParse(aBoolean2);
        });

        return prefs;
    }

    /** Returns a formatted choicebox and textfield hbox **/
    private HBox getCenterOfAsceticTab(ChoiceBox box, TextField field){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);

        Button applyButton = new Button("Apply");
        formatButtonCool(applyButton);
        applyButton.setOnMousePressed(mouseEvent -> {
            fire();
        });

        hbox.getChildren().addAll(box, field,applyButton);
        return hbox;
    }

    /** Updates the canvas with your change **/
    private void fire(){
        String option = (String) colorBox.getValue();
        if (colorMap.containsKey(option)) {
            SimpleStringProperty property = colorMap.get(option);
            property.set(colorEntry.getText());
        }
        win.updateText(win.getText());

    }

    /** Configures the color map **/
    private void configMap(){
        colorMap.put("Background Color", win.backgroundColor);
        colorMap.put("Foreground Color", win.foregroundColor);
        colorMap.put("Keyword Color", keyProperty);
        colorMap.put("String Color", RuleAssembler.getStyleOf(2));
        colorMap.put("Number Color", numberProperty);
        colorMap.put("Class Color", classProperty);

        keyProperty.addListener((observableValue, s, s2) -> {
            RuleAssembler.setStyleOfRange(-50, -1, "<b><font color=\""+
                    keyProperty.getValue() + "\">");
        });
        numberProperty.addListener((observableValue, s, s2) -> {
            RuleAssembler.setStyleOfRange(7,9, "<b><font color=\""+
                    numberProperty.getValue() + "\">");
        });
        classProperty.addListener((observableValue, s, s2) -> {
            RuleAssembler.getStyleOf(-52).set( "<b><font color=\""+
                    classProperty.getValue() + "\">");
        });

    }

    @Override
    protected void setCloseConditions(){
        if (win != null) win.updateText(win.getText());
    }


}

//string -> 17 + length string