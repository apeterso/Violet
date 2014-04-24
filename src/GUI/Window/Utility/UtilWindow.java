package GUI.Window.Utility;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This class is the parent of all Utility Windows that
 * launch independently of the main stage
 * Created by Matt Levine on 4/3/14.
 */
public abstract class UtilWindow extends Stage {

    protected final BorderPane root;
    protected final int utilWidth;
    protected final int utilHeight;
    private final String utilTitle;

    protected UtilWindow(String title, int width, int height){
        root = new BorderPane();
        utilHeight = height;
        utilWidth = width;
        utilTitle = title;

        configure(title,width,height);
        addWidgets();
        setCloseConditions();
    }

    /** Configures the Stage **/
    protected final void configure(String title, int width, int height){
        setWidth(width);
        setHeight(height);
        setTitle(title);
        setResizable(false);
        initStyle(StageStyle.UTILITY);
    }

    /** Adds widgets to the UtilWindow - should be overwritten
     * with a call to super to add functionality
    **/
    protected void addWidgets(){
        Group group = new Group();
        Scene scene = new Scene(group, utilWidth, utilHeight, Color.AZURE );
        setScene(scene);

        root.setTop(getTitleLabel(utilTitle,20));

        group.getChildren().add(root);
    }

    /** Sets the close conditions **/
    protected abstract void setCloseConditions();

    /** Generates a label for the given string of the given size
     * @param title the string
     * @param size the size
     * @return the label
     */
    protected Label getTitleLabel(String title, int size){
        Label titularLabel = new Label(title+"\n\n");
        titularLabel.setFont(Font.font("Courier New", size));
        titularLabel.setStyle("-fx-font-weight: bold;\n"+
        "-fx-text-fill: #333333;\n"+
        "-fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 );");

        return titularLabel;
    }

    /** Generates a label for the given string of the given size
     * @param title the string
     * @param size the size
     * @return the label
     */
    protected Text getColorfulLabel(String title, int size){
        Text fancyText = new Text(title+"\n\n");
        fancyText.setStyle("-fx-font: "+size+"px Tahoma;\n" +
                "    -fx-fill: linear-gradient" +
                "(from 0% 0% to 100% 200%, repeat, aqua 0%, red 50%);\n" +
                "    -fx-stroke: black;\n" +
                "    -fx-stroke-width: 1;");

        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetY(3.0f);
        dropShadow.setColor(Color.color(0.4f, 0.4f, 0.4f));
        fancyText.setEffect(dropShadow);

        return fancyText;
    }

    /** Formats a button in a cool unnecessary way **/
    protected void formatButtonCool(Button... buttons){
        for (Button button : buttons){
            button.setStyle("-fx-background-color: \n" +
                    "        #000000,\n" +
                    "        linear-gradient(#7ebcea, #2f4b8f),\n" +
                    "        linear-gradient(#426ab7, #263e75),\n" +
                    "        linear-gradient(#395cab, #223768);");
            button.setTextFill(Color.WHEAT);
        }
    }

}
