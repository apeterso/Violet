package Neuralizer;

import GUI.DesktopController;
import GUI.Util.GuiLinkageManager;
import GUI.Window.Utility.UtilWindow;
import Neuralizer.IO.NeuralIO;
import Neuralizer.Util.NeuralErrorWin;
import javafx.application.Application;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Trains the Neuralizer.
 * Created by Matt Levine on 4/20/14.
 */
public class Main extends Application{


    public static void main(String[] args){
        System.out.println("Neuralizing...");
        launch(args);
        System.out.println("Program Terminated Safely.");
    }

    @Override
    /** Starts the GUI element **/
    public void start(Stage stage){

        NeuralIO io = new NeuralIO();

    }


}
