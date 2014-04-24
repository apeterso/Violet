package Compiler.Parser.CFG;

import Compiler.Parser.Builder.ASTBuilder;
import Compiler.Parser.Builder.Builder;
import Compiler.Parser.Matcher.Matcher;
import Compiler.Scanner.LexerToken;

import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Math.round;

/**
 * The AdvancedCFG offers several advantages of the plain boring CFG.
 * <br> It includes support for automated-assemblies of the notation: A->{a}, which
 * means that the non-terminal <b></b>A goes to 0 or more instances of the expression
 * <b>a</b>, which could be any arbitrary combination of terminals and non-terminals.
 * To use this functionality correctly, the following meta-characters
 * are reserved: <i>.,$,{,},[,]</i>. The square braces indicate 0 or 1 of the enclosed
 * production. Dot and dollar-sign are used internally
 * <br>The AdvancedCFG offers a more important feature too - <b>prioritization</b>.
 * You can manually lower the priority of a production, meaning that it will only
 * be used if all productions of a higher priority are not matched. This is implemented
 * in a very slow way, and should only be used where manipulating the grammar to accomplish
 * the same goal is impossible or extremely inconvenient. See the manual for more detail.
 * <br>For technical reasons, the advanced properties cannot be used together. For example,
 * a single production cannot employ square and regular braces; productions using square
 * or regular braces cannot have their priority successfully lowered. The priority of
 * a production cannot be lowered twice; rather, lower it by a higher magnitude the
 * first time.
 * Created by Matt Levine on 3/31/14.
 */
public final class AdvancedCFG extends ContextFreeGrammar {

    private static final Pattern assembly = Pattern.compile(".*\\{.*\\}.*");
    private static final Pattern optional = Pattern.compile(".*\\[.*\\].*");
    private int factory = 0;
    private HashSet<String> loweredPriorities = new HashSet<>();

    /** Creates a new Advanced Context Free Grammar
     * @param constructor the string definition
     */
    public AdvancedCFG(String constructor){
        super(constructor);
    }


    @Override
    /** Builds the rule defined by the input string to the Parser.CFG
     * @param definition the string representation of the rule
     */
    protected void buildRule(String definition){
        String[] ruleEncoding = definition.split("->");
        //grab the rule, make somewhere to puts its constituents
        Rule rule = nonterminals.get(ruleEncoding[0]);

        //loop through its productions
        if (!(ruleEncoding.length == 2)) throw new RuntimeException("Malformed CFG at rule: "+definition);
        String prodList = ruleEncoding[1];

        //generate optionals*/
        prodList = generate(prodList,ruleEncoding,'[',']',optional);
        //generate assemblies
        prodList = generate(prodList,ruleEncoding,'{','}',assembly);

        super.buildRule(ruleEncoding[0] + "->" + prodList);
    }

    /** Attempts to lower the priority of the rule with the given name and
     * target, i.e a rule of the form <b>ruleName->ruleTarget</b>. Does
     * nothing if fails. This is a very costly operation, and each lowered
     * priority can drastically slow down the cost of calling <i>matches</i>.
     * @param ruleName the left side of the rule
     * @param productionName the right side of the rule
     * @param magnitude the amount by which to lower the priority
     */
    public void lowerPriority(String ruleName, String productionName, int magnitude){
        if (magnitude == 0) return;
        if (loweredPriorities.contains(ruleName))
            throw new RuntimeException(
                    "Advanced CFG Err: cannot re-lower priority for "+ruleName);
        loweredPriorities.add(ruleName);

        //Uber-convoluted but okay
        if (nonterminals.containsKey(ruleName)){
            Rule rule = nonterminals.get(ruleName);
//            int index = -1;
            for (int index = 0; index < rule.productions.size(); index++) {
                if (rule.productions.get(index).toString().contains(productionName)) {
                    Production prod = rule.productions.get(index);
                    String lastProd = productionName;
                    rule.productions.remove(index);

                    for (int d = 0; d < magnitude; d++) {
                        String constructor = productionName + "." + d + "->" + lastProd;
                        super.addRule(constructor);
                        super.buildRule(constructor);
                        lastProd = productionName + "." + d;
                    }
                    super.buildRule(ruleName + "->" +
                                 (" " +prod.toString()+ " ").replaceAll( "[\" \"]"
                                + productionName +
                                 "[\" \"]", " "+productionName +
                                "." + (magnitude - 1)+ " ").trim());

                }
            }
        }

    }

    /** Returns a matcher associated with this CFG defined by
     * the input CFGTokens and passes to it the given builder
     * @param lexerTokens the input stream
     * @param builder a builder to pass to the matcher
     * @return an associated Parser.Matcher
     */
    @Override
    public Matcher matches(LexerToken[] lexerTokens, Builder builder){
        return new Matcher(this,lexerTokens,builder);
    }

    /** Generates the assemblies for an input
     * @param prodList the constructor on which to generate assemblies
     * @param ruleEncoding the current encoding
     * @return the assembled constructor
     */
    private String generate(String prodList, String[] ruleEncoding,
                                     char start, char end, Pattern pattern) {
        if (pattern.matcher(prodList).matches()){
            StringBuilder assembledRule = new StringBuilder(ruleEncoding[1]);
            while (pattern.matcher(assembledRule.toString()).matches()){
                int firstIndexL = assembledRule.indexOf(String.valueOf(start));
                int firstIndexR = scanBrace(assembledRule.toString(), firstIndexL+1,start,end);
                String substring = assembledRule.substring(firstIndexL+1,firstIndexR);

                //remove stuff
                assembledRule.replace(firstIndexL,firstIndexR+1,substring+"$"+factory);

                //generate assembly rule
                nonterminals.put(substring+"$"+factory,new Rule(substring+"$"+factory));
                buildRule(substring + "$" + factory + "->" + substring);
                if (pattern.equals(assembly))
                    buildRule(substring + "$" + factory + "->" + substring + " "
                            + substring + "$" + factory);
                buildRule(substring + "$" + factory + "->" + " ");

                factory++;
            }
            return assembledRule.toString();
        }
        return prodList;

    }

    /** Returns the closing brace for a left brace or -1 if it isn't found **/
    private int scanBrace(String s, int startIndex, char braceTypeOpen,
                          char braceTypeClose){
        int numL = 1;
        for (int i = startIndex; i < s.length(); i++){
            char c = s.charAt(i);
            numL += c == braceTypeOpen ? 1 : c == braceTypeClose ? -1 : 0;
            if (numL == 0) return i;
        }
        return -1;
    }

    public static void main(String[] args){
        AdvancedCFG advancedCFG = new AdvancedCFG("A->{B} {C}|B->[BB]|BB->INT_CONST|C->NEW|A->D|A->E|A->F");
        AdvancedCFG advancedCFG2 = new AdvancedCFG("A->B|B->C|C->D|D->E|E->Q|Q->INT_CONST|A->FF F|F-> |F->INT_CONST|FF-> ");
        advancedCFG2.lowerPriority("A","F",10);

        LexerToken[] equation = {new LexerToken(LexerToken.TokenIds.INT_CONST,"3"),
                new LexerToken(LexerToken.TokenIds.INT_CONST,"7"),
                new LexerToken(LexerToken.TokenIds.INT_CONST,"144"),
                new LexerToken(LexerToken.TokenIds.NEW,"new"),
                new LexerToken(LexerToken.TokenIds.NEW,"new"),
        };
        LexerToken[] equation2 = {new LexerToken(LexerToken.TokenIds.INT_CONST,"3") };
        ASTBuilder bobTheBuilder = new ASTBuilder();
        Matcher m = advancedCFG2.matches(equation2,bobTheBuilder);
        System.out.println(m.matches());
        bobTheBuilder.printTree();

        System.out.println("-------------");

        System.out.println("done advanced cfg testing");
    }

}
