package GUI.Widget;

import Compiler.AbstractSyntaxTree.RawSyntaxTree;
import Compiler.Nodes.ASTNodeTypeJava7;
import Compiler.Parser.Builder.ASTBuilder;
import Compiler.Parser.CFG.ContextFreeGrammar;
import Compiler.Parser.LanguageSource.JavaGrammar;
import Compiler.Parser.Matcher.Matcher;
import Compiler.Scanner.LexerToken;
import Compiler.Scanner.Scanner;
import Compiler.Visitor.VisitorToken;
import GUI.Util.SearchToken;
import com.sun.javafx.scene.web.skin.HTMLEditorSkin;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.web.HTMLEditor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author: Matt
 * Date: 2/22/14
 * This class is designed to handle user text input. It allows for rich text
 * visualization with simple text input and transmission.
 */
public class RichTextArea extends TextArea {

    protected String fgColor = "white";
    protected String bgColor  = "#101C2A";


    private final HTMLEditor outputStream;
    //flags used for linking caret positions
    private boolean toggleCaretValidtor;
    private boolean toggleTextValidator;
    private boolean toggleSelectionValidator;
    private boolean updated = false;

    //for handling errors
    private int lineOfBadToken = -1;
    private String messageOfBadToken = "";
    private int overFlowLine = -1;//for columns ovrr 90 chars

    //List of current tokens in file
    private final ArrayList<LexerToken> tokens;

    /** The number of lines in the area **/
    private final SimpleIntegerProperty numLines;
    /** The column of the caret **/
    public final SimpleIntegerProperty caret;
    /** Word to search for **/
    SearchToken searchToken = null;

    /** The grammar to parse with **/
    public ContextFreeGrammar grammar;
    private ASTBuilder lastBuilder;

    /** Allows retrieval of messages from visitors on separate threads
     * (I don't like this) **/
    protected final ObservableList<VisitorToken> catalog =
            FXCollections.observableArrayList();
    private boolean threadNet = true;

    //making sure symbols don't get converted to html
    private final HashMap<Character,String> htmlToRawTable = new HashMap<Character,String>(){{
        put('"',"&quot"); put('<',"&lt"); put('>',"&gt");  put('&',"&amp");
        put('?',"&#63");
    }};

    public RichTextArea(){
        super();
        numLines = new SimpleIntegerProperty();
        caret = new SimpleIntegerProperty();
        toggleCaretValidtor = toggleSelectionValidator = true;
        outputStream = new HTMLEditor();
        tokens = new ArrayList<>();
        //default grammar is Java
        grammar = JavaGrammar.getJavaGrammar();

        internal_config();
        linkTextAreas();
        updateFormatting();

        testCode();
    }

    /** Designated spot for test code **/
    private void testCode(){
        setOnMouseClicked(mouseEvent -> {

            if (mouseEvent.isControlDown()){
                System.out.println(tokens);
            }if(mouseEvent.isAltDown()){
                Class type = ASTNodeTypeJava7.class;
                RawSyntaxTree ast = new RawSyntaxTree(lastBuilder.getTreeHead(),type);
                  ast.print();
            }if (mouseEvent.isShiftDown()){
                System.out.println(outputStream.getHtmlText());
            }

        });
    }

    /** Configures the area with stylistic parameters**/
    private void internal_config(){
        setOpacity(0.0);
        setWrapText(false);
        setStyle("-fx-font: 11.5px \"Courier New\"");
        hideToolBars();
    }

    /** Returns the property that holds the number of lines in the TextArea
     * @return The property holding the number of lines in the TextArea.
     */
    public IntegerProperty getNumLinesProperty(){ return numLines; }

    /** Adds the TextArea to a given StackPane
     * @param tab The tab to which the Area is added.
     **/
    public void addToTab(Tab tab){
        //does this break FX convention?
        StackPane layer = new StackPane();
        layer.getChildren().addAll(outputStream,this);
        tab.setContent(layer);
    }

    /** Links the text from the raw TextArea to the htmlTextArea. Updates
     * caret position in front window on caret change in back window
     * and text change.
   **/
    private void linkTextAreas(){
        //some relatively complex logic to avoid both firing at once
        textProperty().addListener((observableValue, oldvalue, newValue) -> {
            //link fields
            if ( toggleTextValidator && !toggleCaretValidtor){
                toggleTextValidator = toggleSelectionValidator =false;
                updateFormatting();
                toggleTextValidator = true;
                toggleCaretValidtor = !toggleCaretValidtor;
            }
        });
        // --- on new selection
        selectionProperty().addListener((observableValue, indexRange, indexRange2) -> {
            if (toggleSelectionValidator)
                updateFormatting();
            toggleSelectionValidator = true;
        });
        // --- on any key release
        addEventFilter(KeyEvent.KEY_PRESSED, car -> {
            //handle special case characters
            String keyString = car.getCode().toString();
            //delete selection on key delete, otherwise runs after key stroke
            if (keyString.equals("BACK_SPACE") || keyString.equals("DELETE")) {
                Platform.runLater(this::updateFormatting);
            }

        });
        //on Scroll Change
        scrollLeftProperty().addListener((observableValue, number, number2) -> {
            updateFormatting();
        });
        scrollTopProperty().addListener((observableValue, number, number2) -> {
            updateFormatting();
        });

    }

    /** Hides the toolbars from the output **/
    private void hideToolBars(){
        outputStream.setSkin(new HTMLSkinWithoutToolbars(outputStream));
    }

    /** Replace HTML tokens
     * @return String the converted value
     * @param input the value to be converted
     */
    private String scan(String input){
        //init vars
        StringBuilder outputStream = new StringBuilder(input.length());
        int index, column;
        index = column = 0;
        Scanner scanner = new Scanner(input,true);
        LexerToken token;

        //add header
        outputStream.append(getHtmlHeader());
        tokens.clear();
        overFlowLine = -1;

        //for one-space, {start string, open comment, and open big comment}
        boolean oneSpace = true;

        if (getCaretPosition() == 0){
            outputStream.append('|');
            caret.set(0);
        }

        //loop the stream and modify for HTML formatting
        while ( (token = scanner.getNextToken()).getIds() != LexerToken.TokenIds.EOF ){
            //grab next token, add to token list if appropriate
            String tokenString = token.getValue();
            if (token.getIds() != LexerToken.TokenIds.COMMENT && token.getIds()
                != LexerToken.TokenIds.NULL )
                    tokens.add(token);

            int iii;
            outputStream.append(getStartModifier(token));

            for ( int r = 0; r < token.getValue().length(); r++ ){
                char c = tokenString.charAt(r);
                boolean terminalChar = ( c == ' ' || c == '\n' || c == '\t' );
                //validate individual words
                if (terminalChar && oneSpace ){
                    oneSpace = false;
                }else if (!terminalChar){
                    oneSpace = true;
                }
                else if (c != '\t') outputStream.append("&#8203");

                //catch selections and misc.
                if (htmlToRawTable.containsKey(c))
                    outputStream.append(htmlToRawTable.get(c));
                else if (c == '\t'){
                    for (int i =0; i < (column+8)/8*8-column-1; i++) outputStream.append("&nbsp");
                    outputStream.append(" ");
                    column += (column+8)/8*8-column - 1;
                }
                else outputStream.append(c);

                //handle caret
                if ( index == getCaretPosition()-1 ){
                    outputStream.append('|');
                    caret.set(column);
                }

                //handle selections
                handleSelections( outputStream, index );

                column = c != '\n' ? column + 1 : 0;
//                handleColumnError(column,token.getColNum()); //probably superfluous
                index++;
            }
            outputStream.append(getEndModifier(token));
        }

        return outputStream.toString();
    }


    /** Calls the parser on the most recent scanned tokens */
    private void parse(){
        ASTBuilder builder = new ASTBuilder();

        if (threadNet) {
            threadNet = false;
            new Thread(() -> {
                Matcher m = grammar.matches(
                        tokens.toArray(new LexerToken[tokens.size()]), builder);

                if (m.matches() && overFlowLine == -1) {
                    handleRawAST(builder);
                    lastBuilder = builder; //primarily for testing
                } else if (!m.matches() && m.getBadToken() != null) {
                    catalog.add(new VisitorToken(m.getBadToken().getLineNum(),
                            "Parser Error: Cannot resolve token \"" +
                            m.getBadToken().getValue() + "\" on line " +
                            m.getBadToken().getLineNum() +  " in column "
                            + m.getBadToken().getColNum()));
                }
                threadNet = true;
            }).start();
        }else return;

        //Updates messages - FIXME add them all or more of them or something

        if (catalog.size() > 0){
            VisitorToken tok = catalog.remove(0);
            report(tok.lineNumber, tok.message);
            catalog.clear();
        }else {
            messageOfBadToken = "";
            lineOfBadToken = -1;
        }

        //hack to get line numbers to reset and show the bad line
        numLines.set(numLines.get() + 1);
        numLines.set(numLines.get() - 1);
    }

    /** Method to be overridden as need by subclasses. Only called on successful parse.
     * @param builder the Parser Builder
     */
    void handleRawAST(ASTBuilder builder){}

   /* *//** Handles errors raised by the column position
     * @param col the column of the error
     * @param row the row of the error
     *//*
    private void handleColumnError(int col, int row){
        if (col > 90){
            overFlowLine = row;
            messageOfBadToken = "Line is over 90 characters";
        }
    }*/

    /** Returns the header for the outputStream in html format **/
    private String getHtmlHeader(){
        String output = "";
        String[] xyScroll = getXYScroll();

        output += "<head><style type=\"text/css\"> " //head
                + "body{white-space:nowrap; font-size:11.5px; line-height: 125%; }"
                + "</style>"
                + "<script> function winScroll(){ window.scrollBy("
                + xyScroll[0] + "," + xyScroll[1] + "); } </script>" //adjust scroll
                + " </head>"
                + "<body bgcolor=\""+bgColor+"\" onload =\"winScroll()\">" //body
                + "<font color=\""+fgColor+"\">"
                + "<font face=\"Courier New\">"
                + "</body>";

        return output;
    }

    /**
     * Handles selection linkage (highlighting)
     * @param outputStream the stream to which to append linkage indicators
     * @param index the current index in the inputstream
     */
    private void handleSelections( StringBuilder outputStream, int index ){
        if (getSelection().getLength() == 0) return; //no selection
        //left selection
        if (getCaretPosition() <= getSelection().getStart()){
            if ( index == getCaretPosition()-1 || getCaretPosition() == 0 ){
                outputStream.append("<font style=\"background-color: #0893cf;\">");
            }
            else if ( index >= getSelection().getEnd() ){
                outputStream.append("<font style=\"background-color: #101C2A;\">");
            }
        }else{ //right selection
            if ( index == getSelection().getStart()-1){
                outputStream.append("<font style=\"background-color: #0893cf;\">");
            }
            else if ( index == getCaretPosition()-1 ||  getCaretPosition() == 0 ){
                outputStream.append("<font style=\"background-color: #101C2A;\">");
            }
        }
    }

    /** Returns a string representation of the XY sccroll amount of the text area
     * @return a two-item string array containing scroll left and scroll top respectively
     */
    public String[] getXYScroll(){
        return new String[]{String.valueOf(getScrollLeft()),
                String.valueOf(getScrollTop()) };
    }

    /** Enhances the text being visualized during the execution loop.
     * Should be overwritten by extending classes for more
     * specific purposes; does nothing in this class. The stream
     * is modified and the boolean flags updated; if the flags are
     * not updated as they are used, the method might generate
     * unexpected behavior. The first three flags are expected to be
     * the flag designating part of a string, a single line comment,
     * and a multi-line comment; the other two are for use by the
     * extender.
     * @param outputStream output stream
     * @param flags boolean triggers
     * @param c The current character being parsed
     * @param grabString The current word being constructed
     */
    protected void enhanceStream( StringBuilder outputStream, boolean[] flags,
                                  char c, StringBuilder grabString ){}

    /** Subroutine for linking areas on change **/
    private void linkAreasSubroutine(){
        //link fields
            String inputStream = getText();
                //step 1 of compilation
                inputStream = scan(inputStream).replace(
                        "\n", "<br>");
                outputStream.setHtmlText(inputStream);

        numLines.set(getText().split("\n", -1).length);
    }

    /** The beginning sequence of a word modification for a given word **/
    public String getStartModifier( LexerToken token ){ return ""; }

    /** The ending sequence of a word modification for a given word **/
    public String getEndModifier( LexerToken token ){ return ""; }

    /** Attempts to update formatting on the textArea. This method can be overwritten
     * to allow for particular actions on changes to the text area, but a call to super
     * should be made every time.
    **/
    public void updateFormatting(){
        updated = false;
        linkAreasSubroutine();
    }

    /** Forces the area to update itself. */
    public void forceUpdate(){
        if (!updated)
            parse();
        updated = true;
    }

    /** Returns true iff the text is empty **/
    public boolean isEmpty(){return getText().equals(""); }

    /** Returns the index of the last bad token
     * @return the index of the last bad token
     */
    public int getLineOfBadToken(){return lineOfBadToken;}

    /** Returns a description of the last bad token
     * @return the description of the last bad token
     */
    public String getMessageOfBadToken(){return messageOfBadToken;}

    /** Reports a token error to  the RichTextRegion; not guaranteed to do anything.
     * @param lineNum the line number of the error
     * @param message the message associated with the error
     */
    public void report(int lineNum, String message){
        lineOfBadToken = lineNum;
        messageOfBadToken = message;
        numLines.set(numLines.get()+1);
        numLines.set(numLines.get() - 1);
    }

    /** Highlights all matching phrases
     * @param token the token to search for
     */
    public void find(SearchToken token){
        searchToken = token;
        updateFormatting();
     }

    /** Sets the foreground color
     * @param style the new style
     */
    public void setForegroundColor(String style){ fgColor = style; }

    /** Sets the foreground color
     * @param style the new style
     */
    public void setBackgroundColor(String style){ bgColor = style; }

}

/** This internal class is an HTMLEditor without the toolbars
 */
class HTMLSkinWithoutToolbars extends HTMLEditorSkin{

    private final GridPane grid = (GridPane) getChildren().get(0);

    public HTMLSkinWithoutToolbars(HTMLEditor htmlEditor) throws NullPointerException{
        super(htmlEditor);
        //remove the toolbars
        ((ToolBar)grid.getChildren().get(0)).setMinHeight(0);
        ((ToolBar)grid.getChildren().get(1)).setMinHeight(0);
    }

    @Override
    protected void layoutChildren(final double x, final double y, final double w,
                                  final double h){
        //we've removed the call to build the toolbars
        layoutInArea(grid, x, y, w, h, -1, HPos.CENTER, VPos.CENTER);
    }

}