package Compiler.Parser.LanguageSource;

import Compiler.Parser.CFG.AdvancedCFG;
import Compiler.Parser.CFG.ContextFreeGrammar;
import IO.IOManager;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class encapsulates the Java 7 Language as defined by chapter 18 of the Java
 * Language Specifications.
 * Created by Matt Levine on 3/24/14.
 */
public class JavaGrammar {

    private final static String javagrammar = makeGrammar();
    private final static AdvancedCFG javaGrammarSource = new
            AdvancedCFG(javagrammar);

    static {
        javaGrammarSource.lowerPriority("BlockStatement","Statement",1);
    }

    //FIXME: Streamline idea: Factory repeated assemblies

    /** Returns a CFG encoding the Bantam Java Grammar
     * @return Bantam CFG
     */
    public static AdvancedCFG getJavaGrammar(){
        return javaGrammarSource;
    }

    /** Returns a CFG String for the Java 7 Langauge **/
    private static String makeGrammar(){

        //CompilationUnit : (SEMI,PACKAGE)
        return "CompilationUnit-> |" +
                        "CompilationUnit->PACKAGE QualifiedIdentifier SEMI|" +
                        "CompilationUnit->Annotations PACKAGE QualifiedIdentifier SEMI|" +
                        "CompilationUnit->ImportDeclarationList|" +
                        "CompilationUnit->TypeDeclarationList|" +
                        "CompilationUnit->ImportDeclarationList TypeDeclarationList|" +
                        "CompilationUnit->PACKAGE QualifiedIdentifier SEMI ImportDeclarationList|" +
                        "CompilationUnit->PACKAGE QualifiedIdentifier SEMI TypeDeclarationList|" +
                        "CompilationUnit->PACKAGE QualifiedIdentifier SEMI ImportDeclarationList TypeDeclarationList|" +
                        "CompilationUnit->Annotations PACKAGE QualifiedIdentifier SEMI ImportDeclarationList|" +
                        "CompilationUnit->Annotations PACKAGE QualifiedIdentifier SEMI TypeDeclarationList|" +
                        "CompilationUnit->Annotations PACKAGE QualifiedIdentifier SEMI ImportDeclarationList TypeDeclarationList|" +
                        "ImportDeclarationList->ImportDeclaration|" +
                        "ImportDeclarationList->ImportDeclaration ImportDeclarationList|" +
                        "TypeDeclarationList->TypeDeclaration|" +
                        "TypeDeclaration->TypeDeclarationList|"
        + "Identifier->ID|"
        //QualifiedIdentifier (Terminals: Identifier, DOT)
        + "QualifiedIdentifier->Identifier|" +
                "QualifiedIdentifier->Identifier QualProd|" +
                "QualProd->DOT Identifier|" +
                "QualProd->DOT Identifier QualProd|"
        //QualifiedIdentifierList: (Terminals: COMMA)
        + "QualifiedIdentifierList->QualifiedIdentifier|" +
                "QualifiedIdentifierList->QualifiedIdentifier QualProdList|" +
                "QualProdList->COMMA QualifiedIdentifier|" +
                "QualProdList->COMMA QualifiedIdentifierList QualProdList|"
        //ImportDeclaration : (STATIC)
        + "ImportDeclaration->IMPORT Identifier SEMI|" +
                "ImportDeclaration->IMPORT STATIC Identifier SEMI|" +
                "ImportDeclaration->IMPORT Identifier DOT TIMES SEMI|" +
                "ImportDeclaration->IMPORT STATIC Identifier DOT TIMES SEMI|" +
                "ImportDeclaration->IMPORT Identifier ImportList SEMI|" +
                "ImportDeclaration->IMPORT STATIC Identifier ImportList SEMI|" +
                "ImportDeclaration->IMPORT Identifier ImportList DOT TIMES SEMI|" +
                "ImportDeclaration->IMPORT STATIC Identifier ImportList DOT TIMES SEMI|" +
                "ImportList->DOT Identifier|ImportList->DOT Identifier ImportList|"
        //TypeDeclaration
        + "TypeDeclaration->ClassOrInterfaceDeclaration|" +
                "TypeDeclaration->SEMI|"
        //ClassOrInterfaceDeclaration
        + "ClassOrInterfaceDeclaration->ClassDeclaration|" +
                "ClassOrInterfaceDeclaration->InterfaceDeclaration|" +
                "ClassOrInterfaceDeclaration->ModifierList ClassDeclaration|" +
                "ClassOrInterfaceDeclaration->ModifierList InterfaceDeclaration|" +
                "ModifierList->Modifier|ModifierList->Modifier ModifierList|"
        //ClassDeclaratoin : (Class)
        + "ClassDeclaration->NormalClassDeclaration|" +
                "ClassDeclaration->EnumDeclaration|"
        //InterfaceDeclaration
        + "InterfaceDeclaration->NormalInterfaceDeclaration|" +
                "InterfaceDeclaration->AnnotationTypeDeclaration|"

        //NormalClassDeclaration : (EXTENDS,IMPLEMENTS
        + "NormalClassDeclaration->CLASS Identifier ClassBody|" +
                "NormalClassDeclaration->CLASS Identifier TypeParameters ClassBody|" +
                "NormalClassDeclaration->CLASS Identifier EXTENDS Type ClassBody|" +
                "NormalClassDeclaration->CLASS Identifier IMPLEMENTS TypeList ClassBody|" +
                "NormalClassDeclaration->CLASS Identifier TypeParameters EXTENDS Type ClassBody|" +
                "NormalClassDeclaration->CLASS Identifier TypeParameters IMPLEMENTS TypeList ClassBody|" +
                "NormalClassDeclaration->CLASS Identifier EXTENDS Type IMPLEMENTS TypeList ClassBody|" +
                "NormalClassDeclaration->CLASS Identifier TypeParameters EXTENDS Type IMPLEMENTS TypeList ClassBody|"
        //Enum Declaration : (ENUM)
        + "EnumDeclaration->ENUM Identifier EnumBody|" +
                "EnumDeclaration->ENUM Identifier IMPLEMENTS TypeList EnumBody|"
        //NormalInterfaceDeclaration : (INTERFACE)
        + "NormalInterfaceDeclaration->INTERFACE Identifier InterfaceBody|" +
                "NormalInterfaceDeclaration->INTERFACE Identifier TypeParameters InterfaceBody|" +
                "NormalInterfaceDeclaration->INTERFACE Identifier EXTENDS TypeList InterfaceBody|" +
                "NormalInterfaceDeclaration->INTERFACE Identifier TypeParameters EXTENDS TypeList InterfaceBody|"
        //AnnotationTypeDeclaration : ( AROBASE )
        + "AnnotationTypeDeclaration->AROBASE INTERFACE Identifier AnnotationTypeBody|"
        //Type : ( LSQBRACE RSQBRACE )
        + "Type->BasicType|" +
                "Type->ReferenceType|" +
                "Type->BasicType TypeAssembly|" +
                "Type->ReferenceType TypeAssembly|" +
                "TypeAssembly->LSQBRACE RSQBRACE|" +
                "TypeAssembly->LSQBRACE RSQBRACE TypeAssembly|"
        //BasicType : (BYTE, SHORT, CHAR, INT, LONG, FLOAT DOUBLE, BOOLEAN)
        +"BasicType->BYTE|" +
                "BasicType->SHORT|" +
                "BasicType->CHAR|" +
                "BasicType->INT|" +
                "BasicType->LONG|" +
                "BasicType->FLOAT|" +
                "BasicType->DOUBLE|" +
                "BasicType->BOOLEAN|"
        //ReferenceType
        + "ReferenceType->Identifier|" +
                "ReferenceType->Identifier TypeArguments|" +
                "ReferenceType->Identifier ReferenceTypeAssembly|" +
                "ReferenceType->Identifier TypeArguments ReferenceTypeAssembly|" +
                "ReferenceTypeAssembly->DOT Identifier|" +
                "ReferenceTypeAssembly->DOT Identifier TypeArguments|" +
                "ReferenceTypeAssembly->DOT Identifier ReferenceTypeAssembly|" +
                "ReferenceTypeAssembly->DOT Identifier TypeArguments ReferenceTypeAssembly|"
        //TypeArguments : ( LT , GT , COMMA )
        + "TypeArguments->LT TypeArgument GT|" +
                "TypeArguments->LT TypeArgument TypeArgumentAssembler GT|" +
                "TypeArgumentAssembler->COMMA TypeArgument|" +
                "TypeArgumentAssembler->COMMA TypeArgument TypeArgumentAssembler|"
        //TypeArgument : (QUESTION , SUPER )
        + "TypeArgument->ReferenceType|" +
                "TypeArgument->QUESTION|" +
                "TypeArgument->QUESTION SUPER ReferenceType|" +
                "TypeArgument->QUESTION EXTENDS ReferenceType|"
        //NonWildcardTypeArguments
        + "NonWildcardTypeArguments->LT TypeList GT|"
        //TypeList : (LBRACE, RBRACE)
        + "TypeList->ReferenceType TypeListAssembly|" +
                "TypeList->ReferenceType|" +
                "TypeListAssembly->COMMA ReferenceType|" +
                "TypeListAssembly->COMMA ReferenceType TypeListAssembly|"
        //TypeArgumentsOrDiamond
        + "TypeArgumentsOrDiamond->LT GT|" +
                "TypeArgumentsOrDiamond->TypeArguments|"
        //NonWildcardTypeArgumentsOrDiamond
        + "NonWildcardTypeArgumentsOrDiamond->LT GT|" +
                "NonWildcardTypeArgumentsOrDiamond->NonWildcardTypeArguments|"
        //TypeParameters
        + "TypeParameters->LT TypeParameter GT|" +
                "TypeParameters->LT TypeParameter TypeParametersAssembly GT|" +
                "TypeParametersAssembly->COMMA TypeParameter|" +
                "TypeParametersAssembly->COMMA TypeParameter TypeParametersAssembly|"
        //TypeParameter
        + "TypeParameter->Identifier|" +
                "TypeParameter->Identifier EXTENDS Bound|"
        //Bound
        + "Bound->ReferenceType|" +
                "Bound->ReferenceType AND ReferenceType|"
        //Modifier : (PUBLIC,PROTECTED,PRIVATE,STATIC,ABSTRACT,FINAL,NATIVE,SYNCHRONIZED,
        // TRANSIENT,VOLATILE,STRICTFP
        + "Modifier->Annotation|" +
                "Modifier->PUBLIC|" +
                "Modifier->PROTECTED|" +
                "Modifier->PRIVATE|" +
                "Modifier->STATIC|" +
                "Modifier->ABSTRACT|" +
                "Modifier->FINAL|" +
                "Modifier->NATIVE|" +
                "Modifier->SYNCHRONIZED|" +
                "Modifier->TRANSIENT|" +
                "Modifier->VOLATILE|" +
                "Modifier->STRICTFP|"
        //Annotations
        + "Annotations->Annotation|" +
                "Annotations->Annotation AnnotationsAssembly|" +
                "AnnotationsAssembly->Annotation|" +
                "AnnotationsAssembly->Annotation AnnotationsAssembly|"
        //Annotation : (LPAREN, RPAREN)
        + "Annotation->AROBASE QualifiedIdentifier|" +
                "Annotation->AROBASE QualifiedIdentifier LPAREN RPAREN|" +
                "Annotation->AROBASE QualifiedIdentifier LPAREN AnnotationElement RPAREN|"
        //AnnotationElement
        + "AnnotationElement->ElementValuePairs|" +
                "AnnotationElement->ElementValue|"
        //ElementValuePairs
        + "ElementValuePairs->ElementValuePair|" +
                "ElementValuePairs->ElementValuePair ElementValuePairsAssembly|" +
                "ElementValuePairsAssembly->COMMA ElementValuePair|" +
                "ElementValuePairsAssembly->COMMA ElementValuePair ElementValuePairsAssembly|"
        //ElementValuePair
        + "ElementValuePair->Identifier ASSIGN ElementValue|"
        //ElementValue
        + "ElementValue->Annotation|" +
                "ElementValue->Expression1|" +
                "ElementValue->ElementValueArrayInitializer|"
        //ElementValueArrayInitializer : (LBRACE, RBRACE)
        + "ElementValueArrayInitializer->LBRACE RBRACE|" +
                "ElementValueArrayInitializer->RBRACE ElementValues RBRACE|" +
                "ElementValueArrayInitializer->LBRACE COMMA RBRACE|" +
                "ElementValueArrayInitializer->LBRACE ElementValues COMMA RBRACE|"
        //ElementValues
        + "ElementValues->ElementValue|" +
                "ElementValues->ElementValue ElementValuesAssembly|" +
                "ElementValuesAssembly->COMMA ElementValue|" +
                "ElementValuesAssembly->COMMA ElementValue ElementValuesAssembly|"
        //ClassBody
        + "ClassBody->LBRACE RBRACE|" +
                "ClassBody->LBRACE ClassBodyAssembly RBRACE|" +
                "ClassBodyAssembly->ClassBodyDeclaration|" +
                "ClassBodyAssembly->ClassBodyDeclaration ClassBodyAssembly|"
        //ClassBodyDeclaration
        + "ClassBodyDeclaration->SEMI|" +
                "ClassBodyDeclaration->MemberDecl|" +
                "ClassBodyDeclaration->ClassBodyDeclarationAssembly MemberDecl|" +
                "ClassBodyDeclarationAssembly->Modifier|" +
                "ClassBodyDeclarationAssembly->Modifier ClassBodyDeclarationAssembly|" +
                "ClassBodyDeclaration->Block|" +
                "ClassBodyDeclaration->STATIC Block|"
        //MemberDecl : (VOID)
        + "MemberDecl->NewScopeMemberDecl|" +
                "MemberDecl->FieldDecl|" +
                "NewScopeMemberDecl->VOID Identifier VoidMethodDeclaratorRest|" +
                "NewScopeMemberDecl->Identifier ConstructorDeclaratorRest|" +
                "NewScopeMemberDecl->GenericMethodOrConstructorDecl|" +
                "NewScopeMemberDecl->ClassDeclaration|" +
                "NewScopeMemberDecl->InterfaceDeclaration|" +
                "NewScopeMemberDecl->MethodDecl|"

        //MethodOrFieldDecl
        + "MethodDecl->Type Identifier MethodDeclaratorRest|" +
                "FieldDecl->Type Identifier FieldDeclaratorsRest|"
/*
        //MethodOrFieldRest
        + "MethodOrFieldRest->FieldDeclaratorsRest SEMI |" +
                "MethodOrFieldRest->MethodDeclaratorRest|"
*/
        //FieldDeclaratorsRest
        + "FieldDeclaratorsRest->VariableDeclaratorRest|" +
                "FieldDeclaratorsRest->VariableDeclaratorRest FieldDeclaratorsRestAssembly|" +
                "FieldDeclaratorsRestAssembly->COMMA VariableDeclarator|" +
                "FieldDeclaratorsRestAssembly->COMMA VariableDeclarator FieldDeclaratorsRestAssembly|"
        //MethodDeclaratorRest : (THROWS)
        + "MethodDeclaratorRest->FormalParameters Block|" +
                "MethodDeclaratorRest->FormalParameters SEMI|" +
                "MethodDeclaratorRest->FormalParameters MethodDeclaratorRestAssembly Block|" +
                "MethodDeclaratorRest->FormalParameters MethodDeclaratorRestAssembly SEMI|" +
                "MethodDeclaratorRest->FormalParameters THROWS QualifiedIdentifierList Block|" +
                "MethodDeclaratorRest->FormalParameters THROWS QualifiedIdentifierList SEMI|" +
                "MethodDeclaratorRest->FormalParameters MethodDeclaratorRestAssembly THROWS " +
                                                            "QualifiedIdentifierList Block|" +
                "MethodDeclaratorRest->FormalParameters MethodDeclaratorRestAssembly " +
                                                    "THROWS QualifiedIdentifierList SEMI|" +
                "MethodDeclaratorRestAssembly->LSQBRACE RSQBRACE|" +
                "MethodDeclaratorRestAssembly->LSQBRACE RSQBRACE MethodDeclaratorRestAssembly|"
        //VoidMethodDeclaratorRest
        + "VoidMethodDeclaratorRest->FormalParameters Block|" +
                "VoidMethodDeclaratorRest->FormalParameters SEMI|" +
                "VoidMethodDeclaratorRest->FormalParameters THROWS QualifiedIdentifierList Block|" +
                "VoidMethodDeclaratorRest->FormalParameters THROWS QualifiedIdentifierList SEMI|"
        //ConstructorDeclaratorRest
        + "ConstructorDeclaratorRest->FormalParameters Block|" +
                "ConstructorDeclaratorRest->FormalParameters THROWS QualifiedIdentifierList Block|"
        //GenericMethodOrConstructorDecl
        + "GenericMethodOrConstructorDecl->TypeParameters GenericMethodOrConstructorRest|"
        //GenericMethodOrConstructorRest : (VOID)
        + "GenericMethodOrConstructorRest->Type Identifier MethodDeclaratorRest|" +
                "GenericMethodOrConstructorRest->VOID Identifier MethodDeclaratorRest|" +
                "GenericMethodOrConstructorRest->Identifier ConstructorDeclaratorRest|"
        //InterfaceBody
        + "InterfaceBody->LBRACE InterfaceBodyAssembly RBRACE|" +
                "InterfaceBody->LBRACE RBRACE|" +
                "InterfaceBodyAssembly->InterfaceBodyDeclaration|" +
                "InterfaceBodyAssembly->InterfaceBodyDeclaration InterfaceBody|"
        //InterfaceBodyDeclaration
        + "InterfaceBodyDeclaration->SEMI|" +
                "InterfaceBodyDeclaration->InterfaceMemberDecl|" +
                "InterfaceBodyDeclaration->InterfaceBodyDeclarationAssembly InterfaceMemberDecl|" +
                "InterfaceBodyDeclarationAssembly->Modifier|" +
                "InterfaceBodyDeclarationAssembly->Modifier InterfaceBodyDeclarationAssembly|"
        //InterfaceMemberDecl
        + "InterfaceMemberDecl->InterfaceMethodOrFieldDecl|" +
                "InterfaceMemberDecl->VOID Identifier VoidInterfaceMethodDeclaratorRest|" +
                "InterfaceMemberDecl->InterfaceGenericMethodDecl|" +
                "InterfaceMemberDecl->ClassDeclaration|" +
                "InterfaceMemberDecl->InterfaceDeclaration|"
        //InterfaceMethodOrFieldDecl
        + "InterfaceMethodOrFieldDecl->Type Identifier InterfaceMethodOrFieldRest|"
        //InterfaceMethodOrFieldRest
        + "InterfaceMethodOrFieldRest->ConstantDeclaratorsRest SEMI|" +
                "InterfaceMethodOrFieldRest->InterfaceMethodDeclaratorRest|"
        //ConstantDeclaratorsRest
        + "ConstantDeclaratorsRest->ConstantDeclaratorRest|" +
                "ConstantDeclaratorsRest->ConstantDeclaratorRest ConstantDeclaratorsRestAssembly|" +
                "ConstantDeclaratorsRestAssembly->COMMA ConstantDeclarator|" +
                "ConstantDeclaratorsRestAssembly->COMMA ConstantDeclarator ConstantDeclaratorsRestAssembly|"
        //ConstantDeclaratorRest
        + "ConstantDeclaratorRest->ASSIGN VariableInitializer|" +
                "ConstantDeclaratorRest->ConstantDeclaratorRestAssembly ASSIGN VariableInitializer|" +
                "ConstantDeclaratorRestAssembly->LSQBRACE RSQBRACE|" +
                "ConstantDeclaratorRestAssembly->LSQBRACE RSQBRACE ConstantDeclaratorRestAssembly|"
        //ConstantDeclarator
        + "ConstantDeclarator->Identifier ConstantDeclaratorRest|"
        //InterfaceMethodDeclaratorRest
        + "InterfaceMethodDeclaratorRest->FormalParameters SEMI|" +
                "InterfaceMethodDeclaratorRest->FormalParameters THROWS QualifiedIdentifierList SEMI|" +
                "InterfaceMethodDeclaratorRest->FormalParameters InterfaceMethodDeclaratorRestAssembly SEMI|" +
                "InterfaceMethodDeclaratorRest->FormalParameters InterfaceMethodDeclaratorRestAssembly " +
                                "THROWS QualifiedIdentifierList SEMI|" +
                "InterfaceMethodDeclaratorRestAssembly->LSQBRACE RSQBRACE|" +
                "InterfaceMethodDeclaratorRestAssembly->LSQBRACE RSQBRACE InterfaceMethodDeclaratorRestAssembly|"
        //VoidInterfaceMethodDeclaratorRest
        + "VoidInterfaceMethodDeclaratorRest->FormalParameters SEMI|" +
                "VoidInterfaceMethodDeclaratorRest->FormalParameters THROWS QualifiedIdentifierList SEMI|"
        //InterfaceGenericMethodDecl
        + "InterfaceGenericMethodDecl->TypeParameters Type Identifier InterfaceMethodDeclaratorRest|" +
                "InterfaceGenericMethodDecl->TypeParameters VOID Identifier InterfaceMethodDeclaratorRest|"
        //FormalParameters
        + "FormalParameters->LPAREN RPAREN|" +
                "FormalParameters->LPAREN FormalParameters_ RPAREN|" +
                "FormalParameters_->FormalParameterDecls|" +
                "FormalParameters_->FormalParameterDecls COMMA FormalParameters_|"
        //FormalParameterDecls
        + "FormalParameterDecls->Type FormalParameterDeclsRest|" +
                "FormalParameterDecls->FormalParameterDeclsAssembly Type FormalParameterDeclsRest|" +
                "FormalParameterDeclsAssembly->VariableModifier|" +
                "FormalParameterDeclsAssembly->VariableModifier FormalParameterDecls|"
        //VariableModifier : (FINAL)
        + "VariableModifier->FINAL|" +
                "VariableModifier->Annotation|"
        //FormalParameterDeclsRest : (DOT)
        + "FormalParameterDeclsRest->VariableDeclaratorId|" +
                "FormalParameterDeclsRest->VariableDeclaratorId|" +
                "FormalParameterDeclsRest->DOT DOT DOT VariableDeclaratorId|"
        //VariableDeclaratorId
        + "VariableDeclaratorId->Identifier|" +
                "VariableDeclaratorId->Identifier VariableDeclaratorIdAssembly|" +
                "VariableDeclaratorIdAssembly->LSQBRACE RSQBRACE|" +
                "VariableDeclaratorIdAssembly->LSQBRACE RSQBRACE VariableDeclaratorIdAssembly|"

        // VariableDeclarators
        + "VariableDeclarators->VariableDeclarator|" +
                "VariableDeclarators->VariableDeclarator VariableDeclaratorsAssembly|" +
                "VariableDeclaratorsAssembly->COMMA VariableDeclarator|" +
                "VariableDeclaratorsAssembly->COMMA VariableDeclarator VariableDeclaratorsAssembly|"
        // VariableDeclarator
        + "VariableDeclarator->Identifier VariableDeclaratorRest|" +
                "VariableDeclarator->ArrayVariableDeclarator|" +
                "ArrayVariableDeclarator->Identifier AVD_ VariableDeclaratorRest|" +
                "AVD_->LSQBRACE RSQBRACE|" +
                "AVD_->LSQBRACE RSQBRACE AVD_|"
        // VariableDeclaratorRest
        + "VariableDeclaratorRest-> |" +
                "VariableDeclaratorRest->ASSIGN VariableInitializer|"
        // VariableInitializer
        + "VariableInitializer->ArrayInitializer|" +
                "VariableInitializer->Expression|"
        // ArrayInitializer : (LBRACE,BRACE)
        + "ArrayInitializer->LBRACE RBRACE|" +
                "ArrayInitializer->LBRACE VariableInitializer RBRACE|" +
                "ArrayInitializer->LBRACE VariableInitializer COMMA RBRACE|" +
                "ArrayInitializer->LBRACE VariableInitializer ArrayInitializerAssembly RBRACE|" +
                "ArrayInitializer->LBRACE VariableInitializer ArrayInitializerAssembly COMMA RBRACE|" +
                "ArrayInitializerAssembly->COMMA VariableInitializer|" +
                "ArrayInitializer->COMMA VariableInitializer ArrayInitializerAssembly|"
        // Block
        + "Block->LBRACE BlockStatements RBRACE|"
        // BlockStatements
        + "BlockStatements-> |" +
                "BlockStatements->BlockStatementsAssembly|" +
                "BlockStatementsAssembly->BlockStatement|" +
                "BlockStatementsAssembly->BlockStatement BlockStatementsAssembly|"
        // BlockStatement : (COLON)
        + "BlockStatement->LocalVariableDeclarationStatement|" +
                "BlockStatement->ClassOrInterfaceDeclaration|" +
                "BlockStatement->Statement|" +
//                "BlockStatement->Statement|" +
                "BlockStatement->Identifier COLON Statement|"
        //LocalVariableDeclarationStatement
        + "LocalVariableDeclarationStatement->Type VariableDeclarators SEMI|" +
                "LocalVariableDeclarationStatement->LocalVariableDeclarationStatementAssembly " +
                                                "Type VariableDeclarators SEMI|" +
                "LocalVariableDeclarationStatementAssembly->VariableModifier|" +
                "LocalVariableDeclarationStatementAssembly-> VariableModifier " +
                        "LocalVariableDeclarationStatementAssembly|"

        //Statement : (IF,ELSE,ASSERT,SWITCH,WHILE,DO,FOR,BREAK,CONTINUE,RETURN,THROW,
        //              SYNCHRONIZED,TRY)
        + "Statement->Block|" +
                "Statement->SEMI|" +
                "Statement->Identifier COLON Statement|" +
                "Statement->StatementExpression SEMI|" +
                "Statement->IfStatement|" +
                "Statement->IfStatement ElseStatement|" +
                "IfStatement->IF ParExpression Statement|" +
                "ElseStatement->ELSE Statement|" +
                "Statement->ASSERT Expression|" +
                "Statement->ASSERT Expression COLON Expression SEMI|" +
                "Statement->SWITCH ParExpression LBRACE SwitchBlockStatementGroups RBRACE|" +
                "Statement->WhileStatement|" +
                "WhileStatement->WHILE ParExpression Statement|" +
                "Statement->DoStatement|" +
                "DoStatement->DO Statement WHILE ParExpression SEMI|" +
                "Statement->ForStatement|" +
                "ForStatement->FOR LPAREN ForControl RPAREN Statement|" +
                "Statement->BREAK SEMI|" +
                "Statement->BREAK Identifier SEMI|" +
                "Statement->CONTINUE SEMI|" +
                "Statement->CONTINUE Identifier SEMI|" +
                "Statement->RETURN SEMI|" +
                "Statement->RETURN Expression SEMI|" +
                "Statement->THROW Expression SEMI|" +
                "Statement->SYNCHRONIZED ParExpression Block|" +
                "Statement->TryStatement CatchStatement|" +
                "Statement->TryStatement FinallyStatement|" +
                "Statement->TryStatement CatchStatement FinallyStatement|" +
                "TryStatement->TRY Block|" +
                "TryStatement->TRY ResourceSpecification Block|" +
                "CatchStatement->Catches|" +
                "FinallyStatement->Finally|"
                // StatementExpression
        + "StatementExpression->Expression|"
        // Catches
        + "Catches->CatchClause|" +
                "Catches->CatchClause CatchesAssembly|" +
                "CatchesAssembly->CatchClause|" +
                "CatchesAssembly->CatchClause CatchesAssembly|"
        // CatchClause : (CATCH)
        + "CatchClause->CATCH LPAREN CatchType Identifier RPAREN Block|" +
                "CatchClause->CATCH LPAREN CatchClauseAssembly CatchType Identifier RPAREN Block|" +
                "CatchClauseAssembly->VariableModifier|" +
                "CatchClauseAssembly->VariableModifier CatchClauseAssembly|"
        // CatchType : (BAR)
        + "CatchType->QualifiedIdentifier|" +
                "CatchType->QualifiedIdentifier CatchTypeAssembly|" +
                "CatchTypeAssembly->BAR QualifiedIdentifier|" +
                "CatchTypeAssembly->BAR QualifiedIdentifier CatchType|"
        // Finally : (FINALLY)
        + "Finally->FINALLY Block|"
        // ResourceSpecification
        + "ResourceSpecification->LPAREN Resources RPAREN|" +
                "ResourceSpecification->LPAREN Resources SEMI RPAREN|"
        // Resources
        + "Resources->Resource|" +
                "Resources->Resource ResourcesAssembly|" +
                "ResourcesAssembly->SEMI Resource|" + //dona dinkle
                "ResourcesAssembly->SEMI Resource ResourcesAssembly|"
        // Resource
        + "Resource->ReferenceType VariableDeclaratorId ASSIGN Expression|" +
                "Resource->ResourceAssembly ReferenceType VariableDeclaratorId ASSIGN Expression|" +
                "ResourceAssembly->VariableModifier|" +
                "ResourceAssembly->VariableModifier ResourceAssembly|"

        // SwitchBlockStatementGroups
        + "SwitchBlockStatementGroups-> |" +
                "SwitchBlockStatementGroups->SwitchBlockStatementGroupsAssembly|" +
                "SwitchBlockStatementGroupsAssembly->SwitchBlockStatementGroup|" +
                "SwitchBlockStatementGroupsAssembly->SwitchBlockStatementGroup " +
                                        "SwitchBlockStatementGroupsAssembly|"
        // SwitchBlockStatementGroup
        + "SwitchBlockStatementGroup->SwitchLabels BlockStatements|"
        // SwitchLabels
        + "SwitchLabels->SwitchLabel|" +
                "SwitchLabels->SwitchLabel SwitchLabelsAssembly|" +
                "SwitchLabelsAssembly->SwitchLabel|" +
                "SwitchLabelsAssembly->SwitchLabel SwitchLabelsAssembly|"
        // SwitchLabel : (CASE,DEFAULT,COLON)
        + "SwitchLabel->CASE Expression COLON|" +
                "SwitchLabel->CASE EnumConstantName COLON|" +
                "SwitchLabel->DEFAULT COLON|"
        // EnumConstantName
        + "EnumConstantName->Identifier|"

        // ForControl
        + "ForControl->ForVarControl|" +
                "ForControl->ForInit SEMI SEMI|" +
                "ForControl->ForInit SEMI Expression SEMI|" +
                "ForControl->ForInit SEMI SEMI ForUpdate|" +
                "ForControl->ForInit SEMI Expression SEMI ForUpdate|"
        //ForVarControl
        + "ForVarControl->Type VariableDeclaratorId ForVarControlRest|" +
                "ForVarControl->ForVarControlAssembly Type VariableDeclaratorId ForVarControlRest|" +
                "ForVarControlAssembly->VariableModifier|" +
                "ForVarControlAssembly->VariableModifier ForVarControlAssembly|"
        // ForVarControlRest
        + "ForVarControlRest->ForVariableDeclaratorsRest SEMI SEMI|" +
                "ForVarControlRest->ForVariableDeclaratorsRest SEMI Expression SEMI|" +
                "ForVarControlRest->ForVariableDeclaratorsRest SEMI SEMI ForUpdate|" +
                "ForVarControlRest->ForVariableDeclaratorsRest SEMI Expression SEMI ForUpdate|" +
                "ForVarControlRest->COLON Expression|"
        // ForVariableDeclaratorsRest
        + "ForVariableDeclaratorsRest-> |" +
                "ForVariableDeclaratorsRest->ASSIGN VariableInitializer|" +
                "ForVariableDeclaratorsRest->ForVariableDeclaratorsRestAssembly|" +
                "ForVariableDeclaratorsRest->ASSIGN VariableInitializer " +
                                                "ForVariableDeclaratorsRestAssembly|" +
                "ForVariableDeclaratorsRestAssembly->COMMA VariableDeclarator|" +
                "ForVariableDeclaratorsRestAssembly->COMMA VariableDeclarator " +
                                                    "ForVariableDeclaratorsRestAssembly|"
        // ForInit FIXME: Really? This isn't a Java 7 Specification Typo...?
        + "ForInit-> |"
        // ForUpdate
        + "ForUpdate->StatementExpression|" +
                "ForUpdate->StatementExpression ForUpdateAssembly|" +
                "ForUpdateAssembly->COMMA StatementExpression|" +
                "ForUpdateAssembly->COMMA StatementExpression ForUpdateAssembly|"
        // Expression
        + "Expression->Expression1|" +
                "Expression->Expression1 AssignmentOperator Expression1|"
        // AssignmentOperator : (ASSIGN, PLSEQ, MINEQ, TIMEQ, DIVEQ, ANDEQ,
        //                      OREQ, CAREQ, MODEQ, DECREQ, INCREQ, TRIPEQ)
        + "AssignmentOperator->ASSIGN|" +
                "AssignmentOperator->PLSEQ|" +
                "AssignmentOperator->MINEQ|" +
                "AssignmentOperator->TIMEQ|" +
                "AssignmentOperator->DIVEQ|" +
                "AssignmentOperator->ANDEQ|" +
                "AssignmentOperator->OREQ|" +
                "AssignmentOperator->CAREQ|" +
                "AssignmentOperator->MODEQ|" +
                "AssignmentOperator->DECREQ|" +
                "AssignmentOperator->INCREQ|" +
                "AssignmentOperator->TRIPEQ|"
        // Expression1
        + "Expression1->Expression2|" +
                "Expression1->Expression2 Expression1Rest|"
        // Expression1Rest
        + "Expression1Rest->QUESTION Expression COLON Expression1|"
        // Expression2
        + "Expression2->Expression3|" +
                "Expression2->Expression3 Expression2Rest|"
        // Expression2Rest : (INSTANCEOF)
        + "Expression2Rest->Expression2RestAssembly|" +
                "Expression2Rest->INSTANCEOF Type|" +
                "Expression2RestAssembly->InfixOp Expression3|" +
                "Expression2RestAssembly->InfixOp Expression3 Expression2RestAssembly|"
        // InfixOp : (OR, AND, BAR, CARET, AMP, EQ, NE, LT, GT, LEQ,
        //              GEQ, LTLT, GTGT, GTGTGT, PLUS, MINUS, TIMES, DIVIDE, MODULUS )
        + "InfixOp->OR|" +
                "InfixOp->OR|" +
                "InfixOp->AND|" +
                "InfixOp->BAR|" +
                "InfixOp->CARET|" +
                "InfixOp->AMP|" +
                "InfixOp->EQ|" +
                "InfixOp->NE|" +
                "InfixOp->LT|" +
                "InfixOp->GT|" +
                "InfixOp->LEQ|" +
                "InfixOp->GEQ|" +
                "InfixOp->LTLT|" +
                "InfixOp->GTGT|" +
                "InfixOp->GTGTGT|" +
                "InfixOp->PLUS|" +
                "InfixOp->MINUS|" +
                "InfixOp->TIMES|" +
                "InfixOp->DIVIDE|" +
                "InfixOp->MODULUS|"
        // Expression3
        + "Expression3->PrefixOp Expression3|" +
                "Expression3->LPAREN Expression RPAREN Expression3|" +
                "Expression3->LPAREN Type RPAREN Expression3|" +
                "Expression3->Primary|" +
                "Expression3->Primary Expression3Assembly1|" +
                "Expression3->Primary Expression3Assembly2|" +
                "Expression3->Primary Expression3Assembly1 Expression3Assembly2|" +
                "Expression3Assembly1->Selector|" +
                "Expression3Assembly1->Selector Expression3Assembly1|" +
                "Expression3Assembly2->PostfixOp|" +
                "Expression3Assembly2->PostfixOp Expression3Assembly2|"
        // PrefixOp : (INCR, DECR, NOT, SQUIGGLE )
        + "PrefixOp->INCR|" +
                "PrefixOp->DECR|" +
                "PrefixOp->NOT|" +
                "PrefixOp->SQUIGGLE|" +
                "PrefixOp->PLUS|" +
                "PrefixOp->MINUS|"
        // PostfixOp
        + "PostfixOp->INCR|" +
                "PostfixOp->DECR|"
        // Primary : (CLASS, THIS, SUPER, NEW, VOID)
        + "Primary->Literal|" +
                "Primary->ParExpression|" +
                "Primary->THIS|" +
                "Primary->THIS Arguments|" +
                "Primary->SUPER SuperSuffix|" +
                "Primary->NEW Creator|" +
                "Primary->NonWildcardTypeArguments ExplicitGenericInvocationSuffix|" +
                "Primary->NonWildcardTypeArguments THIS Arguments|" +
                "Primary->Identifier|" +
                "Primary->Identifier PrimaryAssembly1|" +
                "Primary->Identifier IdentifierSuffix|" +
                "Primary->Identifier PrimaryAssembly1 IdentifierSuffix|" +
                "PrimaryAssembly1->DOT Identifier|" +
                "PrimaryAssembly1->DOT Identifier PrimaryAssembly1|" +
                "Primary->BasicType DOT CLASS|" +
                "Primary->BasicType PrimaryAssembly2 DOT CLASS|" +
                "PrimaryAssembly2->LSQBRACE RSQBRACE|" +
                "PrimaryAssembly2->LSQBRACE RSQBRACE PrimaryAssembly2|" +
                "Primary->VOID DOT CLASS|"


        // Literal
        + "Literal->INT_CONST|" +
                "Literal->PLUS INT_CONST|" +
                "Literal->MINUS INT_CONST|" +
                "Literal->FLOAT_CONST|" +
                "Literal->CHAR_CONST|" +
                "Literal->STRING_CONST|" +
                "Literal->BOOLEAN_CONST|" +
                "Literal->NULL_CONST|"
        // ParExpression
        + "ParExpression->LPAREN Expression RPAREN|"
        // Arguments
        + "Arguments->LPAREN RPAREN|" +
                "Arguments->LPAREN Expression RPAREN|" +
                "Arguments->LPAREN Expression ArgumentsAssembly RPAREN|" +
                "ArgumentsAssembly->COMMA Expression|" +
                "ArgumentsAssembly->COMMA Expression ArgumentsAssembly|"
        // SuperSuffix
        + "SuperSuffix->Arguments|" +
                "SuperSuffix->DOT Identifier|" +
                "SuperSuffix->DOT Identifier Arguments|"
        // ExplicitGenericInvocationSuffix
        + "ExplicitGenericInvocationSuffix->SUPER SuperSuffix|" +
                "ExplicitGenericInvocationSuffix->Identifier Arguments|"

        // Creator
        + "Creator->NonWildcardTypeArguments CreatedName ClassCreatorRest|" +
                "Creator->CreatedName ClassCreatorRest|" +
                "Creator->CreatedName ArrayCreatorRest|"
        //CreatedName
        /** NOTE: I ADDED BasicType IN ADDITION TO Identifier - WAS NOT IN
         * JAVA 7 SPECIFICATION BUT SEEMED NECESSARY FOR PRIMITIVE ARRAY
         * ASSIGNMENT
        **/
        + "CreatedName->Identifier|" +
                "CreatedName->Identifier TypeArgumentsOrDiamond|" +
                "CreatedName->Identifier TypeArgumentsOrDiamond CreatedNameAssembly|" +
                "CreatedName->Identifier CreatedNameAssembly|" +
                "CreatedName->BasicType|" +
                "CreatedNameAssembly->DOT Identifier|" +
                "CreatedNameAssembly->DOT Identifier TypeArgumentsOrDiamond|" +
                "CreatedNameAssembly->DOT Identifier CreatedNameAssembly|" +
                "CreatedNameAssembly->DOT Identifier TypeArgumentsOrDiamond CreatedNameAssembly|"
        // ClassCreatorRest
        + "ClassCreatorRest->Arguments|" +
                "ClassCreatorRest->Arguments ClassBody|"
        // ArrayCreatorRest
        + "ArrayCreatorRest->LSQBRACE RSQBRACE ArrayInitializer|" +
                "ArrayCreatorRest->LSQBRACE RSQBRACE ArrayCreatorRestAssembly1|" +
                "ArrayCreatorRest->LSQBRACE RSQBRACE ArrayCreatorRestAssembly1 ArrayInitializer|" +
                "ArrayCreatorRestAssembly1->LSQBRACE RSQBRACE|" +
                "ArrayCreatorRestAssembly1->LSQBRACE RSQBRACE ArrayCreatorRestAssembly1|" +
                "ArrayCreatorRest->LSQBRACE Expression RSQBRACE|" +
                "ArrayCreatorRest->LSQBRACE Expression RSQBRACE ArrayCreatorRestAssembly2|" +
                "ArrayCreatorRest->LSQBRACE Expression RSQBRACE ArrayCreatorRestAssembly1|" +
                "ArrayCreatorRestAssembly2->LSQBRACE Expression RSQBRACE|" +
                "ArrayCreatorRestAssembly2->LSQBRACE Expression RSQBRACE ArrayCreatorRestAssembly2|"
        // IdentifierSuffix
        + "IdentifierSuffix->LSQBRACE Expression RSQBRACE|" +
                "IdentifierSuffix->LSQBRACE IdentifierSuffixAssembly DOT CLASS LSQBRACE|" +
                "IdentifierSuffixAssembly->LSQBRACE RSQBRACE|" +
                "IdentifierSuffixAssembly->LSQBRACE RSQBRACE IdentifierSuffixAssembly|" +
                "IdentifierSuffix->Arguments|" +
                "IdentifierSuffix->DOT CLASS|" +
                "IdentifierSuffix->DOT ExplicitGenericInvocation|" +
                "IdentifierSuffix->DOT THIS|" +
                "IdentifierSuffix->DOT SUPER Arguments|" +
                "IdentifierSuffix->NEW InnerCreator|" +
                "IdentifierSuffix->NEW NonWildcardTypeArguments InnerCreator|"
        // ExplicitGenericInvocation
        + "ExplicitGenericInvocation->NonWildcardTypeArguments ExplicitGenericInvocationSuffix|"
        // InnerCreator
        + "InnerCreator->Identifier ClassCreatorRest|" +
                "InnerCreator->Identifier NonWildcardTypeArgumentsOrDiamond ClassCreatorRest|"


        // Selector
        + "Selector->DOT Identifier|" +
                "Selector->DOT Identifier Arguments|" +
                "Selector->DOT ExplicitGenericInvocation|" +
                "Selector->DOT THIS|" +
                "Selector->DOT SUPER SuperSuffix|" +
                "Selector->DOT NEW NonWildcardTypeArguments InnerCreator|" +
                "Selector->LSQBRACE Expression RSQBRACE|"
        // EnumBody
        + "EnumBody->LBRACE RBRACE|" +
                "EnumBody->LBRACE EnumConstants RBRACE|" +
                "EnumBody->LBRACE COMMA RBRACE|" +
                "EnumBody->LBRACE EnumBodyDeclarations RBRACE|" +
                "EnumBody->LBRACE EnumConstants COMMA RBRACE|" +
                "EnumBody->LBRACE EnumConstants EnumBodyDeclarations RBRACE|" +
                "EnumBody->LBRACE COMMA EnumBodyDeclarations RBRACE|" +
                "EnumBody->LBRACE EnumConstants COMMA EnumBodyDeclarations RBRACE|"
        // EnumConstants
        + "EnumConstants->EnumConstant|" +
                "EnumConstants->EnumConstants COMMA EnumConstant|"
        // EnumConstant
        + "EnumConstant->Identifier|" +
                "EnumConstant->Annotations Identifier|" +
                "EnumConstant->Identifier Arguments|" +
                "EnumConstant->Identifier ClassBody|" +
                "EnumConstant->Annotations Identifier Arguments|" +
                "EnumConstant->Annotations Identifier ClassBody|" +
                "EnumConstant->Identifier Arguments ClassBody|" +
                "EnumConstant->Annotations Identifier Arguments ClassBody|"
        // EnumBodyDeclarations
        + "EnumBodyDeclarations->SEMI|" +
                "EnumBodyDeclarations->SEMI EnumBodyDeclarationsAssembly|" +
                "EnumBodyDeclarationsAssembly->ClassBodyDeclaration|" +
                "EnumBodyDeclarationsAssembly->ClassBodyDeclaration EnumBodyDeclarationsAssembly|"

        //AnnotationTypeBody
        + "AnnotationTypeBody->LBRACE RBRACE|" +
                "AnnotationTypeBody->LBRACE AnnotationTypeElementDeclarations RBRACE|"
        //AnnotationTypeElementDeclarations
        + "AnnotationTypeElementDeclarations->AnnotationTypeElementDeclaration|" +
                "AnnotationTypeElementDeclarations->AnnotationTypeElementDeclarations " +
                                                    "AnnotationTypeElementDeclaration|"
        //AnnotationTypeElementDeclaration
        + "AnnotationTypeElementDeclaration->AnnotationTypeElementRest|" +
                "AnnotationTypeElementDeclaration->AnnotationTypeElementDeclarationAssembly " +
                                                                "AnnotationTypeElementRest|" +
                "AnnotationTypeElementDeclarationAssembly->Modifier|" +
                "AnnotationTypeElementDeclarationAssembly->Modifier AnnotationTypeElementDeclarationAssembly|"
        //AnnotationTypeElementRest
        + "AnnotationTypeElementRest->Type Identifier AnnotationMethodOrConstantRest|" +
                "AnnotationTypeElementRest->ClassDeclaration|" +
                "AnnotationTypeElementRest->InterfaceDeclaration|" +
                "AnnotationTypeElementRest->EnumDeclaration|" +
                "AnnotationTypeElementRest->AnnotationTypeDeclaration|"
        //AnnotationMethodOrConstantRest : (DEFAULT)
        + "AnnotationMethodOrConstantRest->LPAREN RPAREN|" +
                "AnnotationMethodOrConstantRest->LPAREN RPAREN LSQBRACE RSQBRACE|" +
                "AnnotationMethodOrConstantRest->LPAREN RPAREN DEFAULT ElementValue|" +
                "AnnotationMethodOrConstantRest->LPAREN RPAREN LSQBRACE RSQBRACE DEFAULT ElementValue";

    }

    /** Tests the Java CFG in simple ways
      * @param args command line arguments
     */
    public static void main(String[] args){

        StringBuilder output = new StringBuilder();

        output.append("Evaluating Java Grammar...\n\n");
        AdvancedCFG javaGrammar = JavaGrammar.getJavaGrammar();
        output.append("The Grammar compiled...\n\n");
        //FIXME: write out this test to a file, not print on command line

        output.append("------------------\n\n");
        output.append("Reachable non-terminals:\n\n");

        String[] reachables = javaGrammar.viewReachables();
        if (reachables !=null ){
            List<String> reachableList = Arrays.asList(reachables);
            Collections.sort(reachableList);
            for (String reachable : reachableList )
                output.append("    O: ").append(reachable).append("\n");
        }


        output.append("\nReachables retrieved...\n\n");
        output.append("-----------------\n\n");


        output.append("------------------\n\n");
        output.append("Unreachable non-terminals:\n\n");

        List<String> unreachables = javaGrammar.validate(true);
        for (String unreachable : (unreachables != null ? unreachables : new LinkedList<String>()) )
            output.append("    X: ").append(unreachable).append("\n");


        output.append("\nGrammar Validated...\n\n");
        output.append("-----------------\n\n");

        output.append("Retrieving terminals...\n");
        output.append("Number of terminals: ").append(javaGrammar.viewTerminals().length).append("\n\n");

        List<String> terminals = Arrays.asList(javaGrammar.viewTerminals());
        Collections.sort(terminals);

        for (String terminal : terminals)
            output.append("    T: ").append(terminal).append("\n");

        output.append("\nTerminals Retrieved\n\n");
        output.append("-----------------\n\n");

        output.append("Evaluation Terminated: Code {0}\n\n");

        File outputFile = new File("JavaGrammarOutput.txt");

        IOManager.saveFile(outputFile,output.toString());
        System.out.println("JavaGrammar Evaluation Output successfully saved");

    }

}


//Note on Java Notation (and maybe FIXME add to the CFG): [x] 0 or 1
//                       {x} 0 or more  --- make a List production
//                       (x | y) OR (exclusive)