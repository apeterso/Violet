package Compiler.Visitor.Java7;

import Compiler.AbstractSyntaxTree.RawSyntaxTree;
import Compiler.AbstractSyntaxTree.Util.ScopeTable;
import Compiler.Nodes.ASTNode;
import Compiler.Parser.ParserTree.ParserTreeNode;
import java.util.ArrayList;

/**
 * This visitor collects a list of fields in Java 7 code. <br>It violates the OO
 * principle of not returning and acting, as it populates the internal list of treeNodes
 * while returning their string representation, but we accept that without much guilt as
 * most Visitors fall prey to this failure.
 * Created by Matt Levine on 4/1/14.
 */
public class FieldIdentifierJava7Visitor extends Java7Visitor {

    private ScopeTable scopes;
    private ArrayList<ParserTreeNode> fieldNodes = new ArrayList<>();

    /** Returns a list of Field Nodes from an AST
     * @param tree the AST
     * @return a list of Field Nodes
     */
    public ArrayList<ParserTreeNode> getFields(RawSyntaxTree tree){
        scopes = new ScopeTable();
        tree.getRoot().accept(this);
        return fieldNodes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<String> visitCompilationUnit(ASTNode node){
        ArrayList<String> fields = new ArrayList<>();
        for (ASTNode child : node.getChildren()){
            Object o = child.accept(this);
            if (o instanceof ArrayList)
                fields.addAll((ArrayList<String>) child.accept(this));
        }
        return fields;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<String> visitTypeDeclaration(ASTNode node){
        ArrayList<String> fields = new ArrayList<>();
        scopes.incept();

        for (ASTNode child : node.getChildren()){
            Object o = child.accept(this);
            if (o instanceof ArrayList)
                fields.addAll((ArrayList<String>) o);
        }

        scopes.wakeUp();
        return fields;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<String> visitClassDeclaration(ASTNode node){
        ArrayList<String> fields = new ArrayList<>();

        fields.addAll((ArrayList<String>)
                node.getChildren().get(node.getNumChildren() - 1).accept(this));
        return fields;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<String> visitNormalClassDeclaration(ASTNode node){
        ArrayList<String> fields = new ArrayList<>();
        for ( int i = 0; i < node.getNumChildren(); i++){
            Object o = node.getChildren().get(i).accept(this);
            if (o instanceof ArrayList)
                fields.addAll((ArrayList)o);
        }
        return fields;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<String> visitMemberDecl(ASTNode node){
        ArrayList<String> fields = new ArrayList<>();
        for (ASTNode child : node.getChildren()){
            Object o = child.accept(this);
            if (o instanceof ArrayList) fields.addAll((ArrayList)o);
        }
        return fields;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<String> visitEnumDeclaration(ASTNode node){
        ArrayList<String> fields = new ArrayList<>();

        for (ASTNode child : node.getChildren()){
            Object o = child.accept(this);
            if (o instanceof ArrayList)
                fields.addAll((ArrayList<String>) child.accept(this));
        }
        return fields;
    }

    /** Grab the field names here **/

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<String> visitFieldDecl(ASTNode node){
        //add to field list
        fieldNodes.add(node.getChildren().get(1).treeNode);
        //add to the class scope
        String name = node.getChildren().get(1).getChildren().get(0).treeNode.getValue();

        if (scopes.indexOf(name) == 1){
            int linenum = node.getChildren().get(1).getChildren().get(0
                        ).treeNode.value.getEnd_chartRow().getCFGToken().getLineNum();
            addOutcome(linenum,"Duplicate field declaration of " +
                    name + " at line "+linenum);
        }
        scopes.add(node.getChildren().get(1).getChildren().get(0).treeNode.getValue(), node);

        //FIXME: wasteful data structures are being used
        return new ArrayList<String>(){{add(node.getChildren().get(1).treeNode.getValue());}};
    }

    /** Go through methods and add matches here **/

    @Override
    public Object visitNewScopeMemberDecl(ASTNode node){
        scopes.incept();
        for (ASTNode child : node.getChildren())
            child.accept(this);
        scopes.wakeUp();
        return null;
    }


/*    @Override
    public Object visitReferenceType(ASTNode node){
        return null;
    }*/

    @Override
    public Object visitMethodDeclaratorRest(ASTNode node){
        for (ASTNode child : node.getChildren()){
            child.accept(this);
        }
        return null;
    }

    @Override
    public Object visitFormalParameters(ASTNode node){
        for (ASTNode child : node.getChildren()){
            child.accept(this);
        }
        return null;
    }

    @Override
    public Object visitFormalParameterDecls(ASTNode node){
        ASTNode idNode = node.getChildren().get(1);
        scopes.add(idNode.getChildren().get(0).getChildren().get(0).treeNode.getValue(),idNode);
        return null;
    }

    @Override
    public Object visitVariableDeclarator(ASTNode node){
        ASTNode treeNode = (ASTNode) node.getChildren().get(0).accept(this);
        //check for duplicate declaration
        if (scopes.indexOf(treeNode.treeNode.getValue()) > 1){
            int linenum = node.getAssociatedLineNum();
            addOutcome(linenum,"Duplicate variable declaration at line "+linenum);
        }
        scopes.add(treeNode.treeNode.getValue(), treeNode);

        for (ASTNode child : node.getChildren()) child.accept(this);

        return node;
    }

    @Override
    public Object visitExpression3(ASTNode node){
        int mark = -1;

        for (ASTNode child : node.getChildren()){
            mark--;
            //accept it
            Object o = child.accept(this);

            //check if we found a variable after a "this" token
            if (mark == 1 && o != null){
                ASTNode idNode = (ASTNode) ((ASTNode)o).accept(this);
                int level = scopes.indexOf(idNode.treeNode.getValue());
                boolean isField = scopes.isInScope(idNode.treeNode.getValue(),1);
                if (level == -1){
                    addOutcome(idNode.getAssociatedLineNum(),
                            "Field lookup failed for "+idNode.treeNode.getValue()
                            + " at line " + idNode.getAssociatedLineNum());
                }else if (isField){
                    fieldNodes.add(idNode.treeNode);
                }
            }
            //look for "this" tokens
            else if ( o != null &&  o.toString().equals("this")){
                mark = 2;
            }
        }
        return null;
    }

    @Override
    public Object visitPrimary(ASTNode node){
        //grab relevant values
        ASTNode posId = node.getChildren().get(0);
        if (posId.getChildren().size() > 0){
            int level = scopes.indexOf(
                    posId.getChildren().get(0).treeNode.getValue());

            if ( level == 1){
                fieldNodes.add(posId.treeNode);
            }else if (level == -1 && posId.getChildren().get(0).nodeType.toString().equals("ID")){

                ASTNode badNode = posId.getChildren().get(0);
                addOutcome(badNode.getAssociatedLineNum(), "Undeclared variable " +
                        badNode.treeNode.getValue() + " at lne " +
                        badNode.getAssociatedLineNum());
            }
        }

        //pass the buck
        for (ASTNode child : node.getChildren()){
            child.accept(this);
        }

        return node.getChildren().get(0).accept(this);
    }

    @Override
    public Object visitParExpression(ASTNode node){
        for (ASTNode child : node.getChildren()){
            child.accept(this);
        }
        return node.getChildren().get(0).accept(this);
    }

    @Override
    public ASTNode visitSelector(ASTNode node){
        for (ASTNode child : node.getChildren()) {
            child.accept(this);
        }

        if (node.getChildren().size() == 1)
            return node.getChildren().get(0);
        else return null;
    }

    @Override
    public ASTNode visitIdentifier(ASTNode node){
        return node.getChildren().get(0);
    }

    // -- Statements that require a scope change
    @Override public Object visitIfStatement(ASTNode node){
        return scopeStatementSubroutine(node); }
    @Override public Object visitElseStatement(ASTNode node){
        return scopeStatementSubroutine(node);}
    @Override public Object visitWhileStatement(ASTNode node){
        return scopeStatementSubroutine(node); }
    @Override public Object visitTryStatement(ASTNode node){
        return scopeStatementSubroutine(node);}
    @Override public Object visitFinallyStatement(ASTNode node){
        return scopeStatementSubroutine(node);}
    @Override public Object visitCatchStatement(ASTNode node){
        return scopeStatementSubroutine(node);}
    @Override public Object visitForStatement(ASTNode node){
        return scopeStatementSubroutine(node);}
    @Override public Object visitDoStatement(ASTNode node){
        return scopeStatementSubroutine(node);}

    private Object scopeStatementSubroutine(ASTNode node){
        scopes.incept();
        for (ASTNode child : node.getChildren()) child.accept(this);
        scopes.wakeUp();
        return null;
    }

}