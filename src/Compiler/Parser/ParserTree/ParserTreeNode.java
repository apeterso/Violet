package Compiler.Parser.ParserTree;

import Compiler.Parser.Matcher.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/** Represents a node in an Parser.ParserTree
 * Created by Matt Levine on 3/13/14.
 */
public final class ParserTreeNode {
    final private ArrayList<ParserTreeNode> children;
    final public State value;

    /** Constructs a new ParserTreeNode
     * @param value the value of the node
     * @param children the children of the node
     */
    public ParserTreeNode(State value, ParserTreeNode... children){
        this.children = new ArrayList<>(Arrays.asList(children));
        this.value = value;
    }

    public void print(int level){
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < level; i++) s.append("   ");
        s.append(value.toString());
        System.out.println(s);
        for (ParserTreeNode child : children) child.print(level + 1);
    }

    /** Returns an iterator through the children of the TreeNode
     * @return an iterator through this node's children
     */
    public Iterator<ParserTreeNode> getChildren(){
        return children.iterator();
    }

    /** Returns the number of children directly stemming from this node
     * @return the number of children directly stemming from this node
     */
    public int getNumChildren(){return children.size();}

    /** Returns the value of the Node. May throw an exception if
     * the node is malformed... but I don't think it will...
     * @return the value of the node
     */
    public String getValue(){
        return value.production.size() == 1 ? value.production.get(0).getValue() : value.toString();
    }

    /** Returns the value of the node as a string (note: not as useful as
     * printing the node using <i>print</i>).
     * @return the value of the node as a string
     */
    @Override public String toString(){return value.toString();}

}
