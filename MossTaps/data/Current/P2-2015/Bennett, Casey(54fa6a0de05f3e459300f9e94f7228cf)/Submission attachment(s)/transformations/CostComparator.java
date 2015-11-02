package ravensproject.transformations;

import java.util.Comparator;

public final class CostComparator implements Comparator<HasCost> {
    @Override
    public int compare(final HasCost o1, final HasCost o2) {
        return Integer.compare(o1.getCost(), o2.getCost());
    }
}
