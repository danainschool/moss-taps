package ravensproject.solvers;

import ravensproject.RavensFigure;
import ravensproject.RavensObject;
import ravensproject.transformations.*;

import java.util.*;
import java.util.stream.Collectors;

public final class ProblemSolver {
    private static final int MAX_DEPTH = 2;

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

    private static List<Transformation> getShapeshiftTransformations(final String currShape) {
        return ShapeFacts.SHAPES.stream().filter(s -> !s.equals(currShape)).map(ShapeshiftTransformation::new).collect(Collectors.toList());
    }

    private static List<Transformation> getTransformationsForShape(final String shape) {
        final List<Transformation> toRet = new LinkedList<>();
        toRet.add(new NullTransformation());
        toRet.add(new ReflectionTransformation(false));
        toRet.add(new ReflectionTransformation(true));
        if(!"circle".equals(shape)) {
            toRet.addAll(get45DegreeRotationTransformations());
            if(!"square".equals(shape) && !"plus".equals(shape)) {
                toRet.addAll(getRightAngleRotationTransformations());
            }
        }
        toRet.addAll(getScaleTransformations());
        toRet.addAll(getShapeshiftTransformations(shape));
        toRet.addAll(ShapeFacts.FILLS.stream().map(FillTransformation::new).collect(Collectors.toList()));
        return Collections.unmodifiableList(toRet);
    }

    private static List<Transformation> getWorkingTransformations(final Map<String, String> obj1, final Map<String, String> obj2, final List<Transformation> thusFar, final int maxDepth) {
        if(maxDepth <= 0) {
            return Collections.emptyList();
        }
        final List<Transformation> workingTransforms = new LinkedList<>();
        for(final Transformation transformation : getTransformationsForShape(obj1.get("shape"))) {
            final Map<String, String> result = transformation.transform(obj1);
            if(EquivalenceChecker.areObjectsEquivalent(result, obj2)) {
                if(thusFar.isEmpty()) {
                    workingTransforms.add(transformation);
                } else {
                    workingTransforms.add(new CompositeTransformation(thusFar, transformation));
                }
            } else {
                final List<Transformation> thusFarPlusHere = new LinkedList<>(thusFar);
                thusFarPlusHere.add(transformation);
                workingTransforms.addAll(getWorkingTransformations(result, obj2, thusFarPlusHere, maxDepth - 1));
            }
        }
        return workingTransforms;
    }

    public static List<FigureTransformation> getPossibleTransformationsFor2x2(final RavensFigure fig1, final RavensFigure fig2) {
        final Map<String, String> matches = ObjectMatcher.match(fig1.getObjects(), fig2.getObjects());
        final Map<String, List<Transformation>> transformationsByObject = new HashMap<>();
        for(Map.Entry<String, RavensObject> object : fig1.getObjects().entrySet()) {
            final Map<String, String> obj1 = object.getValue().getAttributes();
            if(matches.containsKey(object.getKey())) {
                final Map<String, String> obj2 = fig2.getObjects().get(matches.get(object.getKey())).getAttributes();
                transformationsByObject.put(object.getKey(), getWorkingTransformations(obj1, obj2, Collections.emptyList(), MAX_DEPTH));
            } else {
                transformationsByObject.put(object.getKey(), Collections.singletonList(new DeletionTransformation()));
            }
        }
        return getPossibleTransformations(transformationsByObject, Collections.emptyMap());
    }

    private static List<FigureTransformation> getPossibleTransformations(Map<String, List<Transformation>> transformationsByObject, Map<String, Transformation> acc) {
        if(transformationsByObject.isEmpty()) {
            return Collections.singletonList(new FigureTransformation(acc));
        }
        final Map.Entry<String, List<Transformation>> entry = transformationsByObject.entrySet().iterator().next();
        final List<FigureTransformation> toRet = new LinkedList<>();
        for(final Transformation transformation : entry.getValue()) {
            final Map<String, List<Transformation>> transformationsByObjectCopy = new HashMap<>(transformationsByObject);
            final Map<String, Transformation> accCopy = new HashMap<>(acc);
            transformationsByObjectCopy.remove(entry.getKey());
            accCopy.put(entry.getKey(), transformation);
            toRet.addAll(getPossibleTransformations(transformationsByObjectCopy, accCopy));
        }
        return toRet;
    }
}
