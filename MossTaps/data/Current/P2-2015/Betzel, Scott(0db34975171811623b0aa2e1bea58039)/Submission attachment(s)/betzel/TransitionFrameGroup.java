package ravensproject.betzel;

import ravensproject.RavensObject;
import ravensproject.betzel.helpers.StringHelper;

import java.util.*;

/**
 * Created by scott betzel on 6/26/15.
 *
 *
 */
public class TransitionFrameGroup {

    private FrameGroup sourceFrameGroup;
    private FrameGroup destFrameGroup;
    private HashMap<Frame, TransitionFrame> transitionFramesBySource;
    private HashMap<Frame, TransitionFrame> transitionFramesByDest;

    public TransitionFrameGroup(FrameGroup theSourceFrameGroup,
                                FrameGroup theDestFrameGroup,
                                Collection<TransitionFrame> transitionFrames) {
        this.sourceFrameGroup = theSourceFrameGroup;
        this.destFrameGroup = theDestFrameGroup;
        this.transitionFramesBySource = this.indexTransitionFramesBySource(transitionFrames);
        this.transitionFramesByDest = this.indexTransitionFramesByDest(transitionFrames);
    }

    private HashMap<Frame, TransitionFrame> indexTransitionFramesByDest(Collection<TransitionFrame> transitionFrames) {
        HashMap<Frame, TransitionFrame> toRet = new HashMap<>();

        for (TransitionFrame tf: transitionFrames) {
            toRet.put(tf.getSourceFrame(), tf);
        }

        return toRet;
    }

    private HashMap<Frame, TransitionFrame> indexTransitionFramesBySource(Collection<TransitionFrame> transitionFrames) {
        HashMap<Frame, TransitionFrame> toRet = new HashMap<>();

        for (TransitionFrame tf: transitionFrames) {
            toRet.put(tf.getDestinationFrame(), tf);
        }

        return toRet;
    }

    public FrameGroup getSourceFrameGroup() {
        return sourceFrameGroup;
    }

    public FrameGroup getDestFrameGroup() {
        return destFrameGroup;
    }

    public TransitionFrame getTransitionFromBySource(Frame source) {
        return this.transitionFramesBySource.get(source);
    }

    public TransitionFrame getTransitionFromByDestination(Frame destination) {
        return this.transitionFramesByDest.get(destination);
    }

    public int getDifferences(TransitionFrameGroup theGroup) {
        int toRet = 0;
        TransitionFrame mostSimilar;
        List<TransitionFrame> otherTransitionFrames
                = new ArrayList<>(theGroup.transitionFramesBySource.values());

        for (TransitionFrame tf: this.transitionFramesBySource.values()) {
            mostSimilar = tf.getMostSimilar(otherTransitionFrames);

            toRet += tf.getDifferences(mostSimilar);
            boolean wasRemoved = otherTransitionFrames.remove(mostSimilar);
            assert wasRemoved || (mostSimilar == null);
        }

        TransitionFrame emptyTransitionFrame;
        Frame emptyFrame;
        RavensObject emptyObject;
        int index = 0;

        for (TransitionFrame otherTFrame: otherTransitionFrames) {
            emptyObject = new RavensObject(String.format("Empty %d", index));
            emptyFrame = new Frame(emptyObject);
            emptyTransitionFrame = emptyFrame.createTransitionFrame(emptyFrame);
            toRet += emptyTransitionFrame.getDifferences(otherTFrame);
            index++;
        }

        return toRet;
    }

    public TransitionFrameGroup getMostSimiliar(Collection<TransitionFrameGroup> toSearch) {
        TransitionFrameGroup toRet = null;

        int minDiffs = Integer.MAX_VALUE;
        int current;

        for (TransitionFrameGroup tfg: toSearch) {
            current = this.getDifferences(tfg);

            if (current < minDiffs) {
                minDiffs = current;
                toRet = tfg;
            }
        }

        return toRet;
    }

    @Override
    public String toString() {
        ArrayList<String> toRet = new ArrayList<>();

        toRet.add(String.format("Transition Frame Group '%s'", this.getName()));

        for (TransitionFrame tf: this.transitionFramesBySource.values()) {
            toRet.add(tf.toString());
        }

        return StringHelper.buildTable(toRet);
    }

    public String getName() {
        return String.format("%s -> %s",
                this.sourceFrameGroup.getName(), this.destFrameGroup.getName());
    }
}
