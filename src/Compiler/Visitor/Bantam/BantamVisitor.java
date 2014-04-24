package Compiler.Visitor.Bantam;

import Compiler.Nodes.ASTNode;
import Compiler.Visitor.Visitor;

/**
 * Implements the abstract class for the BantamVisitor pattern constituents, using triple
 * dispatch to minimze code duplication.
 * Created by Matt Levine on 3/8/14.
 */
public abstract class BantamVisitor extends Visitor {


    public Object visitArrayAssignExpr(ASTNode node){return null;}
    public Object visitArrayExpr(ASTNode node){return null;}
    public Object visitAssignExpr(ASTNode node){return null;}
    public Object visitBinaryArithDivideExpr(ASTNode node){return null;}
    public Object visitBinaryArithMinusExpr(ASTNode node){return null;}
    public Object visitBinaryArithModulusExpr(ASTNode node){return null;}
    public Object visitBinaryArithPlusExpr(ASTNode node){return null;}
    public Object visitBinaryArithTimesExpr(ASTNode node){return null;}
    public Object visitBinaryCompEqExpr(ASTNode node){return null;}
    public Object visitBinaryCompGeqExpr(ASTNode node){return null;}
    public Object visitBinaryCompGtExpr(ASTNode node){return null;}
    public Object visitBinaryCompLeqExpr(ASTNode node){return null;}
    public Object visitBinaryCompLtExpr(ASTNode node){return null;}
    public Object visitBinaryCompNeExpr(ASTNode node){return null;}
    public Object visitBinaryLogicAndExpr(ASTNode node){return null;}
    public Object visitBinarLogicOrExpr(ASTNode node){return null;}
    public Object visitBreakStmt(ASTNode node){return null;}
    public Object visitCastExpr(ASTNode node){return null;}
    public Object visitClass_(ASTNode node){return null;}
    public Object visitConstBooleanExpr(ASTNode node){return null;}
    public Object visitConstIntExpr(ASTNode node){return null;}
    public Object visitConstStringExpr(ASTNode node){return null;}
    public Object visitDeclStmt(ASTNode node){return null;}
    public Object visitDispatchExpr(ASTNode node){return null;}
    public Object visitField(ASTNode node){return null;}
    public Object visitFormal(ASTNode node){return null;}
    public Object visitForStmt(ASTNode node){return null;}
    public Object visitIfStmt(ASTNode node){return null;}
    public Object visitElseStmt(ASTNode node){return null;}
    public Object visitInstanceOfExpr(ASTNode node){return null;}
    public Object visitListNode(ASTNode node){return null;}
    public Object visitMethod(ASTNode node){return null;}
    public Object visitNewArrayExpr(ASTNode node){return null;}
    public Object visitNewExpr(ASTNode node){return null;}
    public Object visitProgram(ASTNode node){return null;}
    public Object visitReturnStmt(ASTNode node){return null;}
    public Object visitUnaryDecrExpr(ASTNode node){return null;}
    public Object visitUnaryIncrExpr(ASTNode node){return null;}
    public Object visitUnaryNegExpr(ASTNode node){return null;}
    public Object visitUnaryNotExpr(ASTNode node){return null;}
    public Object visitVarExpr(ASTNode node){return null;}
    public Object visitWhileStmt(ASTNode node){return null;}
    public Object visitTerminal(ASTNode node){return null;}


}



