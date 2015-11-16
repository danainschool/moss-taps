package ravensproject.transformations;

import ravensproject.solvers.ShapeFacts;

import java.util.HashMap;
import java.util.Map;

public class RotationTransformation implements Transformation {
    private final int degrees;

    public RotationTransformation(final int degrees) {
        this.degrees = ShapeFacts.normalizeAngle(degrees);
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public Map<String, String> transform(final Map<String, String> toTransform) {
        final String key = "angle";
        final Map<String, String> toRet = new HashMap<>(toTransform);
        final int currAngle = Integer.parseInt(toTransform.getOrDefault(key, "0"));
        toRet.put(key, Integer.toString(ShapeFacts.normalizeAngle(currAngle + degrees)));
        return toRet;
    }
}
