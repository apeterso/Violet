package Neuralizer.Util;

import Compiler.Nodes.ASTNode;
import Compiler.Visitor.Java7.Java7Visitor;
import Neuralizer.Structure.Matrix;

/**
 * This visitor assists in the flattening of Parse Trees
 * for conversion into a SOM readable format.
 * Created by Matt Levine on 4/20/14.
 */
public class TreeFlattenVisitor extends Java7Visitor{

    private int numFields = 0;
    private int numMethods = 0;
    private int numConditionals = 0;
    private int numForLoops = 0;
    private int numWhileLoops = 0;
    private int numCaseStatements = 0;
    private int numDoStatements = 0;
    private int numLiterals = 0;
    private int numIdentifiers = 0;
    private int numAssignments = 0;
    private int numVariableInits = 0;
    private int numEnums = 0;
    private int numClasses = 0;
    private int numTypes = 0;
    private int numArrayInits = 0;
    private int numTrys = 0;
    private int numArguments = 0;
    private int numFormals = 0;

    private final ASTNode root;

    /** Generates a new flatten visitor from a given root node
     * @param root the root node
     */
    public TreeFlattenVisitor(ASTNode root) {
        this.root = root;
    }

    /** Returns a representative matrix of the root, simple summation of quantity of important
     * features. Guaranteed to be the same when called multiple times, or when called from separate
     * visitors on the same root. See "Cerulean Documentation" accompanying the API for details
     * and mathematical veracity.
     * @return a representative matrix.
     */
    public Matrix getMatrixForm(){
        reset();
        root.accept(this);
        final double[][] summary = {{numFields,numMethods,numConditionals,numForLoops,
                numWhileLoops,numCaseStatements,numDoStatements,numLiterals,numIdentifiers,
                numAssignments,numVariableInits,numEnums,numClasses,numTypes,numArrayInits,
                numTrys,numArguments,numFormals}};
        for (int i = 0; i < summary[0].length; i++)
            summary[0][i] = summary[0][i] == 0 ? 0 : 1.0/summary[0][i];
        return new Matrix(summary);
    }

    /** Resets all fields to 0 **/
    private void reset(){
        numFields = numMethods = numConditionals = numForLoops = numWhileLoops
                = numCaseStatements = numDoStatements = numLiterals = numIdentifiers
                = numAssignments = numVariableInits = numEnums = numClasses
                = numTypes = numArrayInits = numTrys = numArguments =
                numFormals = 0;
    }

    @Override public Object visitFieldDecl(ASTNode node){
        numFields++;
        return super.visitFieldDecl(node);
    }

    @Override public Object visitMethodDecl(ASTNode node){
        numMethods++;
        return super.visitMethodDecl(node);
    }

    @Override public Object visitIfStatement(ASTNode node){
        numConditionals++;
        return super.visitIfStatement(node);
    }

    @Override public Object visitForStatement(ASTNode node){
        numForLoops++;
        return super.visitForStatement(node);
    }

    @Override public Object visitWhileStatement(ASTNode node){
        numWhileLoops++;
        return super.visitWhileStatement(node);
    }

    @Override public Object visitSwitchLabel(ASTNode node){
        numCaseStatements++;
        return super.visitSwitchLabel(node);
    }

    @Override public Object visitDoStatement(ASTNode node){
        numDoStatements++;
        return super.visitDoStatement(node);
    }

    @Override public Object visitLiteral(ASTNode node){
        numLiterals++;
        return super.visitLiteral(node);
    }

    @Override public Object visitIdentifier(ASTNode node){
        numIdentifiers++;
        return super.visitIdentifier(node);
    }

    @Override public Object visitAssignmentOperator(ASTNode node){
        numAssignments++;
        return super.visitAssignmentOperator(node);
    }

    @Override public Object visitVariableInitializer(ASTNode node){
        numVariableInits++;
        return super.visitVariableInitializer(node);
    }

    @Override public Object visitEnumDeclaration(ASTNode node){
        numEnums++;
        return super.visitEnumDeclaration(node);
    }

    @Override public Object visitClassDeclaration(ASTNode node){
        numClasses++;
        return super.visitClassDeclaration(node);
    }

    @Override public Object visitTypeDeclaration(ASTNode node){
        numTypes++;
        return super.visitTypeDeclaration(node);
    }

    @Override public Object visitArrayVariableDeclarator(ASTNode node){
        numArrayInits++;
        return super.visitArrayVariableDeclarator(node);
    }

    @Override public Object visitTryStatement(ASTNode node){
        numTrys++;
        return super.visitTryStatement(node);
    }

    @Override public Object visitArguments(ASTNode node){
        numArguments++;
        return super.visitArguments(node);
    }

    @Override public Object visitFormalParameterDecls(ASTNode node){
        numFormals++;
        return super.visitFormalParameterDecls(node);
    }

}
