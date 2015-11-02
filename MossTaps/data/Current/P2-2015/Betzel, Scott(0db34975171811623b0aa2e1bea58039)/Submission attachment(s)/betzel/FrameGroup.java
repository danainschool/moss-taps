package ravensproject.betzel;

import ravensproject.RavensFigure;
import ravensproject.RavensObject;
import ravensproject.betzel.helpers.LoggingHelper;
import ravensproject.betzel.helpers.StringHelper;

import java.util.*;

/**
 * Created by scott betzel on 6/26/15.
 *
 */
public class FrameGroup {

    private RavensFigure figure;
    private List<Frame> frames;
    private String name;
    private HashMap<String, TransitionFrameGroup> transitionFrameGroups;

    public FrameGroup(RavensFigure theFigure) {
        this.figure = theFigure;
        this.name = theFigure.getName();
        this.frames = this.createFrames(theFigure);
        this.transitionFrameGroups = new HashMap<>();
    }

    private List<Frame> createFrames(RavensFigure theFigure) {

        ArrayList<Frame> toRet = new ArrayList<>();
        Frame currentFrame;

        for (RavensObject ro: theFigure.getObjects().values()) {
            currentFrame = new Frame(ro);
            toRet.add(currentFrame);
        }

        for (Frame f: toRet) {
            f.linkFrames(toRet);
        }

        return toRet;
    }

    public RavensFigure getFigure() {
        return figure;
    }

    public List<Frame> getFrames() {
        return frames;
    }

    public String getName() {
        return name;
    }

    public void addTransitionFrame(FrameGroup toTransitionTo) {
        if (this == toTransitionTo) {
            return; // Can't transition to yourself.
        }

        TransitionFrameGroup temp = this.createTransitionFrameGroup(toTransitionTo);
        this.transitionFrameGroups.put(toTransitionTo.getName(), temp);
    }

    public TransitionFrameGroup getTransitionFrameGroup(String destinationName) {
        return this.transitionFrameGroups.get(destinationName);
    }

    public Collection<TransitionFrameGroup> getAllTransitionFrameGroups() {
        return this.transitionFrameGroups.values();
    }

    public Collection<TransitionFrameGroup> getSolutionTransitionFrameGroups() {
        Collection<TransitionFrameGroup> toRet = new ArrayList<>();

        for (String key: this.transitionFrameGroups.keySet()) {
            try {
                int temp = Integer.parseInt(key);
                toRet.add(this.transitionFrameGroups.get(key));
            } catch (Exception ex) {

            }
        }

        return toRet;
    }

    private TransitionFrameGroup createTransitionFrameGroup(FrameGroup toTransitionTo) {
        Frame mostSimilar;
        Set<TransitionFrame> toSet = new HashSet<>();
        List<Frame> toTransitionToFrames = new ArrayList<>(toTransitionTo.getFrames());

        for (Frame f: this.frames) {
            mostSimilar = f.getClosestMatchingFrame(toTransitionToFrames);

            toSet.add(f.createTransitionFrame(mostSimilar));
            boolean removeSuccess = toTransitionToFrames.remove(mostSimilar);
            assert mostSimilar == null || removeSuccess;
        }

        int x = 0;
        RavensObject emptyObject;
        Frame emptyFrame;

        if (toTransitionToFrames.size() > 0) {
            for (Frame f: toTransitionToFrames) {
                emptyObject = new RavensObject(String.format("Empty %d", x));
                emptyFrame = new Frame(emptyObject);
                toSet.add(emptyFrame.createTransitionFrame(f));
                x++;
            }
        }

        return new TransitionFrameGroup(this, toTransitionTo, toSet);
    }

    @Override
    public String toString() {
        ArrayList<String> allStrings = new ArrayList<>();
        allStrings.add(String.format("FrameGroup = '%s'", this.name));

        for (Frame frame: this.frames) {
            allStrings.add(frame.toString());
        }

        return StringHelper.buildTable(allStrings);
    }
}
