package Compiler.Visitor;

import Compiler.Nodes.ASTNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This is the parental Visitor class.
 *
 * <br>The defaultVisit method can be overwritten for a preferable default activities.
 * Created by Matt Levine on 4/1/14.
 */
public abstract class Visitor {

    private final ObservableList<VisitorToken> outcomes =
            FXCollections.observableArrayList();

    /** Returns the outcomes of the visit. This might be an empty list, or a list as
     * used by the particular visitor. Because JavaFX only allows a Stage to be
     * altered from the primary thread, and Visitors are (usually) run on
     * alternate threads for efficiency, the primary thread watches this list
     * to determine if there are any outcomes to handle from the traversal.
     * @return a list of observable outcomes
     */
    public final ObservableList<VisitorToken> getOutcomes(){return outcomes;}

    /** Used internally (and by subclasses) to add a message to the outcome list
     * @param lineNumber the line number of the outcome
     * @param message the message of the outcome
     */
    protected final void addOutcome(int lineNumber, String message){
        outcomes.add(new VisitorToken(lineNumber,message));
    }

    public final Object visit(ASTNode node){return node.accept(this);}

    /** Visits a node and all of its children - for internal use as default action
     * @param node the node to visit
     * @return null
     */
    protected final Object defaultVisit(ASTNode node){
        for (ASTNode child : node.getChildren()) child.accept(this);
        return null;
    }
}
