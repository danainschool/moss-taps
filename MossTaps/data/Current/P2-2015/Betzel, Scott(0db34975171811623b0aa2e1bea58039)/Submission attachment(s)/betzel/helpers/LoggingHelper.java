package ravensproject.betzel.helpers;

import java.util.Collection;

/**
 * Created by scott betzel on 6/27/15.
 *
 *
 */
public class LoggingHelper {

    private static boolean errorLoggingEnabled = true;
    private static boolean normalOutputEnabled = true;

    public static void logError(String toLog) {
        if (errorLoggingEnabled) {
            System.err.println(toLog);
        }
    }

    public static void outputString(String toOutput) {
        if (normalOutputEnabled) {
            System.out.println(toOutput);
        }
    }

    public static boolean isErrorLoggingEnabled() {
        return errorLoggingEnabled;
    }

    public static void setErrorLoggingEnabled(boolean errorLoggingEnabled) {
        LoggingHelper.errorLoggingEnabled = errorLoggingEnabled;
    }

    public static boolean isNormalOutputEnabled() {
        return normalOutputEnabled;
    }

    public static void setNormalOutputEnabled(boolean normalOutputEnabled) {
        LoggingHelper.normalOutputEnabled = normalOutputEnabled;
    }

    public static void outputCollection(Collection toOutput) {
        for (Object obj: toOutput) {
            LoggingHelper.outputString(obj.toString());
        }
    }
}
