package ravensproject.betzel;

import ravensproject.betzel.helpers.CollectionHelper;
import ravensproject.betzel.helpers.DiffHelper;
import ravensproject.betzel.interfaces.IFrame;
import ravensproject.betzel.interfaces.IFrameGroup;

import java.util.*;

import ravensproject.betzel.interfaces.ISolver;

/**
 * Created by scott betzel on 6/4/15.
 *
 * The FrameMatrix class
 */
public class FrameMatrix implements ISolver {

    /**
     * The name of this FrameMatrix
     */
    private final String _name;

    /**
     * Stores the IFrameGroup objects that make up this
     * FrameMatrix.  This makes up the Raven's Problem.
     */
    private IFrameGroup[][] _frameGroups;

    /**
     * This represents the potential solutions to the Raven's Problem.
     */
    private List<IFrameGroup> _solutionFrameGroups;

    /**
     * This represents all the solution neighbors
     */
    private List<IFrameGroup> _solutionNeighbors;

    /**
     * Constructor for the FrameMatrix
     * @param frameGroups The 2D array of IFrameGroup objects that will compose this
     *                    FrameMatrix
     * @param solutionFrameGroups These are the potential solutions.
     * @param name
     */
    public FrameMatrix(IFrameGroup[][] frameGroups, Iterable<IFrameGroup> solutionFrameGroups, String name) {
        this._frameGroups = frameGroups;
        this._solutionFrameGroups = CollectionHelper.toList(solutionFrameGroups);
        this._solutionNeighbors = new ArrayList<>();
        this._name = name;
    }

    /**
     * This will iterate over all the frames and make the names match
     *
     * This code is whack but, I need to just get this bad boy working.
     */
    private void normalizeFrameNames() throws Exception {
        IFrameGroup startingGroup = this._frameGroups[0][0];
        IFrame currentOtherFrame;

        HashMap<String, String> newNameHashMap = new HashMap<>();

        for (int x = 0; x < _frameGroups.length; x++) {

            for (int y = 0; y < _frameGroups[x].length; y++) {
                if (((x == 0) && (y == 0)) || (this._frameGroups[x][y] == null)) {
                    continue;
                }

                for (IFrame f: startingGroup.get_frames()) {
                    currentOtherFrame = f.getMostSimilarFrame(this._frameGroups[x][y].get_frames(), true).fst;

                    if (!newNameHashMap.containsKey(currentOtherFrame.getName())) {
                        newNameHashMap.put(currentOtherFrame.getName(), f.getName());
                    }

                    currentOtherFrame.setName(newNameHashMap.get(currentOtherFrame.getName()));
                }
            }
        }

        String currentValue;

        for (int x = 0; x < _frameGroups.length; x++) {

            for (int y = 0; y < _frameGroups[x].length; y++) {
                if (this._frameGroups[x][y] == null) {
                    continue;
                }

                for (IFrame f: this._frameGroups[x][y].get_frames()) {

                    for (String attributeKey: f.getAllSlotNames()) {

                        currentValue = f.get(attributeKey);

                        if (newNameHashMap.containsKey(currentValue)) {
                            f.put(attributeKey, newNameHashMap.get(currentValue));
                        }
                    }
                }
            }
        }
    }

    /**
     * This connects all the neighbors in the matrix
     * @throws Exception
     */
    public void initialize() throws Exception {


        int rightIndex;
        int bottomIndex;
        IFrameGroup currentFrameGroup;
        IFrameGroup rightFrameGroup;
        IFrameGroup bottomFrameGroup;
        IFrameGroup bottomRightFrameGroup;
        this._solutionNeighbors.clear();

        this.normalizeFrameNames();

        for (int x = 0; x < _frameGroups.length; x++) {

            for (int y = 0; y < _frameGroups[x].length; y++) {

                currentFrameGroup = _frameGroups[x][y];

                // Right IFrameGroup
                rightIndex = x + 1;
                if (rightIndex < _frameGroups.length) {
                    rightFrameGroup = _frameGroups[rightIndex][y];
                } else {
                    rightFrameGroup = null;
                }

                if (null != rightFrameGroup) {
                    currentFrameGroup.addNeighbor(rightFrameGroup);
                }

                // Bottom IFrameGroup
                bottomIndex = y + 1;
                if (bottomIndex < _frameGroups[x].length) {
                    bottomFrameGroup = _frameGroups[x][bottomIndex];
                } else {
                    bottomFrameGroup = null;
                }

                if (null != bottomFrameGroup) {
                    currentFrameGroup.addNeighbor(bottomFrameGroup);
                }

                // Bottom Right IFrameGroup
                if ((bottomIndex < _frameGroups[x].length) && (rightIndex < _frameGroups.length)) {
                    bottomRightFrameGroup = _frameGroups[rightIndex][bottomIndex];
                } else {
                    bottomRightFrameGroup = null;
                }

                if (null != bottomRightFrameGroup) {
                    currentFrameGroup.addNeighbor(bottomRightFrameGroup);
                }

                // Solutions
                if (this.isSolutionNeighbor(x, y)) {

                    // This is kind of lame but, I guess it will do
                    if (!this._solutionNeighbors.contains(currentFrameGroup)) {
                        this._solutionNeighbors.add(currentFrameGroup);
                    }

                    for (IFrameGroup solutionFrameGroup: this._solutionFrameGroups) {

                        currentFrameGroup.addNeighbor(solutionFrameGroup);
                    }
                }
            }
        }
    }

    private boolean isSolutionNeighbor(int x, int y) {
        return ((x == _frameGroups.length - 2) && (y == _frameGroups[x].length - 1))
                || ((x == _frameGroups.length - 1) && (y == _frameGroups[x].length - 2))
                || ((x == _frameGroups.length - 2) && (y == _frameGroups[x].length - 2));
    }

    @Override
    public int solve() {
        System.out.println(String.format("Solving %s", this._name));

        if ((this._frameGroups.length == 2) && (this._frameGroups[0].length == 2)) {
            return this.solve2D();
        }

        System.err.println("WARNING: Non 2D Matrices not implemented");
        return -1;
    }

    private void appendDashes(StringBuilder sb, int length) {
        for (int i = 0; i < length; i++) {
            sb.append("-");
        }
    }

    private void printFrames(String description, HashMap<String, IFrame> toPrint) {
        StringBuilder sb = new StringBuilder(description);

        sb.append("\n");

        this.appendDashes(sb, description.length());

        sb.append("\n");

        for (String key: toPrint.keySet()) {
            sb.append(key);
            sb.append("\n");
            this.appendDashes(sb, key.length());
            sb.append("\n");
            sb.append(toPrint.get(key).toString());
            sb.append("\n");
        }

        sb.append("\n\n");

        System.out.print(sb.toString());
    }

    private int solve2D() {
        // Get Transition Frames from 0,0 to 1,0
        HashMap<String, IFrame> topRowTransitionFrames = this._frameGroups[0][0].getTransitionFrames(this._frameGroups[1][0]);
        this.printFrames("Top Row Transition Frames", topRowTransitionFrames);

        // Get Transition Frames from 0,0 to 0,1
        HashMap<String, IFrame> firstColumnTransitionFrames = this._frameGroups[0][0].getTransitionFrames(this._frameGroups[0][1]);
        this.printFrames("First Column Transition Frames", firstColumnTransitionFrames);

        // Find Transition Frame to solution closely matching both transition frames
        HashMap<String, IFrame> combinedTransitionFrame = this.combineTransitionFrames(topRowTransitionFrames, firstColumnTransitionFrames);
        this.printFrames("Combined Transition Frames", combinedTransitionFrame);

        HashMap<String, IFrame> currentSolutionTransitionFrames = null;
        HashMap<String, IFrame> minSolutionTransitionFrames = null;

        IFrameGroup minFrameGroup = null;
        double minDiff = Double.MAX_VALUE;
        double currentDiff;
//        boolean isCurrentSuspect = false;
        HashSet<HashMap<String, IFrame>> ties = new HashSet<>();

        for (IFrameGroup fg: this._solutionFrameGroups) {
            currentSolutionTransitionFrames = this._frameGroups[0][0].getTransitionFrames(fg);

            this.printFrames(String.format("Solution %s Transition Frames", fg.get_name()), currentSolutionTransitionFrames);

            currentDiff = this.getTransitionFrameDiffs(combinedTransitionFrame.values(), currentSolutionTransitionFrames.values());

            if (currentDiff < minDiff) {
                minDiff = currentDiff;
                minFrameGroup = fg;
                minSolutionTransitionFrames = currentSolutionTransitionFrames;
                ties.clear();
            } else if (currentDiff == minDiff) {
                // Suspect
//                return -1;
                if (minSolutionTransitionFrames != null) {
                    ties.add(minSolutionTransitionFrames);
                }

                ties.add(currentSolutionTransitionFrames);
            }
        }

        if (ties.size() > 0) {
            System.out.print(ties);
//            debugger;
        }

        int toRet = -1;

        if (minFrameGroup != null) {
            try {
                toRet = Integer.parseInt(minFrameGroup.get_name());
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }

        return toRet;
    }

    private IFrame findFrameThatStartsWith(String toStartWith, Iterable<IFrame> framesToSearch) {
        for (IFrame frame: framesToSearch) {
            if (frame.getName().startsWith(toStartWith)) {
                return frame;
            }
        }

        return null;
    }

    private double getTransitionFrameDiffs(Iterable<IFrame> realTransitions, Iterable<IFrame> solutionTransitions) {
        double toRet = 0.0;

        IFrame currentSolutionTransitionFrame;

        for (IFrame realFrame: realTransitions) {
            try {
                currentSolutionTransitionFrame = this.findFrameThatStartsWith(realFrame.getName(), solutionTransitions);

                if (null == currentSolutionTransitionFrame) {
                    currentSolutionTransitionFrame  = realFrame.getMostSimilarFrame(solutionTransitions, false).fst;
                }

                toRet += realFrame.getSumDiff(currentSolutionTransitionFrame);
            } catch (Exception ex) {
                System.err.println("Error occurred searching for most similar frame");
            }
        }

        toRet = Math.round(toRet * 10.0) / 10.0;

        return toRet;
    }

    // This is lame
    private Iterable<String> getSourcesOrDests(Iterable<String> toSplit, int index) {
        if ((index != 0) && (index != 1)) {
            throw new IllegalArgumentException("index has to be 0 or 1.");
        }

        List<String> toRet = new ArrayList<>();
        String[] current;

        for (String str: toSplit) {
            current = DiffHelper.getOriginalKeys(str);
            toRet.add(current[index]);
        }

        return toRet;
    }

    private Iterable<IFrame> getFramesWithSource(HashMap<String, IFrame> transitionFrames, String source) {
        String[] temp;
        source = source.toLowerCase();
        List<IFrame> toRet = new ArrayList<>();

        for (String key: transitionFrames.keySet()) {
            key = key.toLowerCase();
            temp = DiffHelper.getOriginalKeys(key);

            if (temp[0].equals(source)) {
                toRet.add(transitionFrames.get(key));
            }
        }

        return toRet;
    }

    /**
     * This should combine transition frames that originate from the same source.
     * This probably needs to be refactored at some point because its not very efficient.
     *
     * @param transitionFramesOne The first group of transition frames to merge
     * @param transitionFramesTwo The second group of transition frames to merge
     * @return Returns a HashMap of transition frames where the source is the key
     * and the IFrame is the combined transitions frames.
     */
    private HashMap<String, IFrame> combineTransitionFrames(HashMap<String, IFrame> transitionFramesOne, HashMap<String, IFrame> transitionFramesTwo) {
        HashMap<String, IFrame> toRet = new HashMap<>();

        Iterable<String> oneSources = getSourcesOrDests(transitionFramesOne.keySet(), 0);
        Iterable<String> twoSources = getSourcesOrDests(transitionFramesTwo.keySet(), 0);

        Iterable<String> sources = CollectionHelper.union(oneSources, twoSources);
        Iterable<IFrame> currentFramesWithSourceOne;
        Iterable<IFrame> currentFramesWithSourceTwo;
        Iterable<IFrame> allFramesFromSource;
        IFrame currentMasterFrame;

        for (String source: sources) {
            currentFramesWithSourceOne = this.getFramesWithSource(transitionFramesOne, source);
            currentFramesWithSourceTwo = this.getFramesWithSource(transitionFramesTwo, source);
            allFramesFromSource = CollectionHelper.union(currentFramesWithSourceOne, currentFramesWithSourceTwo);

            currentMasterFrame = null;

            for (IFrame currentFrame: allFramesFromSource) {
                if (currentMasterFrame == null) {
                    currentMasterFrame = currentFrame;
                }

                if (currentFrame == currentMasterFrame) {
                    continue;
                }

                currentMasterFrame = currentMasterFrame.mergeTransitions(currentFrame);
            }

            if (currentMasterFrame != null) {
                currentMasterFrame.setName(source);
                toRet.put(source, currentMasterFrame);
            }
        }

        return toRet;
    }
}
