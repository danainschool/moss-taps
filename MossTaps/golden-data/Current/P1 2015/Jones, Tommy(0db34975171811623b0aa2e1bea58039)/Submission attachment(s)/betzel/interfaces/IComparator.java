package ravensproject.betzel.interfaces;

/**
 * Created by scottbetzel on 6/1/15.
 */
public interface IComparator {

    /**
     * Perform Comparison for the given slotname, sourceValue, and destinationValue
     *
     *
     * @param slotName The name of the frame slot you wish to compare
     * @param sourceValue The value of the frame slot you wish to diff
     * @param destinationValue The value of the frame slot you wish to diff
     * @return The diff value. 0.0 means identical, 1.0 means different
     */
    double compare(String slotName, String sourceValue, String destinationValue);
}
