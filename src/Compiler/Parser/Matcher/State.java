package Compiler.Parser.Matcher;

import Compiler.Parser.CFG.*;

import java.util.ArrayList;

/**
 * A State is a unit data structure in a ChartRow. A State is
 * weakly mutable; its primary fields cannot be changed, but the
 * dot index and row containers can ge changed.
 * Created by Matt Levine on 3/13/14.
 */
public final class State{

    public final String name;
    public final Production production;
    private final ChartRow start_chartRow;
    private ChartRow end_chartRow;
    private final int dot_index;
    private final ArrayList<Rule> rules;

    /** Constructs a new State
     * @param name the name of the state
     * @param production the rule of the state
     * @param dot_index the dot index of the state, used in most parsing algorithms
     * @param start_chartRow the starting row for the state
     */
    public State(String name, Production production, int dot_index, ChartRow start_chartRow){
        this.name = name;
        this.production = production;
        this.start_chartRow = start_chartRow;
        this.end_chartRow = null;
        this.dot_index = dot_index;

        this.rules = new ArrayList<>();
        for (Termable rule : production)
            if (rule instanceof Rule)
                this.rules.add((Rule) rule);
    }

    /** Returns a hash for the state uniquely defined by the name, production,
     * starting and ending rows, dot-index, and rules. Two States with those
     * fields equal by their respective equivalence definitions are considered
     * equal classes. See: equals.
     * @return hash for the state
     */
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (production != null ? production.hashCode() : 0);
        result = 31 * result + (start_chartRow != null ? start_chartRow.hashCode() : 0);
        result = 31 * result + (end_chartRow != null ? end_chartRow.hashCode() : 0);
        result = 31 * result + dot_index;
        result = 31 * result + (rules.hashCode());
        return result;
    }

    /** Returns true if the given state is equal to this state, defined by the
     * dot index, starting rows, name, and production under their respective
     * equivalence definitions. Follows Java equals specifications. See hash.
     * @param o the object to check equivalence to
     * @return true if the given obejct equals this one
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        return dot_index == state.dot_index && start_chartRow.equals(state.start_chartRow)
                && name.equals(state.name) && production.equals(state.production);

    }

    /** Returns true if the dot of the state has reached the end of the production
     * chain.
     * @return true if the production is complete
     */
    public boolean completed(){
        return dot_index >= production.size();
    }

    /** Returns the next term in the production
     * @return the next term in the production
     */
    public Termable next_term(){
        if (completed()) return null;
        return production.get(dot_index);
    }

    /** Sets the end row of the state
     * @param chartRow the new end row the state
     */
    public void setEnd_chartRow(ChartRow chartRow){
        end_chartRow = chartRow;}

    /** Returns a state identical to this one with an incremented dot-index and given
     * production.
     * @param production the production value for the new state
     * @return an incremented state
     */
    public State getDotIncrementedState(Production production){
        return new State(name,production, dot_index + 1, start_chartRow);
    }

    /** Returns the starting row of the state
      * @return the starting row of the state
     */
    public ChartRow getStart_chartRow(){return start_chartRow;}

    /** Returns a string representation of the state
     * @return a string representation of the state
     */
    public String toString(){
        StringBuilder terms = new StringBuilder();
        int i = 0;
        for (Termable p : production){
            terms.append(p.toString()).append(" ");
            if (i == dot_index) terms.append("$");
            i++;
        }
        return name + "->" + terms;
    }

    /** returns the rules of the state
     * @return the state rules
     */
    public ArrayList<Rule> getRules() {
        return rules;
    }

    /** Returns the ending row of the chart
     * @return the ending row of the chart
     */
    public ChartRow getEnd_chartRow() {
        return end_chartRow;
    }
}
