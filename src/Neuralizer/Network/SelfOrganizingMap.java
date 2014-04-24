package Neuralizer.Network;

import Neuralizer.Network.NormalizeInput.NormalizationType;
import Neuralizer.Structure.Matrix;

import java.io.Serializable;

/**
 * Created by Matt Levine on 4/14/14.
 */
public class SelfOrganizingMap implements Serializable{

    private static final long serialVersionUID = -3514494417789856185L;

    private Matrix outputWeights;

    /** Output neuron activations **/
    private final double[] output;
    /**Number of input neurons**/
    private final int inputNeuronCount;
    /**Number of output neurons **/
    private final int outputNeuronCount;
    /** Normalization type **/
    private final NormalizationType normalizationType;

    /** Construct a new SelfOrganizingMap **/
    public SelfOrganizingMap(final int inputCount, final int outputCount,
                             final NormalizationType normalizationType){
        inputNeuronCount = inputCount;
        outputNeuronCount = outputCount;

        outputWeights = new Matrix(outputNeuronCount, inputNeuronCount + 1);
        output = new double[outputNeuronCount];
        this.normalizationType = normalizationType;
    }

    /** Returns the input neuron count **/
    public int getInputNeuronCount(){ return inputNeuronCount; }

    /** Returns the normalization type **/
    public NormalizationType getNormalizationType(){ return normalizationType; }

    /** Returns the output neurons **/
    public double[] getOutput(){ return output; }

    /** Returns the output neuron count **/
    public int getOutputNeuronCount(){return outputNeuronCount;}

    /** Returns the output weight **/
    public Matrix getOutputWeights(){return outputWeights;}

    /** Sets the output weights **/
    public void setOutputWeights(Matrix newOutput){
        outputWeights = newOutput;
    }

    /** Returns the "winner" for a given input. This corresponds to the
     * index of a victorious neuron
     * @param input pattern
     * @return neuron index
     */
    public int winner(final double[] input){
        return winner(new NormalizeInput(input,normalizationType));
    }

    /** Returns the "winner" for a given input. This corresponds to the
     * index of a victorious neuron
      * @param input pattern
     * @return neuron index
     */
    public int winner(final NormalizeInput input){
        //fixme: use streams?
        int win = 0;
        double biggest = Double.MIN_VALUE;
        for (int i = 0; i < this.outputNeuronCount; i++){
            final Matrix optr = outputWeights.getRow(i);
            try{
                output[i] = Matrix.MatrixMath.dotProduct(input.getInputMatrix(), optr)
                        * input.getNormfac();
            }catch(Matrix.UnsupportedMatrixOperation e){
                String err = "SOM Err: Input to SOM does not match trained input dimension";
                System.out.println(e);
                throw new SelfOrganizingMapException(err);
            }
             output[i] = (output[i]+1.0)/2.0;
            if (output[i] > biggest){
                biggest = output[i];
                win = i;
            }

            output[i] = output[i] < 0 ? 0 : output[i] > 1 ? 1 : output[i];
         }
        return win;
    }
}

class SelfOrganizingMapException extends RuntimeException{
    public SelfOrganizingMapException(String err){super(err);}
}