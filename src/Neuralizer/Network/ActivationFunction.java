package Neuralizer.Network;

import java.io.Serializable;

/**
 * The interface of functions used to communicate with neural networks.
 * Based on implementation by Heaton.
 * Created by Matt Levine on 4/14/14.
 */
interface ActivationFunction extends Serializable{
    /** Returns the output of an activation
     * @param sum the input
     * @return the output
     */
    public double activationFunction(double sum);

    /** Returns the derivative of the function at the input
     * @param d the input
     * @return the output
     */
    public double derivativeFunction(double d);
}
