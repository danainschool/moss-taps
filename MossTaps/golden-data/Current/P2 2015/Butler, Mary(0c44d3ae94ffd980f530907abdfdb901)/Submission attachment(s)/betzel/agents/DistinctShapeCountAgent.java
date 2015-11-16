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
public class DistinctShapeCountAgent
    extends SimpleCountAgentBase {

    public DistinctShapeCountAgent(String theName) {
        super(theName);

    }

    private int getDistinctNumberOfShapes(RavensFigure figure) {

        HashMap<String, RavensObject> objects = figure.getObjects();
        HashSet<String> shapeSet = new HashSet<>();
        RavensObject currObject;
        String currShape;

        for (String key: objects.keySet()) {
            currObject = objects.get(key);
            currShape = currObject.getAttributes().get("shape");

            if (null != currShape) {
                shapeSet.add(currShape);
            }
        }

        return shapeSet.size();
    }



    @Override
    protected Map<String, Integer> getCounts(RavensProblem problem) throws Exception {
        HashMap<String, Integer> toRet = new HashMap<>();

        HashMap<String, RavensFigure> figureMap = problem.getFigures();
        RavensFigure figure;
        int numShapes;

        for (String key: figureMap.keySet()) {
            figure = figureMap.get(key);
            numShapes = this.getDistinctNumberOfShapes(figure);

            toRet.put(key, numShapes);
        }

        return toRet;
    }

    @Override
    protected EqualityComparisonMode getEqualityComparisonMode() {
        return EqualityComparisonMode.EXACT_MATCH;
    }
}
