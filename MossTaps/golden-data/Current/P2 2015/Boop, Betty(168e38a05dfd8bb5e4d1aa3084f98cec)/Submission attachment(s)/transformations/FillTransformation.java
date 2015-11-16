package ravensproject.transformations;

import java.util.HashMap;
import java.util.Map;

public class FillTransformation implements Transformation {
    private final String newFill;

    public FillTransformation(final String newFill) {
        this.newFill = newFill;
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public Map<String, String> transform(Map<String, String> toTransform) {
        final String key = "fill";
        final Map<String, String> toRet = new HashMap<>(toTransform);
        toRet.put(key, newFill);
        return toRet;
    }

    @Override
    public String toString() {
        return "FIL[" + newFill + "]";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FillTransformation that = (FillTransformation) o;
        return newFill.equals(that.newFill);
    }

    @Override
    public int hashCode() {
        return newFill.hashCode();
    }
}
