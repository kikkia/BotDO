package com.bot.utils;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public static String EVENT_WAITER_TIMEOUT = "You took too long to respond, please try again.";
    public static String WAR_REACTION_YES = "\uD83C\uDDFE";
    public static String WAR_REACTION_NO = "\uD83C\uDDF3";
    public static String WAR_REACTION_MAYBE = "❓";
    public static String WAR_REACTION_REFRESH = "\uD83D\uDD04";
    public static List<String> WAR_REACTIONS = Arrays.asList(WAR_REACTION_NO, WAR_REACTION_YES,
            WAR_REACTION_MAYBE, WAR_REACTION_REFRESH);
}
