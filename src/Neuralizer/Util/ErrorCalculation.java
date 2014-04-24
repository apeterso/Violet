package Neuralizer.Util;

/**
 * Produces an RMS error calculation used by neural networks to calculate error.
 * <br>Based on Heaton's implementation (124) </br>
 * <br>Created by Matt Levine on 4/12/14.
 */
public class ErrorCalculation {

    private double globalError;
    private int setSize;

    /** Returns the RMS error for a complete training set
     * @return The current error for the neural network
     */
    public double calculateRMS(){
        return Math.sqrt(this.globalError / (this.setSize) );
    }

    /** Resets the cumulative error to zero
      */
    public void reset(){
        globalError = 0;
        setSize = 0;
    }

    /** Called to update for each number that should be checked
     * @param actual the actual number
     * @param ideal the idea number
     */
    public void updateError(final double[] actual, final double[] ideal){
        for (int i = 0; i < actual.length; i++){
            final double delta = ideal[i] - actual[i];
            globalError += delta * delta;
            setSize += ideal.length;
        }
    }

}
