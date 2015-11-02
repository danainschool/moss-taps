package ravensproject.solvers;

import ravensproject.PotentialSolution;

import java.util.*;

public final class ObjectMatcher {
    private ObjectMatcher() {
        //Namespace only for static methods
    }

    public static Map<String, String> match(final PotentialSolution fig1, final PotentialSolution fig2) {
        final Map<String, String> toRet = new HashMap<>();
        final Map<String, PotentialSolution.RObject> fig1Mutable = new TreeMap<>(fig1.map);
        final Map<String, PotentialSolution.RObject> fig2Mutable = new TreeMap<>(fig2.map);
        while(!fig1Mutable.isEmpty() && !fig2Mutable.isEmpty()) {
            final String[] keys = findBestMatch(fig1Mutable, fig2Mutable);
            fig1Mutable.remove(keys[0]);
            fig2Mutable.remove(keys[1]);
            toRet.put(keys[0], keys[1]);
        }
        return Collections.unmodifiableMap(toRet);
    }

    private static String[] findBestMatch(final Map<String, PotentialSolution.RObject> a, final Map<String, PotentialSolution.RObject> b) {
        int bestMatchScore = Integer.MAX_VALUE;
        final List<String> bestA = new LinkedList<>(), bestB = new LinkedList<>();
        for(final Map.Entry<String, PotentialSolution.RObject> entryA : a.entrySet()) {
            for(final Map.Entry<String, PotentialSolution.RObject> entryB : b.entrySet()) {
                final int score = entryA.getValue().distanceHeuristic(entryB.getValue());
                if(score < bestMatchScore) {
                    bestMatchScore = score;
                    bestA.clear();
                    bestB.clear();
                    bestA.add(entryA.getKey());
                    bestB.add(entryB.getKey());
                } else if(score == bestMatchScore) {
                    bestA.add(entryA.getKey());
                    bestB.add(entryB.getKey());
                }
            }
        }
        if(bestA.size() == 1 && bestB.size() == 1) {
            return new String[]{bestA.get(0), bestB.get(0)};
        }

        //Tiebreaker
        bestMatchScore = Integer.MAX_VALUE;
        String tiebreakerA = null, tiebreakerB = null;
        for(final String keyA : bestA) {
            for(final String keyB : bestB) {
                final int score = a.get(keyA).withoutSpatial().distanceHeuristic(b.get(keyB).withoutSpatial());
                if(score < bestMatchScore) {
                    bestMatchScore = score;
                    tiebreakerA = keyA;
                    tiebreakerB = keyB;
                }
            }
        }
        return new String[] { tiebreakerA, tiebreakerB };
    }
}
