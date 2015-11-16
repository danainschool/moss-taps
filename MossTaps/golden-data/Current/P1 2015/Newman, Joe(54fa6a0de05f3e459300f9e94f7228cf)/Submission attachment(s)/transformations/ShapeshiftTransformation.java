package ravensproject.transformations;

import java.util.HashMap;
import java.util.Map;

public class ShapeshiftTransformation implements Transformation {
    private final String newShape;

    public ShapeshiftTransformation(final String newShape) {
        this.newShape = newShape;
    }

    @Override
    public int getCost() {
        return 5;
    }

    @Override
    public Map<String, String> transform(final Map<String, String> toTransform) {
        final Map<String, String> toRet = new HashMap<>(toTransform);
        toRet.put("shape", newShape);
        return toRet;
    }
}
