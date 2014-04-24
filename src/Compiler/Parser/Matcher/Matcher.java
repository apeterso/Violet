package Compiler.Parser.Matcher;

import Compiler.Parser.Builder.*;
import Compiler.Parser.CFG.*;
import Compiler.Scanner.LexerToken;

import java.util.ListIterator;

/**
 * Verifies that an input of tokens is accepted by the given CFG. A Matcher is immutable.
 *
 * The Matcher uses the Earley parsing algorithm; it employs a chart and state system
 * referring to a procedure of predicting, scanning, and completing (Earley, Jay: "An Efficient
 * Context-Fee Parsing Algorithm", 1970). The implementation is largely aided by descriptions
 * from "Parsing Techniques, A Practical Guide," by Grune (2nd ed.,Chapter 7).
 * There's also a decent Wikipedia article on the subject, though not enough to make a
 * parser from.
 *
 * The algorithm is convenient because it accepts all CFGs (non-LK/LR dependent) and is
 * okay with ambiguities. The matcher passes on ALL discovered derivations of a match
 * to a builder, though most builders would probably only require one. The Matcher should
 * not pass on an empty list to a Parser.Builder.Parser.Builder (null indicates failure), but don't count
 * on it.
 *
 * The algorithm should operate in O(n^3) time to the number of CFG rules and linearly
 * to the number of tokens. I say should because constructing the thing was tough enough
 * make without analyzing it in detail; my conclusion is kind of haphazardous. We've
 * dropped the look-ahead that Earley originally included, but I think that's archaic.
 * The time complexity should reduce to linear across both parameters when the grammar
 * approaches LK/LR, though my implementation favors readability far more than
 * efficiency (it is pretty though, right?).
 *
 *
 * Created by Matt Levine on 3/13/14.
 * @author Matt Levine
 */
public final class Matcher {

    //we use some iterators in this class where for-loops
    //would be far more concise (and legible) because the
    //arraylists were suffering from concurrent modification woes

    //this could probably all be static, but I like how it parallels with the Regex
    //Matcher; if we wanted to check if it matches more than once, this might
    //make that more efficient (though not really)

    private final boolean matches;
    private ChartRow lastRow;
    private CFGToken badToken = null;

    /** Constructs a new Matcher from a set of CFGTokens and an input rule and
     * attempts to derive the CFGTokens from the rule
     * @param cfg a Context Free Grammar
     * @param lexerTokens set of input
     */
    public Matcher(ContextFreeGrammar cfg, LexerToken[] lexerTokens){
        matches = parse(cfg.getStartRule(), lexerTokens) != null;
    }

    /** Constructs a new Matcher from a set of CFGTokens and an input rule and
     * attempts to derive the CFGTokens from the rule. Passes on the output
     * to the given Parser.Builder.Parser.Builder, or null if the derivation fails.
     * @param cfg a Context Free Grammar
     * @param lexerTokens set of input
     * @param builder a Parser.Builder.Parser.Builder object to operate on the output
     */
    public Matcher (ContextFreeGrammar cfg, LexerToken[] lexerTokens, Builder builder){
        State state = parse(cfg.getStartRule(), lexerTokens);
        if (state == null){
            matches = false;
            return;
        }
        matches = true;
        builder.build(state);
    }

    /** Returns true if and only if the derivation was matched
     * @return true if the derivation was matched
     */
    public boolean matches(){return matches;}

    /** Executes Earley prediction. Fires when a non-complete non-terminal is
     * intercepted and fills the chartRow with possible production paths.
     * @param chartRow the prediction Matcher.Chart chartRow
     * @param rule the current prediction rule
     */
    private void predict(ChartRow chartRow, Rule rule){
        for (int i = 0; i < rule.productions.size(); i++){
            Production prod = rule.productions.get(i);
            chartRow.add(new State(rule.getName(),prod,0, chartRow));
        }
    }

    /** Executes Earley scanning. Fires when a non-complete terminal
     * is intercepted ands the terminal to each state in the chartRow
     * if not already contained.
     * @param chartRow the scanning chartRow
     * @param state the scanning state
     * @param token the terminal token
     */
    private void scan(ChartRow chartRow, State state, Termable token){
        if (!token.equals(chartRow.getCFGToken())) return;
        chartRow.add(state.getDotIncrementedState(new Production(chartRow.getCFGToken())));
        lastRow = chartRow;
    }

    /** Executes Earley completion. Fires when a completed state
     * is discovered. Increments the dot of the state and adds
     * it to the chart chartRow.
     * @param chartRow the chart chartRow
     * @param state the completion state
     */
    private void complete(ChartRow chartRow, State state){
        if (!state.completed()) return;
        for (int i = 0; i < state.getStart_chartRow().size(); i++ ){
            State st = state.getStart_chartRow().get(i);
            Termable term = st.next_term();

            if (! (term instanceof Rule) ) continue;
            if (term.getName().equals(state.name)){
                chartRow.add(st.getDotIncrementedState(st.production));
            }
        }
    }

    /** Attempts to derive the given CFGTokens from the rule using the
     * Earley parsing algorithm.
     * @param rule the starting rule
     * @param lexerTokens the list of lexerTokens
     * @return a state representing a completed tree or null in case of failure
     */
    private State parse( Rule rule, LexerToken[] lexerTokens){
        //construct chart
        Chart table = new Chart(lexerTokens);
        table.get(0).add(new State("GAMMA_RULE",new Production(rule),0,table.get(0)));
        lastRow = table.get(0);

        for ( int i = 0; i < table.size(); i++ ){
            ChartRow chartRow = table.get(i);

            for ( int j = 0; j < chartRow.size(); j++ ){
                State state = chartRow.get(j);

                if (state.completed()){
                    complete(chartRow,state);
                }
                else{
                    Termable term = state.next_term();
                    //polymorphism here?
                    if (term instanceof Rule)
                        predict(chartRow, (Rule) term);
                    else if ( i + 1 < table.size() )
                        scan(table.get(i+1),state, term);
                }
            }
        }

        //validate we've returned the pivot properly

        for (State state : table.get(table.size() - 1)) {
            if (state.name.equals("GAMMA_RULE") && state.completed())
                return state;
        }

        //figure out the bad token
        int indexOfBadToken = table.indexOf(lastRow)+1;
        if (indexOfBadToken < table.size())
            badToken = table.get(indexOfBadToken).getCFGToken();

        return null;
    }

    /** Returns the "bad" token, or the first token that the Parser couldn't match.
     * Returns null if there was no such token
     * @return the bad token
     */
    public CFGToken getBadToken(){return badToken;}


}
