package Neuralizer.Network;

/**
 * Implementation of an activation function for sigmoid functions.
 * Created by Matt Levine on 4/14/14.
 */
public class ActivationSigmoid implements ActivationFunction {

    private static final long serialVersionUID = 5622349801036468572L;

    /** Returns the output of an activation
     * @param d the input
     * @return the output
     */
    @Override
    public double activationFunction(final double d) {
        return 1.0 /  (1+bound(Math.exp(-1.0 * d)));
    }

    /** Bounds the input, preventing it from getting to big or too small
     * @param d the input
     * @return the bounded output
     */
    private double bound(double d){
        return d < -1.0E20 ? -1.0E20 : d > 1.0E20 ? 1.0E20 : d;
    }

    /** Returns the derivative of the function at the input
     * @param d the input
     * @return the output
     */
    @Override
    public double derivativeFunction(double d) {
        return 0;
    }
}
