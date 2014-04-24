package Compiler.Parser.CFG;

/**
 * A term in a state or production must be a Termable.
 * Created by Matt Levine on 3/13/14.
 */
public interface Termable {
    public String getName();
    public String getValue();
}
