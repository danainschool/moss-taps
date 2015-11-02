package ravensproject.betzel.interfaces;

import ravensproject.betzel.Pair;

/**
 * Created by scott betzel on 5/31/15.
 *
 * The IFrame interface
 */
public interface IFrame {
    String getName();

    void put(String slotName, String value);

    String get(String slotName) throws Exception;

    boolean isSlotLinkage(String slotName);

    Iterable<IFrame> getLinks(String slotName);

    void buildLinkages(Iterable<IFrame> insideFrame);

    IFrame buildTransitionFrame(IFrame destinationFrame) throws Exception;

    boolean slotsEquals(IFrame frame) throws Exception;

    Iterable<String> getAllSlotNames();

    int getDiffCount(IFrame frame, boolean ignoreNavigation) throws Exception;

    /**
     * This method will return the sum of all the attributes.
     * If an attribute cannot be parsed, its value is zero.
     * @return Returns the sum of all the slots.
     */
    double getSum();

    /**
     * This gets the Math.abs difference of the sums of this
     * and frameToDiff.
     * @param frameToDiff The difference of the Frame to diff
     * @return Returns a positive value containing the difference between
     * sums from this and frameToDiff
     */
    double getSumDiff(IFrame frameToDiff);

    /**
     * This should be used to determine whether a given slot
     * on this slot contains the specified value
     *
     * @param slotName The name of the slot
     * @param slotValue The value of the slot
     * @return Returns true if equal, false otherwise.
     */
    boolean attributeEquals(String slotName, String slotValue);

    /**
     * This method will merge 2 transition frames together
     *
     * @param toMerge The frame you wish to merge
     * @return Returns the merged frames
     */
    IFrame mergeTransitions(IFrame toMerge);

    void setName(String name);

    /**
     * This will return the frame that most closely matches this frame.
     *
     * @param framesToSearch The frames to search for the most similar
     * @return Returns the frame that most closely matches this frame
     * @throws Exception
     */
    Pair<IFrame, Integer> getMostSimilarFrame(Iterable<IFrame> framesToSearch, boolean ignoreNavigation) throws Exception;

    IFrame getSourceFrame();

    void setSourceFrame(IFrame sourceFrame);

    IFrame getDestinationFrame();

    void setDestinationFrame(IFrame destinationFrame);
}
