package Neuralizer.Network;

import Neuralizer.Structure.Matrix;

/**
 * This class normalizes the input to a SOM network.
 * Based on the implementation by Heaton.
 * Created by Matt Levine on 4/14/14.
 */
public class NormalizeInput {

    public enum NormalizationType{ Z_AXIS, MULTIPLICATIVE }

    private final NormalizationType type;
    /** The normalization factor **/
    protected double normfac;
    /** The synthetic input **/
    protected double synth;
    /** The input expressed in matrix form **/
    protected Matrix inputMatrix;

    /** Normalize input array into a matrix **/
    public NormalizeInput(final double[] input, final NormalizationType type){
        this.type = type;
        calculateFactors(input);
        inputMatrix = createInputMatrix(input,synth);
    }

    /** Create an input matrix that has enough space to hold the extra
     * input
     */
    public Matrix createInputMatrix(final double[] pattern, final
                                    double extra){
        final Matrix result = new Matrix(1, pattern.length + 1);
        for (int i = 0; i < pattern.length; i++) result.set(0,i,pattern[i]);
        result.set(0,pattern.length,extra);

        return result;
    }

    /** Returns the input matrix **/
    public Matrix getInputMatrix(){return inputMatrix;}

    /** Returns the normalization factor **/
    public double getNormfac(){return normfac;}

    /** Returns the synthetic input **/
    public double getSynth(){ return synth; }

    /** Calculates the normalization factor and synthetic input **/
    protected void calculateFactors(final double[] input){
        final Matrix inputMatrix = Matrix.createColumnMatrix(input);
        double len = Matrix.MatrixMath.vectorLength(inputMatrix);
        len = Math.max(len, 1.E-30); //restrict minimum value
        final int numInputs = input.length;

        if ( type.equals(NormalizationType.MULTIPLICATIVE)){
            normfac = 1.0 / len;
            synth = 0.0;
        }else{
            normfac = 1.0 / Math.sqrt(numInputs);
            final double d = numInputs - Math.pow(len,2);
            if ( d > 0.0 ){
                synth = Math.sqrt(d) * normfac;
            }else{
                synth = 0;
            }
        }
    }

}
