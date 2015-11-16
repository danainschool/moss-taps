package ravensproject.betzel;

import ravensproject.betzel.helpers.FrameSlotMapping;
import ravensproject.betzel.helpers.SlotNames;
import ravensproject.betzel.helpers.StringHelper;

import java.util.*;

/**
 * Created by scott betzel on 6/24/15.
 *
 *
 */
public class FrameSlot {

    private static List<String> linkageAttributes;

    private static TreeMap<String, FrameSlotMapping> mappings;

    private final String name;

    private int nameIndex;

    private final String value;

    private final List<Frame> linkedFrames;

    static {
        mappings = new TreeMap<>();

        linkageAttributes = new ArrayList<>();
        linkageAttributes.add(SlotNames.ATTRIBUTE_LEFT_OF);
        linkageAttributes.add(SlotNames.ATTRIBUTE_ABOVE);
        linkageAttributes.add(SlotNames.ATTRIBUTE_OVERLAPS);
        linkageAttributes.add(SlotNames.ATTRIBUTE_INSIDE);
    }

    public FrameSlot(String theName, String theValue) {
        this.name = theName;
        this.value = theValue;
        this.linkedFrames = new ArrayList<>();
        this.nameIndex = -1;

        FrameSlot.addValue(theName, theValue);
    }

    private static void addValue(String theName, String theValue) {
        if (isLinkageAttribute(theName)) {
            return;
        }

        if (!mappings.containsKey(theName)) {
            mappings.put(theName, new FrameSlotMapping(theName));
        }

        FrameSlotMapping theMapping = mappings.get(theName);
        theMapping.addValue(theValue);
    }

    public static boolean isLinkageAttribute(String theName) {
        if (linkageAttributes.contains(theName)) {
            return true;
        }

        return false;
    }

    public final String getName() {
        return this.name;
    }

    public final String getValue() {
        return this.value;
    }

    public final void linkFrames(Collection<Frame> frames) {
        if (!isLinkageAttribute(this.name)) {
            return;
        }

        if (this.value.equals("")) {
            return;
        }

        String[] frameNames = this.value.split(",");

        for (String frameName: frameNames) {
            Frame currentFrame = Frame.findFrameByName(frames, frameName);
            assert currentFrame != null; // This should never happen
            this.linkedFrames.add(currentFrame);
        }
    }

    public boolean isLinked(Frame toFind) {
        boolean toRet = false;

        for (Frame f: this.linkedFrames) {
            if (f.equals(toFind)) {
                toRet = true;
                break;
            }
        }

        return toRet;
    }

    private int getNameIndex() {
        if (this.nameIndex == -1) {
            this.nameIndex = this.getIndexInMapping();
        }

        return this.nameIndex;
    }

    public static int getFrameSlotMappingSize() {
        return mappings.size();
    }

    private int getIndexInMapping() {
        int toRet = -1;
        int index = 0;

        for (String key: mappings.keySet()) {
            if (key.equals(this.name)) {
                toRet = index;
                break;
            }
            index++;
        }

        return toRet;
    }

    @Override
    public String toString() {
        if (this.isLinkage()) {
            return String.format("%s: %s [0x%0" + mappings.size() + "X] -> (%d Linked Frames)",
                    this.name,
                    this.value,
                    this.getNumericLinkageValue(),
                    this.linkedFrames.size());

        }

//        FrameSlotMapping current = mappings.get(this.name);
        String binaryNumber = this.getNumericValueString();
//        String hex = Integer.toString(, 16);

        return String.format("%s: %s [0x%0" + mappings.size() + "X] -> (%d Linked Frames)",
                this.name,
                this.value,
                Integer.parseInt(binaryNumber, 2),
                this.linkedFrames.size());
    }

    private static String getNumericValueString(String name, String value) {
        assert !isLinkageAttribute(name);

        StringBuilder temp = new StringBuilder("");
        FrameSlotMapping current;
        String binaryNumber;

        for (String key: mappings.descendingKeySet()) {

            assert !isLinkageAttribute(name); // There should be no linkage attributes in here

            if (key.equals(name)) {
                current = mappings.get(name);
                binaryNumber = current.getBinaryNumber(value);

                assert binaryNumber != null;
                assert binaryNumber.length() == 4;

                temp.append(binaryNumber);
            } else {
                temp.append("0000");
            }
        }

        return temp.toString();
    }

    private static int getNumericValue(String name, String value) {
        assert !isLinkageAttribute(name);

        String temp = getNumericValueString(name, value);
        return Integer.parseInt(temp, 2);
    }

    public String getNumericValueString() {
        assert !isLinkageAttribute(this.name);

        return getNumericValueString(this.name, this.value);
    }

    public int getNumericValue() {
        assert !isLinkageAttribute(this.name);

        return getNumericValue(this.name, this.value);
    }

    public int getNumericLinkageValue() {
        assert isLinkageAttribute(this.name);

        if (this.linkedFrames.size() <= 0) {
            return 0;
        }

        return this.linkedFrames.get(0).getValueWithoutLinkages();
    }

    public int getNumericValueBasedOnLinkage() {
        if (this.isLinkage()) {
            return this.getNumericLinkageValue();
        }

        return this.getNumericValue();
    }

    public boolean isLinkage() {
        return isLinkageAttribute(this.name);
    }


}
