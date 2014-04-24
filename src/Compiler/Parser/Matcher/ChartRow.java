package Compiler.Parser.Matcher;

import Compiler.Parser.CFG.CFGToken;
import Compiler.Scanner.LexerToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;

/**
 * Represents one column in a Chart, a standard State storage unit.
 * Not externally accessible.
 * Created by Matt Levine on 3/13/14.
 * @author Matthew Levine
 */
public class ChartRow implements Iterable<State> {

    private final int index;
    private final CFGToken CFGToken;
    private final ArrayList<State> states;
    private final HashSet<State> unique;

    /** Constructs a new row with a given index and CFGToken definition
     * @param index the index of the row
     * @param lexerToken the token defining the row
     */
    public ChartRow(int index, LexerToken lexerToken){
        this.index = index;
        if (lexerToken != null)
            this.CFGToken = new CFGToken(lexerToken.getIds().toString(),
                lexerToken.getValue(),lexerToken.getLineNum(),lexerToken.getColNum());
        else
            this.CFGToken = null;
        this.states = new ArrayList<>();
        this.unique = new HashSet<>();
    }

    /** Returns the size of the row.
     * @return the size of the row
     */
    public int size(){return states.size();}

    //not working :( Keep getting concurrent mod exceptions even with explicit use of
    //of iterator or switch from ArrayList ot vector
    public ListIterator<State> iterator(){return states.listIterator(); }

    /** Returns the dot State at the given position in the row
     * @param index the position in the row
     * @return the state at the given position
     */
    public State get(int index){return states.get(index);}

    /** Returns the row CFGToken
     * @return the row CFGToken
     */
    public CFGToken getCFGToken(){return CFGToken;}

    /** Adds a new element to the column if it is not already contained therewithin
     * Here we see a fascinating subtlety of java; built-in structure "contains"
     * methods are only guaranteed to call overwritten equivalence methods if the
     * equivalence contract specified in the Java Specs is completely met. Cool!
     * @param state the state being introduced to the column
     * @return operational success (violates return-OR-act principle for convenience)
     */
    public boolean add(State state){
        if (!unique.contains(state)){
            unique.add(state);
            state.setEnd_chartRow(this);
            states.add(state);
            return true;
        }
        return false;
    }

    /** Returns string representation of ChartRow
     * @return string representation of chartrow
     */
    @Override
    public String toString(){
        return ""+index;
    }

}
