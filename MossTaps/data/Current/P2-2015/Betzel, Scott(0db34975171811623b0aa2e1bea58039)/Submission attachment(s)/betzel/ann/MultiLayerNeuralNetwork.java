package ravensproject.betzel.ann;

/**
 * Created by Scott Betzel on 6/11/15.
 *
 * Attempt at Neural Network.
 *
 *
 *
 * Here is some pseudo code borrowed from
 * http://en.wikipedia.org/wiki/Backpropagation
 * ----------------------------------------------------------------------------
 *
 * initialize network weights (often small random values)
 *
 * do
 * forEach training example ex
 *
 *      // forward pass
 *      prediction = neural-net-output(network, ex)
 *      actual = teacher-output(ex)
 *      compute error (prediction - actual) at the output units
 *
 *      // backward pass
 *      compute \Delta w_h for all weights from hidden layer to output layer
 *
 *      // backward pass continued
 *      compute \Delta w_i for all weights from input layer to hidden layer
 *      update network weights // input layer not modified by error estimate
 *
 * until all examples classified correctly or another stopping criterion
 * satisfied
 *
 * return the network
 */
public class MultiLayerNeuralNetwork {

    //region STATIC FINAL VARIABLES

    /**
     * This represents the default max number of training
     * epochs.
     */
    private static final int DEFAULT_MAX_TRAINING_EPOCHS = 500;

    /**
     * This represents the default max training time in seconds.
     *
     * -1 represents infinite training time.
     */
    private static final int DEFAULT_MAX_TRAINING_TIME = -1;

    /**
     * This represents the default learning rate.
     */
    private static final double DEFAULT_LEARNING_RATE = 0.15;

    //endregion

    //region MEMBER VARIABLES

    /**
     * Max Training Epochs. -1 for no max.
     */
    private int maxTrainingEpochs;

    /**
     * Max Training Time Seconds. -1 for no max.
     */
    private int maxTrainingTimeSeconds;

    /**
     * Layer Setup describes the number of layers
     * along with the number of nodes for each layer.
     */
    private int[] layerSetup;

    /**
     * This controls the learning rate of the ANN.
     */
    private double learningRate;

    /**
     * These are the inputs.
     */
    private double[][] inputs;

    /**
     * These are the inputErrors associated with the inputs.
     */
    private double[][] inputErrors;

    /**
     * Stores whether or not this network has been initialized.
     */
    private boolean initialized;

    /**
     * This holds all our network nodes.
     */
    private MultiLayerNeuralNetworkNode[][] networkNodes;

    //endregion

    //region CONSTRUCTORS

    /**
     * Constructs a new MultiLayerNeuralNetwork object.
     *
     * @param theLayerSetup This parameter controls the the
     *                   number of layers of the network
     *                   by the length of the array.  The
     *                   number in each array shows how
     *                   many nodes are in each layer.
     */
    public MultiLayerNeuralNetwork(final int[] theLayerSetup) {
        this.layerSetup = theLayerSetup;
        this.maxTrainingEpochs = DEFAULT_MAX_TRAINING_EPOCHS;
        this.maxTrainingTimeSeconds = DEFAULT_MAX_TRAINING_TIME;
        this.learningRate = DEFAULT_LEARNING_RATE;
        this.initialized = false;
    }

    /**
     * Constructs a new MultiLayerNeuralNetwork object.
     *
     * @param theLayerSetup This parameter controls the the
     *                   number of layers of the network
     *                   by the length of the array.  The
     *                   number in each array shows how
     *                   many nodes are in each layer.
     * @param theMaxTrainingEpochs The maximum number of iterations
     *                             to perform over the training set
     *                             while training. -1 for no max.
     */
    public MultiLayerNeuralNetwork(final int[] theLayerSetup,
                                   final int theMaxTrainingEpochs) {
        this.layerSetup = theLayerSetup;
        this.maxTrainingEpochs = theMaxTrainingEpochs;
        this.maxTrainingTimeSeconds = DEFAULT_MAX_TRAINING_TIME;
        this.learningRate = DEFAULT_LEARNING_RATE;
        this.initialized = false;
    }

    /**
     * Constructs a new MultiLayerNeuralNetwork object.
     *
     * @param theLayerSetup This parameter controls the the
     *                   number of layers of the network
     *                   by the length of the array.  The
     *                   number in each array shows how
     *                   many nodes are in each layer.
     * @param theMaxTrainingEpochs The maximum number of iterations
     *                             to perform over the training set
     *                             while training. -1 for no max.
     * @param theMaxTrainingTimeSeconds The maximum number of seconds
     *                                  to wait while training.
     *                                  -1 for no max.
     */
    public MultiLayerNeuralNetwork(final int[] theLayerSetup,
                                   final int theMaxTrainingEpochs,
                                   final int theMaxTrainingTimeSeconds) {
        this.layerSetup = theLayerSetup;
        this.maxTrainingEpochs = theMaxTrainingEpochs;
        this.maxTrainingTimeSeconds = theMaxTrainingTimeSeconds;
        this.learningRate = DEFAULT_LEARNING_RATE;
        this.initialized = false;
    }

    /**
     * Constructs a new MultiLayerNeuralNetwork object.
     *
     * @param theLayerSetup This parameter controls the the
     *                   number of layers of the network
     *                   by the length of the array.  The
     *                   number in each array shows how
     *                   many nodes are in each layer.
     * @param theMaxTrainingEpochs The maximum number of iterations
     *                             to perform over the training set
     *                             while training. -1 for no max.
     * @param theMaxTrainingTimeSeconds The maximum number of seconds
     *                                  to wait while training.
     *                                  -1 for no max.
     * @param theLearningRate The learning rate for the ANN.
     */
    public MultiLayerNeuralNetwork(final int[] theLayerSetup,
                                   final int theMaxTrainingEpochs,
                                   final int theMaxTrainingTimeSeconds,
                                   final double theLearningRate) {
        this.layerSetup = theLayerSetup;
        this.maxTrainingEpochs = theMaxTrainingEpochs;
        this.maxTrainingTimeSeconds = theMaxTrainingTimeSeconds;
        this.learningRate = theLearningRate;
        this.initialized = false;
    }

    //endregion

    //region GETTERS AND SETTERS

    /**
     * This returns the max number of epochs
     * used during training.
     *
     * @return This returns the max number of epochs
     * used during training.
     */
    public final int getMaxTrainingEpochs() {
        return maxTrainingEpochs;
    }

    /**
     * This returns the max training time in seconds.
     *
     * @return This returns the max training time in seconds.
     */
    public final int getMaxTrainingTimeSeconds() {
        return maxTrainingTimeSeconds;
    }

    /**
     * This returns the learning rate for the ANN.
     *
     * @return This returns the learning rate for the ANN.
     */
    public final double getLearningRate() {
        return learningRate;
    }

    //endregion

    //region PRIVATE HELPERS

    /**
     * This method will initialize all the various arrays
     * based on the layerSetup member.
     */
    private void initialize() {

        if (this.initialized) {
            return;
        }

        this.initialized = true;

        int currentLayerLength = -1;
        int prevLayerLength;
        MultiLayerNeuralNetworkNode currentNode;
        this.inputs = new double[this.layerSetup.length][];
        this.inputErrors = new double[this.layerSetup.length][];
        this.networkNodes
                = new MultiLayerNeuralNetworkNode[this.layerSetup.length][];

        for (int layerIndex = 0;
                layerIndex < this.layerSetup.length;
                layerIndex++) {

            prevLayerLength = currentLayerLength;
            currentLayerLength = this.layerSetup[layerIndex];
            if (prevLayerLength < 0) {
                // If first layer, there will be 1-to-1 with
                // inputs.
                prevLayerLength = currentLayerLength;
            }

            this.inputs[layerIndex]
                    = new double[prevLayerLength];

            this.inputErrors[layerIndex]
                    = new double[prevLayerLength];

            this.networkNodes[layerIndex]
                    = new MultiLayerNeuralNetworkNode[currentLayerLength];

            for (int nodeIndex = 0;
                    nodeIndex < currentLayerLength;
                    nodeIndex++) {

                this.inputs[layerIndex][nodeIndex] = 0.0;
                this.inputErrors[layerIndex][nodeIndex] = 0.0;

                this.networkNodes[layerIndex][nodeIndex]
                        = new MultiLayerNeuralNetworkNode(
                            layerIndex,
                            nodeIndex,
                            DEFAULT_LEARNING_RATE);

                currentNode = this.networkNodes[layerIndex][nodeIndex];
                currentNode.initialize(this.inputs[layerIndex],
                                       this.inputErrors[layerIndex]);
            }

        }
    }

    /**
     * This subtracts the contents of 2 arrays.
     *
     * @param subtractee This is the array to get subtracted from.
     * @param subtracter This is the array to subtract.
     * @return Returns a new array containing the subtracted values.
     */
    private static double[] subtract(final double[] subtractee,
                                     final double[] subtracter) {

        assert subtractee.length == subtracter.length;

        double[] toRet = new double[subtractee.length];

        for (int i = 0; i < subtractee.length; i++) {
            toRet[i] = subtractee[i] - subtracter[i];
        }

        return toRet;
    }

    /**
     * This trains one record in the data set.
     *
     * @param dataSetInputs These are the inputs for one
     *                      record in the data set.
     * @param numAnswerColumns The number of columns on the end
     *                         that contain the answer.
     */
    private final void trainOne(final double[] dataSetInputs,
                               final int numAnswerColumns) {

        if (!this.initialized) {
            this.initialize();
        }

        double[] actualAnswer = new double[numAnswerColumns];
        System.arraycopy(dataSetInputs,
                         dataSetInputs.length - numAnswerColumns,
                         actualAnswer,
                         0,
                         numAnswerColumns);

        // First Feed Forward
        double[] predictedAnswer = this.feedForward(dataSetInputs);

//        assert predictedAnswer.length == actualAnswer.length;

        // Find Diffs in predicted vs actual
        double[] diffs = MultiLayerNeuralNetwork.subtract(actualAnswer,
                                                          predictedAnswer);
        // Back Propagate to find inputErrors
        backPropagate(diffs);

        // Update Network Weights
        updateNetworkWeights();
    }

    /**
     * After back propagation, iterate over every node
     * and update the weights.
     */
    private void updateNetworkWeights() {

        for (int layerIndex = 0;
                layerIndex < this.layerSetup.length;
                layerIndex++) {

            for (int nodeIndex = 0;
                    nodeIndex < this.layerSetup[layerIndex];
                    nodeIndex++) {

                this.networkNodes[layerIndex][nodeIndex].updateWeights();

            }

        }

    }

    /**
     * This runs backPropagation based on the output error.
     *
     * @param outputError This is the output error.
     */
    private void backPropagate(final double[] outputError) {

        int currentLayerLength;
        MultiLayerNeuralNetworkNode currentNode;
        double[] currentErrors = outputError;
        double[] tempErrors;
        double[] toSum;

        for (int layerIndex = this.layerSetup.length - 1;
             layerIndex >= 0;
             layerIndex--) {

            currentLayerLength = this.layerSetup[layerIndex];

            assert currentErrors != null;
            assert currentLayerLength == currentErrors.length;

            toSum = null;

            // Get outputs for the current layer
            for (int nodeIndex = 0;
                 nodeIndex < currentLayerLength;
                 nodeIndex++) {

                currentNode = this.networkNodes[layerIndex][nodeIndex];
                currentNode.setError(currentErrors[nodeIndex]);
                tempErrors = currentNode.getInputErrors();
                if (null == toSum) {
                    toSum = new double[tempErrors.length];
//                    Arrays.fill(toSum, 0.0);
                }

                for (int prevNodeIndex = 0;
                     prevNodeIndex < tempErrors.length;
                     prevNodeIndex++) {

                    toSum[prevNodeIndex]
                            = toSum[prevNodeIndex] + tempErrors[prevNodeIndex];
                }
            }

            currentErrors = toSum;
        }

    }

    //endregion

    //region PUBLIC API

    /**
     * Use this to train the neural network.
     *
     * @param trainingData This contains all the training data.
     * @param numAnswerColumns The number of columns on the end of the dataset
     *                         that contain the answer for a given row.
     */
    public final double[][] train(final double[][] trainingData,
                            final int numAnswerColumns) {

        assert trainingData != null;
        assert numAnswerColumns > 0;

        double[][] toRet = new double[trainingData.length][];

        for (int epochIndex = 0;
             epochIndex < this.maxTrainingEpochs;
             epochIndex++) {

            for (int dataRowIndex = 0;
                 dataRowIndex < trainingData.length;
                 dataRowIndex++) {

                this.trainOne(trainingData[dataRowIndex], numAnswerColumns);

            }
        }

        for (int dataRowIndex = 0;
                dataRowIndex < trainingData.length;
                dataRowIndex++) {

            toRet[dataRowIndex] = this.feedForward(trainingData[dataRowIndex]);
        }

        return toRet;
    }

    /**
     * This performs the feed forward step for the neural network.
     *
     * @param networkInputs This represents the global inputs.
     * @return Returns an array containing the final outputs.
     */
    public final double[] feedForward(final double[] networkInputs) {

//        assert networkInputs.length == this.inputs[0].length;

        int currentLayerLength;
        MultiLayerNeuralNetworkNode currentNode;
        double[] currentOutputs = networkInputs;

        for (int layerIndex = 0;
             layerIndex < this.layerSetup.length;
             layerIndex++) {

            // Populate the inputs from the outputs of the previous layer
            System.arraycopy(currentOutputs, 0,
                    this.inputs[layerIndex], 0,
                    this.inputs[layerIndex].length);

            currentLayerLength = this.layerSetup[layerIndex];

            // create a new outputs array
            currentOutputs = new double[currentLayerLength];

            // Get outputs for the current layer
            for (int nodeIndex = 0;
                 nodeIndex < currentLayerLength;
                 nodeIndex++) {

                currentNode = this.networkNodes[layerIndex][nodeIndex];

                currentOutputs[nodeIndex] = currentNode.calculateOutput();
            }
        }

        return currentOutputs;
    }

    //endregion
}
