package ravensproject.betzel.attributecomparators;

import ravensproject.betzel.helpers.DiffHelper;
import ravensproject.betzel.interfaces.IAttributeComparator;

import java.util.HashMap;

/**
 * Created by scottbetzel on 6/1/15.
 *
 * This is the comparator for the fill attribute
 */
public class FillComparator implements IAttributeComparator {

    /**
     * These are the available values
     */
    private static String[] _values = {"right-half","no","yes","left-half","bottom-half","top-half"};

    /**
     * This hashmap will hold the relationships between values
     */
    private static HashMap<String, Double> _valueMap;

    static {
        _valueMap = new HashMap<>();

        // right-half
        _valueMap.put(DiffHelper.getKey("right-half", "right-half"), 0.0);
        _valueMap.put(DiffHelper.getKey("right-half", "no"), 0.5);
        _valueMap.put(DiffHelper.getKey("right-half", "yes"), 0.5);
        _valueMap.put(DiffHelper.getKey("right-half", "left-half"), 1.0);
        _valueMap.put(DiffHelper.getKey("right-half", "bottom-half"), 0.5);
        _valueMap.put(DiffHelper.getKey("right-half", "top-half"), 0.5);

        // no
        _valueMap.put(DiffHelper.getKey("no", "right-half"), 0.5);
        _valueMap.put(DiffHelper.getKey("no", "no"), 0.0);
        _valueMap.put(DiffHelper.getKey("no", "yes"), 1.0);
        _valueMap.put(DiffHelper.getKey("no", "left-half"), 0.5);
        _valueMap.put(DiffHelper.getKey("no", "bottom-half"), 0.5);
        _valueMap.put(DiffHelper.getKey("no", "top-half"), 0.5);

        // yes
        _valueMap.put(DiffHelper.getKey("yes", "right-half"), 0.5);
        _valueMap.put(DiffHelper.getKey("yes", "no"), 1.0);
        _valueMap.put(DiffHelper.getKey("yes", "yes"), 0.0);
        _valueMap.put(DiffHelper.getKey("yes", "left-half"), 0.5);
        _valueMap.put(DiffHelper.getKey("yes", "bottom-half"), 0.5);
        _valueMap.put(DiffHelper.getKey("yes", "top-half"), 0.5);

        // left-half
        _valueMap.put(DiffHelper.getKey("left-half", "right-half"), 1.0);
        _valueMap.put(DiffHelper.getKey("left-half", "no"), 0.5);
        _valueMap.put(DiffHelper.getKey("left-half", "yes"), 0.5);
        _valueMap.put(DiffHelper.getKey("left-half", "left-half"), 0.0);
        _valueMap.put(DiffHelper.getKey("left-half", "bottom-half"), 0.5);
        _valueMap.put(DiffHelper.getKey("left-half", "top-half"), 0.5);

        // bottom-half
        _valueMap.put(DiffHelper.getKey("bottom-half", "right-half"), 0.5);
        _valueMap.put(DiffHelper.getKey("bottom-half", "no"), 0.5);
        _valueMap.put(DiffHelper.getKey("bottom-half", "yes"), 0.5);
        _valueMap.put(DiffHelper.getKey("bottom-half", "left-half"), 0.5);
        _valueMap.put(DiffHelper.getKey("bottom-half", "bottom-half"), 0.0);
        _valueMap.put(DiffHelper.getKey("bottom-half", "top-half"), 1.0);

        // top-half
        _valueMap.put(DiffHelper.getKey("top-half", "right-half"), 0.5);
        _valueMap.put(DiffHelper.getKey("top-half", "no"), 0.5);
        _valueMap.put(DiffHelper.getKey("top-half", "yes"), 0.5);
        _valueMap.put(DiffHelper.getKey("top-half", "left-half"), 0.5);
        _valueMap.put(DiffHelper.getKey("top-half", "bottom-half"), 1.0);
        _valueMap.put(DiffHelper.getKey("top-half", "top-half"), 0.0);
    }

    /**
     * This will return the name of the attribute
     *
     * @return This will return the name of the attribute
     */
    @Override
    public String get_name() {
        return "fill";
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

        String key = DiffHelper.getKey(fromAttributeValue, toAttributeValue);
        if (!_valueMap.containsKey(key)) {
            return 1.0;
        }

        return _valueMap.get(key);
    }
}
