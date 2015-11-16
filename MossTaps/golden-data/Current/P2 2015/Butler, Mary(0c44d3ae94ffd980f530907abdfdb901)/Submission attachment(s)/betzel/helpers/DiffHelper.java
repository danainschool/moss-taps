package ravensproject.betzel.helpers;


import java.util.HashMap;
import java.util.List;

/**
 * Created by scott betzel on 6/1/15.
 *
 * Use this class as a utility for creating diffs amongst an array of strings
 */
public final class DiffHelper {

    /**
     * Use this to get the key for the HashMap created by createDiffMap
     *
     * @param sourceString The source String used to generate the key
     * @param destString The dest String used to generate the key
     * @return Returns the key based on the sourceString and the destString
     */
    public static String getKey(String sourceString, String destString) {
        String toRet = String.format("%s_%s", sourceString, destString);
        return toRet;
    }

    /**
     * Pass in an ordered array of strings (ordered by similarity).  This will
     * return a HashMap containing lookup values of the difference.  If you have a large
     * number of possible attributes, it may be better to evaluate these values on the fly.
     *
     * @param orderedArray An array of Strings ordered by similarity.
     * @return Returns a HashMap where the key is sourceString_destString and the value
     * is the similarity (which is a double from 0 to 1).
     */
    public static HashMap<String, Double> createDiffMap(String[] orderedArray) {
        List<String> orderedList = CollectionHelper.toList(orderedArray);
        String currentKey;
        HashMap<String, Double> toRet = new HashMap<>();
        String firstString;
        String secondString;
        double toSet;

        if (orderedList.size() == 1) {
            toRet.put(String.format("%s_%s", orderedList.get(0), orderedList.get(0)), 0.0);
            return toRet;
        }

        for (int i = 0; i < orderedList.size(); i++) {
            firstString = orderedList.get(i);

            for (int j = 0; j < orderedList.size(); j++) {
                secondString = orderedList.get(j);

                currentKey = DiffHelper.getKey(firstString, secondString);

                if (!toRet.containsKey(currentKey)) {
                    toSet = DiffHelper.getDiffValue(i, j, orderedList.size());
                    toRet.put(currentKey, toSet);
                }

            }

        }

        return toRet;
    }

    /**
     * This returns 0.0 if the attribute values are the same, but 1.0 if they are different
     *
     * @param sourceValue the source value
     * @param destValue the destination value
     * @return This returns 0.0 if the attribute values are the same, but 1.0 if they are different
     */
    public static double performAllOrNothingComparison(String sourceValue, String destValue) {
        return sourceValue.compareTo(destValue);
    }

    /**
     * Use this to get a diff value from a uniformly distributed collection
     * that is ordered by similarity
     * @param sourceIndex the index of the source element
     * @param destIndex the index of the destination element
     * @param collectionSize the dize of the entire collection
     * @return The difference value.  0.0 if the same, 1.0 if completely different
     */
    public static double getDiffValue(int sourceIndex, int destIndex, int collectionSize) {
        if (collectionSize <= 1) {
            return 0.0;
        }

        return (double)Math.abs(sourceIndex - destIndex) / (double)(collectionSize - 1);
    }

    public static double getValue(int sourceIndex, int collectionSize) {
        return (double)sourceIndex / (double)(collectionSize - 1);
    }

    /**
     * Get the original strings back that you constructed your key from
     *
     * @param key The constructed key
     * @return A 2D array of strings that made up the key
     */
    public static String[] getOriginalKeys(String key) {
        return key.split("_");
    }
}
