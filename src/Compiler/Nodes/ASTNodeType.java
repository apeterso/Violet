package Compiler.Nodes;

import Compiler.Visitor.Visitor;

/**
 * This type must be used by all Nodes being used by a syntax tree.
 * Created by Matt Levine on 3/31/14.
 */
public interface ASTNodeType{

    abstract Object accept(Visitor v, ASTNode node);

}
