package ravensproject.betzel.agents;

import ravensproject.RavensFigure;
import ravensproject.RavensObject;
import ravensproject.RavensProblem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by scott betzel on 6/23/15.
 *
 * This agent will make predictions about
 * possible solutions by simply counting the number
 * of shapes in each Figure, noting if they are
 * increasing or decreasing, and then returning
 * only possible answers that meet the predicted
 * criteria.
 */
public class FrameValueCountAgent
    extends SimpleCountAgentBase {

    public FrameValueCountAgent(String theName) {
        super(theName);

    }



    @Override
    protected Map<String, Integer> getCounts(RavensProblem problem) throws Exception {
        HashMap<String, Integer> toRet = new HashMap<>();

        HashMap<String, RavensFigure> figureMap = problem.getFigures();
        RavensFigure figure;

        for (String key: figureMap.keySet()) {
            figure = figureMap.get(key);


        }

        return toRet;
    }

    @Override
    protected EqualityComparisonMode getEqualityComparisonMode() {
        return EqualityComparisonMode.EXACT_MATCH;
    }
}
