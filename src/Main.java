import GUI.DesktopController;
import GUI.Util.GuiLinkageManager;
import GUI.Widget.RichTextArea;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * Author: Matt
 * Date: 2/20/14
 * This class is designed to execute the Complier with its GUI
 */
public class Main extends Application{

    public static void main(String[] args) throws FileNotFoundException {
//        PrintStream errorReDirection = new PrintStream("C:\\Users\\Matt Levine\\Desktop\\log.txt");
//        System.setOut(errorReDirection);

        System.out.println("Running...");
        launch(args);
        System.out.println("Program Terminated Safely.");
    }

    @Override
    /** Starts the GUI element **/
    public void start(Stage stage){

        DesktopController desktopController =
                new DesktopController(stage);
        new GuiLinkageManager(
                desktopController, stage);
    }

}
