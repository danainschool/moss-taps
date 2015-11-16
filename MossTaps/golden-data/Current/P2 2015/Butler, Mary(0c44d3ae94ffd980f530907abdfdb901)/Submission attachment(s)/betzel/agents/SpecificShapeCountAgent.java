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
public class SpecificShapeCountAgent
    extends SimpleCountAgentBase {

    private String shape;

    public SpecificShapeCountAgent(String theName, String theShape) {
        super(theName);

        this.shape = theShape;
    }

    public String getShape() {
        return shape;
    }

    private int getNumberWithShape(RavensFigure figure) {

        HashMap<String, RavensObject> objects = figure.getObjects();
        RavensObject currObject;
        String currShape;
        int toRet = 0;

        for (String key: objects.keySet()) {
            currObject = objects.get(key);
            currShape = currObject.getAttributes().get("shape");

            if (null != currShape
                    && currShape.equalsIgnoreCase(this.shape)) {
                toRet++;
            }
        }

        return toRet;
    }



    @Override
    protected Map<String, Integer> getCounts(RavensProblem problem) throws Exception {
        HashMap<String, Integer> toRet = new HashMap<>();

        HashMap<String, RavensFigure> figureMap = problem.getFigures();
        RavensFigure figure;
        int numShapes;

        for (String key: figureMap.keySet()) {
            figure = figureMap.get(key);
            numShapes = this.getNumberWithShape(figure);

            toRet.put(key, numShapes);
        }

        return toRet;
    }

    @Override
    protected EqualityComparisonMode getEqualityComparisonMode() {
        return EqualityComparisonMode.EXACT_MATCH;
    }
}
