package Neuralizer.Util;

/**
 * This class models machine learning using the "delta" rule, &#916 = 2 &#924 x_i (actual - ideal)
 * <br>Created by Matt Levine on 4/13/14.
 */
public class Delta {

    /** Simply runs the Delta's run method **/
    public static void main(String[] args){
        final Delta delta = new Delta();
        delta.run();
    }

    double w1;
    double w2;
    double w3;
    double rate = 0.5;
    int epoch = 1;

    /** Processes one epoch; learns from three training samples and adjusts waits based
     * on discovered error.
     */
    protected void epoch(){
        System.out.println("***Beginning Epoch #"+epoch+"***");
        presentPattern(0,0,1,0);
        presentPattern(0,1,1,0);
        presentPattern(1,0,1,0);
        presentPattern(1,1,1,1);
        epoch++;
    }

    /** Calculates the error between expected and actual output
     * @param actual actual output
     * @param anticipated expected output
     * @return the error
     */
    protected double getError(final double actual, final double anticipated){
        return anticipated - actual;
    }

    /** Present a Pattern and learn from it
     * @param i1 input to first neuron
     * @param i2 input to second neuron
     * @param i3 input to third neuron
     * @param anticipated expected output
     */
    protected void presentPattern(final double i1, final double i2, final double i3,
                                  final double anticipated){
        double error, actual, delta;

        //run the network as is on training data and calculate error
        System.out.println("Presented ["+i1+","+i2+","+i3+"]");
        actual = recognize(i1,i2,i3);
        error = getError(actual,anticipated);
        System.out.println(" anticipated="+anticipated);
        System.out.println(" actual="+actual);
        System.out.println(" error="+error);

        //adjust weights
        delta = trainingFunction(rate, i1, error);
        w1 += delta;
        delta = trainingFunction(rate, i2, error);
        w2 += delta;
        delta = trainingFunction(rate, i3, error);
        w3 += delta;
    }

    /** Recognizes three neuron inputs
     * @param i1 input to first neuron
     * @param i2 input to second neuron
     * @param i3 input to third neruon
     * @return the output from the network
     */
    protected double recognize(final double i1, final double i2, final double i3){
        return ((w1*i1)+(w2*i2)+(w3*i3)) * 0.5;
    }

    /** Tests method iterates 100 epochs **/
    public void run(){ for (int i = 0; i < 100; i++) epoch(); }

    /** The learningFunction implements the Delta rule, as specified above. It returnsthe
     * weight adjustment for the specified input neuron.
     * @param rate The learning rate
     * @param input The input neuron we're processing
     * @param error The error between the actual output and the anticipated output
     * @return the amount to adjust the weight ("delta")
     */
    protected double trainingFunction(final double rate, final double input,
                                      final double error){
        return rate * input * error;
    }

}
