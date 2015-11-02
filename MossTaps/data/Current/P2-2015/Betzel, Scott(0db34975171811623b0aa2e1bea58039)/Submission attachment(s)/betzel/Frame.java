package ravensproject.betzel;

import ravensproject.RavensObject;
import ravensproject.betzel.helpers.LoggingHelper;
import ravensproject.betzel.helpers.SlotNames;
import ravensproject.betzel.helpers.StringHelper;

import java.util.*;

/**
 * Created by scott betzel on 6/23/15.
 *
 * Frame class
 */
public class Frame {

    private String name;
    private Map<String, FrameSlot> slots;


    public Frame(RavensObject theRavensObject) {
        this.name = theRavensObject.getName();
        this.slots = this.getFrameSlots(theRavensObject);
    }

    public void linkFrames(Collection<Frame> frames) {

        FrameSlot current;

        ArrayList<Frame> temp = new ArrayList<>(frames);
        temp.remove(this);

        for (String slotName: slots.keySet()) {
            current = this.slots.get(slotName);
            current.linkFrames(temp);
        }

    }

    private HashMap<String, String> normalizeAttributes(HashMap<String, String> originalAttributes) {
        HashMap<String, String> toRet = new HashMap<>(originalAttributes);

        if (toRet.containsKey(SlotNames.ATTRIBUTE_SHAPE)
                && toRet.get(SlotNames.ATTRIBUTE_SHAPE).equals("square"))
        {
            toRet.put(SlotNames.ATTRIBUTE_SHAPE, "rectangle");

            if (toRet.containsKey(SlotNames.ATTRIBUTE_SIZE)) {
                String size = toRet.remove(SlotNames.ATTRIBUTE_SIZE);
                toRet.put(SlotNames.ATTRIBUTE_WIDTH, size);
                toRet.put(SlotNames.ATTRIBUTE_HEIGHT, size);
            }
        }

        return toRet;
    }

    private Map<String, FrameSlot> getFrameSlots(RavensObject theRavensObject) {
        HashMap<String, String> attributes = normalizeAttributes(theRavensObject.getAttributes());
        HashMap<String, FrameSlot> toRet = new HashMap<>();

        // DEFAULTS
        toRet.put(SlotNames.ATTRIBUTE_SHAPE, new FrameSlot(SlotNames.ATTRIBUTE_SHAPE, ""));
        toRet.put(SlotNames.ATTRIBUTE_SIZE, new FrameSlot(SlotNames.ATTRIBUTE_SIZE, ""));
        toRet.put(SlotNames.ATTRIBUTE_WIDTH, new FrameSlot(SlotNames.ATTRIBUTE_WIDTH, ""));
        toRet.put(SlotNames.ATTRIBUTE_HEIGHT, new FrameSlot(SlotNames.ATTRIBUTE_HEIGHT, ""));
        toRet.put(SlotNames.ATTRIBUTE_FILL, new FrameSlot(SlotNames.ATTRIBUTE_FILL, ""));
        toRet.put(SlotNames.ATTRIBUTE_ANGLE, new FrameSlot(SlotNames.ATTRIBUTE_ANGLE, ""));
        toRet.put(SlotNames.ATTRIBUTE_ABOVE, new FrameSlot(SlotNames.ATTRIBUTE_ABOVE, ""));
        toRet.put(SlotNames.ATTRIBUTE_LEFT_OF, new FrameSlot(SlotNames.ATTRIBUTE_LEFT_OF, ""));
        toRet.put(SlotNames.ATTRIBUTE_OVERLAPS, new FrameSlot(SlotNames.ATTRIBUTE_OVERLAPS, ""));
        toRet.put(SlotNames.ATTRIBUTE_INSIDE, new FrameSlot(SlotNames.ATTRIBUTE_INSIDE, ""));

        for (String key: attributes.keySet()) {
            if (null == toRet.put(key, new FrameSlot(key, attributes.get(key)))) {
                LoggingHelper.logError(String.format("WARNING: Unknown attribute '%s' detected", key));
            }
        }

        return toRet;
    }

    public static Frame findFrameByName(Collection<Frame> frames, String name) {

        for (Frame f: frames) {
            if (name.equals(f.name)) {
                return f;
            }
        }

        return null;
    }

    public int getValueWithoutLinkages() {
        int toRet = 0;

        for (FrameSlot fs: this.slots.values()) {
            if (fs.isLinkage()) {
                continue;
            }

            toRet = toRet | fs.getNumericValue();
        }

        return toRet;
    }

    public int getNumberOfDifferences(Frame toCompare) {
        int toRet = 0;
        FrameSlot ourCurrentFrameSlot;
        FrameSlot theirCurrentFrameSlot;

        int ourValue;
        int theirValue;

        for (String key: this.slots.keySet()) {
            ourCurrentFrameSlot = this.slots.get(key);

            if (!toCompare.slots.containsKey(key)) {
                LoggingHelper.logError("WARNING: Comparing Frames with mismatching slots.");
                toRet++;
                continue;
            }

            theirCurrentFrameSlot = toCompare.slots.get(key);

            ourValue = ourCurrentFrameSlot.getNumericValueBasedOnLinkage();
            theirValue = theirCurrentFrameSlot.getNumericValueBasedOnLinkage();

            if (ourValue != theirValue) {
                toRet++;
            }
        }

        return toRet;
    }

    private int getMinDiffs(Collection<Frame> frames) {

        int minDiffs = Integer.MAX_VALUE;
        int currentDiffs;

        for (Frame f: frames) {
            currentDiffs = this.getNumberOfDifferences(f);

            if (currentDiffs < minDiffs) {
                minDiffs = currentDiffs;
            }
        }

        return minDiffs;
    }

    public Frame getClosestMatchingFrame(Collection<Frame> frames) {

        int minDiffs = this.getMinDiffs(frames);
        int currentDiffs;
        Frame minFrame = null;

        for (Frame f: frames) {
            currentDiffs = this.getNumberOfDifferences(f);

            if (minFrame == null && currentDiffs <= minDiffs) {
                minFrame = f;
            } else if (currentDiffs <= minDiffs) {
                LoggingHelper.logError("WARNING: Frames match exactly the same.");
            }
        }

        return minFrame;
    }

    public TransitionFrame createTransitionFrame(Frame otherFrame) {
        assert otherFrame == null || this.slots.size() == otherFrame.slots.size();

        HashMap<String, Integer> theTransitionMap = new HashMap<>();
        Frame theirFrame = otherFrame;
        FrameSlot ourCurrent;
        FrameSlot theirCurrent;
        int ourCurrentValue;
        int theirCurrentValue;
        int toSet;

        if (theirFrame == null) {
            theirFrame = new Frame(new RavensObject(String.format("Empty for %s", this.getName())));
        }

        for (String key: this.slots.keySet()) {
            ourCurrent = this.slots.get(key);

            if (theirFrame.slots.containsKey(key)) {
                theirCurrent = theirFrame.slots.get(key);

                ourCurrentValue = ourCurrent.getNumericValueBasedOnLinkage();
                theirCurrentValue = theirCurrent.getNumericValueBasedOnLinkage();
            } else {
                ourCurrentValue = ourCurrent.getNumericValueBasedOnLinkage();
                theirCurrentValue = 0;
            }

            toSet = theirCurrentValue - ourCurrentValue;

            theTransitionMap.put(key, toSet);
        }

        return new TransitionFrame(this, theirFrame, theTransitionMap);
    }

    @Override
    public String toString() {
        int valueWithoutLinkages = this.getValueWithoutLinkages();
        String representation = "0000000000000000000000000000000" + Integer.toString(valueWithoutLinkages, 16);
        representation
                = representation.substring(representation.length() - FrameSlot.getFrameSlotMappingSize(), representation.length());
        ArrayList<String> theStrings = new ArrayList<>();

        theStrings.add(String.format("Frame = '%s' [0x%s]", this.name, representation));

        for (FrameSlot fs: this.slots.values()) {
            theStrings.add(fs.toString());
        }

        return StringHelper.buildConstantWidthString(theStrings);
    }

    //region ACCESSORS

    public String getName() {
        return name;
    }

    //endregion


}
