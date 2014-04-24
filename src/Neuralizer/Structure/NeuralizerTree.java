package Neuralizer.Structure;

import Compiler.AbstractSyntaxTree.RawSyntaxTree;
import Compiler.Parser.ParserTree.ParserTreeNode;
import Neuralizer.Util.TreeFlattenVisitor;

/**
 * The NeuralizerTree accesses the information from a Parser
 * Tree and translates into data that can be fed to a SOM.
 * Created by Matt Levine on 4/20/14.
 */
public class NeuralizerTree {

    private final RawSyntaxTree base;
    private final TreeFlattenVisitor assist;

    /** Constructs a new Neuralizer Tree
     * @param root the root of the tree
     * @param type the type of the tree
     */
    public NeuralizerTree(ParserTreeNode root, Class type){
        base = new RawSyntaxTree(root,type);
        assist = new TreeFlattenVisitor(base.getRoot());
    }

    /** Returns a version of this tree that can be fed
     * into a SOM. Does not affect the tree
     * @return a Matrix representing the tree
     */
    public Matrix flatten(){
        return assist.getMatrixForm();
    }



}
