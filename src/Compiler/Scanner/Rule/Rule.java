package Compiler.Scanner.Rule;

import Compiler.Scanner.LexerToken;
import javafx.beans.property.SimpleStringProperty;

/**
 * Author: Matt
 * Date: 2/22/14
 * This class is designed to give a toplevel structue for rules
 */
public interface Rule {

    /** Returns true iff the string is contained by the language
     * of the rule
     * @param s The input string
     * @return true if the string is contained by the language of the regex
     */
    public boolean matches(CharSequence s);

    /** Returns hitEnd called on the rule Pattern.
     * @return hitEnd called on the rule Pattern
     */
    public  boolean hitEnd();

    /**Returns the token id
     * @return the token id
     */
    public LexerToken.TokenIds getTokenId();

    /** Returns the style of the Rule (can be empty string if not meaningful).
     * @return the style of the Rule
     */
    public String getStyle();

    /** Returns the style property of the rule
     * @return the style proprety of the rule
     */
    public SimpleStringProperty getStyleProperty();

}
