package Compiler.Parser.Matcher;

import Compiler.Parser.CFG.*;
import Compiler.Scanner.LexerToken;

import java.util.ArrayList;

/**
 * An ArrayList-based Chart data structure, as specified by the Early algorithm.
 *
 * Chart-based algorithms are a standard, efficient alternative to top-down parsers and
 * parser-generators.
 * Created by Matt Levine on 3/13/14.
 */
final class Chart extends ArrayList<ChartRow>{

    /** Constructs a new Chart from an input of CFGTokens
     * @param lexerTokens the input for the chart
     */
    public Chart(LexerToken[] lexerTokens){
        super();

        //add null state
        add(new ChartRow(0,null));

        //add CFGTokens
        for (int i = 0; i < lexerTokens.length; i++){
            add( new ChartRow(i+1, lexerTokens[i]) );
        }
    }

}
