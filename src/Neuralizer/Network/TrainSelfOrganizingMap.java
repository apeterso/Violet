package Neuralizer.Network;

import Neuralizer.Structure.Matrix;

import java.util.Arrays;

/**
 * Implements unsupervised training of Self Organizing Map (SOM).
 * Based on te Implementation by Heaton.
 * Created by Matt Levine on 4/14/14.
 */
public class TrainSelfOrganizingMap {

    public enum LearningMethod{ ADDITIVE, SUBTRACTIVE }

    /** The map to train **/
    private final SelfOrganizingMap selfOrganizingMap;
    private final int outputNeuronCount;
    private final int inputNeuronCount;
    private final SelfOrganizingMap bestnet;
    private double bestError;
    private Matrix work;
    private Matrix correction;

    /** The learning method **/
    private LearningMethod learnMethod;
    /** The learning rate **/
    private double learnRate;
    /** Reduction factor **/
    private double reduction = 0.99;
    /** Mean square error of the network **/
    private double totalError;
    /** Mean square of best error **/
    private double globalError;

    private int[] won;
    private double[][] train;

    /** Sets the reduction factor
     * @param factor the new reduction amount
     */
    public void setReduction(double factor){
        reduction = factor;
    }

    /** Construct the trainer for the som
     * @param som the map
     * @param train training method
     * @param learnMethod learning method
     * @param learnRate learn rate
     */
    public TrainSelfOrganizingMap(final SelfOrganizingMap som,
                                  final double train[][], LearningMethod learnMethod,
                                  double learnRate){
        this.selfOrganizingMap = som;
        this.train = train;
        this.totalError = 1.0;
        this.learnMethod = learnMethod;
        this.learnRate = learnRate;

        this.outputNeuronCount = som.getOutputNeuronCount();
        this.inputNeuronCount = som.getInputNeuronCount();

        totalError = 1.0;

        //Check for input error: Slow... necessary?
        for (double[] aTrain : train) {
            final Matrix dptr = Matrix.createColumnMatrix(aTrain);

            if (Matrix.MatrixMath.vectorLength(dptr) < 1.E-30) {
                throw new RuntimeException("Multiplicative normalization " +
                        "has null training case");
            }
        }

        bestnet = new SelfOrganizingMap(inputNeuronCount,outputNeuronCount,
                selfOrganizingMap.getNormalizationType());

        won = new int[outputNeuronCount];
        correction = new Matrix(outputNeuronCount,inputNeuronCount + 1);
        work = this.learnMethod == LearningMethod.ADDITIVE ?
                new Matrix(1,inputNeuronCount+1) : null;

        initialize();
        bestError = Double.MAX_VALUE;
    }

    /** Adjust weights to learn **/
    void adjustWeights(){
        for (int i = 0; i < outputNeuronCount; i++){
            if (won[i] == 0) continue;
            double f = 1.0 / won[i];
            if (learnMethod == LearningMethod.SUBTRACTIVE) f *= learnRate;


            for (int j = 0; j < inputNeuronCount; j++){
                final double corr = f * this.correction.get(i,j);
                this.selfOrganizingMap.getOutputWeights().add(i,j,corr);
            }
        }

    }

    /** Copy the weights between matrices **/
    private void copyWeights(final SelfOrganizingMap source,
                             final SelfOrganizingMap target){
        Matrix.MatrixMath.copy(source.getOutputWeights(),target.getOutputWeights());
    }

    /** Evaluates the current error level of the network **/
    void evaluateErrors(){
        correction.clear();
        Arrays.setAll(won,i->0);
        globalError = 0.0;

        //iterate training sets and calc correction
        for (double[] aTrain : train) {
            final NormalizeInput input = new NormalizeInput(aTrain,
                    selfOrganizingMap.getNormalizationType());
            final int best = selfOrganizingMap.winner(input);
            won[best]++;
            final Matrix wptr = selfOrganizingMap.getOutputWeights().getRow(best);

            double length = 0.0;
            double diff;
            for (int i = 0; i < this.inputNeuronCount; i++) {
                diff = aTrain[i] * input.getNormfac() - wptr.get(0, i);
                length += diff * diff;
                if (learnMethod == LearningMethod.SUBTRACTIVE) {
                    correction.add(best, i, diff);
                } else {
                    work.set(0, i, learnRate * aTrain[i] *
                            input.getNormfac() + wptr.get(0, i));
                }
            }
            diff = input.getSynth() - wptr.get(0, inputNeuronCount);
            length += diff * diff;
            if (this.learnMethod == LearningMethod.SUBTRACTIVE) {
                correction.add(best, inputNeuronCount, diff);
            } else {
                work.set(0, inputNeuronCount, learnRate * input.getSynth() +
                        wptr.get(0, inputNeuronCount));
            }

            globalError = length > globalError ? length : globalError;


            if (learnMethod == LearningMethod.ADDITIVE) {
                normalizeWeight(work, 0);
                for (int i = 0; i <= inputNeuronCount; i++) {
                    correction.add(best, i, work.get(0, i) - wptr.get(0, i));
                }
            }

        }
        globalError = Math.sqrt(globalError);
    }

    /** Force a win, if no neuron won. **/
    void forceWin(){
        int best, which = 0;
        final Matrix outputWeights = selfOrganizingMap.getOutputWeights();

        //Iterate and retrieve the least output
        double dist = Double.MAX_VALUE;
        for (int tset = 0; tset < train.length; tset++){
            best = selfOrganizingMap.winner(train[tset]);
            final double[] output = selfOrganizingMap.getOutput();

            if (output[best] < dist){
                dist = output[best];
                which = tset;
            }
        }

        final NormalizeInput input = new NormalizeInput(train[which],
                selfOrganizingMap.getNormalizationType());
        /*best = selfOrganizingMap.winner(input);*/
        final double[] output = selfOrganizingMap.getOutput();

        dist = Double.MIN_VALUE;
        int i = outputNeuronCount;
        while ((i--) > 0){
            if (won[i] != 0){
                continue;
            }
            if (output[i] > dist){
                dist = output[i];
                which = i;
            }
        }

        for (int j = 0; j < input.getInputMatrix().getCols(); j++){
            outputWeights.set(which,j,input.getInputMatrix().get(0,j));
        }

        normalizeWeight(outputWeights,which);
    }

    /** Returns the best error **/
    public double getBestError(){return bestError;}

    /** Returns the total error **/
    public double getTotalError() { return totalError;  }

    /** Initializes the SOM **/
    public void initialize(){
        selfOrganizingMap.getOutputWeights().randomize(-1,1);
        for (int i = 0; i < outputNeuronCount; i++){
            normalizeWeight(selfOrganizingMap.getOutputWeights(), i);
        }
    }

    /** Iterates the training module.
     * Proper use is repeated call while error is unacceptable.
     */
    public void iteration(){
        evaluateErrors();
        totalError = globalError;
        if (totalError < bestError){
            bestError = totalError;
            copyWeights(selfOrganizingMap,bestnet);
        }

        int winners = 0;
        for (int aWon : won) if (aWon != 0) winners++;

        if ((winners < outputNeuronCount) && (winners < train.length)){
            forceWin();
            return;
        }

        adjustWeights();
        learnRate = learnRate > 0.01 ? learnRate* reduction : learnRate;
    }

    /** Normalize the row in the weight matrix
     * @param matrix the weight matrix
     * @param row the row index
     */
    void normalizeWeight(final Matrix matrix, final int row){
        double len = Matrix.MatrixMath.vectorLength(matrix.getRow(row));
        len = Math.max(len, 1.E-30);
        len = 1.0/len;
        for ( int i = 0; i < inputNeuronCount; i++){
            matrix.set(row,i,matrix.get(row,i)*len);
        }
        matrix.set(row,inputNeuronCount,0);
    }


}
