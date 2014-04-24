package Compiler.Parser.Builder;

import Compiler.Parser.Matcher.*;

/**
 * The interface that must be implemented by any classes wanting to use information
 * from a Parser.CFG Parser.Matcher.
 * Created by Matt Levine on 3/13/14.
 */
public interface Builder {
    public Object build(State inputState);
}
