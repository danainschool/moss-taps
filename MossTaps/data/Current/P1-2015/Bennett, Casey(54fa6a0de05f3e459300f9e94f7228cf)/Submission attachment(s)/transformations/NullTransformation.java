package ravensproject.transformations;

import java.util.Map;

public final class NullTransformation implements Transformation {
    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public Map<String, String> transform(Map<String, String> toTransform) {
        return toTransform;
    }
}
