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
}
