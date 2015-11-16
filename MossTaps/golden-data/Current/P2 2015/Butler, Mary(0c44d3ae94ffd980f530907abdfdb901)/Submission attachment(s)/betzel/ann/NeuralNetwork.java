package ravensproject.betzel.ann;

import java.util.Random;

/**
 * Created by scott betzel on 6/14/15.
 *
 * Third iteration on my attempted Neural Network.
 *
 * Using the following source as a coding reference.
 * http://ecee.colorado.edu/~ecen4831/lectures/NNet3.html
 */
public class NeuralNetwork {

    public static final double DEFAULT_MOMENTUM = 0.1;

    /**
     * This is the default mean used to generate random
     * weights.
     */
    private static final double DEFAULT_GUASSIAN_MEAN = 0.00;

    /**
     * This is the default variance used to generate random
     * weights.
     */
    private static final double DEFAULT_GUASSIAN_VARIANCE = 1.00;

    /**
     * This represents the default learning rate.
     */
    public static final double DEFAULT_LEARNING_RATE = 0.3;

    private int numLayers;

    private int[] numNodesInLayer;

    private double momentum;

    private double learningRate;

    private double[][][] weights;

    private double[][][] previousWeightChange;

    private double[][][] weightChangeSum;

    private double[][] activations;

    /**
     * This will be used to create random weights.
     */
    private Random random;

    public NeuralNetwork(final int[] layerSetup) {
        int[] layerSetupWithBiases = this.getLayerSetupWithBiasNodes(layerSetup);

        this.numLayers = layerSetupWithBiases.length;
        this.numNodesInLayer = layerSetupWithBiases;
        this.random = new Random();
        this.momentum = DEFAULT_MOMENTUM;
        this.learningRate = DEFAULT_LEARNING_RATE;
        this.weights = this.createRandomWeights(layerSetupWithBiases);
        this.activations = this.createActivations(layerSetupWithBiases);
        this.previousWeightChange = null;
        this.weightChangeSum = this.createWeightChangeSum(layerSetupWithBiases);
    }

    public NeuralNetwork(final int[] layerSetup,
                         final double theMomentum,
                         final double theLearningRate) {
        this(layerSetup);

        this.momentum = theMomentum;
        this.learningRate = theLearningRate;
    }

    private int[] getLayerSetupWithBiasNodes(final int[] layerSetup) {
        int[] toRet = new int[layerSetup.length];

        for (int i = 0; i < toRet.length; i++) {
            if (toRet.length - 1 == i) {
                // If output layer then just keep it normal
                toRet[i] = layerSetup[i];
            } else {
                toRet[i] = layerSetup[i] + 1;
            }

        }

        return toRet;
    }

    private double[][] createActivations(final int[] layerSetup) {
        double[][] toRet = new double[layerSetup.length][];

        for (int i = 0; i < layerSetup.length; i++) {
            toRet[i] = new double[layerSetup[i]];

            if (this.isBiasNode(i, 0)) {
                toRet[i][0] = 1.0; // bias node
            }
        }

        return toRet;
    }

    private double[][][] createWeightChangeSum(final int[] layerSetup) {

        double[][][] toRet = new double[layerSetup.length][][];
        int nextLayerIndex;
        int currentNumNodesInLayer;
        int nextNumNodesInLayer;

        for (int layerIndex = 0;
             layerIndex < layerSetup.length;
             layerIndex++) {

            nextLayerIndex = layerIndex + 1;

            if (nextLayerIndex >= layerSetup.length) {
                break;
            }

            currentNumNodesInLayer = layerSetup[layerIndex];
            nextNumNodesInLayer = layerSetup[nextLayerIndex];

            toRet[layerIndex] = new double[currentNumNodesInLayer][];

            for (int srcNodeIndex = 0;
                 srcNodeIndex < currentNumNodesInLayer;
                 srcNodeIndex++) {

                toRet[layerIndex][srcNodeIndex]
                        = new double[nextNumNodesInLayer];
            }

        }

        return toRet;
    }

    private boolean isBiasNode(final int layerIndex,
                               final int nodeIndex) {
        if (layerIndex == this.numLayers - 1) {
            return false;
        }

        return nodeIndex == 0;
    }

    private double getPreviousWeightChange(final int layerIndex,
                                          final int srcNodeIndex,
                                          final int destNodeIndex) {
        if (null == this.previousWeightChange) {
            return 0.0;
        }

        return this.previousWeightChange[layerIndex]
                                        [srcNodeIndex]
                                        [destNodeIndex];
    }

    private double getActivation(final int layerIndex,
                                 final int nodeIndex) {
        if (isBiasNode(layerIndex, nodeIndex)) {
            return 1.0; // Bias activation should always be 1
        }

        return this.activations[layerIndex][nodeIndex];
    }

    private void setActivation(final int layerIndex,
                               final int nodeIndex,
                               final double activation) {
        this.activations[layerIndex][nodeIndex] = activation;
    }

    public final double[][][] getWeights() {
        return this.weights;
    }

    public void setWeights(double[][][] weights) {
        // output layer does not have any weights
        assert weights[weights.length - 1] == null;

        this.weights = weights;
    }

    public final double getLearningRate() {
        return this.learningRate;
    }

    public final double getMomentum() {
        return this.momentum;
    }

    public final int getNumLayers() {
        return this.numLayers;
    }

    public final int getNumNodesInLayer(final int layerIndex) {
        return this.numNodesInLayer[layerIndex];
    }

    public final double getWeight(final int layerIndex,
                            final int sourceNodeIndex,
                            final int destNodeIndex) {

        return this.weights[layerIndex][sourceNodeIndex][destNodeIndex];
    }

    /**
     * This method represents our sigmoid function.
     *
     * @param x The x value to apply the sigmoid function to.
     * @return Returns a double value ranging from -1 to 1.
     */
    public final double calculateSigmoid(final double x) {
        return 1.0 / (1 + Math.exp(-1.0 * x));
    }

    /**
     * This method represents the derivative of the sigmoid
     * function.
     *
     * @param x The x value to apply the sigmoid function to.
     * @return Returns the derivative of the sigmoid function
     * with x as the parameter.
     */
    private double calculateSigmoidDerivative(final double x) {
        return this.calculateSigmoid(x) * (1.0 - this.calculateSigmoid(x));
    }

    public final double calculateSummation(final int layer,
                                           final int node,
                                           final double[] inputs) {
        double toRet = 0.0;
        double currentWeight;
        double currentInput;

        // You shouldn't need to use this on the input layer
        assert layer > 0;
        // You shouldn't need to use this on a bias node.
        assert !this.isBiasNode(layer, node);

        for (int j = 0; j < inputs.length; j++) {
            currentInput = inputs[j];

            currentWeight = this.getWeight(layer - 1, j, node);

            toRet += currentInput * currentWeight;
        }

        return toRet;
    }

    final double calculateApproxTargetActivation(final int layerIndex,
                                                        final int nodeIndex,
                                                        final double[] currentSignalErrors) {
        double toRet = 0.0;
        double currentDelta;
        double currentWeight;
        int numNodesInLayer = this.getNumNodesInLayer(layerIndex);

        // If node index is greater than something is messed up
        assert numNodesInLayer > nodeIndex;

        // You should use this method on all layers except the last
        assert layerIndex < this.getNumLayers() - 1;

        int signalErrorLength = currentSignalErrors.length;

        for (int k = 0; k < signalErrorLength; k++) {
            if ((layerIndex < this.getNumLayers() - 2) && (k == 0)) {
                // If second to last layer, there will be no
                // need to skip over bias node because a bias
                // node does not exist in the output layer.
                continue;
            }

            currentDelta = currentSignalErrors[k];

            currentWeight = this.getWeight(layerIndex, nodeIndex, k);

            toRet += currentDelta * currentWeight;
        }

        return toRet;
    }

    private double[][][] createRandomWeights(final int[] theLayerSetup) {
        double[][][] toRet = new double[theLayerSetup.length][][];
        int nextLayerIndex;
        int currentNumNodesInLayer;
        int nextNumNodesInLayer;

        for (int layerIndex = 0;
             layerIndex < theLayerSetup.length;
             layerIndex++) {

            nextLayerIndex = layerIndex + 1;

            if (nextLayerIndex >= theLayerSetup.length) {
                break;
            }

            currentNumNodesInLayer = theLayerSetup[layerIndex];
            nextNumNodesInLayer = theLayerSetup[nextLayerIndex];

            toRet[layerIndex] = new double[currentNumNodesInLayer][];

            for (int srcNodeIndex = 0;
                    srcNodeIndex < currentNumNodesInLayer;
                    srcNodeIndex++) {

                toRet[layerIndex][srcNodeIndex]
                        = this.createRandomWeights(nextNumNodesInLayer);
            }

        }

        return toRet;
    }

    /**
     * This will create random weights for the inputs.
     *
     * @param size The size of the random weights array we need.
     * @return Returns an array with the corresponding size with
     * random weights.
     */
    private double[] createRandomWeights(final int size) {

        double[] toRet = new double[size];

        for (int i = 0; i < toRet.length; i++) {
            toRet[i] = DEFAULT_GUASSIAN_MEAN
                    + this.random.nextGaussian() * DEFAULT_GUASSIAN_VARIANCE;
        }

        return toRet;

    }

    private double getRidOfOnesAndZeros(final double input) {

        if(Math.abs(1.0 - input) < 0.0000001) {
            return 0.9;
        }

        if (Math.abs(0.0 - input) < 0.0000001) {
            return 0.1;
        }

        return input;
    }

    /**
     * Use this to train the neural network.
     *
     * @param trainingData This contains all the training data.
     * @param numAnswerColumns The number of columns on the end of the dataset
     *                         that contain the answer for a given row.
     */
    public final double[][] train(final double[][] trainingData,
                                  final int numAnswerColumns,
                                  final int epochs) {

        assert trainingData != null;
        assert numAnswerColumns > 0;

        double[][] toRet = new double[trainingData.length][];

        for (int epochIndex = 0;
             epochIndex < epochs;
             epochIndex++) {

            for (int dataRowIndex = 0;
                 dataRowIndex < trainingData.length;
                 dataRowIndex++) {

                this.previousWeightChange
                        = this.trainOne(trainingData[dataRowIndex],
                        numAnswerColumns);

                this.sumWeightChanges(this.previousWeightChange);
            }

            this.applyWeightChangeSums();
        }

        for (int dataRowIndex = 0;
             dataRowIndex < trainingData.length;
             dataRowIndex++) {

            toRet[dataRowIndex]
                    = this.feedForwardWithBias(trainingData[dataRowIndex],
                                               numAnswerColumns);
        }

        return toRet;
    }

    private final void sumWeightChanges(double[][][] weightChanges) {
        for (int layerIndex = 0;
                layerIndex < weightChanges.length - 1;
                layerIndex++) {

            for (int srcNodeIndex = 0;
                    srcNodeIndex < weightChanges[layerIndex].length;
                    srcNodeIndex++) {

                for (int destNodeIndex = 0;
                        destNodeIndex < weightChanges[layerIndex][srcNodeIndex].length;
                        destNodeIndex++)  {

                    this.weightChangeSum[layerIndex][srcNodeIndex][destNodeIndex]
                            += weightChanges[layerIndex][srcNodeIndex][destNodeIndex];

                }
            }
        }
    }

    private final void applyWeightChangeSums() {
        for (int layerIndex = 0;
             layerIndex < this.weightChangeSum.length - 1;
             layerIndex++) {

            for (int srcNodeIndex = 0;
                 srcNodeIndex < this.weightChangeSum[layerIndex].length;
                 srcNodeIndex++) {

                for (int destNodeIndex = 0;
                     destNodeIndex < this.weightChangeSum[layerIndex][srcNodeIndex].length;
                     destNodeIndex++)  {

                    this.weights[layerIndex][srcNodeIndex][destNodeIndex]
                            = this.weightChangeSum[layerIndex][srcNodeIndex][destNodeIndex];

                }
            }
        }
    }

    final double[][][] trainOne(final double[] dataSetRow,
                                final int numAnswerColumns) {

        double[] targetActivations
                = new double[numAnswerColumns];

        System.arraycopy(dataSetRow,
                         dataSetRow.length - numAnswerColumns,
                         targetActivations,
                         0,
                         numAnswerColumns);

        double[] predictedActivations = feedForwardWithBias(dataSetRow, numAnswerColumns);
        double[] outputSignalErrors
                = this.calculateOutputSignalErrors(targetActivations,
                                                   predictedActivations);

        return this.backPropagate(outputSignalErrors);

    }

    double[] feedForwardWithBias(double[] dataSetRow, int numAnswerColumns) {
        double[] activations
                = new double[dataSetRow.length - numAnswerColumns + 1];
        activations[0] = 1.0; // this is the bias

        System.arraycopy(dataSetRow,
                0,
                activations,
                1,
                dataSetRow.length - numAnswerColumns); // +1 for activation

        return this.feedForward(activations);
    }

    final double calculateWeightChange(final double destError,
                                              final double sourceActivation,
                                              final double previousWeightChange) {
//        double tempSourceActivation = this.getRidOfOnesAndZeros(sourceActivation);

        return this.learningRate
                * destError
                * sourceActivation
                    + this.momentum
                        * previousWeightChange;
    }

    final double calculateSingleOutputDelta(final double targetActivation,
                                            final double predictedActivation) {

        // So this doesn't equal zero
        double tempTargetActivation = this.getRidOfOnesAndZeros(targetActivation);
        double tempPredictedActivation = this.getRidOfOnesAndZeros(predictedActivation);

        return (tempTargetActivation - tempPredictedActivation)
                * tempPredictedActivation
                * (1 - tempPredictedActivation);
    }

    final double calculateSingleHiddenErrorSignal(final double summedAndWeightErrorSignals,
                                                  final double predictedActivation) {

        // So this doesn't equal zero
//        double tempSummedAndWeightedErrorSignals
//                = this.getRidOfOnesAndZeros(summedAndWeightErrorSignals);
        double tempPredictedActivation
                = this.getRidOfOnesAndZeros(predictedActivation);

        return summedAndWeightErrorSignals
                * tempPredictedActivation
                * (1 - tempPredictedActivation);
    }

    final double[] calculateOutputSignalErrors(final double[] targetActivation,
                                               final double[] predictedActivation) {
        assert targetActivation != null;
        assert predictedActivation != null;
        assert targetActivation.length == predictedActivation.length;

        double[] toRet = new double[targetActivation.length];

        for (int i = 0; i < toRet.length; i++) {
            toRet[i] = this.calculateSingleOutputDelta(targetActivation[i],
                    predictedActivation[i]);
        }

        return toRet;
    }

    final double[][] getBackPropagationSignalErrors(final double[] deltas) {
        int layerSize;
        double[] currentDeltas = deltas;
        double[] nextDeltas;
        double approxTargetActivation;
        double predictedActivation;
        double[][] allSignalErrors = new double[this.numLayers][];

        allSignalErrors[this.numLayers - 1] = currentDeltas;

        for (int layerIndex = this.numLayers - 2;
             layerIndex >= 0;
             layerIndex--) {

            layerSize = this.getNumNodesInLayer(layerIndex);
            nextDeltas = new double[layerSize];

            // Start at
            for (int nodeIndex = 0;
                 nodeIndex < layerSize;
                 nodeIndex++) {

                approxTargetActivation
                        = this.calculateApproxTargetActivation(layerIndex,
                        nodeIndex,
                        currentDeltas);

                predictedActivation = this.getActivation(layerIndex, nodeIndex);
                nextDeltas[nodeIndex]
                        = this.calculateSingleHiddenErrorSignal(approxTargetActivation,
                                                            predictedActivation);
            }

            currentDeltas = nextDeltas;
            allSignalErrors[layerIndex] = currentDeltas;
        }

        return allSignalErrors;
    }

    final double[][][] getBackPropagationWeightChanges(double[][] allSignalErrors) {
        int numLayers = this.getNumLayers();

        double[][][] toRet = new double[numLayers][][];

        int currentLayerSize;
        int nextLayerSize;
        int nextLayerIndex;
        double destSignalError;
        double sourceActivation;
        boolean isNextBiasNode;
        boolean isCurrentBiasNode;
        double prevWeightChange;

        for (int layerIndex = 0;
                layerIndex < numLayers;
                layerIndex++) {

            nextLayerIndex = layerIndex + 1;

            if (nextLayerIndex >= numLayers) {
                break; // we are done
            }

            currentLayerSize = this.getNumNodesInLayer(layerIndex);
            nextLayerSize = this.getNumNodesInLayer(nextLayerIndex);

            toRet[layerIndex] = new double[currentLayerSize][nextLayerSize];

            for (int srcNodeIndex = 0;
                    srcNodeIndex < currentLayerSize;
                    srcNodeIndex++) {

                sourceActivation = this.getActivation(layerIndex, srcNodeIndex);

                for (int destNodeIndex = 0;
                        destNodeIndex < nextLayerSize;
                        destNodeIndex++) {

                    isCurrentBiasNode = this.isBiasNode(layerIndex, srcNodeIndex);
                    isNextBiasNode = this.isBiasNode(nextLayerIndex, destNodeIndex);

                    if (isCurrentBiasNode && isNextBiasNode) {
                        toRet[layerIndex][srcNodeIndex][destNodeIndex]
                                = 0; // 2 bias nodes are not supposed to
                                     // be linked at all so their weight
                                     // change should just be zero
                        continue;
                    }

                    prevWeightChange
                            = this.getPreviousWeightChange(layerIndex,
                                                           srcNodeIndex,
                                                           destNodeIndex);

                    destSignalError = allSignalErrors[nextLayerIndex][destNodeIndex];

                    toRet[layerIndex][srcNodeIndex][destNodeIndex]
                            = this.calculateWeightChange(destSignalError,
                                                         sourceActivation,
                                                         prevWeightChange);


                }

            }

        }

        return toRet;
    }

    /**
     * Returns weight change n + 1
     * @param outputSignalErrors The signal errors you got from the output.
     * @return A new 3D array with the new weights for this network
     */
    final double[][][] backPropagate(double[] outputSignalErrors) {

        double[][] allSignalErrors = this.getBackPropagationSignalErrors(outputSignalErrors);
        return this.getBackPropagationWeightChanges(allSignalErrors);
    }

    final double[] feedForward(final double[] activations) {

        double[] currentActivations = activations;
        double[] nextActivations;
        double nodeSummation;
        double nodeActivation;
        int layerSize;

        this.activations[0] = currentActivations;

        for (int layerIndex = 1;
                layerIndex < this.numLayers;
                layerIndex++) {
            layerSize = this.getNumNodesInLayer(layerIndex);
            nextActivations = new double[layerSize];

            for (int nodeIndex = 0;
                    nodeIndex < layerSize;
                    nodeIndex++) {

                if (this.isBiasNode(layerIndex, nodeIndex)) {
                    nodeActivation = 1;
                } else {
                    nodeSummation
                            = this.calculateSummation(layerIndex,
                            nodeIndex,
                            currentActivations);

                    nodeActivation = this.calculateSigmoid(nodeSummation);
                }

                nextActivations[nodeIndex] = nodeActivation;
            }

            currentActivations = nextActivations;
            this.activations[layerIndex] = currentActivations;
        }

        return currentActivations;
    }
}
