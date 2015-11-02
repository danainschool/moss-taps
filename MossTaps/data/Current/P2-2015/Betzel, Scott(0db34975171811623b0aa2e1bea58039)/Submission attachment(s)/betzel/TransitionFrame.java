package ravensproject.betzel;

import ravensproject.betzel.helpers.StringHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by scott betzel on 6/26/15.
 *
 *
 */
public class TransitionFrame {

    private Frame sourceFrame;
    private Frame destinationFrame;
    private HashMap<String, Integer> diffSlots;

    public TransitionFrame(Frame theSourceFrame,
                           Frame theDestinationFrame,
                           HashMap<String, Integer> diffSlots) {

        this.sourceFrame = theSourceFrame;
        this.destinationFrame = theDestinationFrame;
        this.diffSlots = diffSlots;
    }

    public Frame getSourceFrame() {
        return sourceFrame;
    }

    public Frame getDestinationFrame() {
        return destinationFrame;
    }

    public int getDifferences(TransitionFrame toCompare) {

        int toRet = 0;

        HashMap<String, Integer> myDiffSlots = this.diffSlots;
        HashMap<String, Integer> otherDiffSlots = null;
        if (toCompare != null) {
            otherDiffSlots = toCompare.diffSlots;
            assert myDiffSlots.size() == otherDiffSlots.size();
        } else {
            otherDiffSlots = new HashMap<>();
        }

        int myValue;
        int otherValue;

        for (String key: myDiffSlots.keySet()) {
            if (!otherDiffSlots.containsKey(key)) {
                toRet++;
                continue;
            }

            myValue = myDiffSlots.get(key);
            otherValue = otherDiffSlots.get(key);

            if (otherValue != myValue) {
                toRet++;
            }
        }

        return toRet;
    }

    public TransitionFrame getMostSimilar(Collection<TransitionFrame> transitionFrames) {
        TransitionFrame toRet = null;
        int currentDifference;
        int minValue = Integer.MAX_VALUE;

        for (TransitionFrame tf: transitionFrames) {
            currentDifference
                    = this.getDifferences(tf);
//                        + this.sourceFrame.getNumberOfDifferences(tf.getSourceFrame())
//                        + this.destinationFrame.getNumberOfDifferences(tf.getDestinationFrame());

            if (currentDifference < minValue) {
                minValue = currentDifference;
                toRet = tf;
            }
        }

        return toRet;
    }

    @Override
    public String toString() {
        ArrayList<String> temp = new ArrayList<>();
        temp.add(String.format("Transition Frame = '%s -> %s'",
                this.sourceFrame.getName(), this.destinationFrame.getName()));
        int diffs;
        int oppositeDiffs;
        int mappingSize = FrameSlot.getFrameSlotMappingSize();
        String toAdd;

        for (String key: this.diffSlots.keySet()) {
            diffs = this.diffSlots.get(key);
            oppositeDiffs = -1 * diffs;
            toAdd = String.format("%s: [0x%0" + mappingSize + "X] (-[0x%08X])",
                    key, diffs, oppositeDiffs);
            temp.add(toAdd);
        }

        return StringHelper.buildConstantWidthString(temp);
    }
}
