package Compiler.Visitor.Java7;

import Compiler.Nodes.ASTNode;
import Compiler.Visitor.Visitor;

/**
 * Created by Matt Levine on 4/1/14.
 */
public abstract class Java7Visitor extends Visitor {

    public Object visitQualifiedIdentifierList(ASTNode node){return defaultVisit(node);}
    public Object visitClassDeclaration(ASTNode node){return defaultVisit(node);}
    public Object visitNormalClassDeclaration(ASTNode node){return defaultVisit(node);}
    public Object visitEnumDeclaration(ASTNode node){return defaultVisit(node);}
    public Object visitCompilationUnit(ASTNode node){return defaultVisit(node);}
    public Object visitImportDeclaration(ASTNode node){return defaultVisit(node);}
    public Object visitTypeDeclaration(ASTNode node){return defaultVisit(node);}
    public Object visitReferenceType(ASTNode node){return defaultVisit(node);}
    public Object visitTypeArguments(ASTNode node){return defaultVisit(node);}
    public Object visitNonWildcardTypeArguments(ASTNode node){return defaultVisit(node);}
    public Object visitTypeParameters(ASTNode node){return defaultVisit(node);}
    public Object visitBound(ASTNode node){return defaultVisit(node);}
    public Object visitModifier(ASTNode node){return defaultVisit(node);}
    public Object visitAnnotations(ASTNode node){return defaultVisit(node);}
    public Object visitMemberDecl(ASTNode node){return defaultVisit(node);}
    public Object visitNewScopeMemberDecl(ASTNode node){return defaultVisit(node);}
    public Object visitFormalParameters(ASTNode node){return defaultVisit(node);}
    public Object visitFormalParameterDecls(ASTNode node){return defaultVisit(node);}
    public Object visitVariableModifier(ASTNode node){return defaultVisit(node);}
    public Object visitVariableDeclarator(ASTNode node){return defaultVisit(node);}
    public Object visitArrayVariableDeclarator(ASTNode node){return defaultVisit(node);}
    public Object visitVariableInitializer(ASTNode node){return defaultVisit(node);}
    public Object visitStatement(ASTNode node){return defaultVisit(node);}
    public Object visitIfStatement(ASTNode node){return defaultVisit(node);}
    public Object visitElseStatement(ASTNode node){return defaultVisit(node);}
    public Object visitWhileStatement(ASTNode node){return defaultVisit(node);}
    public Object visitTryStatement(ASTNode node){return defaultVisit(node);}
    public Object visitCatchStatement(ASTNode node){return defaultVisit(node);}
    public Object visitFinallyStatement(ASTNode node){return defaultVisit(node);}
    public Object visitForStatement(ASTNode node){return defaultVisit(node);}
    public Object visitDoStatement(ASTNode node){return defaultVisit(node);}
    public Object visitExpression(ASTNode node){return defaultVisit(node);}
    public Object visitExpression1(ASTNode node){return defaultVisit(node);}
    public Object visitExpression2(ASTNode node){return defaultVisit(node);}
    public Object visitExpression3(ASTNode node){return defaultVisit(node);}
    public Object visitInfixOp(ASTNode node){return defaultVisit(node);}
    public Object visitPrefixOp(ASTNode node){return defaultVisit(node);}
    public Object visitPrimary(ASTNode node){return defaultVisit(node);}
    public Object visitLiteral(ASTNode node){return defaultVisit(node);}
    public Object visitParExpression(ASTNode node){return defaultVisit(node);}
    public Object visitArguments(ASTNode node){return defaultVisit(node);}
    public Object visitCreator(ASTNode node){return defaultVisit(node);}
    public Object visitSelector(ASTNode node){return defaultVisit(node);}
    public Object visitFieldDeclaratorsRest(ASTNode node){return defaultVisit(node);}
    public Object visitMethodDeclaratorRest(ASTNode node){return defaultVisit(node);}
    public Object visitFieldDecl(ASTNode node){return defaultVisit(node);}
    public Object visitMethodDecl(ASTNode node){return defaultVisit(node);}
    public Object visitIdentifier(ASTNode node){return defaultVisit(node);}
    public Object visitAssignmentOperator(ASTNode node){return defaultVisit(node);}
    public Object visitSwitchLabel(ASTNode node) {return defaultVisit(node);}

    /** Default behavior for the terminals is to return the terminal value as a string
     * @param node the terminal node
     * @return the string value of the terminal
     */
    public Object visitTerminal(ASTNode node){
        return node.treeNode.value.getEnd_chartRow().getCFGToken().getValue();
    }


}
