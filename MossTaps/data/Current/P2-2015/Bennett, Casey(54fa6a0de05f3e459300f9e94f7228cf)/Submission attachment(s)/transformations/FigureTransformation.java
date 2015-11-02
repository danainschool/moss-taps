package ravensproject.transformations;

import ravensproject.PotentialSolution;

import java.util.*;

public class FigureTransformation implements HasCost {
    private final Map<String, Transformation> transformations;
    private final List<PotentialSolution.RObject> additions;

    public FigureTransformation(final Map<String, Transformation> transformations, final List<PotentialSolution.RObject> additions) {
        this.transformations = Collections.unmodifiableMap(transformations);
        this.additions = Collections.unmodifiableList(additions);
    }

    public PotentialSolution transform(final PotentialSolution figure) {
        final Map<String, Map<String, String>> toRet = new HashMap<>();
        for(final String key : figure.map.keySet()) {
            if(transformations.containsKey(key)) {
                toRet.put(key, transformations.get(key).transform(figure.map.get(key).getMap()));
            } else {
                toRet.put(key, figure.map.get(key).getMap());
            }
        }

        int num = 0;
        for(final PotentialSolution.RObject addition : additions) {
            toRet.put("ADD" + ++num, addition.getMap());
        }
        return new PotentialSolution(toRet);
    }

    public FigureTransformation forFigure(final Map<String, String> matches) {
        final Map<String, Transformation> newTransformations = new HashMap<>();
        for(Map.Entry<String, String> match : matches.entrySet()) {
            newTransformations.put(match.getValue(), transformations.getOrDefault(match.getKey(), new DeletionTransformation()));
        }
        return new FigureTransformation(newTransformations, additions);
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
