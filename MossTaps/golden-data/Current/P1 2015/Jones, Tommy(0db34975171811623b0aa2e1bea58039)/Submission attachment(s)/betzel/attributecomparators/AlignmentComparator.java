package ravensproject.betzel.attributecomparators;

import ravensproject.betzel.helpers.DiffHelper;
import ravensproject.betzel.interfaces.IAttributeComparator;

import java.util.HashMap;

/**
 * Created by scott betzel on 6/1/15.
 *
 * This class does the diff on the alignment attribute
 */
public class AlignmentComparator
        implements IAttributeComparator {

    /**
     * This stores the relationships between the values
     */
    private static HashMap<String, Double> _valueMap;

    static {
        _valueMap = new HashMap<>();
//        Value: top-right (2)
//        Value: bottom-right (2)
//        Value: bottom-left (3)
//        Value: top-left (2)

        // top-left
        _valueMap.put(DiffHelper.getKey("top-left", "top-right"), 0.25);
        _valueMap.put(DiffHelper.getKey("top-left", "bottom-right"), 0.50);
        _valueMap.put(DiffHelper.getKey("top-left", "bottom-left"), 0.75);
        _valueMap.put(DiffHelper.getKey("top-left", "top-left"), 0.0);

        // top-right
        _valueMap.put(DiffHelper.getKey("top-right", "top-right"), 0.0);
        _valueMap.put(DiffHelper.getKey("top-right", "bottom-right"), 0.25);
        _valueMap.put(DiffHelper.getKey("top-right", "bottom-left"), 0.50);
        _valueMap.put(DiffHelper.getKey("top-right", "top-left"), 0.75);

        // bottom-right
        _valueMap.put(DiffHelper.getKey("bottom-right", "top-right"), 0.75);
        _valueMap.put(DiffHelper.getKey("bottom-right", "bottom-right"), 0.0);
        _valueMap.put(DiffHelper.getKey("bottom-right", "bottom-left"), 0.25);
        _valueMap.put(DiffHelper.getKey("bottom-right", "top-left"), 0.50);

        // bottom-left
        _valueMap.put(DiffHelper.getKey("bottom-left", "top-right"), 0.50);
        _valueMap.put(DiffHelper.getKey("bottom-left", "bottom-right"), 0.75);
        _valueMap.put(DiffHelper.getKey("bottom-left", "bottom-left"), 0.0);
        _valueMap.put(DiffHelper.getKey("bottom-left", "top-left"), 0.25);
    }

    /**
     * This will return the name of the attribute
     *
     * @return This will return the name of the attribute
     */
    @Override
    public String get_name() {
        return "alignment";
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
//        if (fromAttributeValue.toLowerCase().equals(toAttributeValue.toLowerCase())) {
//            return 0.0;
//        }
//
//        String key = DiffHelper.getKey(fromAttributeValue, toAttributeValue);
//        if (!_valueMap.containsKey(key)) {
//            return 1.0;
//        }
//
//        return _valueMap.get(key);

        if (fromAttributeValue.toLowerCase().equals(toAttributeValue.toLowerCase())) {
            return 0.0;
        }

        double toRet;

        try {
            fromAttributeValue = this.convertToAngle(fromAttributeValue);
            toAttributeValue = this.convertToAngle(toAttributeValue);

            if (fromAttributeValue.equals("-1") || toAttributeValue.equals("-1")) {
                toRet = 1.0; // If either one is whack... say its way different
            } else {
                double fromDouble = Double.parseDouble(fromAttributeValue);
                double toDouble = Double.parseDouble(toAttributeValue);

                int fromInteger = getAttributeValueLessThan360(fromDouble);
                int toInteger = getAttributeValueLessThan360(toDouble);

                toRet = DiffHelper.getDiffValue(fromInteger, toInteger, 360);
            }
        } catch (Exception ex) {
            toRet = 1.0;
            System.err.println(ex.getMessage());
            System.err.println(ex.getStackTrace().toString());
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

    private String convertToAngle(String alignment) {
        alignment = alignment.toLowerCase();

        if (alignment.equals("bottom-right")) {
            return "45";
        }

        if (alignment.equals("bottom-left")) {
            return "135";
        }

        if (alignment.equals("top-left")) {
            return "225";
        }

        if (alignment.equals("top-right")) {
            return "315";
        }

        System.err.println("Unknown alignment detected");

        return "-1";
    }
}
