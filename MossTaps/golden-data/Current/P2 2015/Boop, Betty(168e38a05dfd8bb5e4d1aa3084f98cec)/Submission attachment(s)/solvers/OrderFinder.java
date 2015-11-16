package ravensproject.solvers;

import ravensproject.PotentialSolution;
import ravensproject.RavensObject;
import ravensproject.transformations.Axis;

import java.util.*;
import java.util.stream.Collectors;

public final class OrderFinder {
    private OrderFinder() {

    }

    public static PotentialSolution replaceOrdering(final Map<String, RavensObject> fig) {
        final Map<String, PotentialSolution.RObject> toRet = new HashMap<>();
        for(final Map.Entry<String, RavensObject> entry : fig.entrySet()) {
            final Map<String, String> newObj = new HashMap<>(entry.getValue().getAttributes());
            for(final Axis axis : Axis.values()) {
                final int ordinal = findLevel(fig, entry.getKey(), axis.preKey, Collections.emptySet());
                newObj.remove(axis.preKey);
                newObj.put(axis.postKey, Integer.toString(ordinal));
            }
            toRet.put(entry.getKey(), new PotentialSolution.RObject(newObj));
        }

        return new PotentialSolution(toRet, false);
    }

    public static PotentialSolution rebalance(final PotentialSolution solution) {
        PotentialSolution toRet = solution;
        for(final Axis axis : Axis.values()) {
            toRet = rebalance(toRet, axis.postKey);
        }
        return toRet;
    }

    private static PotentialSolution rebalance(final PotentialSolution solution, final String key) {
        final Set<Integer> set = solution.map.values().stream().filter(o -> o.getMap().containsKey(key)).map(o -> Integer.parseInt(o.getMap().get(key))).distinct().collect(Collectors.toSet());
        final int min = set.isEmpty() ? 0 : set.stream().min(Comparator.<Integer>naturalOrder()).get();
        final int max = set.isEmpty() ? 0 : set.stream().max(Comparator.<Integer>naturalOrder()).get();
        final Map<String, String> oldToNewNums = new HashMap<>();
        int offset = min;
        for(int i = 0; i + offset <= max; ++i) {
            while(!set.contains(i + offset)) {
                offset++;
            }
            oldToNewNums.put(Integer.toString(i + offset), Integer.toString(i));
        }

        final Map<String, PotentialSolution.RObject> objs = new HashMap<>();
        for(final Map.Entry<String, PotentialSolution.RObject> entry : solution.map.entrySet()) {
            if(entry.getValue().getMap().containsKey(key)) {
                final Map<String, String> m = new HashMap<>(entry.getValue().getMap());
                m.put(key, oldToNewNums.getOrDefault(m.get(key), Integer.toString(0)));
                objs.put(entry.getKey(), new PotentialSolution.RObject(m));
            } else {
                objs.put(entry.getKey(), entry.getValue());
            }
        }
        return new PotentialSolution(objs, false);
    }

    private static int findLevel(final Map<String, RavensObject> fig, final String subject, final String key, final Set<String> seen) {
        if(seen.contains(subject)) {
            System.err.println("WARN: Cycle detected on " + key + ". Bailing to avoid stack overflow.");
            return 0;
        }
        final List<String> neighbors = findNeighbors(fig, subject, key);
        final Set<String> nowSeen = new HashSet<>(seen);
        nowSeen.add(subject);
        return neighbors.isEmpty() ? 0 : findLevel(fig, neighbors.get(0), key, nowSeen) + 1;
    }

    private static List<String> findNeighbors(final Map<String, RavensObject> fig, final String subject, final String key) {
        final List<String> neighbors = new LinkedList<>();
        for(final Map.Entry<String, RavensObject> entry : fig.entrySet()) {
            if(entry.getValue().getAttributes().containsKey(key)) {
                final List<String> keys = Arrays.asList(entry.getValue().getAttributes().get(key).split(","));
                if(keys.contains(subject)) {
                    neighbors.add(entry.getKey());
                }
            }
        }
        return neighbors;
    }
}
