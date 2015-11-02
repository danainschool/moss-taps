package ravensproject.transformations;

import ravensproject.RavensFigure;

import java.util.*;

public class FigureTransformation implements HasCost {
    private final Map<String, Transformation> transformations;

    public FigureTransformation(final Map<String, Transformation> transformations) {
        this.transformations = Collections.unmodifiableMap(transformations);
    }

    public Map<String, Map<String, String>> transform(final RavensFigure figure) {
        final Map<String, Map<String, String>> toRet = new HashMap<>();
        for(final String key : figure.getObjects().keySet()) {
            if(transformations.containsKey(key)) {
                toRet.put(key, transformations.get(key).transform(figure.getObjects().get(key).getAttributes()));
            } else {
                toRet.put(key, figure.getObjects().get(key).getAttributes());
            }
        }
        return Collections.unmodifiableMap(toRet);
    }

    public FigureTransformation forFigure(final Map<String, String> matches) {
        final Map<String, Transformation> newTransformations = new HashMap<>();
        for(Map.Entry<String, String> match : matches.entrySet()) {
            newTransformations.put(match.getValue(), transformations.getOrDefault(match.getKey(), new DeletionTransformation()));
        }
        return new FigureTransformation(newTransformations);
    }

    @Override
    public int getCost() {
        int cost = 0;
        for(Transformation transformation : transformations.values()) {
            cost += transformation.getCost();
        }
        return cost;
    }
}
