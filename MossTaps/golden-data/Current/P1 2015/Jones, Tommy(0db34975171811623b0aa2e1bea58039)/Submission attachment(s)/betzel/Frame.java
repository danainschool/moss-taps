package ravensproject.betzel;

import ravensproject.betzel.helpers.CollectionHelper;
import ravensproject.betzel.interfaces.IComparator;
import ravensproject.betzel.interfaces.IFrame;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by scott betzel on 5/30/15.
 *
 * This represents a Frame object that will be used to represent
 * the relationships of a single object within an Image.
 */
public class Frame implements IFrame {

    /**
     * This holds the names of valid linkage slots
     */
    private static List<String> linkageSlots;

    /**
     * This is the name of this Frame
     */
    private String _name;

    /**
     * The internal storage of our String values or IFrame objects
     */
    private HashMap<String, String> _hashMap;

    /**
     * This hashmap will be used to store linked Frames
     */
    private HashMap<String, List<IFrame>> _linkageHashMap;

    /**
     * This is the comparator to use when generating transition frames.
     */
    private IComparator _masterComparator;

    /**
     * This is the sum of all the attributes
     */
    private double _sum;

    /**
     * Source Frame - will only be populated if this is a transition frame
     */
    private IFrame _sourceFrame;

    /**
     * Destination Frame - will only be populated if this is a transition frame
     */
    private IFrame _destinationFrame;

    /**
     * Initialize the slot names that represent linkages to other frames
     */
    static {
        String[] temp = {"inside", "above", "overlaps"};

        linkageSlots = new ArrayList<>(temp.length);

        Collections.addAll(linkageSlots, temp);
    }

    /**
     * Constructor
     * @param name The name of this frame
     * @param comparator The comparator to use when generating TransitionFrames
     */
    public Frame(String name, IComparator comparator) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        this._name = name;
        this._hashMap = new HashMap<>();
        this._linkageHashMap = new HashMap<>();
        this._masterComparator = comparator;
        this._sum = 0.0;
    }

    /**
     * Constructor
     * @param name The name of this frame
     * @param hashMap The hashmap for this frame
     * @param comparator The comparator to use when generating TransitionFrames
     */
    public Frame(String name, HashMap<String, String> hashMap, IComparator comparator) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        if (hashMap == null) {
            throw new IllegalArgumentException("hashMap cannot be null");
        }

        this._name = name;
        this._hashMap = hashMap;
        this._linkageHashMap = new HashMap<>();
        this._masterComparator = comparator;
        this._sum = 0.0;
    }

    /**
     * Get the name of this frame
     * @return Returns the name of this frame
     */
    @Override
    public String getName() {
        return this._name;
    }

    /**
     * Store a String value in a slot
     * @param slotName The name of the slot
     * @param value The value for the slot
     */
    @Override
    public void put(String slotName, String value) {
        this._sum = 0.0;

        _hashMap.put(slotName, value);
    }

    /**
     * Get a value stored at slot
     * @param slotName The name of the slot
     * @return Returns either a string or an IFrame object value from the slot
     */
    @Override
    public String get(String slotName) throws Exception {
        return _hashMap.get(slotName);
    }

    /**
     * Returns true if the slotName provided is a linkage slot name
     * @param slotName the name of the slot you wish to check
     * @return Returns true if the slotName provided is a linkage slot name, false otherwise
     */
    @Override
    public boolean isSlotLinkage(String slotName) {
        return Frame.linkageSlots.contains(slotName);
    }

    /**
     * Returns all the links created from buildLinkages()
     * @param slotName the slotname you want to get the linkages for
     * @return A collection of IFrames that belong to this slot.
     */
    @Override
    public Iterable<IFrame> getLinks(String slotName) {
        if (!this.isSlotLinkage(slotName)) {
            throw new IllegalArgumentException(String.format("%s is not a linkage slot", slotName));
        }

        if (!this._linkageHashMap.containsKey(slotName)) {
            return new ArrayList<>();
        }

        return this._linkageHashMap.get(slotName);
    }

    /**
     * This will create links between frames
     * @param allFrames allFrames in the current Scope
     */
    @Override
    public void buildLinkages(Iterable<IFrame> allFrames) {

        List<IFrame> allFramesList = CollectionHelper.toList(allFrames);

        this._linkageHashMap.clear();

        String frameString;

        // Not the most efficient but the number is so small,
        // I think we will be alright
        // O(N^3)
        for (String key: this._hashMap.keySet()) {

            if (this.isSlotLinkage(key)) {

                for (IFrame frame: allFramesList) {

                    frameString = this._hashMap.get(key);

                    buildLinkageIfNeeded(frameString, key, frame);
                }
            }
        }
    }

    @Override
    public IFrame buildTransitionFrame(IFrame destinationFrame) throws Exception {
        Frame toRet = new Frame(String.format("%s_%s", this.getName(), destinationFrame.getName()), this._masterComparator);
        Iterable<String> sourceSlotNames = this.getAllSlotNames();
        Iterable<String> destSlotNames = destinationFrame.getAllSlotNames();
        Iterable<String> allSlotNames = CollectionHelper.union(sourceSlotNames, destSlotNames);

        String currentSourceSlotValue;
        String currentDestSlotValue;
        double currentDiff;

        toRet.put("transition", "true");

        for (String slotName: allSlotNames) {
            currentSourceSlotValue = this.get(slotName);
            currentDestSlotValue = destinationFrame.get(slotName);

//            toRet.put(String.format("from%s", slotName), currentSourceSlotValue);
//            toRet.put(String.format("to%s", slotName), currentDestSlotValue);

            if ((null == currentSourceSlotValue) || (null == currentDestSlotValue)) {
                toRet.put(slotName, "1.000");
            } else {
                currentDiff = this._masterComparator.compare(slotName, currentSourceSlotValue, currentDestSlotValue);
                toRet.put(slotName, String.format("%.3f", currentDiff));
            }
        }

        toRet.setSourceFrame(this);
        toRet.setDestinationFrame(destinationFrame);

        return toRet;
    }

    @Override
    public boolean slotsEquals(IFrame frame) throws Exception {
        return (this.getDiffCount(frame, false) == 0);
    }

    @Override
    public Iterable<String> getAllSlotNames() {
        return this._hashMap.keySet();
    }

    @Override
    public int getDiffCount(IFrame frame, boolean ignoreNavigation) throws Exception {
        String currentValue;
        String otherValue;
        int toRet = 0;

        List<String> frameKeys = CollectionHelper.toList(frame.getAllSlotNames());

        for (String key : frameKeys) {
//            if (Frame.isInformationalSlot(key)) {
//                continue;
//            }
//
//            if (ignoreNavigation && Frame.isNavigationSlot(key)) {
//                continue;
//            }

            if (!this._hashMap.containsKey(key)) {
                toRet++;
            }

            currentValue = this._hashMap.get(key);
            otherValue = frame.get(key);
            if ((otherValue == null) && (currentValue != null)) {
                toRet++;
            } else if ((currentValue == null) && (otherValue != null)) {
                toRet++;
            } else if (currentValue != null) {
                if (!otherValue.equals(currentValue)) {
                    toRet++;
                }
            }
        }

        for (String key : this.getAllSlotNames()) {
//            if (Frame.isInformationalSlot(key)) {
//                continue;
//            }
//
//            if (ignoreNavigation && Frame.isNavigationSlot(key)) {
//                continue;
//            }

            if (frameKeys.contains(key)) {
                continue;
            }

            currentValue = frame.get(key);
            otherValue = this._hashMap.get(key);
            if ((otherValue == null) && (currentValue != null)) {
                toRet++;
            } else if ((currentValue == null) && (otherValue != null)) {
                toRet++;
            } else if (currentValue != null) {
                if (!otherValue.equals(currentValue)) {
                    toRet++;
                }
            }
        }

        return toRet;
    }

    /**
     * This method will return the sum of all the attributes.
     * If an attribute cannot be parsed, its value is zero.
     *
     * @return Returns the sum of all the slots.
     */
    @Override
    public double getSum() {
        double toSet = 0.0;
        String currentSlotValue;
        double toAdd;

        for (String slotName: this.getAllSlotNames()) {
            if (isInformationalSlot(slotName)) {
                continue;
            }

            currentSlotValue = this._hashMap.get(slotName);

            try {
                toAdd = Double.parseDouble(currentSlotValue);
                toSet += toAdd;
            } catch (Exception ex) {
                System.err.print("Trouble parsing string as double ");
                System.err.println(ex.getMessage());
            }
        }

        return toSet;
    }



    public static boolean isInformationalSlot(String slotName) {
        return slotName.startsWith("from")
                || slotName.startsWith("to")
                || slotName.startsWith("transition")//;
                || slotName.startsWith("delete")
                || slotName.startsWith("add");
    }

    public static boolean isNavigationSlot(String slotName) {
        return slotName.startsWith("inside")
                || slotName.startsWith("above")
                || slotName.startsWith("overlaps");
    }

    /**
     * This gets the Math.abs difference of the sums of this
     * and frameToDiff.
     *
     * @param frameToDiff The difference of the Frame to diff
     * @return Returns a positive value containing the difference between
     * sums from this and frameToDiff
     */
    @Override
    public double getSumDiff(IFrame frameToDiff) {
//        return Math.abs(this.getSum() - frameToDiff.getSum());

        double toRet = 0.0;
        String ourCurrentSlotValue;
        String theirCurrentSlotValue;
        double ourValue;
        double theirValue;
        Set<String> slotSet = new HashSet<>();

        for (String slotName: this.getAllSlotNames()) {
            try {
                if (isInformationalSlot(slotName)) {
                    continue;
                }

                ourCurrentSlotValue = this.get(slotName);
                theirCurrentSlotValue = frameToDiff.get(slotName);

                if (theirCurrentSlotValue == null) {
                    theirCurrentSlotValue = "0.000";
                }

                ourValue = Double.parseDouble(ourCurrentSlotValue);
                theirValue = Double.parseDouble(theirCurrentSlotValue);

                toRet += Math.abs(theirValue - ourValue);

                slotSet.add(slotName);
            } catch (Exception ex) {
                System.err.print("Trouble parsing string as double ");
                System.err.println(ex.getMessage());
            }
        }

        for (String slotName: frameToDiff.getAllSlotNames()) {
            try {
                if (slotSet.contains(slotName)) {
                    continue;
                }

                if (isInformationalSlot(slotName)) {
                    continue;
                }

                ourCurrentSlotValue = this.get(slotName);
                theirCurrentSlotValue = frameToDiff.get(slotName);

                if (ourCurrentSlotValue == null) {
                    ourCurrentSlotValue = "0.000";
                }

                ourValue = Double.parseDouble(ourCurrentSlotValue);
                theirValue = Double.parseDouble(theirCurrentSlotValue);

                toRet += Math.abs(theirValue - ourValue);

                slotSet.add(slotName);
            } catch (Exception ex) {
                System.err.print("Trouble parsing string as double ");
                System.err.println(ex.getMessage());
            }
        }

        toRet = Math.round(toRet * 10.0) / 10.0;

        return toRet;
    }

    /**
     * This should be used to determine whether a given slot
     * on this slot contains the specified value
     *
     * @param slotName  The name of the slot
     * @param slotValue The value of the slot
     * @return Returns true if equal, false otherwise.
     */
    @Override
    public boolean attributeEquals(String slotName, String slotValue) {
        if (!this._hashMap.containsKey(slotName) || (null == slotValue)) {
            return false;
        }

        String mySlotValue = this._hashMap.get(slotName);

        if (null == mySlotValue) {
            return false;
        }

        return mySlotValue.toLowerCase().equals(slotValue.toLowerCase());
    }

    /**
     * This method will merge 2 transition frames together
     *
     * @param toMerge The frame you wish to merge
     * @return Returns the merged frames
     */
    @Override
    public IFrame mergeTransitions(IFrame toMerge) {
        Frame toRet = new Frame(this.getName(), this._masterComparator);

        List<String> ourSlotNames = CollectionHelper.toList(this.getAllSlotNames());
        List<String> theirSlotNames = CollectionHelper.toList(toMerge.getAllSlotNames());

        Iterable<String> uniqueSlotNames = CollectionHelper.union(ourSlotNames, theirSlotNames);
        Double currentOurSlotValue;
        Double currentTheirSlotValue;
        Double currentToSet;

        for (String key: uniqueSlotNames) {
            if (Frame.isInformationalSlot(key)) {
                continue;
            }

            try {

                if (ourSlotNames.contains(key) && theirSlotNames.contains(key)) {
                    try {
                        currentOurSlotValue = Double.parseDouble(this.get(key));
                        currentTheirSlotValue = Double.parseDouble(toMerge.get(key));

                        currentToSet = this.combineTransformAttrValues(currentOurSlotValue, currentTheirSlotValue);

                        toRet.put(key, String.format("%.3f", currentToSet));
                    } catch (Exception ex2) {
                        // If its not a double
                        toRet.put(key, this.get(key));
                    }
                } else if (ourSlotNames.contains(key) && !theirSlotNames.contains(key)) {
                    toRet.put(key, this.get(key));
                } else if (!ourSlotNames.contains(key) && theirSlotNames.contains(key)) {
                    toRet.put(key, toMerge.get(key));
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }

        return toRet;
    }

    @Override
    public void setName(String name) {
        this._name = name;
    }

    private double combineTransformAttrValues(double valueOne, double valueTwo) {
        double toRet = Math.abs(valueOne - valueTwo);

        while (toRet > 1.0) {
            toRet = toRet - 1.0;
        }

        return toRet;
    }

    /**
     * This method will build a linkage for a given linkage slot value
     * @param frameString the value of the current linkage slot.  Comma separated string of names.
     * @param key the key of the current linkage slot
     * @param frame the frame to see if its name is equal to a value in the frameString.
     */
    private void buildLinkageIfNeeded(String frameString, String key, IFrame frame) {
        String[] frameNames = frameString.split(",");
        List<IFrame> currentFrameList;

        for (String currentFrameName: frameNames) {

            if (frame.getName().equals(currentFrameName)) {

                if (this._linkageHashMap.containsKey(key)) {

                    currentFrameList = this._linkageHashMap.get(key);

                } else {

                    currentFrameList = new ArrayList<>();
                    this._linkageHashMap.put(key, currentFrameList);
                }

                currentFrameList.add(frame);
            }
        }
    }

    /**
     * Custom toString method
     * @return A String description of this frame.
     */
    @Override
    public String toString() {
        List<String> keys = CollectionHelper.toList(this._hashMap.keySet());
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder("");
        sb.append(String.format("Frame '%s'\n", this.getName()));
        sb.append("*********************\n");
        for (String key: keys) {
            sb.append(String.format("%s: %s\n", key, this._hashMap.get(key)));
        }

        sb.append("\n");

        if (this._sourceFrame != null) {
            sb.append("Source Frame\n");
            sb.append("*********************\n");
            sb.append(String.format("%s", this._sourceFrame.toString()));
            sb.append("\n");
        }

        if (this._destinationFrame != null) {
            sb.append("Destination Frame\n");
            sb.append("*********************\n");
            sb.append(String.format("%s", this._destinationFrame.toString()));
            sb.append("\n");
        }

        return sb.toString();

    }

    /**
     * This will return the frame that most closely matches this frame.
     *
     * @param framesToSearch The frames to search for the most similar
     * @return Returns the frame that most closely matches this frame
     * @throws Exception
     */
    public Pair<IFrame, Integer> getMostSimilarFrame(Iterable<IFrame> framesToSearch, boolean ignoreNavigation) throws Exception {

        List<IFrame> lstAllFrames = CollectionHelper.toList(framesToSearch);

        // If there is only one, it must be the closest
        if (lstAllFrames.size() == 1) {
            return new Pair<>(lstAllFrames.get(0), lstAllFrames.get(0).getDiffCount(this, ignoreNavigation));
        }

        IFrame firstFrame = lstAllFrames.get(0);
        Pair<IFrame, Integer> minPair = new Pair<>(firstFrame, firstFrame.getDiffCount(this, ignoreNavigation));
        int currentDiffCount;

        for (IFrame currentFrame: lstAllFrames) {
            currentDiffCount = currentFrame.getDiffCount(this, ignoreNavigation);

            if (minPair.snd > currentDiffCount) {
                minPair = new Pair<>(currentFrame, currentDiffCount);
            }
        }

        return minPair;
    }

    @Override
    public IFrame getSourceFrame() {
        return this._sourceFrame;
    }

    @Override
    public void setSourceFrame(IFrame sourceFrame) {
        this._sourceFrame = sourceFrame;
    }

    @Override
    public IFrame getDestinationFrame() {
        return this._destinationFrame;
    }

    @Override
    public void setDestinationFrame(IFrame destinationFrame) {
        this._destinationFrame = destinationFrame;
    }
}
