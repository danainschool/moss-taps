package ravensproject.betzel.attributecomparators;

import ravensproject.betzel.helpers.DiffHelper;
import ravensproject.betzel.interfaces.IAttributeComparator;

/**
 * Created by scott betzel on 6/1/15.
 *
 * This will be used to compare the above attribute (slot)
 */
public class InsideComparator
    implements IAttributeComparator {

    /**
     * This will return the name of the attribute
     *
     * @return This will return the name of the attribute
     */
    @Override
    public String get_name() {
        return "inside";
    }

    /**
     * This will return a number from 0 to 1 which represents how different
     * each attribute is.  0 means no difference, 1 means completely opposite
     *
     * @param fromAttributeValue the value you are converting from
     * @param toAttributeValue   the value you are converting to
     * @return Returns a double indicating the amount of differences.
     * 0 means no difference, 1 means completely opposite
     */
    @Override
    public double getDiff(String fromAttributeValue, String toAttributeValue) {
        /**
         * For above, its either the same or different.
         */
        return DiffHelper.performAllOrNothingComparison(fromAttributeValue, toAttributeValue);
    }
}
