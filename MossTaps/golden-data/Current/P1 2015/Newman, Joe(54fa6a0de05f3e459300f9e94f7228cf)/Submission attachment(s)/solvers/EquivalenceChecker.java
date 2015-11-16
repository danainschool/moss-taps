package ravensproject.solvers;

import ravensproject.RavensFigure;
import ravensproject.RavensObject;

import java.util.*;

public final class EquivalenceChecker {
    private EquivalenceChecker() {
        //Namespace only for static methods
    }

    private static Set<String> getReferentialKeys() {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "inside", "left-of", "right-of", "above", "below"
        )));
    }

    private static boolean doesAngleMatter(final Map<String, String> obj) {
        final int angle = Integer.parseInt(obj.getOrDefault("angle", "0"));
        switch(obj.getOrDefault("shape", "")) {
            case "circle":
                return false;
            case "square":
            case "plus":
                return angle % 90 != 0;
            default:
                return true;
        }
    }

    public static boolean areObjectsEquivalent(final Map<String, String> obj1, final Map<String, String> obj2) {
        final Map<String, String> obj1Copy = new HashMap<>(obj1);
        final Map<String, String> obj2Copy = new HashMap<>(obj2);
        obj1Copy.putIfAbsent("angle", "0");
        obj2Copy.putIfAbsent("angle", "0");
        for(final String key: getReferentialKeys()) {
            obj1Copy.remove(key);
            obj2Copy.remove(key);
        }
        if(!doesAngleMatter(obj1)) {
            obj1Copy.remove("angle");
        }
        if(!doesAngleMatter(obj2)) {
            obj2Copy.remove("angle");
        }
        return obj1Copy.equals(obj2Copy);
    }

    public static boolean areFiguresEquivalent(final Map<String, Map<String, String>> fig1, final RavensFigure fig2) {
        final Map<String, Map<String, String>> m = new HashMap<>();
        for(Map.Entry<String, RavensObject> entry : fig2.getObjects().entrySet()) {
            m.put(entry.getKey(), entry.getValue().getAttributes());
        }
        return areFiguresEquivalent(fig1, m);
    }

    public static boolean areFiguresEquivalent(final Map<String, Map<String, String>> fig1, final Map<String, Map<String, String>> fig2) {
        final Map<String, Map<String, String>> fig2Copy = new HashMap<>(fig2);
        for(Map<String, String> obj1 : fig1.values()) {
            if(obj1.isEmpty()) {
                continue;
            }
            boolean found = false;
            for(Map.Entry<String, Map<String, String>> obj2 : fig2Copy.entrySet()) {
                if(areObjectsEquivalent(obj1, obj2.getValue())) {
                    fig2Copy.remove(obj2.getKey());
                    found = true;
                    break;
                }
            }
            if(!found) {
                return false;
            }
        }
        return true;
    }
}
