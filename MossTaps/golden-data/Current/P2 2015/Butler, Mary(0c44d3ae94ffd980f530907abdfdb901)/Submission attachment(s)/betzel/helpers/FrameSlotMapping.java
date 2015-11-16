package ravensproject.betzel.helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by scott betzel on 6/25/15.
 *
 */
public class FrameSlotMapping {

    private static String[] sizes = {"", "very small", "small", "medium", "large", "very large", "huge"};

    private static String[] alignments = {"", "bottom-right", "bottom-left", "top-left", "top-right"};

    private static String[] angles = {"", "0", "45", "90", "135", "180", "225", "270", "315"};

    private static String[] fills = {"", "no", "left-half", "bottom-half", "right-half", "top-half", "yes"};

    private static final String[] shapes = {
            "circle",
            "heart",
            "triangle",
            "right triangle",
            "pac-man",
            "rectangle",
            "diamond",
            "pentagon",
            "octagon",
            "star",
            "plus"
    };

    public String slotName;

    public HashMap<String, String> valueMap;

    public FrameSlotMapping(String theSlotName) {
        this.slotName = theSlotName;
        this.valueMap = createValueMap(theSlotName);
    }

    private static HashMap<String, String> createValueMap(String theSlotName) {
        HashMap<String, String> toRet = new HashMap<>();

        if (theSlotName.equalsIgnoreCase(SlotNames.ATTRIBUTE_SIZE)
                || theSlotName.equalsIgnoreCase(SlotNames.ATTRIBUTE_WIDTH)
                || theSlotName.equalsIgnoreCase(SlotNames.ATTRIBUTE_HEIGHT)) {
            mapCollection(sizes, toRet);
        } else if (theSlotName.equalsIgnoreCase(SlotNames.ATTRIBUTE_ALIGNMENT)) {
            mapCollection(alignments, toRet);
        } else if (theSlotName.equalsIgnoreCase(SlotNames.ATTRIBUTE_ANGLE)) {
            mapCollection(angles, toRet);
        } else if (theSlotName.equalsIgnoreCase(SlotNames.ATTRIBUTE_FILL)) {
            mapCollection(fills, toRet);
        } else if (theSlotName.equalsIgnoreCase(SlotNames.ATTRIBUTE_SHAPE)) {
            mapCollection(shapes, toRet);
        } else {
            assert false; // You should never get here.
        }

        return toRet;
    }

    private static void mapCollection(String[] toMap, Map<String, String> map) {
        for (String element: toMap) {
            addValueToMap(element, map);
        }
    }

    public String getSlotName() {
        return slotName;
    }

    public String getBinaryNumber(String theValue) {
        String valueToFetch = theValue;

        if (valueToFetch.equalsIgnoreCase("square")) {
            valueToFetch = "rectangle"; // BOOTLEG
        }

        return this.valueMap.get(valueToFetch);
    }

    public void addValue(String theValue) {
        addValueToMap(theValue, this.valueMap);
    }

    private static void addValueToMap(String theValue, Map<String, String> map) {
        if (map.containsKey(theValue)) {
            return;
        }

        int numItems = 0; // Zero is only for empty string.
        if (!theValue.equals("")) {
            numItems = map.size() + 1; // This should prevent zero
        }

        assert numItems <= 15;
        String binaryString = "0000" + Integer.toBinaryString(numItems);
        String toSet = binaryString.substring(binaryString.length() - 4, binaryString.length());
        map.put(theValue, toSet);
    }
}
