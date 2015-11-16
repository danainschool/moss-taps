package ravensproject.betzel.agents;

import ravensproject.RavensFigure;
import ravensproject.RavensProblem;
import ravensproject.betzel.FrameGroup;
import ravensproject.betzel.TransitionFrameGroup;
import ravensproject.betzel.helpers.LoggingHelper;

import java.util.*;

/**
 * Created by scott betzel on 6/26/15.
 *
 *
 */
public class TransitionComparisonAgent
    extends AgentBase {

    private Map<String, FrameGroup> frameGroups;
    private String sourceFigure;
    private String destFigure;
    private String neighborFigure;
    private boolean verbose;

    public TransitionComparisonAgent(String theName,
                                     String theSourceFigure,
                                     String theDestFigure,
                                     String theNeighborFigure,
                                     boolean theVerbose) {
        super(theName);

        this.frameGroups = new HashMap<>();
        this.sourceFigure = theSourceFigure;
        this.destFigure = theDestFigure;
        this.neighborFigure = theNeighborFigure;
        this.verbose = theVerbose;
    }

    protected void populateFrameGroups(RavensProblem problem) {
        frameGroups.clear();

        FrameGroup current;

        for (RavensFigure figure: problem.getFigures().values()) {
            current = new FrameGroup(figure);
            frameGroups.put(figure.getName(), current);

        }

        for (FrameGroup fg: frameGroups.values()) {

            for (FrameGroup fg2: frameGroups.values()) {
                if (fg == fg2) {
                    continue;
                }

                fg.addTransitionFrame(fg2);
            }
        }
    }

    private void outputString(String toOutput) {
        if (this.verbose) {
            LoggingHelper.outputString(toOutput);
        }
    }

    private void outputCollection(Collection toOutput) {
        if (this.verbose) {
            LoggingHelper.outputCollection(toOutput);
        }
    }

    @Override
    public Set<Integer> solve(RavensProblem problem) throws Exception {
        this.populateFrameGroups(problem);

        TransitionFrameGroup mostSimilar
                = getMostSimilarTransitionFrameGroup(this.sourceFigure, this.destFigure, this.neighborFigure);

        FrameGroup solutionFG
                = mostSimilar.getDestFrameGroup();

        HashSet<Integer> toRet = new HashSet<>();
        toRet.add(Integer.parseInt(solutionFG.getName()));

        return toRet;
    }

    private TransitionFrameGroup getMostSimilarTransitionFrameGroup(String sourceName,
                                                                    String destName,
                                                                    String solutionNeighborName) {
        FrameGroup srcFrameGroup = this.frameGroups.get(sourceName); // D
        this.outputString(srcFrameGroup.toString());

        FrameGroup destFrameGroup = this.frameGroups.get(destName); // G
        this.outputString(destFrameGroup.toString());

        TransitionFrameGroup sourceToDest = srcFrameGroup.getTransitionFrameGroup(destName);
        this.outputString(sourceToDest.toString());

        FrameGroup solutionNeighborFrameGroup = this.frameGroups.get(solutionNeighborName);
        this.outputString(solutionNeighborFrameGroup.toString());

        Collection<TransitionFrameGroup> solutionNeighborTFGs
                = solutionNeighborFrameGroup.getSolutionTransitionFrameGroups(); // F
        this.outputCollection(solutionNeighborTFGs);

        TransitionFrameGroup mostSimilar
                = sourceToDest.getMostSimiliar(solutionNeighborTFGs);
        this.outputString("Most Similar");
        this.outputString("------------------------------------");
        this.outputString(mostSimilar.toString());

        return mostSimilar;
    }
}
