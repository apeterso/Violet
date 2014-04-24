package Compiler.Visitor.Bantam;

import Compiler.Nodes.ASTNode;
import Compiler.AbstractSyntaxTree.RawSyntaxTree;
import Compiler.AbstractSyntaxTree.Util.ScopeTable;
import Compiler.Parser.CFG.CFGToken;
import Compiler.Parser.ParserTree.ParserTreeNode;
import Compiler.Visitor.Bantam.BantamVisitor;
import GUI.Widget.RichTextArea;

import java.util.ArrayList;

/**
 * This visitor gathers a list of Fields in a Program.
 * Created by Matt Levine on 3/19/14.
 */
public class FieldIdentifierBantamVisitor extends BantamVisitor {

    private final ScopeTable scopes = new ScopeTable();
    private ASTNode curClass;
    private ArrayList<ParserTreeNode> fieldNodes = new ArrayList<>();

    /** Returns a list of Field Nodes from an AST
     * @param tree the AST
     * @return a list of Field Nodes
     */
    @SuppressWarnings("unchecked")
    public ArrayList<ParserTreeNode> getFields(RawSyntaxTree tree){
        tree.getRoot().accept(this);
        return fieldNodes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<String> visitProgram(ASTNode node){
        ArrayList<String> fields = new ArrayList<>();
        for (ASTNode child : node.getChildren()){
            fields.addAll((ArrayList<String>) child.accept(this));
        }
        return fields;
    }

    @Override
    public ArrayList<String> visitClass_(ASTNode node){
        node.setProperty("fields",new ArrayList<ASTNode>());
        node.setProperty("name",node.getChildren().get(0));
        curClass = node;
        scopes.incept();

        ArrayList<String> fields = new ArrayList<>();
        for (ASTNode child: node.getChildren()){
            fields.add((String) child.accept(this));
        }

        scopes.wakeUp();
        return fields;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String visitField(ASTNode node){
        String name = node.getChildren().get(1).treeNode.getValue();
        node.setProperty("name",name);
        node.setProperty("value",node.getChildren().get(0).treeNode.getValue());

        if (scopes.indexOf(name) == 1 ){
            int lineNum = node.getChildren().get(
                    1).treeNode.value.getStart_chartRow().getCFGToken().getLineNum();
            addOutcome(lineNum,"Duplicate field declaration at line "+lineNum);

        }
        scopes.add((String) node.getProperty("name"),node);
        ((ArrayList<ASTNode>)curClass.getProperty("fields")).add(node.getChildren().get(1));
        fieldNodes.add(node.getChildren().get(1).treeNode);
        return node.getChildren().get(1).treeNode.getValue();
    }

    @Override
    public String visitTerminal(final ASTNode node){
        return "";
    }

    @Override
    public String visitMethod(ASTNode node){
        scopes.incept();
        for( ASTNode child : node.getChildren() ){
            child.accept(this);
        }
        scopes.wakeUp();
        return "";
    }

    @Override
    public String visitFormal(ASTNode node){
        scopes.add(node.getChildren().get(1).treeNode.getValue(),node);
        node.setProperty("name", node.getChildren().get(1).treeNode.getValue());
        node.setProperty("type",node.getChildren().get(0).treeNode.getValue());
        return "";
    }

    @Override
    public String visitDeclStmt(ASTNode node){
        String name = node.getChildren().get(1).treeNode.getValue();
        node.setProperty("name",name);
        node.setProperty("type",node.getChildren().get(0).treeNode.getValue());
        node.setProperty("expr",node.getChildren().get(2).treeNode.getValue());
        if (scopes.indexOf(name) > 1){
            int lineNum = node.getChildren().get(
                    1).treeNode.value.getStart_chartRow().getCFGToken().getLineNum();
            addOutcome(lineNum,"Local variable \"" + name +
                    "\" already declared at line "+lineNum+". \n Was declared at line "+
                    ((ASTNode)scopes.get(name)).getChildren().get(1
                        ).treeNode.value.getStart_chartRow().getCFGToken().getLineNum());
        }
        scopes.add(name,node);

        return null;
    }

    @Override
    public String visitIfStmt(ASTNode node){
        scopes.incept();
        for (ASTNode child : node.getChildren()){
            child.accept(this);
        }
        scopes.wakeUp();

        return null;
    }

    @Override
    public String visitElseStmt(ASTNode node){
        scopes.wakeUp();
        for (ASTNode child : node.getChildren())
            child.accept(this);
        scopes.incept();

        return null;
    }

    @Override
    public String visitWhileStmt(ASTNode node){
        scopes.incept();
        for (ASTNode child : node.getChildren()) child.accept(this);
        scopes.wakeUp();

        return null;
    }

    @Override
    public String visitForStmt(ASTNode node){
        scopes.incept();
        for (ASTNode child : node.getChildren()) child.accept(this);
        scopes.wakeUp();

        return null;
    }

    @Override
    public String visitInstanceOfExpr(ASTNode node){
        for (ASTNode child : node.getChildren()) child.accept(this);
        return null;
    }

    @Override
    public String visitCastExpr(ASTNode node){
        for (ASTNode child : node.getChildren()) child.accept(this);
        return null;
    }

    @Override
    public String visitDispatchExpr(ASTNode node){
        for (ASTNode child : node.getChildren()) child.accept(this);
        return null;
    }

    @Override
    public String visitAssignExpr(ASTNode node){
        for (ASTNode child : node.getChildren()) child.accept(this);
        return null;
    }

    @Override
    public String visitReturnStmt(ASTNode node){
        for (ASTNode child : node.getChildren()) child.accept(this);
        return null;
    }

    @Override
    public String visitVarExpr(ASTNode node){
        String name = node.treeNode.value.getEnd_chartRow().getCFGToken().getValue();
        CFGToken refTok =
                node.getChildren().get(0).treeNode.value.getEnd_chartRow().getCFGToken();
        if ( refTok != null && refTok.getValue().equals("this") ) {
            if (scopes.isInScope(name, 0))
                fieldNodes.add(node.treeNode);
        }

        if (scopes.indexOf(name) == 1){
            fieldNodes.add(node.treeNode);
        }

        return null;
    }

}
