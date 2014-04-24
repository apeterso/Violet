package GUI.Widget;

import Compiler.AbstractSyntaxTree.RawSyntaxTree;
import Compiler.Nodes.ASTNodeTypeBantam;
import Compiler.Nodes.ASTNodeTypeJava7;
import Compiler.Parser.Builder.ASTBuilder;
import Compiler.Parser.CFG.CFGToken;
import Compiler.Parser.CFG.ContextFreeGrammar;
import Compiler.Parser.LanguageSource.BantamGrammarSource;
import Compiler.Parser.LanguageSource.JavaGrammar;
import Compiler.Parser.ParserTree.ParserTreeNode;
import Compiler.Scanner.LexerToken;
import Compiler.Visitor.Bantam.FieldIdentifierBantamVisitor;
import Compiler.Visitor.Java7.FieldIdentifierJava7Visitor;
import Compiler.Visitor.Visitor;
import GUI.Util.SearchManager;
import GUI.Util.SearchToken;

import java.util.*;

/**
 * Author: Matt
 * Date: 2/23/14
 * This class is a Rich Text Area that adds specialized features like
 * keyword recognition.
 */
public class SmartRichTextArea extends RichTextArea {
    //store the keywords somewhere


    private boolean isSaved,foundField,foundSearch;
    private LexerToken selectionYield = null;

    private ArrayList<ParserTreeNode> fieldNodes = new ArrayList<>();

    /** Constructs a new SmartRichTextArea **/
    public SmartRichTextArea(){
        super();
        isSaved = foundField = foundSearch = true;
    }

    /** Returns true if teh token is a field
     * @param token the token to verify
     * @return true if the token is matched
     */
    private boolean isField(LexerToken token){
        for (ParserTreeNode treeNode : fieldNodes){
            CFGToken tok = treeNode.value.getEnd_chartRow().getCFGToken();
            if (token.getValue().equals(tok.getValue()) && token.getColNum() == tok.getColNum() &&
                    tok.getLineNum() == token.getLineNum())
                        return true;
        }
        return false;
    }



    /** Returns the associated start tag for a keyword, else empty string. **/
    @Override
    public String getStartModifier(LexerToken token ){
        //highlight java keywords
        if (SearchManager.isSearchedFor(token,searchToken)){
            foundSearch = false;
            selectionYield = SearchManager.selectionYield;
            return " <font style=\"background-color: #707070;\"> ";
        }else if (isField(token)){
            foundField = true;
            return  " <b><font color=\"#9900CC\"> ";
        }
        return token.style;
    }

    /** Returns the associated end tag for a keyword, else empty string. **/
    @Override
    public String getEndModifier(LexerToken token ){

        if (!foundSearch) {
            foundSearch = true;
            return "<font style=\"background-color: "+bgColor+";\">";
        }

        if (!token.style.equals("") || foundField){
            foundField = false;
            return " </font></b></i></a></u></s> ";
        }
        return "";

    }

    /** Sets whether the area is saved or unsaved
     * @param isSaved the flag indicating whether the area is saved
    **/
    public void setSaved(boolean isSaved){
        this.isSaved = isSaved;
    }

    /** Returns true iff the area has been saved
     * @return true iff the area has been saved
     */
    public boolean isSaved(){return isSaved;}

    private final static HashMap<ContextFreeGrammar,Class> classMap =
            new HashMap<ContextFreeGrammar, Class>(){{
       put(BantamGrammarSource.getBantamGrammar(), ASTNodeTypeBantam.class);
       put(JavaGrammar.getJavaGrammar(), ASTNodeTypeJava7.class);
    }};

    @Override
    void handleRawAST(ASTBuilder buidler){
        Class type = classMap.get(grammar);
        RawSyntaxTree ast = new RawSyntaxTree(buidler.getTreeHead(),type);

        //FIXME: make more elegant
        Visitor v = null;
        if (grammar.equals(BantamGrammarSource.getBantamGrammar())) {
            FieldIdentifierBantamVisitor visitor = new FieldIdentifierBantamVisitor();
            fieldNodes = visitor.getFields(ast);
            v = visitor;
        }
        if (grammar.equals(JavaGrammar.getJavaGrammar())) {
            FieldIdentifierJava7Visitor visitor = new FieldIdentifierJava7Visitor();
            fieldNodes = visitor.getFields(ast);
            v = visitor;
        }
        if (v != null && v.getOutcomes().size() > 0){
            catalog.addAll(v.getOutcomes());
        }

    }

    /**Updates the formatting (see: RichTextArea) and marks unsaved**/
    @Override
    public void updateFormatting(){
//        fields.clear();
        super.updateFormatting();
        isSaved = false;
    }

    @Override
    /** Highlights all matching phrases
     * @param token the token to search for
     */
    public void find(SearchToken token){
        super.find(token);
/*
        if (token != null && token.type.equals(SearchToken.SearchTokenType.SINGLE))
            if (selectionYield != null){
                double dY = selectionYield.getLineNum()*12 > getBoundsInParent().getMaxY() ?
                        getBoundsInParent().getMaxY() : selectionYield.getLineNum()*12;
                setScrollTop(dY);
            }
*/
    }
}
