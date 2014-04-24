package Compiler.Nodes;

import Compiler.Parser.ParserTree.ParserTreeNode;
import Compiler.Visitor.Visitor;

import java.util.*;

/**
 * This class represents a single unit in a Syntax Tree. As opposed
 * to there being a very large number of repetitive Syntax Tree
 * Nodes, we use <u>triple</u> dispatch in implementing the BantamVisitor
 * Pattern.
 *
 * ASTNodes are immutable.
 * Created by Matt Levine on 3/19/14.
 */
public final class ASTNode {

    public final ASTNodeType nodeType; //immutable so okay
    public final ParserTreeNode treeNode; //immutable so okay
    private final LinkedList<ASTNode> children;
    private final HashMap<String,Object> properties;

    /** Constructs an ParserTreeNode from the given name of a Node Type;
     * assumes Abstract if the node type is not found (does not
     * report this assumption - it is considered proper usage).
     * @param nodeTypeName The type of the node that corresponds to
     *                 an enumerated ParserTreeNode type
     * @param constructor the node from which to grab information
     */
    public ASTNode(String nodeTypeName, ParserTreeNode constructor){
        nodeType = (ASTNodeType) assignType(nodeTypeName,ASTNodeTypeBantam.class);
        children = new LinkedList<>();
        treeNode = constructor;
        properties = new HashMap<>();
    }

    /** Constructs an ParserTreeNode from the given name of a Node Type;
     * assumes Abstract if the node type is not found (does not
     * report this assumption - it is considered proper usage).
     * @param nodeTypeName The type of the node that corresponds to
     *                 an enumerated ParserTreeNode type
     * @param constructor the node from which to grab information
     * @param enumLocation The location of the enum type
     */
    public ASTNode(String nodeTypeName, ParserTreeNode constructor, Class enumLocation){
        nodeType = (ASTNodeType) assignType(nodeTypeName,enumLocation);
        children = new LinkedList<>();
        treeNode = constructor;
        properties = new HashMap<>();
    }



    /**Delegator method for assigning type of node to enum
     * @param type the name of the enum
     * @param enumLocation the class of the enumerated type
     * @return the type of the node
     */
    @SuppressWarnings("unchecked") //use wisely
    private Enum assignType(String type, Class enumLocation){
        //slight misuse of try/catch for convenience
        try{
            return Enum.valueOf(enumLocation,type);
        }catch (IllegalArgumentException e){
            return Enum.valueOf(enumLocation,"AbstractNode");
        }
    }

    /** Adds the given children to the node's children list.
     * @param nodes the children to add
     */
    public void addChildren(ASTNode... nodes){ Collections.addAll(children, nodes); }

    /** Returns the number of children this Node has
     * @return the number of children this Node has
     */
    public int getNumChildren(){return children.size();}

    /** Returns an unmodifiable list of the children
     * @return an unmodifiable list of the children
     */
    public List<ASTNode> getChildren(){
        return Collections.unmodifiableList(children);
    }

    /** Returns the underlying value
     * @return the underlying value
     */
    public String getValue(){ return treeNode.value.name;  }


    /** Passes the buck to its type
     * @return a visitor's something
     */
    public Object accept(Visitor v){ return nodeType.accept(v,this); }

    /** Sets the given property to the given key. Overwrites the property
     * if it exists.
     * @param key the property id
     * @param prop the quality
     */
    public void setProperty(String key, Object prop){
        properties.put(key,prop);
    }

    /** Returns the property with the given name or null if there is no
     * such property
     */
    public Object getProperty(String key){
        return properties.containsKey(key) ? properties.get(key) : null;
    }

    /** Returns the string form of the Node type **/
    @Override
    public String toString(){
        return "{"+nodeType.toString() + ":" + treeNode.getValue()+"}";
    }

    /** Returns the associated line number (owned by ParseTreeNode reference).
     *
     */
    public int getAssociatedLineNum(){
        return treeNode.value.getEnd_chartRow().getCFGToken().getLineNum();
    }

}