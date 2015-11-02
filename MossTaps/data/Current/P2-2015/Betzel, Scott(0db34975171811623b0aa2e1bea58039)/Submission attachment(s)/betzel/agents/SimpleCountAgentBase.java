package ravensproject.betzel.agents;

import ravensproject.RavensProblem;
import ravensproject.betzel.helpers.LoggingHelper;

import java.util.*;

/**
 * Created by scott betzel on 6/23/15.
 *
 * This is the base class for my agents.
 */
public abstract class SimpleCountAgentBase
    extends AgentBase {

    protected static final double ERROR_INTERVAL = 0.075;

    private List<Integer> rowsToLearnDirection;
    private List<Integer> colsToLearnDirection;

    protected enum NumericDirection {
        ASCENDING,
        SAME,
        DESCENDING,
        UNKNOWN,
        ASCENDING_THEN_DESCENDING,
        DESCENDING_THEN_ASCENDING
    }

    protected enum Comparison {
        LESS_THAN,
        GREATER_THAN,
        EQUAL
    }

    protected enum GridDirection {
        HORIZONTAL,
        VERTICAL
    }

    protected enum EqualityComparisonMode {
        EXACT_MATCH,
        ERROR_INTERVAL_MATCH
    }



    public SimpleCountAgentBase(String theName) {
        super(theName);

        this.rowsToLearnDirection = new ArrayList<>();
        this.rowsToLearnDirection.add(0);
        this.rowsToLearnDirection.add(1);
        this.rowsToLearnDirection.add(2);

        this.colsToLearnDirection = new ArrayList<>();
        this.colsToLearnDirection.add(0);
        this.colsToLearnDirection.add(1);
        this.colsToLearnDirection.add(2);
    }

    public SimpleCountAgentBase(String theName,
                                int[] theLearningRows,
                                int[] theLearningCols) {
        super(theName);

        this.rowsToLearnDirection = new ArrayList<>();
        for (int i: theLearningRows) {
            assert i >= 0 && i <= 2;
            this.rowsToLearnDirection.add(i);
        }

        this.colsToLearnDirection = new ArrayList<>();
        for (int i: theLearningCols) {
            assert i >= 0 && i <= 2;
            this.colsToLearnDirection.add(i);
        }
    }

    public Set<Integer> solve(RavensProblem problem) throws Exception {

        Set<Integer> toRet = null;

        if (problem.getProblemType().equals("3x3")) {
            Map<String, Integer> counts = this.getCounts(problem);

            toRet = getPossibleSolutions(counts);
        } else {
            LoggingHelper.logError("2x2 not supported.");
        }

        return toRet;
    }

    protected abstract Map<String, Integer> getCounts(RavensProblem problem) throws Exception;

    protected abstract EqualityComparisonMode getEqualityComparisonMode();

    protected Set<Integer> getPossibleSolutions(Map<String, Integer> pixelCounts) {
        Set<Integer> toRet;

        NumericDirection horizontalDirection
                = this.getHorizontalDirection(pixelCounts);

        NumericDirection verticalDirection
                = this.getVerticalDirection(pixelCounts);

        Map<String, Integer> horizontalSolutions
                = this.getSolutions(pixelCounts,
                horizontalDirection,
                GridDirection.HORIZONTAL);

        Map<String, Integer> verticalSolutions
                = this.getSolutions(pixelCounts,
                verticalDirection,
                GridDirection.VERTICAL);

        toRet = this.combineMaps(horizontalSolutions, verticalSolutions);
        return toRet;
    }

    protected Set<Integer> combineMaps(Map<String, Integer> one,
                                         Map<String, Integer> two) {
        Set<Integer> toRet = new HashSet<>();

        for (String keyOne: one.keySet()) {
            if (two.containsKey(keyOne)) {
                try {
                    toRet.add(Integer.parseInt(keyOne));
                } catch (Exception ex) {
                    // Nothing to do... Only want integers
                }

            }
        }

        return toRet;
    }

    protected Map<String, Integer> getSolutions(Map<String, Integer> counts,
                                                  NumericDirection direction,
                                                  GridDirection cellDirection) {

//        NumericDirection direction = this.getHorizontalDirection(pixelCounts);

        if (direction == NumericDirection.UNKNOWN) {
            return new HashMap<>();
        }

        String figureName = "H";
        if (cellDirection == GridDirection.VERTICAL) {
            figureName = "F";
        }


        if ((direction == NumericDirection.DESCENDING)
                || (direction == NumericDirection.ASCENDING_THEN_DESCENDING)) {
            return filterCounts(counts, Comparison.LESS_THAN, counts.get(figureName));
        }

        if ((direction == NumericDirection.ASCENDING)
                || (direction == NumericDirection.DESCENDING_THEN_ASCENDING)) {
            return filterCounts(counts, Comparison.GREATER_THAN, counts.get(figureName));
        }

        if (direction == NumericDirection.SAME) {
            return filterCounts(counts, Comparison.EQUAL, counts.get(figureName));
        }

        LoggingHelper.logError("You should never get here");

        return counts;
    }

    private Map<String, Integer> filterCounts(Map<String, Integer> counts,
                                                  Comparison comparison,
                                                  int value) {
        Map<String, Integer> toRet = new HashMap<>();
        int count;

        for (String key: counts.keySet()) {
            count = counts.get(key);

            if (comparison == Comparison.LESS_THAN) {
                if (count < value) {
                    toRet.put(key, count);
                }
            } else if (comparison == Comparison.GREATER_THAN) {
                if (count > value) {
                    toRet.put(key, count);
                }
            } else if (comparison == Comparison.EQUAL) {
                if (this.isFuzzyEqual(count, value)
                        && (value > 0)
                        && (count > 0)) {
                    toRet.put(key, count);
                }
            } else {
                LoggingHelper.logError("You should never get here");
            }

        }

        return toRet;
    }

    protected NumericDirection getHorizontalDirection(Map<String, Integer> counts) {

        boolean areAllEqual = true;
        NumericDirection prev = getRowDirection(counts, this.rowsToLearnDirection.get(0));

        for (int i = 0; i < this.rowsToLearnDirection.size(); i++) {
            areAllEqual = prev == getRowDirection(counts, this.rowsToLearnDirection.get(i));
        }

        if (areAllEqual) {
            return prev;
        }

        return NumericDirection.UNKNOWN;
    }

    protected NumericDirection getVerticalDirection(Map<String, Integer> counts) {
        boolean areAllEqual = true;
        NumericDirection prev = getColDirection(counts, this.colsToLearnDirection.get(0));

        for (int i = 0; i < this.colsToLearnDirection.size(); i++) {
            areAllEqual = prev == getColDirection(counts, this.colsToLearnDirection.get(i));
        }

        if (areAllEqual) {
            return prev;
        }

        return NumericDirection.UNKNOWN;
    }

//    private NumericDirection getDiagonalDirection(Map<String, Integer> counts) {
//
//        int zero;
//        int one;
//        int two;
//
//        if (colNumber == 0) {
//            zero = counts.get("A");
//            one = counts.get("D");
//            two = counts.get("G");
//
//            return getDirection(new int[] {zero, one, two});
//        }
//
//        if (colNumber == 1) {
//            zero = counts.get("B");
//            one = counts.get("E");
//            two = counts.get("H");
//
//            return getDirection(new int[] {zero, one, two});
//        }
//
//        zero = counts.get("C");
//        one = counts.get("F");
//
//        return getDirection(zero, one);
//    }

    private NumericDirection getColDirection(Map<String, Integer> counts,
                                             int colNumber) {
        assert colNumber >= 0 && colNumber <= 2;

        int zero;
        int one;
        int two;

        if (colNumber == 0) {
            zero = counts.get("A");
            one = counts.get("D");
            two = counts.get("G");

            return getDirection(new int[] {zero, one, two});
        }

        if (colNumber == 1) {
            zero = counts.get("B");
            one = counts.get("E");
            two = counts.get("H");

            return getDirection(new int[] {zero, one, two});
        }

        zero = counts.get("C");
        one = counts.get("F");

        return getDirection(zero, one);
    }

    private NumericDirection getRowDirection(Map<String, Integer> counts,
                                             int rowNumber) {
        assert rowNumber >= 0 && rowNumber <= 2;

        int zero;
        int one;
        int two;

        if (rowNumber == 0) {
            zero = counts.get("A");
            one = counts.get("B");
            two = counts.get("C");

            return getDirection(new int[] {zero, one, two});
        }

        if (rowNumber == 1) {
            zero = counts.get("D");
            one = counts.get("E");
            two = counts.get("F");

            return getDirection(new int[] {zero, one, two});
        }

        zero = counts.get("G");
        one = counts.get("H");

        return getDirection(zero, one);
    }

    private NumericDirection getDirection(int[] myIntegers) {
        assert myIntegers.length == 3;

        int zero = myIntegers[0];
        int one = myIntegers[1];
        int two = myIntegers[2];

        NumericDirection zeroOneDirection = this.getDirection(zero, one);
        NumericDirection oneTwoDirection = this.getDirection(one, two);

        if (zeroOneDirection == NumericDirection.ASCENDING
                && oneTwoDirection == NumericDirection.ASCENDING) {

            return NumericDirection.ASCENDING;
        }

        if (zeroOneDirection == NumericDirection.DESCENDING
                && oneTwoDirection == NumericDirection.DESCENDING) {

            return NumericDirection.DESCENDING;
        }

        if (zeroOneDirection == NumericDirection.DESCENDING
                && oneTwoDirection == NumericDirection.ASCENDING) {

            return NumericDirection.DESCENDING_THEN_ASCENDING;
        }

        if (zeroOneDirection == NumericDirection.ASCENDING
                && oneTwoDirection == NumericDirection.DESCENDING) {

            return NumericDirection.ASCENDING_THEN_DESCENDING;
        }

        if (zeroOneDirection == NumericDirection.SAME
                && oneTwoDirection == NumericDirection.SAME) {

            return NumericDirection.SAME;
        }

        return NumericDirection.UNKNOWN;
    }

    private NumericDirection getDirection(int one, int two) {

        if (this.isFuzzyEqual(one, two)) {
            return NumericDirection.SAME;
        }

        if (one < two) {
            return NumericDirection.ASCENDING;
        }

        return NumericDirection.DESCENDING;
    }

    private boolean isFuzzyEqual(int one, int two) {

        EqualityComparisonMode mode = this.getEqualityComparisonMode();

        if (mode == EqualityComparisonMode.ERROR_INTERVAL_MATCH) {
            int oneMax = Math.abs(one + (int)(one * ERROR_INTERVAL));
            int oneMin = Math.abs(one - (int)(one * ERROR_INTERVAL));

            if (oneMax > two && oneMin < two) {
                return true;
            }

            return false;
        }

        if (mode == EqualityComparisonMode.EXACT_MATCH) {
            return one == two;
        }

        LoggingHelper.logError("Invalid EqualityComparisonMode found.");
        return false;
    }
}
