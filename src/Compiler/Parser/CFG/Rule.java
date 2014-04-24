package Compiler.Parser.CFG;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A rule represents a Terminal or Non-Terminal component of a Parser.CFG.
 * A rule is immutable (except that its abstraction status can be toggled).
 * Created by Matt Levine on 3/13/14.
 * @author Matt Levine
 */
public final class Rule implements Termable {

    private final String name;
    public final ArrayList<Production> productions;

    /** Construct a new rule from a name id and any number of productions
     * @param name the name id of the rule
     * @param productions any number of productions that this rule should link to
     */
    public Rule(String name, Production... productions){
        this.name = name;
        this.productions = new ArrayList<>(Arrays.asList(productions));
    }

    /**
     * Adds any number of productions to the Rule
     * @param productions any number of productions that this rule should link to
     */
    public void add(Production... productions){
        this.productions.addAll(Arrays.asList(productions));
    }

    /** Returns the name of the Rule
     * @return the name of the rule
     */
    @Override
    public String getName(){return name;}

    /** Returns the name of the Rule
     * @return the name of the rule
     */
    @Override
    public String getValue(){return name;}

    /** Returns a string representation of the rule and its linkages
     * @return a string representation of the rule and its linkages
     */
    @Override
    public String toString(){
//        StringBuilder s = new StringBuilder();
//        for (Production p : productions) s.append(" | ").append(p);
//        return  this.name + " ->" + s.toString();
        return name;
    }

}
