package Compiler.Scanner;

/**
 * Author: Matt
 * Date: 2/20/14
 * This class is designed to store scanner information. It is immutable containing an id,
 * text-value, and optional message. Some tokens are not meant to be passed to the
 * parser, like comments, but colorization makes use of them.
 *
 */
public final class LexerToken {

    public static enum TokenIds {
        //Just for Bantam Java
        AND, ASSIGN, AROBASE, BREAK, COMMENT, BOOLEAN_CONST, CLASS,
        DECR, DIVIDE, DOT, EQ, EOF, ELSE, EXTENDS, ERRORID, FOR, GEQ, GT, LEX_ERROR,
        LSQBRACE, ID, IF, LBRACE, LT, MINUS, MODULUS, NE, NEW, NOT, NULL, OR, INCR,
        INSTANCEOF, INT_CONST, PLUS, RBRACE, RETURN, RPAREN, RSQBRACE, SEMI,
        STRING_CONST, TIMES, WHILE, COMMA, LEQ, LPAREN,

        //Included for full Java
        ABSTRACT, AMP, ANDEQ, ASSERT, BAR, CARET, CASE, CATCH, CHAR_CONST,
        COLON, CONTINUE, DECREQ, DEFAULT, DIVEQ, DO, ENUM, FINAL, FINALLY,
        FLOAT_CONST, GTGT, GTGTGT,  IMPLEMENTS, IMPORT, INCREQ, INTERFACE,
        LTLT, MINEQ, MODEQ, NATIVE, OREQ, PACKAGE, PLSEQ, PRIVATE, PROTECTED,
        PUBLIC, QUESTION, SQUIGGLE, STATIC, STRICTFP, SUPER, SWITCH,
        SYNCHRONIZED, THIS, THROW, THROWS, TIMEQ, TRANSIENT, TRIPEQ, TRY, VOID, VOLATILE,
        BYTE, SHORT, CHAR, INT, LONG, FLOAT, DOUBLE, BOOLEAN, NULL_CONST
    }



    private final TokenIds ids;
    private final String text;
    private final String data;
    private final int lineNum;
    private final int colNum;
    public final String style;


    /** Construct a new CFGToken from a given TokenId **/
    public LexerToken(TokenIds tokenIds, String value){
        ids = tokenIds;
        data = value;
        text = style = "";
        lineNum = colNum = 0;
    }

    /** Construct a new CFGToken from a given TokenId and message **/
    public LexerToken(TokenIds tokenIds, String value, String message){
        ids = tokenIds;
        data = value;
        text = message;
        lineNum = colNum = 0;
        style = "";
    }

    /** Construct a new CFGToken from a given TokenId and message and line number**/
    public LexerToken(TokenIds tokenIds, String value, String message,
                      int lineNum, int colNum){
        ids = tokenIds;
        data = value;
        text = message;
        this.lineNum = lineNum;
        this.colNum = colNum;
        style = "";
    }

    /** Construct a new CFGToken from a given TokenId and message and line number
     * and style. **/
    public LexerToken(TokenIds tokenIds, String value, String message,
                      int lineNum, int colNum, String style){
        ids = tokenIds;
        data = value;
        text = message;
        this.lineNum = lineNum;
        this.colNum = colNum;
        this.style = style;
    }

    /**Returns the id of the CFGToken **/
    public TokenIds getIds(){return ids;}

    /**Returns the value of the CFGToken**/
    public String getValue(){return data;}

    /**Returns the message of the token**/
    public String getMessage(){return  text;}

    /**Returns the line number of the token**/
    public int getLineNum(){return lineNum;}

    /**Returns the column number of the token**/
    public int getColNum(){return colNum;}


    /**Returns a string form of the CFGToken id
     * @return string form of CFGToken id
     */
    public String toString(){return ids.toString();}

    /** Returns true if the tokens have the same id
     * @param o the object to which to compare this object
     * @return true if the tokens have the same id
     */
    @Override
     public boolean equals(Object o){
        return o instanceof LexerToken && ((LexerToken) o).getIds().equals(ids);
    }

}
