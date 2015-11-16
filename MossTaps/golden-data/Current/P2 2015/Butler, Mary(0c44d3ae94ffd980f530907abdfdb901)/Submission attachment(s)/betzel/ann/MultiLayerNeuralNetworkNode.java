package ravensproject.betzel.ann;

import java.util.Random;

/**
 * Created by Scott Betzel on 6/12/15.
 *
 * This represents a single node in a MultiLayerNeuralNetwork
 */
public class MultiLayerNeuralNetworkNode {

    //region Static Final Variables

    /**
     * This is the default mean used to generate random
     * weights.
     */
    private static final double DEFAULT_GUASSIAN_MEAN = 0.0;

    /**
     * This is the default variance used to generate random
     * weights.
     */
    private static final double DEFAULT_GUASSIAN_VARIANCE = 1.0;

    /**
     * This represents the default learning rate.
     */
    private static final double DEFAULT_LEARNING_RATE = 0.10;

    //endregion

    //region Member Variables
    /**
     * A unique Id for this node.
     * 0 based.
     */
    private int nodeId;

    /**
     * A unique Id for the layer this
     * node belongs.
     * 0 based.
     */
    private int layerId;

    /**
     * List of inputs for this Node.
     */
    private double[] inputs;

    /**
     * List of weights for this Node, coming
     * from previous nodes.
     */
    private double[] weights;

    /**
     * These are the errors for the given inputs.
     */
    private double[] inputErrors;

    /**
     * The error of this node.
     */
    private double error;

    /**
     * The last calculated output for this node.
     */
    private double output;

    /**
     * The learning rate to use when modifying
     * the weights.
     */
    private double learningRate;

    /**
     * This will be used to create random weights.
     */
    private Random random;

    //endregion

    //region Constructors

    /**
     * Constructor for a MultiLayerNeuralNetworkNode.
     * @param theLayerId The ID of the layer this node belongs.
     * @param theNodeId The ID of the node.
     */
    public MultiLayerNeuralNetworkNode(final int theLayerId,
                                       final int theNodeId) {
        this.nodeId = theNodeId;
        this.layerId = theLayerId;
        this.inputs = null;
        this.inputErrors = null;
        this.weights = null;
        this.learningRate = DEFAULT_LEARNING_RATE;
        this.error = 0.0;
        this.output = 0.0;
        this.random = new Random();
    }

    /**
     * Constructor for a MultiLayerNeuralNetworkNode.
     * @param theLayerId The ID of the layer this node belongs.
     * @param theNodeId The ID of the node.
     * @param theLearningRate The learning rate for this node.
     */
    public MultiLayerNeuralNetworkNode(final int theLayerId,
                                       final int theNodeId,
                                       final double theLearningRate) {
        this.nodeId = theNodeId;
        this.layerId = theLayerId;
        this.inputs = null;
        this.inputErrors = null;
        this.weights = null;
        this.learningRate = theLearningRate;
        this.error = 0.0;
        this.output = 0.0;
        this.random = new Random();
    }

    //endregion

    //region Getters and Setters

    /**
     * Get the Node ID for this node.
     * @return Returns the Node ID for this node.
     */
    public final int getNodeId() {
        return nodeId;
    }

    /**
     * Get the layer ID for this node.
     * @return Returns the Layer ID for this node.
     */
    public final int getLayerId() {
        return layerId;
    }

    /**
     * Retrieve the learning rate for this node.
     *
     * @return Returns the learning rate for this node.
     */
    public final double getLearningRate() {
        return learningRate;
    }

    /**
     * Set the learning rate for this node.
     * @param theLearningRate The learning rate to set.
     */
    public final void setLearningRate(final double theLearningRate) {
        this.learningRate = theLearningRate;
    }

    /**
     * This should be used to set the error for this particular node.
     * If this node is an output node, the error would be the
     * calculatedOutput - actual.  If this is a hidden node,
     * the error should be the previous error * the weight of the
     * connection.  This method updates the input errors from
     * the output errors.  After you call this method,
     * use getInputErrors to get the input errors.
     *
     * @param outputError If this node is an output node, the error would be the
     * calculatedOutput - actual.  If this is a hidden node,
     * the error should be the previous error * the weight of the
     * connection.
     */
    public final void setError(final double outputError) {
        this.error = outputError;

        assert this.inputs.length == this.inputErrors.length;
        assert this.weights.length == this.inputErrors.length;

        for (int i = 0; i < this.weights.length; i++) {
            this.inputErrors[i] = this.error * this.weights[i];
        }
    }

    /**
     * Returns the output error for this node.
     *
     * @return Returns the output error for this node.
     */
    public final double getError() {
        return this.error;
    }

    /**
     * Get the array of the input errors.
     *
     * @return The array represents the error associated with
     * each input from the previous layer.  This means the
     * nodes position in the previous layer are the index.
     */
    public final double[] getInputErrors() {
        return this.inputErrors;
    }

    //endregion

    //region Private Helpers

    /**
     * This method represents our sigmoid function.
     *
     * @param x The x value to apply the sigmoid function to.
     * @return Returns a double value ranging from -1 to 1.
     */
    private double calculateSigmoid(final double x) {
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

    /**
     * This method calculates the updated weight for this node.
     *
     * @param previousWeight This is the previous weight value we are updating
     *                       from.
     * @param input This is the input associated with this previous weight.
     * @return Returns the updated weight value.
     */
    private double calculateUpdatedWeight(final double previousWeight,
                                          final double input) {
        double sigmoidDerivative
                = this.calculateSigmoidDerivative(this.output);

        double weightChange = (this.learningRate
                                * this.error
                                * sigmoidDerivative
                                * input);

        return previousWeight + weightChange;
    }

    /**
     * This calculates the weighted summation of all the inputs.
     * @return Returns each input * its given weight, summed together.
     */
    private double calculateWeightedSummation() {
        assert this.inputs.length == this.weights.length;

        double toRet = 0.0;

        for (int i = 0; i < inputs.length; i++) {
            toRet += this.inputs[i] * this.weights[i];
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

    //endregion

    //region Public Methods

    /**
     * This initializes the node with inputs and input errors.
     * This is so we can share common arrays to cut down on
     * a bit of the iterating and memory usage.
     *
     * @param theInputs The shared input array.
     * @param theInputErrors The shared input error array.
     */
    public final void initialize(final double[] theInputs,
                                 final double[] theInputErrors) {
        assert this.inputs == null;
        assert this.inputErrors == null;
        assert this.weights == null;
        assert theInputs != null;
        assert theInputErrors != null;
        assert theInputs.length == theInputErrors.length;

        this.inputs = theInputs;
        this.inputErrors = theInputErrors;
        this.weights = this.createRandomWeights(this.inputs.length);
    }

    /**
     * This calculates the output (y) for this particular
     * node.
     *
     * @return Returns the output based on the current
     * inputs and the current weights associated with
     * those inputs.
     */
    public final double calculateOutput() {
        this.output = this.calculateWeightedSummation();

        return this.calculateSigmoid(this.output);
    }

    /**
     * Once you calculate all the errors for each neuron,
     * the weights can be updated.
     */
    public final void updateWeights() {

        double toSet;

        for (int i = 0; i < this.weights.length; i++) {
            toSet = this.calculateUpdatedWeight(this.weights[i],
                    this.inputs[i]);
            this.weights[i] = toSet;
        }
    }

    //endregion
}
