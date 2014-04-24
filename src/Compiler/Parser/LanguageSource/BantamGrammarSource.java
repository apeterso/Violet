package Compiler.Parser.LanguageSource;

import Compiler.Parser.CFG.ContextFreeGrammar;

/**
 * Author: Matt
 * Date: 2/26/14
 * This class is designed to include Bantam grammar encodings with prioritization.
 */
public final class BantamGrammarSource{

    private final static String bantamgrammar = makeGrammar();
    private final static ContextFreeGrammar bantamGrammarSource = new
            ContextFreeGrammar(bantamgrammar);

    /** Returns a CFG encoding the Bantam Java Grammar
     * @return Bantam CFG
     */
    public static ContextFreeGrammar getBantamGrammar(){
        return bantamGrammarSource;
    }

    /** Constructs the Bantam CFG String encoding
     * @return the Bantam CFG String encoding
     */
    private static String makeGrammar(){
        String program = "Program->ClassList|"+
                "ClassList->Class|ClassList->ClassList Class";
        String class_ = "Class->CLASS IDStmt LBRACE MemberListNode RBRACE|"+
                "Class->CLASS IDStmt EXTENDS IDStmt LBRACE MemberListNode RBRACE|" +
                "Class->CLASS IDStmt LBRACE RBRACE|"+
                "Class->CLASS IDStmt EXTENDS IDStmt LBRACE RBRACE";
        String member = "MemberListNode->MemberList|" +
                        "MemberListNode->Field|" +
                        "MemberList->MemberListNode Field|"+
                        "MemberListNode->Method|" +
                        "MemberList->MemberListNode Method";
        String method = "Method->IDStmt LPAREN RPAREN LBRACE RBRACE|"+
                "Method->IDStmt IDStmt LPAREN RPAREN LBRACE RBRACE|"+
                "Method->IDStmt LPAREN FormalList RPAREN LBRACE RBRACE|"+
                "Method->IDStmt IDStmt LPAREN FormalList RPAREN LBRACE RBRACE|"+
                "Method->IDStmt LPAREN FormalList RPAREN LBRACE Stmt RBRACE|"+
                "Method->IDStmt IDStmt LPAREN FormalList RPAREN LBRACE Stmt RBRACE|"+
                "Method->IDStmt LPAREN RPAREN LBRACE Stmt RBRACE|"+
                "Method->IDStmt IDStmt LPAREN RPAREN LBRACE Stmt RBRACE";
        String field = "Field->IDStmt IDStmt SEMI|" +
                "Field->IDStmt LSQBRACE RSQBRACE IDStmt SEMI|" +
                "Field->IDStmt IDStmt ASSIGN Expr SEMI|"+
                "Field->IDStmt LSQBRACE RSQBRACE IDStmt ASSIGN Expr SEMI";
        String formal = "FormalList->Formal|FormalList->Formal COMMA FormalList|" +
                "Formal->IDStmt IDStmt|"+
                "Formal->IDStmt LSQBRACE RSQBRACE IDStmt";
        String stmt = "Stmt->Stmt Stmt|"+
                "Stmt->ExprStmt|"+"Stmt->DeclStmt|"+"Stmt->IfStmt|"+
                "Stmt->WhileStmt|"+"Stmt->ForStmt|"+"Stmt->BreakStmt|"+"Stmt->ReturnStmt|"+
                "Stmt->BlockStmt";
        String exprstmt = "ExprStmt->Expr SEMI";
        String declstmt = "DeclStmt->IDStmt IDStmt ASSIGN Expr SEMI|"+
                          "DeclStmt->IDStmt LSQBRACE RSQBRACE IDStmt ASSIGN Expr SEMI";
        String ifstmt = "IfStmt->IF LPAREN Expr RPAREN Stmt|" +
                        "IfStmt->IF LPAREN Expr RPAREN ElseStmt|"
                        +"ElseStmt->Stmt ELSE Stmt";
        String whilestmt = "WhileStmt->WHILE LPAREN Expr RPAREN Stmt";
        String forstmt = "ForStmt->FOR LPAREN SEMI SEMI RPAREN Stmt|"+
                         "ForStmt->FOR LPAREN Expr SEMI SEMI RPAREN Stmt|"+
                         "ForStmt->FOR LPAREN SEMI Expr SEMI RPAREN Stmt|"+
                         "ForStmt->FOR LPAREN SEMI SEMI Expr RPAREN Stmt|"+
                         "ForStmt->FOR LPAREN Expr SEMI Expr SEMI RPAREN Stmt|"+
                         "ForStmt->FOR LPAREN Expr SEMI SEMI Expr RPAREN Stmt|"+
                         "ForStmt->FOR LPAREN SEMI Expr SEMI Expr RPAREN Stmt|"+
                         "ForStmt->FOR LPAREN Expr SEMI Expr SEMI Expr RPAREN Stmt";
        String breakstmt = "BreakStmt->BREAK SEMI";
        String returnstmt = "ReturnStmt->RETURN SEMI|"+
                            "ReturnStmt->RETURN Expr SEMI";
        String blockStmt = "BlockStmt->LBRACE Stmt RBRACE|"+
                           "BlockStmt->LBRACE RBRACE";
        String expr = "Expr->AssignExpr|Expr->DispatchExpr|Expr->NewExpr|"+
                      "Expr->InstanceofExpr|Expr->CastExpr|Expr->BinaryExpr|"+
                      "Expr->UnaryExpr|Expr->ConstExpr|Expr->VarExpr|"+
                      "Expr->LPAREN Expr RPAREN";
        String assignexpr = "AssignExpr->VarExpr ASSIGN Expr";
        String dispatchexpr = "DispatchExpr->IDStmt LPAREN DispatchFormal RPAREN|"+
                              "DispatchExpr->IDStmt LPAREN RPAREN|"+
                              "DispatchExpr->Expr DOT IDStmt LPAREN RPAREN|"+
                              "DispatchExpr->Expr DOT IDStmt LPAREN DispatchFormal RPAREN|"+
                              "DispatchFormal->Expr|DispatchFormal->Expr COMMA DispatchFormal";
        String newexpr = "NewExpr->NEW IDStmt LPAREN RPAREN|" +
                         "NewExpr->NEW IDStmt LPAREN NexExprFormal RPAREN|"+
                         "NexExprFormal->VarExpr|" +
                        "NexExprFormal->NexExprFormal COMMA NexExprFormal|"+
                        "NexExprFormal->ConstExpr|"+
                        "NexExprFormal->DispatchExpr|"+
                         "NewExpr->NEW IDStmt LSQBRACE Expr RSQBRACE";
        String instanceofexpr = "InstanceofExpr->Expr INSTANCEOF IDStmt|"+
                                "InstanceofExpr->Expr INSTANCEOF IDStmt LSQBRACE RSQBRACE";
        String castexpr = "CastExpr->LPAREN IDStmt RPAREN LPAREN Expr RPAREN|"+
                      "CastExpr->LPAREN IDStmt LSQBRACE RSQBRACE RPAREN LPAREN Expr RPAREN";
        String binaryexpr = "BinaryExpr->BinaryArithExpr|BinaryExpr->BinaryCompExpr|"+
                            "BinaryExpr->BinaryLogicExpr";
        String unaryexpr = "UnaryExpr->UnaryNegExpr|UnaryExpr->UnaryNotExpr|"+
                           "UnaryExpr->UnaryIncrExpr|UnaryExpr->UnaryDecrExpr";
        String constExpr = "ConstExpr->INT_CONST|ConstExpr->BOOLEAN_CONST|"+
                           "ConstExpr->STRING_CONST|ConstExpr->PLUS INT_CONST|" +
                           "ConstExpr->MINUS INT_CONST";
        String binaryarithexpr = "BinaryArithExpr->Expr PLUS Expr|"+
                                 "BinaryArithExpr->Expr MINUS Expr|"+
                                 "BinaryArithExpr->Expr TIMES Expr|"+
                                 "BinaryArithExpr->Expr DIVIDE Expr|"+
                                 "BinaryArithExpr->Expr MODULUS Expr";
        String binarycompexpr = "BinaryCompExpr->Expr EQ Expr|"+
                                 "BinaryCompExpr->Expr NE Expr|"+
                                 "BinaryCompExpr->Expr LT Expr|"+
                                 "BinaryCompExpr->Expr LEQ Expr|"+
                                 "BinaryCompExpr->Expr GT Expr|"+
                                 "BinaryCompExpr->Expr GEQ Expr";
        String binarylogicexpr = "BinaryLogicExpr->Expr AND Expr|"+
                                 "BinaryLogicExpr->Expr OR Expr";
        String unaryNegExpr = "UnaryNegExpr->MINUS Expr";
        String unaryNotExpr = "UnaryNotExpr->NOT Expr";
        String unaryIncrExpr = "UnaryIncrExpr->INCR Expr|UnaryIncrExpr->Expr INCR";
        String unaryDecrExpr = "UnaryDecrExpr->DECR Expr|UnaryDecrExpr->Expr DECR";
        String varexpr = "VarExpr->IDStmt|VarExpr->Expr DOT IDStmt|"+
                          "VarExpr->Expr DOT IDStmt LSQBRACE Expr RSQBRACE|"+
                          "VarExpr->IDStmt LSQBRACE Expr RSQBRACE";
        //for compatability with Java7 Lexer Tokens
        // (purposefully don't include char... etc.)
        String legacy = "IDStmt->ID|IDStmt->INT|IDStmt->BOOLEAN|IDStmt->STRING|" +
                "IDStmt->NULL_CONST|IDStmt->VOID";

        return program + "|" + class_ + "|" + member + "|" + method
                + "|" + field + "|" + formal + "|" + stmt + "|" +
                exprstmt + "|" + declstmt + "|" + ifstmt + "|" +
                whilestmt + "|" + forstmt + "|" + breakstmt + "|" +
                returnstmt + "|" + expr + "|" + blockStmt + "|" +
                assignexpr + "|" + dispatchexpr + "|" + newexpr +
                "|" + instanceofexpr + "|" + castexpr + "|" +
                binaryexpr + "|" + unaryexpr + "|" + constExpr
                + "|" + binaryarithexpr + "|" + binarycompexpr
                + "|" + binarylogicexpr + "|" + unaryNegExpr
                + "|" + unaryNotExpr + "|" + unaryIncrExpr
                + "|" + unaryDecrExpr + "|" + varexpr + "|" + legacy;
    }

}
