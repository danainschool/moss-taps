package ravensproject.betzel.agents;

import ravensproject.RavensFigure;
import ravensproject.RavensObject;
import ravensproject.RavensProblem;

import java.util.HashMap;
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
public class FillCountAgent
    extends SimpleCountAgentBase {


    public FillCountAgent(String theName) {
        super(theName);

    }

    private int getNumberWithFill(RavensFigure figure) {

        HashMap<String, RavensObject> objects = figure.getObjects();
        RavensObject currObject;
        String currFill;
        int toRet = 0;
        int halves = 0;

        for (String key: objects.keySet()) {
            currObject = objects.get(key);
            currFill = currObject.getAttributes().get("fill");

            if (null != currFill) {
                if (currFill.equalsIgnoreCase("yes")) {
                    toRet++;
                } else if (!currFill.equalsIgnoreCase("no")) {
                    halves++;
                }
            }
        }

        return toRet + (halves / 2);
    }



    @Override
    protected Map<String, Integer> getCounts(RavensProblem problem) throws Exception {
        HashMap<String, Integer> toRet = new HashMap<>();

        HashMap<String, RavensFigure> figureMap = problem.getFigures();
        RavensFigure figure;
        int numShapes;

        for (String key: figureMap.keySet()) {
            figure = figureMap.get(key);
            numShapes = this.getNumberWithFill(figure);

            toRet.put(key, numShapes);
        }

        return toRet;
    }

    @Override
    protected EqualityComparisonMode getEqualityComparisonMode() {
        return EqualityComparisonMode.EXACT_MATCH;
    }
}
