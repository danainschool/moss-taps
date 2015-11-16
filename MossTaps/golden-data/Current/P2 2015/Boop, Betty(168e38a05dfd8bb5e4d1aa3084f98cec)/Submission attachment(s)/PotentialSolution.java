package ravensproject;

import ravensproject.transformations.Axis;

import java.util.*;

public class PotentialSolution {
    public final SortedMap<String, RObject> map;

    public PotentialSolution(final Map<String, Map<String, String>> map) {
        this.map = Collections.unmodifiableSortedMap(convert(map));
    }

    public PotentialSolution(final Map<String, RObject> map, final boolean typeErasureDummy) {
        this.map = Collections.unmodifiableSortedMap(new TreeMap<>(map));
    }

    public PotentialSolution(final RavensFigure fig) {
        final SortedMap<String, RObject> m = new TreeMap<>();
        for(Map.Entry<String, RavensObject> entry : fig.getObjects().entrySet()) {
            m.put(entry.getKey(), new RObject(entry.getValue().getAttributes()));
        }
        this.map = Collections.unmodifiableSortedMap(removeEmpty(m));
    }

    private static SortedMap<String, RObject> convert(final Map<String, Map<String, String>> m) {
        final SortedMap<String, RObject> toRet = new TreeMap<>();
        for(final Map.Entry<String, Map<String, String>> entry : m.entrySet()) {
            if(!entry.getValue().isEmpty()) {
                toRet.put(entry.getKey(), new RObject(entry.getValue()));
            }
        }
        return toRet;
    }

    private static SortedMap<String, RObject> removeEmpty(final SortedMap<String, RObject> m) {
        final Iterator<Map.Entry<String, RObject>> it = m.entrySet().iterator();
        while(it.hasNext()) {
            final Map.Entry<String, RObject> entry = it.next();
            if(!entry.getValue().map.containsKey("shape")) {
                it.remove();
            }
        }
        return m;
    }

    public int distanceHeuristic(final PotentialSolution other) {
        int distance = 0;
        final SortedMap<String, RObject> otherCopy = new TreeMap<>(other.map);
        for(final RObject obj : map.values()) {
            if(otherCopy.isEmpty()) {
                break;
            }
            int bestDist = Integer.MAX_VALUE;
            String bestMatch = null;
            for(final Map.Entry<String, RObject> entry : otherCopy.entrySet()) {
                final int dist = obj.distanceHeuristic(entry.getValue());
                if(dist < bestDist) {
                    bestDist = dist;
                    bestMatch = entry.getKey();
                }
            }
            distance += bestDist;
            otherCopy.remove(bestMatch);
        }
        distance += 2 * otherCopy.size();
        distance += 6 * Math.max(0, map.size() - other.map.size());
        return distance;
    }

    public Map<String, Map<String, String>> getMap() {
        final Map<String, Map<String, String>> m = new HashMap<>();
        for(Map.Entry<String, RObject> entry : map.entrySet()) {
            m.put(entry.getKey(), entry.getValue().map);
        }
        return Collections.unmodifiableMap(m);
    }

    public static final class RObject {
        private final Map<String, String> map;

        public RObject(final Map<String, String> map) {
            final Map<String, String> mCopy = new HashMap<>(map);
            if(doesAngleMatter(mCopy)) {
                mCopy.putIfAbsent("angle", "0");
            } else {
                mCopy.remove("angle");
            }
            this.map = Collections.unmodifiableMap(mCopy);
        }

        public int distanceHeuristic(final RObject other) {
            int distance = 0;
            distance += Utils.complement(map.keySet(), other.map.keySet()).size();
            distance += Utils.complement(other.map.keySet(), map.keySet()).size();
            for(final String key : Utils.intersection(map.keySet(), other.map.keySet())) {
                if(!map.get(key).equals(other.map.get(key))) {
                    switch (key) {
                        default:
                            distance++;
                    }
                }
            }
            return distance;
        }

        public RObject withoutSpatial() {
            final Map<String, String> mCopy = new HashMap<>(map);
            for(final Axis axis : Axis.values()) {
                mCopy.remove(axis.postKey);
            }
            return new RObject(mCopy);
        }

        public Map<String, String> getMap() {
            return map;
        }

        private static boolean doesAngleMatter(final Map<String, String> map) {
            switch(map.getOrDefault("shape", "")) {
                case "circle":
                    return false;
                case "square":
                case "plus":
                    return Integer.parseInt(map.getOrDefault("angle", "0")) % 90 != 0;
                default:
                    return true;
            }
        }
    }
}
