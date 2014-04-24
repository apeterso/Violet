package Compiler.Parser.CFG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents a linkage between Rules
 * Created by Matt Levine on 3/13/14.
 * @author Matt Levine
 */
public final class Production implements Iterable<Termable> {

    private final ArrayList<Termable> terms;

    /** Produces a new Production from a set of rules
     * @param rules the input rules
     */
    public Production(Termable... rules){
        this.terms = new ArrayList<>(Arrays.asList(rules));
    }

    /** Produces a new Production from a string (token id)
     * @param rule the string token id
     */
    public Production(String rule){
        this.terms = new ArrayList<>();
        terms.add(new CFGToken(rule,"no value",-1,-1));
    }

    /** Returns the number of terms in the production.
     * @return the size of the production
     */
    public int size(){return terms.size();}

    /** Returns the term at the given index.
     * @param index the index of the term
     * @return the Term
     */
    public Termable get(int index){return terms.get(index);}

    /** Returns an iterator of the terms. Can aid in concurrent modification procedures.
     * @return an iterator of terms
     */
    public Iterator<Termable> iterator(){return terms.iterator(); }

    /** Returns true if the Productions are term-wise equal
     * @param o the object to test equivalence against
     * @return true if the terms are equivalent
     */
    @Override
    public boolean equals(Object o){
        if (!(o instanceof Production)) return false;
        Production p = (Production) o;

        for (int i = 0; i < p.size(); i++)
            if (i >= terms.size() || !get(i).equals(p.get(i))) return false;
        return true;
    }

    /** Returns a string representation of the Production
     * @return string representation of the Production
     */
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        for (Termable t : terms) s.append(t.getName()).append(" ");
        return s.length() > 0 ? s.toString().substring(0,s.length()-1) : s.toString();
    }
}
