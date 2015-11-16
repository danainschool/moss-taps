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

    @Override
    public String toString() {
        return "NUL";
    }

    @Override
    public boolean equals(final Object o) {
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return 1013;
    }
}
