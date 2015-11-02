package ravensproject.betzel;

import ravensproject.betzel.helpers.DiffHelper;
import ravensproject.betzel.interfaces.IComparator;
import ravensproject.betzel.interfaces.IFrame;
import ravensproject.betzel.interfaces.IFrameGroup;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by scott betzel on 5/31/15.
 *
 * This represents a group of Frames who are all linked to one another.
 * This is analogous to the RavensFigure object.
 */
public class FrameGroup implements IFrameGroup {

    /**
     * The name of this FrameGroup
     */
    private String _name;

    /**
     * The frames that make up this FrameGroup
     */
    private Iterable<IFrame> _frames;

    /**
     * The frames that make up this FrameGroup indexed by a HashMap
     */
    private HashMap<String, IFrame> _hashMap;

    /**
     * All the transition frames for all the neighbors of this FrameGroup
     */
    private HashMap<String, HashMap<String, IFrame>> _neighborTransitionFrames;

    /**
     * This HashMap contains all the neighbors
     */
    private HashMap<String, IFrameGroup> _neighborHashMap;

    /**
     * The comparator
     */
    private IComparator _comparator;

    public boolean is_suspectMapping() {
        return _suspectMapping;
    }

    private boolean _suspectMapping;

    /**
     * Constructor for the FrameGroup object
     * @param name The name of this FrameGroup
     * @param frames The frames that make up this Frame Group
     */
    public FrameGroup(String name, Iterable<IFrame> frames) {
        this._name = name;
        this._frames = frames;
        this._hashMap = new HashMap<>();
        this._neighborTransitionFrames = new HashMap<>();
        this._neighborHashMap = new HashMap<>();
        this._comparator = new MasterComparator();
        this._suspectMapping = false;
    }

    /**
     * Get the name of this FrameGroup
     * @return the name of this FrameGroup
     */
    @Override
    public String get_name() {
        return _name;
    }

    /**
     * Get the frames that make up this frame group
     * @return All the frames of this FrameGroup
     */
    @Override
    public Iterable<IFrame> get_frames() {
        return _frames;
    }

    /**
     * This method will build all the initial relationships between the frames
     * for a particular Figure.
     */
    @Override
    public void initialize() {

        for (IFrame frame: this._frames) {
            frame.buildLinkages(this._frames);
            this._hashMap.put(frame.getName(), frame);
        }

    }

    /**
     * This returns the Frame with the specified name
     *
     * @param frameName The name of the frame you want to retrieve
     * @return The Frame with the specified name
     */
    @Override
    public IFrame getFrame(String frameName) {
        return this._hashMap.get(frameName);
    }

    /**
     * This will add a neighbor to this FrameGroup.
     * (which creates the transition Frames internally)
     *
     * @param frameGroup The FrameGroup to add as the neighbor
     */
    @Override
    public void addNeighbor(IFrameGroup frameGroup) throws Exception {
        if (this.containsNeighbor(frameGroup)) {
            this.removeNeighbor(frameGroup.get_name());
        }

        this._neighborHashMap.put(frameGroup.get_name(), frameGroup);
        Pair<IFrame, Integer> theirPair;
        IFrame theirFrame;
        String name;
        IFrame transitionFrame;
        HashMap<String, IFrame> currentSubHashMap;
        Iterable<IFrame> ourFrames = this.get_frames();
        int currentOccurrenceCount;
        // ourFrame / theirPair
        HashMap<IFrame, Pair<IFrame, Integer>> frameMappings = new HashMap<>();
        // theirFrame / OccurrenceCount
        HashMap<IFrame, Integer> theirFrameOccurrenceCount = new HashMap<>();

        /**
         * TODO: Clean this up.  This code is really lame.
         *
         *       You should really implement CompareTo on the frame object.
         *       or possibly add some sort of helper object.
         *
         *       Sorted collections would have helped out tremendously as well.
         */

        if (!this._neighborTransitionFrames.containsKey(frameGroup.get_name())) {
            currentSubHashMap = new HashMap<>();
            this._neighborTransitionFrames.put(frameGroup.get_name(), currentSubHashMap);
        } else {
            currentSubHashMap = this._neighborTransitionFrames.get(frameGroup.get_name());
        }

        // Get Mappings from Our Frames to Their Frames and keep track of the count
        for (IFrame ourFrame: ourFrames) {
            theirPair = ourFrame.getMostSimilarFrame(frameGroup.get_frames(), false);

            if (theirPair == null) {
                System.err.print("WARNING: No Pair found for Frame");
                continue;
            }

            frameMappings.put(ourFrame, theirPair);

            if (theirFrameOccurrenceCount.containsKey(theirPair.fst)) {
                currentOccurrenceCount = theirFrameOccurrenceCount.get(theirPair.fst);
                theirFrameOccurrenceCount.put(theirPair.fst, currentOccurrenceCount + 1);
            } else {
                theirFrameOccurrenceCount.put(theirPair.fst, 1);
            }
        }

        // Check for additions
        for (IFrame theirFrameToAdd: frameGroup.get_frames()) {
            if (theirFrameOccurrenceCount.containsKey(theirFrameToAdd)) {
                continue;
            }

            Frame toAdd = new Frame(theirFrameToAdd.getName(), _comparator);
            toAdd.put("added", "true");
            this._hashMap.put(toAdd.getName(), toAdd);

            int diffCount = toAdd.getDiffCount(theirFrameToAdd, false);
            Pair<IFrame, Integer> tempPair = new Pair<>(theirFrameToAdd, diffCount);
            frameMappings.put(toAdd, tempPair);

            theirFrameOccurrenceCount.put(tempPair.fst, 1);
        }

        for (IFrame ourFrame: frameMappings.keySet()) {
            theirPair = frameMappings.get(ourFrame);
            currentOccurrenceCount = theirFrameOccurrenceCount.get(theirPair.fst);

            if (currentOccurrenceCount == 1) {
                theirFrame = theirPair.fst;
            } else if (currentOccurrenceCount > 1) {
                theirFrame = this.retrieveMinDiffFrame(ourFrame, frameMappings);
            } else {
                System.err.println("WARNING: Current Occurrence Count < 1 which should never happen.");
                continue;
            }

            name = DiffHelper.getKey(ourFrame.getName(), theirFrame.getName());
            transitionFrame = ourFrame.buildTransitionFrame(theirFrame);
            currentSubHashMap.put(name, transitionFrame);
        }
    }

    private IFrame retrieveMinDiffFrame(IFrame ourFrame,
                                        HashMap<IFrame, Pair<IFrame, Integer>> ourFrameTheirFrameMapping) {

        Pair<IFrame, Integer> currentPair = ourFrameTheirFrameMapping.get(ourFrame);
        IFrame targetFrame = currentPair.fst;
        IFrame minFrame = null;
        Integer minDiff = Integer.MAX_VALUE;

        for (IFrame keyFrame: ourFrameTheirFrameMapping.keySet()) {
            currentPair = ourFrameTheirFrameMapping.get(keyFrame);

            if ((currentPair == null)
                    || (currentPair.fst != targetFrame)) {
                continue;
            }

            if (currentPair.snd < minDiff) {
                minFrame = keyFrame;
                minDiff = currentPair.snd;
            } else if (Objects.equals(currentPair.snd, minDiff)) {
                this._suspectMapping = true;
            }
        }

        if (ourFrame.equals(minFrame)) {
            return targetFrame;
        }

        IFrame toRet = new Frame(ourFrame.getName(), _comparator);
        toRet.put("deleted", "true");
        return toRet;
    }

    /**
     * This will return true if this IFrameGroup contains
     * the specified neighbor
     *
     * @param frameGroup The FrameGroup to check
     * @return true if it contains the neighbor, false otherwise
     */
    @Override
    public boolean containsNeighbor(IFrameGroup frameGroup) {
        return this._neighborHashMap.containsKey(frameGroup.get_name());
    }

    /**
     * This will return true if this IFrameGroup contains
     * the specified neighbor
     *
     * @param frameGroupName The name of the FrameGroup to check for
     * @return True if it contains the FrameGroup, False otherwise
     */
    @Override
    public boolean containsNeighbor(String frameGroupName) {
        return this._neighborHashMap.containsKey(frameGroupName);
    }

    /**
     * Remove the neighbor with the given name
     *
     * @param frameGroupName THe name of the frame group to remove
     * @return Returns the remove frame group
     */
    @Override
    public IFrameGroup removeNeighbor(String frameGroupName) {
        this._neighborTransitionFrames.remove(frameGroupName);

        return this._neighborHashMap.remove(frameGroupName);
    }

    /**
     * This will return the transition frames for the FrameGroup passed in
     *
     * @param destinationFrameGroup This is the destination FrameGroup
     * @return Returns the frameGroups pointing to the destination
     */
    @Override
    public HashMap<String, IFrame> getTransitionFrames(IFrameGroup destinationFrameGroup) {
        return this._neighborTransitionFrames.get(destinationFrameGroup.get_name());
    }


}
