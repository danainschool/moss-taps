package ravensproject.betzel.interfaces;


import ravensproject.betzel.FrameGroup;

import java.util.HashMap;

/**
 * Created by scottbetzel on 5/31/15.
 */
public interface IFrameGroup {
    /**
     * Returns the name of this FrameGroup
     * @return Returns the name of this FrameGroup
     */
    String get_name();

    /**
     * This returns all the IFrames in this FrameGoup
     * @return Returns an Iterable of IFrames
     */
    Iterable<IFrame> get_frames();

    /**
     * This initializes your FrameGroup by
     * creating internal linkages
     */
    void initialize();

    /**
     * Get an internal Frame by name
     * @param frameName the name of the frame you want to retrieve
     * @return The IFrame you want
     */
    IFrame getFrame(String frameName);

    /**
     * This will add a neighbor to this FrameGroup.
     * (which creates the transition Frmaes internally)
     *
     * @param frameGroup The framegroup to add as the neighbor
     */
    void addNeighbor(IFrameGroup frameGroup) throws Exception;

    /**
     * This will return true if this IFrameGroup contains
     * the specified neighbor
     *
     * @param frameGroup The framegroup to check
     * @return true if it contains the neighbor, false otherwise
     */
    boolean containsNeighbor(IFrameGroup frameGroup);

    /**
     * This will return true if this IFrameGroup contains
     * the specified neighbor
     * @param frameGroupName The name of the FrameGroup to check for
     * @return True if it contains the FrameGroup, False otherwise
     */
    boolean containsNeighbor(String frameGroupName);

    /**
     * Remove the neighbor with the given name
     * @param frameGroupName THe name of the frame group to remove
     * @return Returns the remove frame group
     */
    IFrameGroup removeNeighbor(String frameGroupName);

    /**
     * This will return the transition frames for the FrameGroup passed in
     *
     * @param destinationFrameGroup This is the destination FrameGroup
     * @return Returns the frameGroups pointing to the destination
     */
    HashMap<String,IFrame> getTransitionFrames(IFrameGroup destinationFrameGroup);

    boolean is_suspectMapping();
}
