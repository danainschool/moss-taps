package ravensproject.solvers;

import ravensproject.RavensObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ObjectMatcher {
    private ObjectMatcher() {
        //Namespace only for static methods
    }

    public static Map<String, String> match(final Map<String, RavensObject> fig1, final Map<String, RavensObject> fig2) {
        final Map<String, String> toRet = new HashMap<>();
        if (fig1.size() == 0 || fig2.size() == 0) {
            return Collections.emptyMap();
        }
        if(fig1.size() == 1 && fig2.size() == 1) {
            toRet.put(fig1.keySet().iterator().next(), fig2.keySet().iterator().next());
            return Collections.unmodifiableMap(toRet);
        }
        final Map<String, RavensObject> fig1Mutable = new HashMap<>(fig1);
        final Map<String, RavensObject> fig2Mutable = new HashMap<>(fig2);
        toRet.putAll(findAndRemovePerfectMatches(fig1Mutable, fig2Mutable));
        toRet.putAll(findAndRemoveForcedMatches(fig1Mutable, fig2Mutable));
        while(!fig1Mutable.isEmpty() && !fig2Mutable.isEmpty()) {
            //TODO Should be able to apply transformations to find the closest matches
            System.err.println("WARN: Doing dumb random match");
            final String key1 = fig1Mutable.keySet().iterator().next();
            final String key2 = fig2Mutable.keySet().iterator().next();
            fig1Mutable.remove(key1);
            fig2Mutable.remove(key2);
            toRet.put(key1, key2);
        }
        return Collections.unmodifiableMap(toRet);
    }

    private static Map<String, String> findAndRemovePerfectMatches(final Map<String, RavensObject> fig1, final Map<String, RavensObject> fig2) {
        final Map<String, String> toRet = new HashMap<>();
        for(Map.Entry<String, RavensObject> entry1 : fig1.entrySet()) {
            for(Map.Entry<String, RavensObject> entry2 : fig2.entrySet()) {
                if(EquivalenceChecker.areObjectsEquivalent(entry1.getValue().getAttributes(), entry2.getValue().getAttributes())) {
                    toRet.put(entry1.getKey(), entry2.getKey());
                }
            }
        }
        for(Map.Entry<String, String> entry : toRet.entrySet()) {
            fig1.remove(entry.getKey());
            fig2.remove(entry.getValue());
        }
        return Collections.unmodifiableMap(toRet);
    }

    private static Map<String, String> findAndRemoveForcedMatches(final Map<String, RavensObject> fig1, final Map<String, RavensObject> fig2) {
        final Map<String, String> toRet = new HashMap<>();
        if(fig1.size() == 1 && fig2.size() == 1) {
            toRet.put(fig1.keySet().iterator().next(), fig2.keySet().iterator().next());
        }
        for(Map.Entry<String, String> entry : toRet.entrySet()) {
            fig1.remove(entry.getKey());
            fig2.remove(entry.getValue());
        }
        return Collections.unmodifiableMap(toRet);
    }
}
