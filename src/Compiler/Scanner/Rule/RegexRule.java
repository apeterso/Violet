package Compiler.Scanner.Rule;

import Compiler.Scanner.LexerToken;
import Compiler.Scanner.Rule.Rule;
import javafx.beans.property.SimpleStringProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Matt
 * Date: 2/22/14
 * This is a wrapper class for a regex and its id. This class
 * is immutable.
 */
class RegexRule implements Rule {
    private final Pattern regex;
    public final LexerToken.TokenIds tokenId;
    private Matcher matcher;
    private final String message;
    private final SimpleStringProperty style;

    /**
     * Creates a new rule from a regex and token id
     * @param rule the regex
     * @param id the token id
     */
    public RegexRule(String rule, LexerToken.TokenIds id){
        regex = Pattern.compile(rule);
        tokenId = id;
        matcher = null;
        message = "";
        style = new SimpleStringProperty("");
    }

    /**
     * Creates a new rule from a regex and token id with given message
     * @param rule the regex
     * @param id the token id
     * @param tokenMessage the message to be passed on to a token
     */
    public RegexRule(String rule, LexerToken.TokenIds id, String tokenMessage){
        regex = Pattern.compile(rule);
        tokenId = id;
        matcher = null;
        message = tokenMessage;
        style = new SimpleStringProperty("");
    }

    /**
     * Creates a new rule from a regex and token id with given message
     * @param rule the regex
     * @param id the token id
     * @param tokenMessage the message to be passed on to a token
     * @param tokenStyle the style of the token if displayed (in HTML)
     */
    public RegexRule(String rule, LexerToken.TokenIds id, String tokenMessage,
                     String tokenStyle){
        regex = Pattern.compile(rule);
        tokenId = id;
        matcher = null;
        message = tokenMessage;
        style = new SimpleStringProperty(tokenStyle);
    }


    /** Returns true iff the string is contained by the language
     * of the regex
     * @param s The input string
     * @return true if the string is contained by the language of the regex
     */
    public boolean matches(CharSequence s){
        matcher = regex.matcher(s);
        return matcher.matches();
    }

    /** Returns hitEnd called on the regex Pattern.
     *
     * @return hitEnd called on the regex Pattern
     */
    public  boolean hitEnd(){
        if (matcher == null){
            throw new RuntimeException("Regex matcher not instantiated when calling hitEnd");
        }
        return matcher.hitEnd();
    }

    /** Returns the message of the rule or empty string if no message
     * is defined.
     * @return the message in string form
     */
    public String toString(){ return message; }

    /**Returns the token id
     * @return the token id
     */
    @Override
    public LexerToken.TokenIds getTokenId(){ return tokenId; }

    /** Returns the style of the token
     * @return the style of the token
     */
    @Override
    public String getStyle(){return style.getValue();}

    /** Returns the style property of the rule
     * @return the style property of the rule
     */
    public SimpleStringProperty getStyleProperty(){return style;}


}
