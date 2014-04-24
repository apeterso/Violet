package Compiler.Parser.CFG;

import Compiler.Parser.Builder.*;
import Compiler.Parser.Matcher.*;
import Compiler.Scanner.LexerToken;
import Compiler.Scanner.LexerToken.*;

import java.util.*;

/**
 * Created by Matt Levine on 3/17/14.
 *
 * <br>This class is designed to offer CFG functionalities. A CFG is immutable.
 *
 * <br>The only nontrivial public methods in the CFG class is "matches"
 * which return true if and only if the given input string is an element of
 * the language described by the CFG.
 *
 * <br>The CFG is <b>extremely</b> flexible. Inputting a language follows (semi) standard
 * input format, e.g
 * S->class B | S -> { class B } | B -> extends
 *
 * <br>Anything on the left side of an arrow is considered a nonterminal; anything
 * with a production rule is considered a nonterminal. Everything else is
 * considered a terminal. Thus, there is no such thing as a malformed CFG;
 * forgetting to add a production rule is the fault of the user (the only exception is if
 * the user claims to define a production but does not include a transition symbol,
 * i.e, write "-" instead of "->").
 *
 * <br>The first nonterminal is considered the root. This is by choice, as it
 * seems to always be the case anyways by convention.
 *
 * <br>It would also be trivial to implement a mutable version of this class
 * where Rules could be queried and dynamically modified, but we're nixing
 * that for now - the immutability is much more appealing.
 *
 * <br>It is recommended that productions that are not intended to be abstract (will not be
 * removed by a Builder) are not self-generative; this will not affect the matching process, but
 * might make life easier down the rode. For example, if we expect Q to be in our final product,
 * it might be more effective to write
 *
 * <br><br>S->QAbstract|QAbstract->Q|QAbstract->Q QAbstract|Q-q
 *
 * <br><br>than the simpler grammar with the same language: "S->Q|Q->Q Q|Q->q"
 *
 * <br>This class has support for epsilon transitions.
 *
 * FIXME: to add: support for custom error messages
 */
public class ContextFreeGrammar {

    protected final HashMap<String,Rule> nonterminals;
    protected final HashSet<String> terminals;
    private final String root;
    //Someone listening can do something when an error is reported by the CFG
//    public static SimpleBooleanProperty errorReported = new SimpleBooleanProperty();

    /** Constructs a new CFG from the given string. Assume proper form for a constructor.
     * @param constructor the CFG string
     */
    public ContextFreeGrammar(String constructor){
        terminals = new HashSet<>();
        nonterminals = new HashMap<>();
        String[] splitConstructor = constructor.split("\\|");
        if (splitConstructor.length == 0 || splitConstructor[0].split("->").length == 0){
            root = null;
            return;
        }

        root = splitConstructor[0].split("->")[0];

        //build a rule for each termable
        for (String rule : splitConstructor){
            addRule(rule);
        }

        //build the CFG
        for (String rule : splitConstructor)
            buildRule(rule);
    }

    /** For internal use in copying CFG grammars **/
    protected ContextFreeGrammar(HashMap<String,Rule> nonterminals,
                                       ContextFreeGrammar parent){
        this.nonterminals = nonterminals;
        this.terminals = parent.terminals;
        this.root = parent.root;
    }

    /** Adds the rule defined by the input string to the CFG
     * @param definition the string representation of the rule
     */
    protected final void addRule(String definition){
        String[] rule = definition.split("->");
        nonterminals.put(rule[0],new Rule(rule[0]));
     }

    /** Builds the rule defined by the input string to the CFG
     * @param definition the string representation of the rule
     */
    protected void buildRule(String definition){
        String[] ruleEncoding = definition.split("->");
        //grab the rule, make somewhere to puts its constituents
        Rule rule = nonterminals.get(ruleEncoding[0]);
        ArrayList<Rule> ruleset = new ArrayList<>();

        //loop through its productions
        if (!(ruleEncoding.length == 2)) throw new RuntimeException("Malformed CFG at rule: "+definition);
        for (String prod : ruleEncoding[1].split(" ")){

            //if known nonterminal
            if (nonterminals.containsKey(prod))
                ruleset.add(nonterminals.get(prod));
            //don't know it: must be a terminal (by definition)
            else{
                ruleset.add(new Rule(prod,new Production(prod)));
                terminals.add(prod);
            }
        }
        //add a production from that ruleset to the rule
        rule.add(new Production(ruleset.toArray(new Rule[ruleset.size()])));
    }

    /** Returns a matcher associated with this CFG defined by
     * the input CFGTokens
     * @param lexerTokens the input stream
     * @return an associated Parser.Matcher
     */
    public Matcher matches(LexerToken[] lexerTokens){
        return new Matcher(this, lexerTokens);
    }

    /** Returns a matcher associated with this CFG defined by
     * the input CFGTokens and passes to it the given builder
     * @param lexerTokens the input stream
     * @param builder a builder to pass to the matcher
     * @return an associated Parser.Matcher
     */
    public Matcher matches(LexerToken[] lexerTokens, Builder builder){
        return new Matcher(this, lexerTokens,builder);
    }

    /** Returns the starting rule
     * @return the starting rule
     */
    public Rule getStartRule(){return nonterminals.get(root);}

    /** Returns null if all non-terminals have a path to the starting non-terminal.
     * Operates in O(n^2) time (?) - only should be used for debugging or once
     * per CFG construction. Is not implicitly called by CFGs. If there are non-terminals
     * that are unreachable, returns them in a List if "debug" is true or returns the
     * a list containing the first discovered unreachable non-terminal if "debug" is false.
     * @param debug If true, returns all unreachable non=terminals
     * @return null if the CFG has no unreachable non-terminals
     */
    public List<String> validate(boolean debug){
        boolean discovered;
        HashSet<Rule> markedFlags = new HashSet<>();
        Stack<Rule> reachableRules = new Stack<>();
        LinkedList<String> unreachables = new LinkedList<>();
        Rule head = nonterminals.get(root);

        for ( Rule r :  nonterminals.values()) {
            if (r.equals(nonterminals.get(root))) continue;
            markedFlags.clear();
            reachableRules.clear();
            reachableRules.add(head);
            markedFlags.add(head);
            discovered = false;

            while (reachableRules.size() > 0 && !discovered){
                //get the next rule and loop through every term it maps to directly
                Rule nextRule = reachableRules.pop();

                for (Production prod : nextRule.productions){
                    for (Termable t : prod){

                        //if we haven't visited, and he's a non-terminal, visit him next
                        if (nonterminals.containsKey(t.toString()) &&
                                !markedFlags.contains((Rule) t)){
                            markedFlags.add((Rule) t);
                            reachableRules.add((Rule)t);
                        }

                        //if we've found, note that
                        if (t.equals(r)) discovered = true;
                    }
                }

            }

            if (!discovered){
                unreachables.add(r.toString());
                if (!debug) return unreachables;
            }


        }

        return unreachables.size() == 0 ? null : unreachables;
    }

    /** Returns a List of all reachable non-terminals
     * @return a list of all reachable non-terminals
     */
    public String[] viewReachables(){

        Rule r = nonterminals.get(root);
        HashSet<String> markedRules = new HashSet<>();
        Stack<Rule> reachableRules = new Stack<>();

        reachableRules.add(r);

        while (reachableRules.size() > 0){
            r = reachableRules.pop();
            markedRules.add(r.toString());
            for (Production prods : r.productions){
                for (Termable t : prods){
                    if (nonterminals.containsKey(t.getName()) && !markedRules.contains(t.toString())){
                        reachableRules.add((Rule) t);
                    }
                }
            }
        }



        return markedRules.toArray(new String[markedRules.size()]);
    }

    public String[] viewTerminals(){ return terminals.toArray(new String[terminals.size()]); }

    /** Test function for CFg
     * @param args command line input
     */
    public static void main(String[] args){
        //Epsilon: String constructor = "MemberList->Field MemberList|MemberList-> |Field->INT_CONST";
        //example of malformed CFG (validate will catch)
        String constructor = "A->B|B->C|C->D|D->E|E->F|F->G|H->I|I->J|J->K";
        ContextFreeGrammar contextFreeGrammar = new ContextFreeGrammar(constructor);

        System.out.println("Valid: "+contextFreeGrammar.validate(true));

        LexerToken[] equation = {new LexerToken(TokenIds.INT_CONST,"3"),
                new LexerToken(TokenIds.INT_CONST,"7"),
                new LexerToken(TokenIds.INT_CONST,"144")};
        ASTBuilder bobTheBuilder = new ASTBuilder();
        Matcher m = contextFreeGrammar.matches(equation,bobTheBuilder);
//        bobTheBuilder.printTree();
    }

}
