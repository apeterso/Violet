package GUI.Util;

import Compiler.Scanner.LexerToken;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This class manages search functions.
 * Created by Matt Levine on 3/23/14.
 */
public class SearchManager {

    public static LexerToken lastSelected = null;
    public static LexerToken selectionYield = null;


    /** Returns true if the token was searched for. Could be made much more
     * elagant by using a functionMapProxy.
     * @param token the token to verify
     * @param searchToken the token to verify against
     * @return true if searched for
     */
    public static boolean isSearchedFor(LexerToken token, SearchToken searchToken){
        return !(searchToken == null || searchToken.phrase.equals("")) && searchToken.type.accept(token, searchToken);


        /*switch(searchToken.type){
            //select all
            case ALL:
                if (searchToken.caseSensitive){
                    if (searchToken.wholeWord)
                        return token.getValue().equals(searchToken.phrase);
                    return token.getValue().contains(searchToken.phrase);
                }else{
                    if (searchToken.wholeWord)
                        return token.getValue().toUpperCase().equals(searchToken.phrase.toUpperCase());
                    return token.getValue().toUpperCase().contains(searchToken.phrase.toUpperCase());
                }

            //select just the next one
            case SINGLE:
                String searchable = searchToken.caseSensitive ?
                        searchToken.phrase.toUpperCase() : searchToken.phrase;
                String current = searchToken.caseSensitive ?
                        token.getValue().toUpperCase() : token.getValue();

                if (lastSelected == null ){
                    if ((searchToken.wholeWord && current.equals(searchable)) ||
                            (!searchToken.wholeWord && searchable.contains(current))){
                        lastSelected = token;
                        selectionYield = token;
                        return true;
                    }else if (current.contains(searchable)){
                        lastSelected = token;
                        selectionYield = token;
                        return true;
                    }
                }else if (lastSelected.getLineNum() ==
                        token.getLineNum() && lastSelected.getColNum() ==
                        token.getColNum() ){
                    if ((searchToken.wholeWord && searchable.equals(current)) ||
                            (!searchToken.wholeWord && searchable.contains(current))){
                        lastSelected = null;
                        return false;
                    }
                }

            //select using regexes
            case REGEX:
                try{
                    return (Pattern.compile(searchToken.phrase).matcher(
                            token.getValue()).matches());
                }catch(PatternSyntaxException e) {return false;}

            default:
                return false;
        }*/
    }

    private static boolean findSingleSubroutine(SearchToken searchToken, LexerToken lexerToken, boolean flag){
        if(lastSelected == null && flag) {
            lastSelected = lexerToken;
            selectionYield = lexerToken;
            return true;
        }
        else{
            if(lastSelected != null && lastSelected.getLineNum() == lexerToken.getLineNum() &&
                    lastSelected.getColNum() == lexerToken.getColNum()){
                lastSelected = null;
                return false;
            }
        }
        return false;
    }
    
    public static boolean visitSingle(SearchToken searchToken, LexerToken lexerToken){
        return findSingleSubroutine(searchToken,lexerToken,visitAll(searchToken,lexerToken));
    }

    public static boolean visitSingleReg(SearchToken searchToken, LexerToken lexerToken){
        return findSingleSubroutine(searchToken,lexerToken,visitAllReg(searchToken,lexerToken));
    }

    public static boolean visitSingleCS(SearchToken searchToken, LexerToken lexerToken){
        return findSingleSubroutine(searchToken,lexerToken,visitAllCS(searchToken,lexerToken));
    }

    public static boolean visitSingleWW(SearchToken searchToken, LexerToken lexerToken){
        return findSingleSubroutine(searchToken,lexerToken,visitAllWW(searchToken,lexerToken));
    }

    public static boolean visitSingleCSWW(SearchToken searchToken, LexerToken lexerToken){
        return findSingleSubroutine(searchToken,lexerToken,visitAllCSWW(searchToken,lexerToken));
    }

    public static boolean visitSingleRegCS(SearchToken searchToken, LexerToken lexerToken){
        return findSingleSubroutine(searchToken,lexerToken,visitAllRegCS(searchToken,lexerToken));
    }

    public static boolean visitSingleRegWW(SearchToken searchToken, LexerToken lexerToken){
        return findSingleSubroutine(searchToken,lexerToken,visitAllRegWW(searchToken,lexerToken));
    }

    public static boolean visitSingleRegCSWW(SearchToken searchToken, LexerToken lexerToken){
        return findSingleSubroutine(searchToken,lexerToken,visitAllRegCSWW(searchToken,lexerToken));
    }

    
    
    
    public static boolean visitAll(SearchToken searchToken, LexerToken lexerToken){
        return lexerToken.getValue().toUpperCase().contains(searchToken.phrase.toUpperCase());
    }

    public static boolean visitAllReg(SearchToken searchToken, LexerToken lexerToken){
        try{
            return (Pattern.compile(searchToken.phrase.toLowerCase()).matcher(
                    lexerToken.getValue()).matches());
        }catch(PatternSyntaxException e) {return false;}
    }

    public static boolean visitAllCS(SearchToken searchToken, LexerToken lexerToken){
        return lexerToken.getValue().contains(searchToken.phrase);
    }

    public static boolean visitAllWW(SearchToken searchToken, LexerToken lexerToken){
        return lexerToken.getValue().toUpperCase().equals(searchToken.phrase.toUpperCase());
    }

    public static boolean visitAllCSWW(SearchToken searchToken, LexerToken lexerToken){
        return lexerToken.getValue().equals(searchToken.phrase);
    }

    public static boolean visitAllRegCS(SearchToken searchToken, LexerToken lexerToken){
        return false;
    }

    public static boolean visitAllRegWW(SearchToken searchToken, LexerToken lexerToken){
        return false;
    }

    public static boolean visitAllRegCSWW(SearchToken searchToken, LexerToken lexerToken){
        return false;
    }
    


}
