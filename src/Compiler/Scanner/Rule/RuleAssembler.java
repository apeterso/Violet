package Compiler.Scanner.Rule;

import Compiler.Scanner.LexerToken;
import Compiler.Scanner.Rule.MutableSlowSearchRule;
import Compiler.Scanner.Rule.RegexRule;
import Compiler.Scanner.Rule.Rule;
import Compiler.Scanner.Rule.SlowSearchRule;
import javafx.beans.property.SimpleStringProperty;

import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * This class organized Lexing rules.
 * Created by Matt Levine on 3/30/14.
 */
public final class RuleAssembler {

    private static String KEY = "<font color=\"#045672\"><b>";
    private static String COMMENT = "<font color=\"#808080 \">";

    private final static HashSet<LexerToken.TokenIds> usedTokens = new HashSet<>();

    /** Sets the Style property of rule at the given index
     * @param index the position of the rule
     * @return the style property
     */
    public static SimpleStringProperty getStyleOf(int index){
        if (index < 0) index = assembledRules.length + index;
        return assembledRules[index].getStyleProperty();
    }

    /**
     * Sets the style in the given range, throws IndexOutOfBounds exceptions
     * as expected (but accepts negative indeces).
     * @param start the starting index
     * @param end the ending index
     */
    public static void setStyleOfRange(int start, int end, String style){
        if (start < 0 ) start = assembledRules.length + start;
        if (end < 0 ) end = assembledRules.length + end;
        for (int i = start; i < end; i++){
            assembledRules[i].getStyleProperty().set(style);
        }
    }



    // -- NOTE: the ordering of these assembly rules is important - do not change in any way
    // without considering ALL objects that reference this class directly.

    public static final Rule[] assembledRules = {
            //least prioritized up here

            //"Fallbacks" Go Here
            SlowSearchRule.getBlockCommentRule(),
            SlowSearchRule.getUnterminatedBlockCommentRule(),
            MutableSlowSearchRule.getStringRule(),
            MutableSlowSearchRule.getCharacterRule(),
            factoryRegexRule("//.*$", LexerToken.TokenIds.COMMENT,COMMENT),
            factoryRegexRule(".",
                    LexerToken.TokenIds.LEX_ERROR," <u><font color=\"#FF0000\"> "),
            factoryRegexRule("[a-zA-Z][_0-9a-zA-Z]*", LexerToken.TokenIds.ID),

            //Literals Go Here
            MutableSlowSearchRule.getIntegerRule(),
            MutableSlowSearchRule.getFloatRule(),

            //Symbols Go Here
            // -- Single Symbols Go Here
            factoryRegexRule(">", LexerToken.TokenIds.GT," <font color=\"#04A781\"> "),
            factoryRegexRule("^", LexerToken.TokenIds.CARET," <font color=\"#04A781\"> "),
            factoryRegexRule(",", LexerToken.TokenIds.COMMA),
            factoryRegexRule(";", LexerToken.TokenIds.SEMI),
            factoryRegexRule(":", LexerToken.TokenIds.COLON),
            factoryRegexRule("/", LexerToken.TokenIds.DIVIDE," <font color=\"#04A781\"> "),
            factoryRegexRule("@", LexerToken.TokenIds.AROBASE),
            factoryRegexRule("<", LexerToken.TokenIds.LT," <font color=\"#04A781\"> "),
            factoryRegexRule("&", LexerToken.TokenIds.AMP),
            factoryRegexRule("%", LexerToken.TokenIds.MODULUS," <font color=\"#04A781\"> "),
            factoryRegexRule("~", LexerToken.TokenIds.SQUIGGLE," <font color=\"#04A781\"> "),
            factoryRegexRule("-", LexerToken.TokenIds.MINUS," <font color=\"#04A781\"> "),
            factoryRegexRule("\\?", LexerToken.TokenIds.QUESTION),
            factoryRegexRule("\\]", LexerToken.TokenIds.RSQBRACE),
            factoryRegexRule("\\}", LexerToken.TokenIds.RBRACE),
            factoryRegexRule("\\*", LexerToken.TokenIds.TIMES," <font color=\"#04A781\"> "),
            factoryRegexRule("\\{", LexerToken.TokenIds.LBRACE),
            factoryRegexRule("\\.", LexerToken.TokenIds.DOT),
            factoryRegexRule("\\[", LexerToken.TokenIds.LSQBRACE),
            factoryRegexRule("\\+", LexerToken.TokenIds.PLUS," <font color=\"#04A781\"> "),
            factoryRegexRule("\\|", LexerToken.TokenIds.BAR),
            factoryRegexRule("\\(", LexerToken.TokenIds.LPAREN),
            factoryRegexRule("!", LexerToken.TokenIds.NOT," <font color=\"#04A781\"> "),
            // -- Equals Operations (can) Go Here
            factoryRegexRule("<=", LexerToken.TokenIds.LEQ," <font color=\"#04A781\"> "),
            factoryRegexRule("!=", LexerToken.TokenIds.NE," <font color=\"#04A781\"> "),
            factoryRegexRule("==", LexerToken.TokenIds.EQ," <font color=\"#04A781\"> "),
            factoryRegexRule("<<=", LexerToken.TokenIds.DECREQ," <font color=\"#04A781\"> "),
            factoryRegexRule("/=", LexerToken.TokenIds.DIVEQ," <font color=\"#04A781\"> "),
            factoryRegexRule(">=", LexerToken.TokenIds.GEQ," <font color=\"#04A781\"> "),
            factoryRegexRule(">>=", LexerToken.TokenIds.INCREQ," <font color=\"#04A781\"> "),
            factoryRegexRule("-=", LexerToken.TokenIds.MINEQ," <font color=\"#04A781\"> "),
            factoryRegexRule("%=", LexerToken.TokenIds.MODEQ," <font color=\"#04A781\"> "),
            factoryRegexRule("|=", LexerToken.TokenIds.OREQ," <font color=\"#04A781\"> "),
            factoryRegexRule("\\+=", LexerToken.TokenIds.PLSEQ," <font color=\"#04A781\"> "),
            factoryRegexRule("\\*=", LexerToken.TokenIds.TIMEQ," <font color=\"#04A781\"> "),
            factoryRegexRule(">>>=", LexerToken.TokenIds.TRIPEQ," <font color=\"#04A781\"> "), //who uses this???
            factoryRegexRule("=", LexerToken.TokenIds.ASSIGN," <font color=\"#04A781\"> "),
            // -- Back to Non-Equals Symbols
            factoryRegexRule("\\|\\|", LexerToken.TokenIds.OR," <font color=\"#04A781\"> "),
            factoryRegexRule("&&", LexerToken.TokenIds.AND," <font color=\"#04A781\"> "),
            factoryRegexRule("\\)", LexerToken.TokenIds.RPAREN),
            factoryRegexRule("\\+\\+", LexerToken.TokenIds.INCR," <font color=\"#04A781\"> "),
            factoryRegexRule("--", LexerToken.TokenIds.DECR," <font color=\"#04A781\"> "),
            factoryRegexRule("\\s", LexerToken.TokenIds.NULL),
            factoryRegexRule(">>", LexerToken.TokenIds.GTGT," <font color=\"#04A781\"> "),
            factoryRegexRule("<<", LexerToken.TokenIds.LTLT," <font color=\"#04A781\"> "),
            factoryRegexRule(">>>", LexerToken.TokenIds.GTGTGT," <font color=\"#04A781\"> "),

            //Keywords Go Here
            factoryRegexRule("class", LexerToken.TokenIds.CLASS, " <font color=\"#0099FF\"><b> "),
            factoryRegexRule("break", LexerToken.TokenIds.BREAK, KEY),
            factoryRegexRule("error", LexerToken.TokenIds.ERRORID, KEY),
            factoryRegexRule("new", LexerToken.TokenIds.NEW, KEY),
            factoryRegexRule("true|false", LexerToken.TokenIds.BOOLEAN_CONST, KEY),
            factoryRegexRule("return", LexerToken.TokenIds.RETURN, KEY),
            factoryRegexRule("extends", LexerToken.TokenIds.EXTENDS, KEY),
            factoryRegexRule("while", LexerToken.TokenIds.WHILE, KEY),
            factoryRegexRule("else", LexerToken.TokenIds.ELSE, KEY),
            factoryRegexRule("if", LexerToken.TokenIds.IF, KEY),
            factoryRegexRule("instanceof", LexerToken.TokenIds.INSTANCEOF, KEY),
            factoryRegexRule("abstract", LexerToken.TokenIds.ABSTRACT, KEY),
            factoryRegexRule("&=", LexerToken.TokenIds.ANDEQ, KEY),
            factoryRegexRule("assert", LexerToken.TokenIds.ASSERT, KEY),
            factoryRegexRule("case", LexerToken.TokenIds.CASE, KEY),
            factoryRegexRule("catch", LexerToken.TokenIds.CATCH, KEY),
            factoryRegexRule("default", LexerToken.TokenIds.DEFAULT, KEY),
            factoryRegexRule("continue", LexerToken.TokenIds.CONTINUE, KEY),
            factoryRegexRule("for", LexerToken.TokenIds.FOR, KEY),
            factoryRegexRule("do", LexerToken.TokenIds.DO, KEY),
            factoryRegexRule("enum", LexerToken.TokenIds.ENUM, KEY),
            factoryRegexRule("final", LexerToken.TokenIds.FINAL, KEY),
            factoryRegexRule("finally", LexerToken.TokenIds.FINALLY, KEY),
            factoryRegexRule("implements", LexerToken.TokenIds.IMPLEMENTS, KEY),
            factoryRegexRule("import", LexerToken.TokenIds.IMPORT, KEY),
            factoryRegexRule("interface", LexerToken.TokenIds.INTERFACE, KEY),
            factoryRegexRule("native", LexerToken.TokenIds.NATIVE, KEY),
            factoryRegexRule("package", LexerToken.TokenIds.PACKAGE, KEY),
            factoryRegexRule("private", LexerToken.TokenIds.PRIVATE, KEY),
            factoryRegexRule("public", LexerToken.TokenIds.PUBLIC, KEY),
            factoryRegexRule("protected", LexerToken.TokenIds.PROTECTED, KEY),
            factoryRegexRule("static", LexerToken.TokenIds.STATIC, KEY),
            factoryRegexRule("strictfp", LexerToken.TokenIds.STRICTFP, KEY),
            factoryRegexRule("super", LexerToken.TokenIds.SUPER, KEY),
            factoryRegexRule("switch", LexerToken.TokenIds.SWITCH, KEY),
            factoryRegexRule("synchronized", LexerToken.TokenIds.SYNCHRONIZED, KEY),
            factoryRegexRule("this", LexerToken.TokenIds.THIS, KEY),
            factoryRegexRule("throw", LexerToken.TokenIds.THROW, KEY),
            factoryRegexRule("throws", LexerToken.TokenIds.THROWS, KEY),
            factoryRegexRule("transient", LexerToken.TokenIds.TRANSIENT, KEY),
            factoryRegexRule("try", LexerToken.TokenIds.TRY, KEY),
            factoryRegexRule("void", LexerToken.TokenIds.VOID, KEY),
            factoryRegexRule("volatile", LexerToken.TokenIds.VOLATILE, KEY),
            factoryRegexRule("byte", LexerToken.TokenIds.BYTE, KEY),
            factoryRegexRule("short", LexerToken.TokenIds.SHORT, KEY),
            factoryRegexRule("int", LexerToken.TokenIds.INT, KEY),
            factoryRegexRule("char", LexerToken.TokenIds.CHAR, KEY),
            factoryRegexRule("long", LexerToken.TokenIds.LONG, KEY),
            factoryRegexRule("float", LexerToken.TokenIds.FLOAT, KEY),
            factoryRegexRule("double", LexerToken.TokenIds.DOUBLE, KEY),
            factoryRegexRule("boolean", LexerToken.TokenIds.BOOLEAN, KEY),
            factoryRegexRule("null", LexerToken.TokenIds.NULL_CONST, KEY)
            //most prioritized down here
    };

    /** Returns a new RegexRule from the given regex and ID if that ID is not already
     * in use, otherwise throws an exception.
     * @param regex the rule regex
     * @param ids the token id
     * @return a factory generated Rule
     */
    private static RegexRule factoryRegexRule(String regex, LexerToken.TokenIds ids){
        if (!usedTokens.contains(ids)){
            usedTokens.add(ids);
            return new RegexRule(regex,ids);
        }
        throw new RuntimeException("Duplicate Lexer-Rule Declaration for "+ids);
    }

    /** Returns a new RegexRule from the given regex and ID if that ID is not already
     * in use, otherwise throws an exception.
     * @param regex the rule regex
     * @param ids the token id
     * @return the style of the rule
     */
    private static RegexRule factoryRegexRule(String regex, LexerToken.TokenIds ids, String style){
        if (!usedTokens.contains(ids)){
            usedTokens.add(ids);
            return new RegexRule(regex,ids,"",style);
        }
        throw new RuntimeException("Duplicate Lexer-Rule Declaration for "+ids);
    }

    public static void main(String[] args){
        System.out.println("A"+assembledRules[assembledRules.length-51].getTokenId() + " " + assembledRules[assembledRules.length-1].getTokenId());
    }

}
