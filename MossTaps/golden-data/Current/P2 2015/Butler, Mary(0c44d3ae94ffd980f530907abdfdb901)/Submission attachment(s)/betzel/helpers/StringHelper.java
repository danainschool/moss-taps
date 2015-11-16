package ravensproject.betzel.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by scott betzel on 6/27/15.
 *
 *
 */
public class StringHelper {

    public static String buildConstantWidthString(List<String> theStrings) {
        int maxWidth = getMaxWidth(theStrings);
        StringBuilder toRet = new StringBuilder(fixedLengthColumnString(theStrings.get(0), maxWidth));
        toRet.append(fixedLengthColumnString(getDashes(maxWidth), maxWidth));
        String str;

        for (int i = 1; i < theStrings.size(); i++) {
            str = theStrings.get(i);
            toRet.append(fixedLengthColumnString(str, maxWidth));
        }

        return toRet.toString();
    }

    public static String getDashes(int width) {
        StringBuilder toRet = new StringBuilder("");

        for (int i = 0; i < width; i++) {
            toRet.append("-");
        }

        return toRet.toString();
    }

    public static String fixedLengthString(String string, int length) {
        return String.format("%1$" + length + "s", string);
    }

    private static String fixedLengthColumnString(String string, int length) {
        return String.format("| %s\n", fixedLengthString(string, length));
    }

    public static int getMaxWidth(Collection<String> theStrings) {
        int maxWidth = 0;
        int currentLength;

        for (String str: theStrings) {
            currentLength = str.length();

            if (currentLength > maxWidth) {
                maxWidth = currentLength;
            }
        }

        return maxWidth;
    }

    public static String buildTable(List<String> allStrings) {
        String[][] theStrings = new String[allStrings.size()][];
        StringBuilder toRet = new StringBuilder("");

        int maxSize = 0;

        for (int i = 1; i < allStrings.size(); i++) {
            theStrings[i] = allStrings.get(i).split("\\r?\\n");
            if (theStrings[i].length > maxSize) {
                maxSize = theStrings[i].length;
            }
        }

        for (int j = 0; j < maxSize; j++) {

            for (int i = 1; i < theStrings.length; i++) {
                if (theStrings[i].length > j) {
                    toRet.append(theStrings[i][j]);
                }
            }

            toRet.append("\n");
        }

        int maxWidthOfTable = getMaxWidth(CollectionHelper.toList(toRet.toString().split("\\r?\\n")));

        StringBuilder main = new StringBuilder("");
        main.append(fixedLengthColumnString(allStrings.get(0), maxWidthOfTable));
        main.append(fixedLengthColumnString(getDashes(maxWidthOfTable), maxWidthOfTable));
        main.append(toRet);

        return main.toString();
    }
}
