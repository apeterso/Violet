package Compiler.Scanner.Rule;

import Compiler.Scanner.LexerToken;
import javafx.beans.property.SimpleStringProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Matt
 * Date: 2/23/14
 * This class is designed to give alternative implementation for rules when
 * regular expressions are difficult to construct. Extending classes have
 * mutable token ids, which may lead to less elegant or less efficient code;
 * the RegexRule class is preferable. The class includes factory methods
 * for common child classes.
**/
abstract class MutableSlowSearchRule implements Rule {
    String lastString = null; //for anonymous inners
    String message = ""; //for anonymous inners
    private LexerToken.TokenIds tokenId;

    /**Returns the token id
     * @return the token id
     */
    @Override
    public LexerToken.TokenIds getTokenId(){ return tokenId; }

    /** Used by inners to change id when necessary
     * @param newTokenId the new TokenId
     */
    void setTokenId( LexerToken.TokenIds newTokenId ){ tokenId = newTokenId; }

    /** Returns a Rule for recognizing strings
     * @return a Rule for recognizing string literals
     */
    public static Rule getStringRule(){
        return new MutableSlowSearchRule() {
            final Pattern illegalEscapePatter = Pattern.compile(".*\\\\[^tbnrf\'\"\\\\].*");
            private final SimpleStringProperty style = new SimpleStringProperty("#055212");
            private String actualStyle = " <font color=\"#055212\"> ";

            @Override
            public boolean matches(CharSequence s) {
               lastString = s.toString();
                if (!lastString.startsWith("\"")) return false;
                if (lastString.length() == 1 || lastString.endsWith("\\\"")){
                    setTokenId(LexerToken.TokenIds.LEX_ERROR);
                    message = "EOF while scanning string literal";
                    actualStyle = " <u><font color=\"#FF0000\"> ";
                    return true;
                }
                if (lastString.contains("\n")){
                    setTokenId(LexerToken.TokenIds.LEX_ERROR);
                    message = "Unsupported multi-line string";
                    actualStyle = " <u><font color=\"#FF0000\"> ";
                    return true;
                }if (illegalEscapePatter.matcher(s).matches()){
                    setTokenId(LexerToken.TokenIds.LEX_ERROR);
                    message = "Illegal escape character in string";
                    actualStyle = " <u><font color=\"#FF0000\"> ";
                    return true;
                }if (lastString.endsWith("\"") && lastString.length() > 1 &&
                        lastString.split("\"",-1).length - 1 == 2 ){
                    setTokenId(LexerToken.TokenIds.STRING_CONST);
                    message = "";
                    actualStyle = "<font color=\""+style.getValue()+"\"> ";
                    return true;
                }
                return false;
            }

            @Override
            public boolean hitEnd() {
                return lastString.startsWith("\"") &&
                        lastString.split("\"", -1).length - 1 == 1;
            }

            @Override public String toString(){return message;}

            @Override public String getStyle(){return actualStyle;}
            @Override public SimpleStringProperty getStyleProperty(){return style;}

        };
    }

    /** Returns a Rule for recognizing characteres
     * @return a Rule for recognizing character literals
     */
    public static Rule getCharacterRule(){
        return new MutableSlowSearchRule() {
            final Pattern illegalEscapePatter = Pattern.compile(".*\\\\[^tbnrf\'\'\\\\].*");
            private final SimpleStringProperty style = new SimpleStringProperty("");

            @Override
            public boolean matches(CharSequence s) {
                lastString = s.toString();
                if (!lastString.startsWith("\'")) return false;
                if (lastString.length() == 1 || lastString.endsWith("\\\'")){
                    setTokenId(LexerToken.TokenIds.LEX_ERROR);
                    message = "EOF while scanning string literal";
                    style.set(" <u><font color=\"#FF0000\"> ");
                    return true;
                }
                if (lastString.contains("\n")){
                    setTokenId(LexerToken.TokenIds.LEX_ERROR);
                    message = "Unsupported multi-line character";
                    style.set(" <u><font color=\"#FF0000\"> ");
                    return true;
                }if (illegalEscapePatter.matcher(s).matches()){
                    setTokenId(LexerToken.TokenIds.LEX_ERROR);
                    message = "Illegal escape character in string";
                    style.set(" <u><font color=\"#FF0000\"> ");
                    return true;
                }
                if (lastString.indexOf('\'',1) != -1){
                    String lastRelevantString = lastString.substring(1,lastString.indexOf('\'',1));
                    if (lastRelevantString.length() == 0 || lastRelevantString.length() > 2 ||
                        (!lastRelevantString.contains("\\") && lastRelevantString.length() > 1)){
                            setTokenId(LexerToken.TokenIds.LEX_ERROR);
                            message = "Character literal too long";
                            style.set(" <u><font color=\"#FF0000\"> ");
                            return true;
                    }
                }if (lastString.endsWith("\'") && lastString.length() > 1 &&
                        lastString.split("\'",-1).length - 1 == 2 ){
                    setTokenId(LexerToken.TokenIds.CHAR_CONST);
                    message = "";
                    style.set(" <font color=\"#800000\"> ");
                    return true;
                }
                return false;
            }

            @Override
            public boolean hitEnd() {
                return lastString.startsWith("\'") &&
                        lastString.split("\'", -1).length - 1 == 1;
            }

            @Override
            public String toString(){
                return message;
            }

            @Override public String getStyle(){return style.getValue();}
            @Override public SimpleStringProperty getStyleProperty(){return style;}
        };
    }

    /** Returns a Rule for integer values. Doesn't include underscores... because
     * because those are stupid (just kidding: FIXME).
     * @return returns a Rule for integer literals
     */
    public static Rule getIntegerRule(){
            return new MutableSlowSearchRule(){
                Matcher matcher = null;
                final private Pattern regex = Pattern.compile(
                        "(([0-9]+[lL]?)|(0x[0-9a-fA-F]+)|(0b[01]+))");
                //+- handled by parser

                @Override
                public boolean hitEnd(){
                    boolean result = matcher.hitEnd();
                    if (result) checkOverflow();
                    return result;
                }

                @Override
                public boolean matches(CharSequence charSequence){
                    lastString = charSequence.toString();
                    matcher = regex.matcher(charSequence);
                    boolean result = matcher.matches();
                    if (result) checkOverflow();
                    return result;
                }

                /**Checks for overflow in the integer, changes token type if found **/
                private void checkOverflow(){
                    try{
                        Integer.parseInt(lastString);
                        setTokenId(LexerToken.TokenIds.INT_CONST);
                        message = "";
                    }catch (NumberFormatException nfs){
                        setTokenId(LexerToken.TokenIds.LEX_ERROR);
                        message = "Integer " + lastString + " too large";
                    }

                }

                @Override
                public String toString(){
                    return message;
                }

                private final SimpleStringProperty style = new SimpleStringProperty(" <font color=\"#9EA80F\"> ");
                @Override public String getStyle(){return style.getValue();}
                @Override public SimpleStringProperty getStyleProperty(){return style;}
            };
    }

    /** Returns a Rule for floating point values. Doesn't include underscores... because
     * because those are stupid (just kidding: FIXME).
     * @return returns a Rule for floating point literals
     */
    public static Rule getFloatRule(){
        return new MutableSlowSearchRule(){
            Matcher matcher = null;
            final private Pattern regex = Pattern.compile(
                    "([0-9]+[\\.eE][0-9]*|\\.[0-9]+)[FfDd]?");
            private final SimpleStringProperty style =
                    new SimpleStringProperty(" <font color=\"#9EA80F\"> ");

            @Override
            public boolean hitEnd(){
                boolean result = matcher.hitEnd();
                if (result) checkOverflow();
                return result;
            }

            @Override
            public boolean matches(CharSequence charSequence){
                lastString = charSequence.toString();
                matcher = regex.matcher(charSequence);
                boolean result = matcher.matches();
                if (result) checkOverflow();
                return result;
            }

            /**Checks for overflow in the integer, changes token type if found **/
            private void checkOverflow(){
                try{
                    Float.parseFloat(lastString);
                    setTokenId(LexerToken.TokenIds.FLOAT_CONST);
                    message = "";
                }catch (NumberFormatException nfs){
                    setTokenId(LexerToken.TokenIds.LEX_ERROR);
                    message = "Float " + lastString + " too large";
                }

            }

            @Override
            public String toString(){
                return message;
            }

            @Override public String getStyle(){return style.getValue();}
            @Override public SimpleStringProperty getStyleProperty(){return style;}
        };
    }
}
