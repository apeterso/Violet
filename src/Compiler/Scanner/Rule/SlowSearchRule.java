package Compiler.Scanner.Rule;

import Compiler.Scanner.LexerToken;
import javafx.beans.property.SimpleStringProperty;

/**
 * Author: Matt
 * Date: 2/22/14
 * This class is designed to give alternative implementation for rules when
 * regular expressions are difficult to construct. Extending classes have
 * immutable token ids. The RegexRule class is preferable. The class
 * includes factory methods for common child classes.
 */
abstract class SlowSearchRule implements Rule {

    String lastString = null; //for anonymous inners
    public final LexerToken.TokenIds tokenId;
    private static Rule blockCommentRule = null;
    private static Rule uBlockCommentRule = null;

    /** Creates a new SlowSearchRule; not generally accessible
     * @param id the TokenId for the rule
     */
    private SlowSearchRule(LexerToken.TokenIds id){
        tokenId = id;
    }

    /**Returns the token id
     * @return the token id
     */
    @Override
    public LexerToken.TokenIds getTokenId(){ return tokenId; }

    public static Rule getBlockCommentRule(){
        if (blockCommentRule == null)
            blockCommentRule = new SlowSearchRule(LexerToken.TokenIds.COMMENT){
            @Override
            public boolean hitEnd(){
                if (lastString == null)
                    throw new RuntimeException("Match not initialized");
                //still uses regexes, more indirectly
                boolean startsRight = lastString.length() <= 1 ?
                        lastString.startsWith("/") : lastString.startsWith("/*");
                return startsRight && lastString.split("\\*/",-1).length-1 == 0;
            }

            @Override
            public boolean matches(CharSequence charSequence){
                lastString = charSequence.toString();
                return lastString.startsWith("/*") && lastString.endsWith("*/");
            }

            @Override
            public String toString(){
                return "";
            }

            private final SimpleStringProperty style = new SimpleStringProperty("");

            @Override public String getStyle(){
                if (lastString.startsWith("/**"))
                    style.set(" <font color=\"#00FFCC \"> ");
                else style.set("<font color=\"#808080 \">");
                return style.getValue();
            }

            @Override public SimpleStringProperty getStyleProperty(){
                if (lastString.startsWith("/**"))
                    style.set(" <font color=\"#00FFCC \"> ");
                else style.set("<font color=\"#808080 \">");
                return style;
            }
        };
        return blockCommentRule;
    }

    public static Rule getUnterminatedBlockCommentRule(){
        if (uBlockCommentRule == null)
            uBlockCommentRule = new SlowSearchRule(LexerToken.TokenIds.LEX_ERROR){
                @Override
                public boolean hitEnd(){
                    if (lastString == null)
                        throw new RuntimeException("Match not initialized");
                    //still uses regexes, more indirectly
                    boolean startsRight = lastString.length() <= 1 ?
                            lastString.startsWith("/") : lastString.startsWith("/*");
                    return startsRight && lastString.split("\\*/",-1).length-1 == 0;
                }

                @Override
                public boolean matches(CharSequence charSequence){
                    lastString = charSequence.toString();
                    return lastString.startsWith("/*") && !lastString.contains("*/");
                }

                @Override
                public String toString(){
                    return "Unterminated block comment";
                }

                private final SimpleStringProperty style =
                        new SimpleStringProperty(" <u><font color=\"#FF0000\"> ");
                @Override public String getStyle(){
                    return style.getValue();
                }
                @Override public SimpleStringProperty getStyleProperty(){
                    return style;
                }
            };
        return uBlockCommentRule;
    }



}
