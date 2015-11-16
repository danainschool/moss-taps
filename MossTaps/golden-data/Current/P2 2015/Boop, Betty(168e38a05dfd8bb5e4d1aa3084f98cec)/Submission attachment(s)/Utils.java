package ravensproject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Utils {
    private Utils() {
        //Hide constructor
    }

    public static <T> Set<T> union(final Collection<T> a, final Collection<T> b) {
        final Set<T> union = new HashSet<>(a);
        union.addAll(b);
        return union;
    }

    public static <T> Set<T> intersection(final Collection<T> a, final Collection<T> b) {
        final Set<T> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        return intersection;
    }

    public static <T> Set<T> complement(final Collection<T> a, final Collection<T> b) {
        final Set<T> complement = new HashSet<>(b);
        complement.removeAll(a);
        return complement;
    }
}
