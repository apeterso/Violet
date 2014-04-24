package Compiler.AbstractSyntaxTree;

import Compiler.Nodes.ASTNode;
import Compiler.Nodes.ASTNodeType;
import Compiler.Nodes.ASTNodeTypeBantam;
import Compiler.Parser.ParserTree.ParserTreeNode;

import java.util.*;

/**
 * The abstract syntax tree is the final production structure that
 * verifies a program before translation; it employs the visitor
 * pattern to do so.
 *
 * <br>We use triple dispatch to tremendously relive
 * repetition and coding burden over double-dispatch.
 *
 * <br>This tree is raw, as opposed to a Decorated Syntax Tree.
 *
 * Created by Matt Levine on 3/19/14.
 */
public class RawSyntaxTree {

    private final ASTNode root;
    private final ASTNodeType abstract_node;

    /** Constructs a new RawSyntaxTree from a root
     * ParseNode.
     * @param type the type of nodes to use
     * @param root The head of a ParsedTree
     */
    public RawSyntaxTree(ParserTreeNode root, Class type){
        abstract_node =  (ASTNodeType) Enum.valueOf(type,"AbstractNode");
        this.root = build(root,type);
    }

    /** Recursive suboutine for constructing tree
     * @param node the current node to modify
     * @return the build ASTNode
     */
    private ASTNode build(ParserTreeNode node,Class type){
        if (node.getNumChildren() == 0){
            return new ASTNode(node.value.name,node,type);
        }
        ASTNode newNode = new ASTNode(node.value.name,node,type);
        ParserTreeNode treeNode;
        Iterator<ParserTreeNode> kids = node.getChildren();
        while ( kids.hasNext() ){
            treeNode = kids.next();
            ASTNode childNode = build(treeNode,type);

            //add all non-abstract nodes (which should, if well designed, include all leaves)
            if (!childNode.nodeType.equals(abstract_node) ){
                newNode.addChildren(childNode);
            }else{
                newNode.addChildren(childNode.getChildren().toArray(
                        new ASTNode[childNode.getNumChildren()]));
            }
        }
        return newNode;
    }

    /** Returns the root of the tree
     * @return the root of the tree
     */
    public ASTNode getRoot(){return root;}

    /** Prints the RawSyntaxTree */
    public void print(){
        //what's the queue class again?
        Stack<ASTNode> nodes = new Stack<>();
        Stack<Integer> levels = new Stack<>();
        nodes.add(root);
        levels.add(0);

        while (!nodes.isEmpty()){
            final ASTNode node = nodes.pop();
            nodes.addAll(node.getChildren());

            final int level = levels.pop();
            levels.addAll( new ArrayList<Integer>(){{
                        {for (int i = 0; i < node.getNumChildren(); i++) add(level+1); }  }});
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < level; i++ ) s.append("  ");
            System.out.println(s + " " + node.nodeType + " : " + node.getValue()
                    + " - " + node.treeNode.getValue());

        }
    }

}
