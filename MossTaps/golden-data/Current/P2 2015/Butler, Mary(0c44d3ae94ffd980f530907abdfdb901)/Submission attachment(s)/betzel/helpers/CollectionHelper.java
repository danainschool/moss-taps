package ravensproject.betzel.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by scottbetzel on 5/31/15.
 *
 * This is a helper for dealing with collections.
 */
public final class CollectionHelper {

    public static <T> List<T> toList(Iterable<T> toConvert) {

        if (toConvert == null) {
            return null;
        }

        if (toConvert instanceof List<?>) {
            return (List<T>)toConvert;
        }

        List<T> toRet = new ArrayList<>();

        for (T item: toConvert) {
            toRet.add(item);
        }

        return toRet;
    }

    public static <T> List<T> toList(T[] toConvert) {
        if (toConvert == null) {
            return null;
        }

        List<T> toRet = new ArrayList<>();
        Collections.addAll(toRet, toConvert);

        return toRet;
    }

    public static <T> List<T> reverse(Iterable<T> toReverse) {
        if (toReverse == null) {
            return null;
        }

        List<T> lstToReverse = CollectionHelper.toList(toReverse);
        List<T> toRet = new ArrayList<>();

        if (lstToReverse.size() <= 0) {
            return toRet;
        }

        for (int i = lstToReverse.size() - 1; i >= 0; i--) {
            toRet.add(lstToReverse.get(i));
        }

        return toRet;
    }

    /**
     * Use this to perform a logical union of 2 Iterables
     *
     * @param firstIterable First Iterable
     * @param secondIterable Second Iterable
     * @return Returns an iterable representing a logical union between the first and second parameters.
     */
    public static <T> Iterable<T> union(Iterable<T> firstIterable, Iterable<T> secondIterable) {
        ArrayList<T> toRet = new ArrayList<>();

        for (T str: firstIterable) {
            toRet.add(str);
        }

        for (T str: secondIterable) {
            if (!toRet.contains(str)) {
                toRet.add(str);
            }
        }

        return toRet;
    }
}
