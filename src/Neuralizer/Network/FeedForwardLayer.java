package Neuralizer.Network;

import Neuralizer.Structure.Matrix;

import java.io.Serializable;

/**
* This class represents one layer in a feed forward network.
 * Based on implementation by Heaton.
* Created by Matt Levine on 4/14/14.
*/
public class FeedForwardLayer implements Serializable {

    private static final long serialVersionUID = -3698708039331150031L;

    /** Last calculated outputs **/
    private double fire[];
    /** Weights and thresholds **/
    private Matrix matrix;
    private FeedForwardLayer previous;
    private FeedForwardLayer next;
    private final ActivationFunction activationFunction;

    /** Constructs a new layer
     * @param thresholdFunction the threshold function
     * @param neuronCount number of neruons
     */
    public FeedForwardLayer(final ActivationFunction thresholdFunction, final int
                            neuronCount){
        fire = new double[neuronCount];
        activationFunction = thresholdFunction;
    }

    /** Construct this layer with sigmoid threshold function **/
    public FeedForwardLayer(final int neruonCount){
        this(new ActivationSigmoid(), neruonCount); //FIXME
    }

    /** Clones structure (not data) **/
    public FeedForwardLayer cloneStructure(){
        return new FeedForwardLayer(activationFunction,getNeuronCount());
    }

    /** Computes the outputs for the layer for an input pattern
     * @param pattern the input
     * @return the output
     */
    public double[] computeOutputs(final double pattern[]){
        if (pattern!=null){
            for (int i = 0; i < getNeuronCount(); i++) setFire(i,pattern[i]);
        }
        final Matrix inputMatrix = createInputMatrix(fire);

        for (int i = 0; i < next.getNeuronCount(); i++){
            final Matrix col = matrix.getCol(i);
            final double sum = Matrix.MatrixMath.dotProduct(col,inputMatrix);

            next.setFire(i,activationFunction.activationFunction(sum));
        }
        return fire;
    }

    /** Creates an input matrix for calculating the results of the input array,
     * including threshold in calculation
     * @param pattern the input
     * @return matrix representing input
     */
    private Matrix createInputMatrix(final double[] pattern){
        final Matrix result = new Matrix(1, pattern.length + 1 );
        for (int i = 0; i < pattern.length; i++) result.set(0,i, pattern[i]);
        result.set(0,pattern.length,1);
        return result;
    }

    /** Get the output array from the last time that the output was calculated
     * @return the output
     */
    public double[] getFire(){return fire;}

    /** Gets the output of a neuron; assumes valid index.
     * @param index the neuron index
     * @return the output
    */
    public double getFire(final int index){return fire[index];}

    /** Returns the weight/threshold matrix. **/
    public Matrix getMatrix(){return matrix;}

    /** Returns size of matrix **/
    public int getMatrixSize(){
        return matrix == null ? 0 : matrix.size();
    }

    /** Returns the number of neruons **/
    public int getNeuronCount(){return fire.length;}

    /** Returns the next layer **/
    public FeedForwardLayer getNext(){return next;}

    /** Returns the previous layer **/
    public FeedForwardLayer getPrevious(){return previous;}

    /** Returns true if the matrix is defined **/
    public boolean hasMatrix(){return matrix != null;}

    /** Returns true if is a hidden layer **/
    public boolean isHidden(){ return next!= null && previous != null;}

    /** Returns true if is input layer **/
    public boolean isInput(){return previous != null;}

    /** Returns true if is output layer **/
    public boolean isOutput(){return next == null;}

    /** Removes a neuron from the layer and all entries in the weight matrix and
     * other layers
     * @param neuron The neruon to remove; zero indexes first neruon
     */
    public void prune(final int neuron){
        if (matrix!=null) setMatrix(Matrix.MatrixMath.deleteRow(matrix,neuron));
        final FeedForwardLayer previous = getPrevious();
        if (previous != null && previous.getMatrix() != null)
                previous.setMatrix(Matrix.MatrixMath.deleteCol(previous.getMatrix(), neuron));
    }

    /** Resets the weight matrix and threshold values to random numbers between -1 and 1 **/
    public void reset(){if (matrix!=null) matrix.randomize(-1,1);}

    /** Sets the last output value for the given neuron
     * @param index the locatino of the neuron
     * @param fireValue the new value
     */
    public void setFire(final int index, final double fireValue){
        fire[index] = fireValue;
    }

    /** Sets the weight/threshold matrix **/
    public void setMatrix(final Matrix matrix){
        if (matrix.getRows() < 2 ){
            throw new RuntimeException("Weight matrix must have at least 2 rows.");
        }
        if (matrix != null) fire = new double[matrix.getRows()-1];
        this.matrix = matrix;
    }

    /** Sets the next layer **/
    public void setNext(final FeedForwardLayer next){
        this.next = next;
        matrix = new Matrix(getNeuronCount() + 1, next.getNeuronCount());
    }

    /** Sets the previous layer **/
    public void setPrevious(final FeedForwardLayer previous){ this.previous = previous;}

    /** Returns a useful string representation of the layer **/
    @Override public String toString(){
        return "[FeedforwardLayer: Neuron Count = "+getNeuronCount()+"]";
    }

    public ActivationFunction getActivationFunction(){ return activationFunction;}

}
