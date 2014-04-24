package Neuralizer.Util;

import GUI.Window.Utility.UtilWindow;
import IO.PreferenceManager;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.util.function.Function;
import java.util.prefs.Preferences;

/**
 * Created by Matt Levine on 4/21/14.
 */
public class NeuralErrorWin extends UtilWindow {

    private final SimpleBooleanProperty cflag;
    private final SimpleBooleanProperty sflag;

    public NeuralErrorWin(String s, int w, int h, SimpleBooleanProperty continue_flag,
                          SimpleBooleanProperty stop_flag){
        super(s,w,h);
        cflag = continue_flag;
        sflag = stop_flag;
    }

    private ScrollPane scrollPane;
    private BorderPane lastBorderPane;


    @Override protected void setCloseConditions() {
        if (isShowing()) {
           System.exit(0);
        }
    }

    @Override protected void addWidgets(){
        super.addWidgets();
        scrollPane = new ScrollPane();
        lastBorderPane = new BorderPane();
        scrollPane.setPrefWidth(390);
        scrollPane.setPrefHeight(500);
        BorderPane base = new BorderPane();
        root.setCenter(base);
        base.setCenter(scrollPane);
        scrollPane.setContent(lastBorderPane);
        lastBorderPane.setTop(new Text("Window Initialized"));

        BorderPane buttonPane = new BorderPane();
        Button continue_button = new Button("Continue");
        continue_button.setOnMousePressed(mouseEvent -> {
            if (cflag.get()){
                cflag.set(false);
                continue_button.setText("Continue");
            }else{
                cflag.set(true);
                cflag.set(false);
            }
        });
        buttonPane.setLeft(continue_button);
        Button stop_button = new Button("Finalize");
        stop_button.setOnMousePressed(mouseEvent -> { sflag.set(true); });
        formatButtonCool(continue_button,stop_button);
        buttonPane.setCenter(stop_button);

        base.setTop(buttonPane);
    }

    public Object mutate(Object text){
        if (lastBorderPane == null) return null;
        lastBorderPane.setCenter(new Text(text + "\n"));
        BorderPane borderPane = new BorderPane();
        lastBorderPane.setBottom(borderPane);
        lastBorderPane = borderPane;

        scrollPane.setVvalue(scrollPane.getVmax());
        show();
        return null;
    }


}
