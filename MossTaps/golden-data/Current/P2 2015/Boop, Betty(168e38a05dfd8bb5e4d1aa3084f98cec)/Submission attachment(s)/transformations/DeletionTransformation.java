package ravensproject.transformations;

import java.util.Collections;
import java.util.Map;

public class DeletionTransformation implements Transformation {
    @Override
    public int getCost() {
        return 4;
    }

    @Override
    public Map<String, String> transform(final Map<String, String> toTransform) {
        return Collections.emptyMap();
    }

    @Override
    public String toString() {
        return "DEL";
    }

    @Override
    public boolean equals(final Object o) {
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return 1009;
    }
}
