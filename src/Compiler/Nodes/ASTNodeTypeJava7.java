package Compiler.Nodes;

import Compiler.Visitor.Java7.Java7Visitor;
import Compiler.Visitor.Visitor;

/**
 * This is the (triple-dispatching) node type for Java 7
 * Created by Matt Levine on 3/31/14.
 */
public enum ASTNodeTypeJava7 implements ASTNodeType{

    //Non-Terminals
    QualifiedIdentifierList{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitQualifiedIdentifierList(node);}},

    CompilationUnit{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitCompilationUnit(node);}},

    ImportDeclaration{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitImportDeclaration(node);}},

    TypeDeclaration{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTypeDeclaration(node);}},

    ClassDeclaration{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitClassDeclaration(node);}},

    NormalClassDeclaration{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitNormalClassDeclaration(node);}},

    EnumDeclaration{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitEnumDeclaration(node);}},

    ReferenceType{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitReferenceType(node);}},

    TypeArguments{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTypeArguments(node);}},

    NonWildcardTypeArguments{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitNonWildcardTypeArguments(node);}},

    TypeParameters{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTypeParameters(node);}},

    Bound{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitBound(node);}},

    Modifier{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitModifier(node);}},

    Annotations{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitAnnotations(node);}},

    MemberDecl{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitMemberDecl(node);}},

    FormalParameters{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitFormalParameters(node);}},

    FormalParameterDecls{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitFormalParameterDecls(node);}},

    VariableModifier{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitVariableModifier(node);}},

    VariableDeclarator{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitVariableDeclarator(node);}},
    VariableDeclaratorId{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitVariableDeclarator(node);}},
    ArrayVariableDeclarator{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitArrayVariableDeclarator(node);}},


    VariableInitializer{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitVariableInitializer(node);}},

    Statement{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitStatement(node);}},

    //ScopeStatements
    IfStatement{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitIfStatement(node);}},
    ElseStatement{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitElseStatement(node);}},
    WhileStatement{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitWhileStatement(node);}},
    TryStatement{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTryStatement(node);}},
    CatchStatement{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitCatchStatement(node);}},
    FinallyStatement{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitFinallyStatement(node);}},
    ForStatement{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitForStatement(node);}},
    DoStatement{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitDoStatement(node);}},

    Expression{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitExpression(node);}},

    Expression1{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitExpression1(node);}},

    Expression2{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitExpression2(node);}},

    Expression3{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitExpression3(node);}},

    InfixOp{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitInfixOp(node);}},

    PrefixOp{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitPrefixOp(node);}},

    Primary{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitPrimary(node);}},

    Literal{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitLiteral(node);}},

    ParExpression{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitParExpression(node);}},

    Arguments{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitArguments(node);}},

    Creator{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitCreator(node);}},

    Selector{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitSelector(node);}},

    MethodDecl{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitMethodDecl(node);}},

    FieldDecl{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitFieldDecl(node);}},

    NewScopeMemberDecl{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitNewScopeMemberDecl(node);}},

    FieldDeclaratorsRest{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitFieldDeclaratorsRest(node);}},

    MethodDeclaratorRest{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitMethodDeclaratorRest(node);}},

    Identifier{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitIdentifier(node);}},

    AssignmentOperator{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitAssignmentOperator(node);}},

    SwitchLabel{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitSwitchLabel(node);}},

    // Terminals (could offer each their own visit method, or could remove and just
    //call them "terminal" nodes (all leaves)... not doing either for now)
    AND{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    AMP{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    BOOLEAN{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    BOOLEAN_CONST{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    BYTE{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    CAREQ{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    CARET{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    CHAR{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    CHAR_CONST{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    DECREQ{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    DIVEQ{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    DIVIDE{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    DOUBLE{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    EQ{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    FLOAT{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    FLOAT_CONST{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    GEQ{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    GT{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    GTGT{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    GTGTGT{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    ID{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    INCR{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    INCREQ{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    INT{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    INT_CONST{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    lT{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    LTLT{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    LEQ{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    LONG{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    MINEQ{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    MINUS{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    MODULUS{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    NE{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    NOT{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    NULL_CONST{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    OR{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    OREQ{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    PLSEQ{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    PLUS{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    QUESTION{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    SQUIGGLE{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    THIS{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    TIMEQ{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    TIMES{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    TRIPEQ{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    VOID{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    PRIVATE{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    PROTECTED{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    STATIC{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    NATIVE{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},
    VOLATILE{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitTerminal(node);}},

//    LSQBRACE{@Override public Object accept(Visitor v, ASTNode node){
//        return ((Java7Visitor)v).visitTerminal(node);}},
//    RSQBRACE{@Override public Object accept(Visitor v, ASTNode node){
//        return ((Java7Visitor)v).visitTerminal(node);}},


 /*

    QualifiedIdentiferList{@Override public Object accept(Visitor v, ASTNode node){
        return ((Java7Visitor)v).visitArrayAssignExpr(node);}},

 */

    AbstractNode{@Override public Object accept(Visitor v, ASTNode node){
        return null;}},

}

//"Is ingenious, a deep thinker #humblebrag"