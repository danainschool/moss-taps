package ravensproject.transformations;

import ravensproject.solvers.ShapeFacts;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class StretchTransformation implements Transformation {
    private static int indexOf(final String target) {
        int i = 0;
        for(final String s : ShapeFacts.SIZES) {
            if(s.equals(target)) {
                return i;
            }
            ++i;
        }
        throw new NoSuchElementException(target);
    }

    private static int bound(final int i) {
        return Math.min(Math.max(0, i), ShapeFacts.SIZES.size() - 1);
    }

    private final int magnitude;

    public StretchTransformation(final int magnitude) {
        this.magnitude = magnitude;
    }

    @Override
    public Map<String, String> transform(Map<String, String> toTransform) {
        final Map<String, String> toRet = new HashMap<>(toTransform);
        switch(toTransform.get("shape")) {
            case "rectangle":
                if(toTransform.containsKey("height") && toTransform.containsKey("width")) {
                    //Rectangles have height and width
                    final int currHeight = indexOf(toTransform.get("height"));
                    final int currWidth = indexOf(toTransform.get("width"));
                    final int newHeightR = magnitude > 0 ? bound(currHeight + magnitude) : currHeight;
                    final int newWidthR = magnitude < 0 ? bound(currWidth - magnitude) : currWidth;
                    if (newHeightR == newWidthR) {
                        toRet.put("shape", "square");
                        toRet.put("size", ShapeFacts.SIZES.get(newHeightR));
                        toRet.remove("height");
                        toRet.remove("width");
                    } else {
                        toRet.put("height", ShapeFacts.SIZES.get(newHeightR));
                        toRet.put("width", ShapeFacts.SIZES.get(newWidthR));
                    }
                }
                break;
            case "square":
                if(toTransform.containsKey("size")) {
                    final int currSize = indexOf(toTransform.get("size"));
                    final int newHeightS = magnitude > 0 ? bound(currSize + magnitude) : currSize;
                    final int newWidthS = magnitude < 0 ? bound(currSize - magnitude) : currSize;
                    if (newHeightS == newWidthS) {
                        toRet.put("size", ShapeFacts.SIZES.get(newHeightS));
                    } else {
                        toRet.put("shape", "rectangle");
                        toRet.remove("size");
                        toRet.put("height", ShapeFacts.SIZES.get(newHeightS));
                        toRet.put("width", ShapeFacts.SIZES.get(newWidthS));
                    }
                }
                break;
            default:
        }
        return toRet;
    }

    @Override
    public int getCost() {
        return 2 * Math.abs(magnitude);
    }

    @Override
    public String toString() {
        return "STR[" + magnitude + "]";
    }

    @Override
    public int hashCode() {
        return 53 * Integer.hashCode(magnitude);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StretchTransformation that = (StretchTransformation) o;
        return magnitude == that.magnitude;
    }
}
