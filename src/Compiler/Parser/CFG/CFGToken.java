package Compiler.Parser.CFG;

/** A token is the unit of terminals. It is immutable.
 * It is not fundamentally necessary to separate LexerTokens from CFGTokens. In fact,
 * the two are extremely similar and there isn't an inheritance conflict. The CFGToken
 * is more flexible, dropping the enumerated type requirement of the LexerToken.
 * This decision was made to preserve the flexibility of the CFG class without
 * broadening the Lexer, whose functionality is pretty well-formed. It also makes some
 * sense to separate the tokens used in the different phases of compilation - there is
 * again a separate structure for ParserTree nodes
 * Created by Matt Levine on 3/13/14.
 */
public class CFGToken implements Termable {
    private final String name;
    private final int lineNum;
    private final int colNum;
    private final String value;

    /** Constructs a new CFGToken
     * @param constructor the name of the token
     * @param value the value of the token
     * @param lineNum the line number associated with the token value
     * @param colNum the column number associated with the token value
     */
    public CFGToken(String constructor, String value, int lineNum, int colNum){
        name = constructor;
        this.value = value;
        this.lineNum = lineNum;
        this.colNum = colNum;
    }

    /** Returns true if the token name matches the token name of
     * the given token.
     * @param t the given token
     * @return true if the two are equal
     */
    public boolean equals(Object t){
        return t instanceof CFGToken && ((CFGToken)t).name.equals(name);
    }

    /** Returns the name of the token
     * @return the name of the token
     */
    @Override
    public String getName(){return name;}

    /** String representation of the token
     * @return a string representation of the terminal
     */
    @Override
    public String toString(){return "{"+name+" : "+value+"}";}

    /** Returns the line number associated with the token data
     * @return token line number
     */
    public int getLineNum() {return lineNum;}

    /** Returns the column number associated with the token data
     * @return token column number
     */
    public int getColNum() {return colNum;}

    /**Returns the token value
     * @return the token value
     */
    public String getValue(){return value;}
}
