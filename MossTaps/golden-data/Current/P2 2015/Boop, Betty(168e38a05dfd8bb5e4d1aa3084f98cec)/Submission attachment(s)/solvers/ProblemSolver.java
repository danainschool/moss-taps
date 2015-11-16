package ravensproject.solvers;

import ravensproject.PotentialSolution;
import ravensproject.PotentialSolution.RObject;
import ravensproject.Utils;
import ravensproject.transformations.*;

import java.util.*;
import java.util.stream.Collectors;

public final class ProblemSolver {
    private static final int MAX_DEPTH = 3;

    private ProblemSolver() {
        //Namespace only for static methods
    }

    private static List<Transformation> getRightAngleRotationTransformations() {
        final List<Transformation> toRet = new ArrayList<>(3);
        for(int i = 90; i < 360; i += 90) {
            toRet.add(new RotationTransformation(i));
        }
        return Collections.unmodifiableList(toRet);
    }

    private static List<Transformation> get45DegreeRotationTransformations() {
        final List<Transformation> toRet = new ArrayList<>(4);
        for(int i = 45; i < 360; i += 90) {
            toRet.add(new RotationTransformation(i));
        }
        return Collections.unmodifiableList(toRet);
    }

    private static List<Transformation> getScaleTransformations() {
        final int maxDistance = 3;
        final List<Transformation> toRet = new ArrayList<>(maxDistance * 2);
        for(int i = 1; i <= maxDistance; i++) {
            toRet.add(new ScaleTransformation(i));
            toRet.add(new ScaleTransformation(-i));
        }
        return Collections.unmodifiableList(toRet);
    }

    private static List<Transformation> getMoveTransformations() {
        final int maxDistance = 1;
        final List<Transformation> toRet = new ArrayList<>(maxDistance * Axis.values().length * 2);
        for(int i = 1; i <= maxDistance; i++) {
            for(final Axis axis : Axis.values()) {
                toRet.add(new MoveTransformation(axis, i));
                toRet.add(new MoveTransformation(axis, -i));
            }
        }
        return Collections.unmodifiableList(toRet);
    }

    private static List<Transformation> getShapeshiftTransformations(final String currShape) {
        return ShapeFacts.SHAPES.stream().filter(s -> !s.equals(currShape)).map(ShapeshiftTransformation::new).collect(Collectors.toList());
    }

    private static List<Transformation> getTransformationsForShape(final String shape) {
        final List<Transformation> toRet = new LinkedList<>();
        toRet.add(new NullTransformation());
        toRet.add(new ReflectionTransformation(false));
        toRet.add(new ReflectionTransformation(true));
        if("square".equals(shape) || "rectangle".equals(shape)) {
            toRet.add(new StretchTransformation(-2));
            toRet.add(new StretchTransformation(2));
        }
        if(!"circle".equals(shape)) {
            toRet.addAll(get45DegreeRotationTransformations());
            if(!"square".equals(shape) && !"plus".equals(shape)) {
                toRet.addAll(getRightAngleRotationTransformations());
            }
        }
        toRet.addAll(getMoveTransformations());
        toRet.addAll(getScaleTransformations());
        toRet.addAll(getShapeshiftTransformations(shape));
        toRet.addAll(ShapeFacts.FILLS.stream().map(FillTransformation::new).collect(Collectors.toList()));
        return Collections.unmodifiableList(toRet);
    }

    private static List<Transformation> getWorkingTransformations(final RObject obj1, final RObject obj2, final List<Transformation> thusFar, final int maxDepth) {
        if(maxDepth <= 0) {
            return Collections.emptyList();
        }
        final List<Transformation> workingTransforms = new LinkedList<>();
        final int startDistance = obj1.distanceHeuristic(obj2);
        for(final Transformation transformation : getTransformationsForShape(obj1.getMap().get("shape"))) {
            final RObject result = new RObject(transformation.transform(obj1.getMap()));
            final int distance = result.distanceHeuristic(obj2);
            if(distance <= 0) {
                if(thusFar.isEmpty()) {
                    workingTransforms.add(transformation);
                } else {
                    workingTransforms.add(new CompositeTransformation(thusFar, transformation));
                }
            } else if(distance < startDistance || NullTransformation.class.equals(transformation.getClass())) {
                final List<Transformation> thusFarPlusHere = new LinkedList<>(thusFar);
                thusFarPlusHere.add(transformation);
                workingTransforms.addAll(getWorkingTransformations(result, obj2, thusFarPlusHere, maxDepth - 1));
            }
        }
        return workingTransforms;
    }

    public static List<FigureTransformation> getPossibleTransformationsFor2x2(final PotentialSolution fig1, final PotentialSolution fig2) {
        final Map<String, String> matches = ObjectMatcher.match(fig1, fig2);
        final Map<String, List<Transformation>> transformationsByObject = new HashMap<>();
        for(Map.Entry<String, RObject> object : fig1.map.entrySet()) {
            final RObject obj1 = object.getValue();
            if(matches.containsKey(object.getKey())) {
                final RObject obj2 = fig2.map.get(matches.get(object.getKey()));
                transformationsByObject.put(object.getKey(), getWorkingTransformations(obj1, obj2, Collections.emptyList(), MAX_DEPTH));
            } else {
                transformationsByObject.put(object.getKey(), Collections.singletonList(new DeletionTransformation()));
            }
        }
        final List<RObject> additions = new ArrayList<>(fig2.map.size() - matches.size());
        for(final String addition : Utils.complement(matches.values(), fig2.map.keySet())) {
            additions.add(fig2.map.get(addition));
        }
        return getPossibleTransformations(transformationsByObject, Collections.emptyMap(), additions);
    }

    private static List<FigureTransformation> getPossibleTransformations(Map<String, List<Transformation>> transformationsByObject, Map<String, Transformation> acc, final List<RObject> additions) {
        if(transformationsByObject.isEmpty()) {
            return Collections.singletonList(new FigureTransformation(acc, additions));
        }
        final Map.Entry<String, List<Transformation>> entry = transformationsByObject.entrySet().iterator().next();
        final List<FigureTransformation> toRet = new LinkedList<>();
        for(final Transformation transformation : entry.getValue()) {
            final Map<String, List<Transformation>> transformationsByObjectCopy = new HashMap<>(transformationsByObject);
            final Map<String, Transformation> accCopy = new HashMap<>(acc);
            transformationsByObjectCopy.remove(entry.getKey());
            accCopy.put(entry.getKey(), transformation);
            toRet.addAll(getPossibleTransformations(transformationsByObjectCopy, accCopy, additions));
        }
        return toRet;
    }
}
