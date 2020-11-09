package ktplusplus.util;

public class CheckUtil {
    public static String checkName(String check) {
        if (!check.startsWith("com.puppycrawl.tools.checkstyle")) {
            return check;
        }
        String[] checkParts = check.split("\\.");
        return checkParts[checkParts.length - 1];
    }
}
