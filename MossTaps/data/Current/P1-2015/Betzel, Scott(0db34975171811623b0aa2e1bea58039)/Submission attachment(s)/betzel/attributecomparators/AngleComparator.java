package ravensproject.betzel.attributecomparators;

import ravensproject.betzel.helpers.DiffHelper;
import ravensproject.betzel.interfaces.IAttributeComparator;

/**
 * Created by scottbetzel on 6/1/15.
 */
public class AngleComparator implements IAttributeComparator {

    /**
     * This will return the name of the attribute
     *
     * @return This will return the name of the attribute
     */
    @Override
    public String get_name() {
        return "angle";
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
        if (fromAttributeValue.toLowerCase().equals(toAttributeValue.toLowerCase())) {
            return 0.0;
        }

        double toRet;

        try {
            double fromDouble = Double.parseDouble(fromAttributeValue);
            double toDouble = Double.parseDouble(toAttributeValue);

            int fromInteger = getAttributeValueLessThan360(fromDouble);
            int toInteger = getAttributeValueLessThan360(toDouble);

            toRet = DiffHelper.getDiffValue(fromInteger, toInteger, 360);

        } catch (Exception ex) {
            toRet = 1.0;
            System.err.println(ex.getMessage());
            System.err.println(ex.getStackTrace());
        }

        return toRet;
    }

    private int getAttributeValueLessThan360(double attributeValue) {
        while (attributeValue >= 360.0) {
            attributeValue -= 360.0;
        }

        int toRet = (int)attributeValue;
        return toRet;
    }
}
