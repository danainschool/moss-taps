package ravensproject.transformations;

import java.util.HashMap;
import java.util.Map;

public class MoveTransformation implements Transformation {
    private final Axis axis;
    private final int delta;

    public MoveTransformation(final Axis axis, final int delta) {
        this.axis = axis;
        this.delta = delta;
    }

    @Override
    public Map<String, String> transform(final Map<String, String> toTransform) {
        final Map<String, String> toRet = new HashMap<>(toTransform);
        final int curr = Integer.parseInt(toTransform.getOrDefault(axis.postKey, "0"));
        toRet.put(axis.postKey, Integer.toString(curr + delta));
        return toRet;
    }

    @Override
    public int getCost() {
        return Math.abs(delta);
    }

    @Override
    public String toString() {
        return "MOV[" + axis + "," + delta + "]";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MoveTransformation that = (MoveTransformation) o;
        return axis.equals(that.axis) && delta == that.delta;
    }

    @Override
    public int hashCode() {
        return axis.hashCode() + 43 * Integer.hashCode(delta);
    }
}
