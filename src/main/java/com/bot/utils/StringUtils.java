package com.bot.utils;

public class StringUtils {

    public static boolean isNumeric(String toCheck) {
        try {
            Integer.parseInt(toCheck);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean nullSafeEquals(String first, String second) {
        if (first == null && second == null) {
            return true;
        }
        return first != null && first.equals(second);
    }
}
