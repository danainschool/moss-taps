package ravensproject.transformations;

import ravensproject.solvers.ShapeFacts;

import java.util.*;

public class ScaleTransformation implements Transformation {
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

    private final int delta;
    private final boolean absoluteMode;

    public ScaleTransformation(final int delta) {
        this.delta = delta;
        this.absoluteMode = false;
    }

    public ScaleTransformation(final String target) {
        this.delta = indexOf(target);
        this.absoluteMode = true;
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public Map<String, String> transform(final Map<String, String> toTransform) {
        final String key = "size";
        Map<String, String> toRet = new HashMap<>(toTransform);
        if(absoluteMode) {
            toRet.put(key, ShapeFacts.SIZES.get(delta));
        } else if(toTransform.containsKey(key)) {
            final String currSize = toTransform.get(key);
            try {
                final int currSizeIdx = indexOf(currSize);
                final int newIdx = Math.min(Math.max(0, currSizeIdx + delta), ShapeFacts.SIZES.size() - 1);
                toRet.put(key, ShapeFacts.SIZES.get(newIdx));
            } catch(final NoSuchElementException ex) {
                System.err.println("WARN: Unexpected size \"" + currSize + "\"");
            }
        }
        return toRet;
    }
}
