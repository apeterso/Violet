package Compiler.Nodes;
import Compiler.Visitor.Bantam.BantamVisitor;
import Compiler.Visitor.Visitor;

public enum ASTNodeTypeBantam implements ASTNodeType {
    ArrayAssignExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitArrayAssignExpr(node);}},
    ArrayExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitArrayExpr(node);}},
    AssignExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitAssignExpr(node);}},
    BinaryArithDivideExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitBinaryArithDivideExpr(node);}},
    BinaryArithMinusExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitBinaryArithMinusExpr(node);}},
    BinaryArithModulusExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitBinaryArithModulusExpr(node);}},
    BinaryArithPlusExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitBinaryArithPlusExpr(node);}},
    BinaryArithTimesExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitBinaryArithTimesExpr(node);}},
    BinaryCompEqExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitBinaryCompEqExpr(node);}},
    BinaryCompGeqExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitBinaryCompGeqExpr(node);}},
    BinaryCompGtExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitBinaryCompGtExpr(node);}},
    BinaryCompLeqExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitBinaryCompLeqExpr(node);}},
    BinaryCompLtExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitBinaryCompLtExpr(node);}},
    BinaryCompNeExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitBinaryCompNeExpr(node);}},
    BinaryLogicAndExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitBinaryLogicAndExpr(node);}},
    BinarLogicOrExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitBinarLogicOrExpr(node);}},
    BreakStmt{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitBreakStmt(node);}},
    CastExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitCastExpr(node);}},
    Class{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitClass_(node);}},
    ConstBooleanExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitConstBooleanExpr(node);}},
    ConstIntExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitConstIntExpr(node);}},
    ConstStringExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitConstStringExpr(node);}},
    DeclStmt{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitDeclStmt(node);}},
    DispatchExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitDispatchExpr(node);}},
    Field{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitField(node);}},
    Formal{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitFormal(node);}},
    ForStmt{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitForStmt(node);}},
    IfStmt{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitIfStmt(node);}},
    ElseStmt{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitElseStmt(node);}},
    InstanceOfExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitInstanceOfExpr(node);}},
    ListNode{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitListNode(node);}},
    Method{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitMethod(node);}},
    NewArrayExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitNewArrayExpr(node);}},
    NewExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitNewExpr(node);}},
    Program{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitProgram(node);}},
    ReturnStmt{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitReturnStmt(node);}},
    UnaryDecrExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitUnaryDecrExpr(node);}},
    UnaryIncrExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitUnaryIncrExpr(node);}},
    UnaryNegExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitUnaryNegExpr(node);}},
    UnaryNotExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitUnaryNotExpr(node);}},
    VarExpr{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitVarExpr(node);}},
    WhileStmt{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitWhileStmt(node);}},
    AbstractNode{@Override public Object accept(Visitor v, ASTNode node){return null;}},
    INT_CONST{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitTerminal(node);}},
    BOOLEAN_CONST{@Override public Object accept(Visitor v, ASTNode node){
            return ((BantamVisitor)v).visitTerminal(node);}},
    ID{@Override public Object accept(Visitor v, ASTNode node){
        return ((BantamVisitor)v).visitTerminal(node);}},
    STRING_CONST{@Override public Object accept(Visitor v, ASTNode node){
                return ((BantamVisitor)v).visitTerminal(node);
    }};

//    abstract Object accept(Visitor visitor, ASTNode node);
}