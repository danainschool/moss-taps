package ravensproject.transformations;

import ravensproject.solvers.ShapeFacts;

import java.util.HashMap;
import java.util.Map;

public class ReflectionTransformation implements Transformation {
    private final boolean acrossHorizontal;

    public ReflectionTransformation(final boolean acrossHorizontal) {
        this.acrossHorizontal = acrossHorizontal;
    }
    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public Map<String, String> transform(final Map<String, String> toTransform) {
        final String key = "angle", alignmentKey = "alignment", fillKey = "fill";
        final int currAngle = Integer.parseInt(toTransform.getOrDefault(key, "0"));
        final Map<String, String> toRet = new HashMap<>(toTransform);
        toRet.put(key, Integer.toString(ShapeFacts.normalizeAngle(getNewAngle(toTransform.get("shape"), currAngle))));
        if(toTransform.containsKey(alignmentKey)) {
            toRet.put(alignmentKey, getNewAlignment(toTransform.get(alignmentKey)));
        }
        if(toTransform.containsKey(fillKey)) {
            toRet.put(fillKey, getNewFill(toTransform.get(fillKey)));
        }
        return toRet;
    }

    private int getNewAngle(final String shape, final int currAngle) {
        switch(shape) {
            case "pac-man":
            case "star":
            case "plus":
            case "heart":
            case "octagon":
            case "pentagon":
                return acrossHorizontal ? 360 - currAngle : 180 - currAngle;
            case "right triangle":
            case "triangle":
                return acrossHorizontal ? 90 - currAngle : 270 - currAngle;
            case "circle":
            case "diamond":
            case "rectangle":
            case "square":
            default:
                return currAngle;
        }
    }

    private String getNewAlignment(final String currAlignment) {
        switch(currAlignment) {
            case "bottom-left":
                return acrossHorizontal ? "top-left" : "bottom-right";
            case "top-left":
                return acrossHorizontal ? "bottom-left" : "top-right";
            case "bottom-right":
                return acrossHorizontal ? "top-right" : "bottom-left";
            case "top-right":
                return acrossHorizontal ? "bottom-right" : "top-left";
            default:
                return currAlignment;
        }
    }

    private String getNewFill(final String currFill) {
        switch(currFill) {
            case "bottom-half":
                return acrossHorizontal ? "top-half" : currFill;
            case "top-half":
                return acrossHorizontal ? "bottom-half" : currFill;
            case "left-half":
                return acrossHorizontal ? currFill : "right-half";
            case "right-half":
                return acrossHorizontal ? currFill : "left-half";
            case "yes":
            case "no":
            default:
                return currFill;
        }
    }
}
