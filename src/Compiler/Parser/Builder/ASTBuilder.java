package Compiler.Parser.Builder;

import Compiler.Nodes.ASTNode;
import Compiler.Parser.LanguageSource.JavaGrammar;
import Compiler.Parser.ParserTree.*;
import Compiler.Parser.CFG.*;
import Compiler.Parser.Matcher.*;
import Compiler.Scanner.LexerToken;

import java.util.ArrayList;

/**
 * This builder creates an ParserTree from a state that has been created by a Matcher.
 * A Builder cannot be used except by passing it into a Matcher.
 * Created by Matt Levine on 3/13/14.
 */
public class ASTBuilder implements Builder {

    private ArrayList<ParserTreeNode> tree = null;

    /** Builds the tree from an input state
     * @param inputState a state generated by a Matcher
     * @return an arraylist of head nodes; set of possible derivation trees
     */
    public ArrayList<ParserTreeNode> build(State inputState){
        if (inputState == null) return null;
        tree = buildTree(new ArrayList<>(), inputState,
                inputState.getRules().size() - 1, inputState.getEnd_chartRow());
        return tree;
    }

    /** Subroutine for building ParserTree
     * @param children the children of the input node
     * @param state the input state or current head
     * @param rule_index the current position of the rule
     * @param end_chartRow the end column containing the rule
     * @return a tree of possible derivations from the input
     */
    private ArrayList<ParserTreeNode> buildTree(final ArrayList<ParserTreeNode> children,
                             final State state, int rule_index, ChartRow end_chartRow){

        ChartRow start_chartRow = null; //might be wasted if rule_index < 0
        if (rule_index < 0){
            //a little complex (and some extra packaging going on) - just creating a node
            //with children param as its input
            return new ArrayList<ParserTreeNode>(){{add(new ParserTreeNode(
                    state, children.toArray(new ParserTreeNode[children.size()])));}};
        }else if (rule_index == 0){
            start_chartRow = state.getStart_chartRow();
        }

        Rule rule = state.getRules().get(rule_index);
        ArrayList<ParserTreeNode> outputs = new ArrayList<>();
        for (State st : end_chartRow) {
            //only require one valid path (also avoids infinite recursion)
            if (outputs.size() > 0) break;
            if (st == state) break;
            if ((st.completed()) && st.name.equals(rule.getName())) {
                if (start_chartRow == null || st.getStart_chartRow().equals(start_chartRow)) {
                    for (final ParserTreeNode subtree : build(st)) {
                        for (ParserTreeNode ParserTreeNode : buildTree(
                                new ArrayList<ParserTreeNode>() {{
                                    add(subtree);
                                    addAll(children);
                                }},
                                state, rule_index - 1, st.getStart_chartRow())) {
                            outputs.add(ParserTreeNode);
                        }
                    }
                }
            }
        }
        return outputs;
    }

    /** Returns the first possible derivation head, or null if none exist
     * @return derivation head
     */
    public ParserTreeNode getTreeHead() {
        if (tree != null && tree.size()>0 && tree.get(0)!=null)
            return tree.get(0).getChildren().next();
        return null;
    }

    /** Prints to output the first derivation tree **/
    public void printTree(){
        if (tree != null)
        for (ParserTreeNode node : tree){
            node.print(0);
        }
    }

    /** Tests prioritization of ast-builder **/
    public static void main(String[] args){
        System.out.println("Testing...");

        String g = "A->B|A->C|C->D|D->INT_CONST|B->INT_CONST|A->F|F->INT_CONST";
        AdvancedCFG grammar = JavaGrammar.getJavaGrammar();
        ContextFreeGrammar grammarSimple = new ContextFreeGrammar(g);
        LexerToken[] simpleTokens = {new LexerToken(LexerToken.TokenIds.INT_CONST,"2")};
        LexerToken[] lexerTokens = {new LexerToken(LexerToken.TokenIds.CLASS, "3"),
                new LexerToken(LexerToken.TokenIds.ID, "myClass"),
                new LexerToken(LexerToken.TokenIds.LBRACE, "{"),
                //method decl
                new LexerToken(LexerToken.TokenIds.ID, "Object"),
                new LexerToken(LexerToken.TokenIds.ID, "foo"),
                new LexerToken(LexerToken.TokenIds.LPAREN, "("),
                new LexerToken(LexerToken.TokenIds.RPAREN, ")"),
                new LexerToken(LexerToken.TokenIds.LBRACE, "{"),
                //Diamond notation
                new LexerToken(LexerToken.TokenIds.ID, "List"),
                new LexerToken(LexerToken.TokenIds.LT, "<"),
                new LexerToken(LexerToken.TokenIds.ID, "Object"),
                new LexerToken(LexerToken.TokenIds.GT, ">"),
                new LexerToken(LexerToken.TokenIds.ID, "myList"),
                new LexerToken(LexerToken.TokenIds.ASSIGN, "="),
                new LexerToken(LexerToken.TokenIds.NEW, "new"),
                new LexerToken(LexerToken.TokenIds.ID, "Object"),
                new LexerToken(LexerToken.TokenIds.LPAREN, "("),
                new LexerToken(LexerToken.TokenIds.RPAREN, ")"),
                new LexerToken(LexerToken.TokenIds.SEMI, ";"),
                //end diamond
                new LexerToken(LexerToken.TokenIds.RBRACE, "}"),
                new LexerToken(LexerToken.TokenIds.RBRACE, "}")
        };
        ASTBuilder b = new ASTBuilder();
//        grammar.lowerPriority("BlockStatement","Statement",1);
//        grammar.lowerPriority("InfixOp",100);
        Matcher m = grammar.matches(lexerTokens, b);
        System.out.println("Matches "+m.matches());
        b.printTree();


        System.out.println("Done.");
    }

}
