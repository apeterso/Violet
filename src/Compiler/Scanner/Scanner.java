package Compiler.Scanner;


import Compiler.Scanner.Rule.*;

/**
 * Author: Matt
 * Date: 2/20/14
 * This class is designed to do the scanning of a file as part of standard  A Scanner is
 * immutable - the input text cannot be modified.
 * procedure.
 */
public class Scanner {

    //in reverse order of priority, table of rules
    private static final Rule[] regexes = RuleAssembler.assembledRules;

    private final String input_string;
    private int position;
    private final Rule[] possiblePatterns = new Rule[regexes.length];
    final private StringBuilder curToken;
    final private boolean includeWhiteSpace;

    private int curLineNum;
    private int curColNum;

    /** Creates the scanner out of an input text
     * @param text the input for the scanner
     */
    public Scanner( String text ){
        input_string = text;
        curToken = new StringBuilder();
        includeWhiteSpace = false;
        curColNum = position = 0;
        curLineNum = 1;
    }

    /** Creates the scanner out of an input text
     * @param text the input for the scanner
     * @param giveWhitespace if true, return whitespace "NULL" tokens
     */
    public Scanner( String text, boolean giveWhitespace ){
        input_string = text;
        curColNum = position = 0;
        curLineNum = 1;
        curToken = new StringBuilder();
        includeWhiteSpace = giveWhitespace;
    }


    /**
     * Returns the next token in the sequence or EOF token if there
     * are not more tokens in the file
     * @return The next token in the sequence or EOF token if there are none remaining
     */
    public LexerToken getNextToken(){
        int numPossiblePatterns = 0;
        int resultIndex = -1;
        boolean matches;

        do {
            //end of file
            if (position >= input_string.length()){
                if (curToken.length() == 0)
                    return new LexerToken(LexerToken.TokenIds.EOF,curToken.toString(),
                            "",-1,-1);
                else{
                    return grabToken(resultIndex);
                }
            }

            //get next character
            lookAhead1();

            //loop through possible regexes
            int i = 0;
            int tempNumPossiblePatterns = 0;
            for ( Rule rule : numPossiblePatterns == 0 ? regexes : possiblePatterns ){
                //if we find a matching token or don't
                if (rule == null) possiblePatterns[i] = null;
                else{
                    matches = rule.matches(curToken);
                    //if it matches, or it might match, count it
                    if ( matches  || rule.hitEnd() ){
                        possiblePatterns[i] = rule;
                        tempNumPossiblePatterns++;
                        //we only return it if it actually matches completely
                        if (matches)
                            resultIndex = i;
                    }else
                        possiblePatterns[i] = null;
                }

                i++;
            }
            numPossiblePatterns = tempNumPossiblePatterns;

            if (numPossiblePatterns == 0){
                curToken.delete(curToken.length() - 1, curToken.length());
                position--;
                return grabToken(resultIndex);
            }
        }while (numPossiblePatterns > 0);

        return grabToken(resultIndex);
    }

    /** Adds one more character to the current token stream **/
    private void lookAhead1(){
        curToken.append( input_string.charAt(position) );
        position++;

    }

    /** Returns the token associated with a rule at a given index
     * @param resultIndex the index of the token rule
     * @return the new token
     */
    private LexerToken grabToken( int resultIndex ){
        String tokenString = curToken.toString();
        curToken.delete(0,curToken.length());
        possiblePatterns[resultIndex] = null;
        LexerToken.TokenIds id = regexes[resultIndex].getTokenId();
        String style = regexes[resultIndex].getStyle();

        //figure out where we are (this is quite slow - only keep bc tokens are short)
        int numNewlines = (tokenString.length() - tokenString.replace("\n", "").length());
        if (numNewlines != 0){
            curLineNum += numNewlines;
            curColNum = tokenString.length() - tokenString.lastIndexOf("\n");
        }else{
            curColNum += tokenString.length();
        }

        return (id != LexerToken.TokenIds.NULL && id != LexerToken.TokenIds.COMMENT) ||
            includeWhiteSpace ? new LexerToken(id,tokenString,
                        regexes[resultIndex].toString(),curLineNum,curColNum,style) : getNextToken();
    }

    /**Tests the scanenr **/
    public static void main(String[] args){
        String program = "\"jknkjnas\\kaknjajs\" \"";

        Scanner scanner = new Scanner(program,true);
        LexerToken t;
        while ((t = scanner.getNextToken()).getIds() != LexerToken.TokenIds.EOF)
            System.out.println(t.getValue() + " " + t.getIds() + " " + t.getMessage());
    }

}
