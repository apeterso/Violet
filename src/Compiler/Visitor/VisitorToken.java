package Compiler.Visitor;

/**
 * This class encapsulates information returned by a Visitor
 * traversal. These tokens are immutable.
 * Created by Matt Levine on 4/7/14.
 */
public final class VisitorToken {
    public final int lineNumber;
    public final String message;

    /** Creates a new VisitorToken
     * @param lineNumber the line number
     * @param message the message
     */
    public VisitorToken(int lineNumber, String message){
        this.lineNumber = lineNumber;
        this.message = message;
    }
}
