package ravensproject.betzel;

import ravensproject.betzel.attributecomparators.*;
import ravensproject.betzel.interfaces.IAttributeComparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by scott betzel on 6/1/15.
 *
 * This class should be used to compare all the slot values.
 */
public class MasterComparator implements ravensproject.betzel.interfaces.IComparator {

    /**
     * This is the comparator map
     */
    private static HashMap<String, IAttributeComparator> comparatorMap;

    /**
     * This is the fallback comparator
     */
    private static IAttributeComparator generalComparator;

    /**
     * Static Initialization Stuff - Wiring this thing up
     */
    static {
        generalComparator = new GeneralComparator();
        comparatorMap = new HashMap<>();

        List<IAttributeComparator> comparators = new ArrayList<>();
        comparators.add(new AboveComparator());
        comparators.add(new AlignmentComparator());
        comparators.add(new AngleComparator());
        comparators.add(new FillComparator());
        comparators.add(new InsideComparator());
        comparators.add(new OverlapsComparator());
        comparators.add(new ShapeComparator());
        comparators.add(new SizeComparator());

        for (IAttributeComparator comparator: comparators) {
            comparatorMap.put(comparator.get_name().toLowerCase(), comparator);
        }

    }

    /**
     * Perform Comparison for the given slotname, sourceValue, and destinationValue
     *
     *
     * @param slotName The name of the frame slot you wish to compare
     * @param sourceValue The value of the frame slot you wish to diff
     * @param destinationValue The value of the frame slot you wish to diff
     * @return The diff value. 0.0 means identical, 1.0 means different
     */
    @Override
    public double compare(String slotName, String sourceValue, String destinationValue) {
        slotName = slotName.toLowerCase();

        if (comparatorMap.containsKey(slotName)) {
            return comparatorMap.get(slotName).getDiff(sourceValue, destinationValue);
        }

        return generalComparator.getDiff(sourceValue, destinationValue);
    }

}
