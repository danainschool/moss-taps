package ravensproject.betzel.interfaces;


/**
 * Created by scott betzel on 5/31/15.
 *
 * This interface will implement all AttributeComparators
 */
public interface IAttributeComparator {

    /**
     * This will return the name of the attribute
     * @return This will return the name of the attribute
     */
    String get_name();

    /**
     * This will return a number from 0 to 1 which represents how different
     * each attribute is.  0 means no difference, 1 means completely opposite
     * @param fromAttributeValue the value you are converting from
     * @param toAttributeValue the value you are converting to
     * @return Returns a double indicating the amount of differences.
     * 0 means no difference, 1 means completely opposite
     */
    double getDiff(String fromAttributeValue, String toAttributeValue);
}
