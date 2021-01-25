package com.bot.utils;

import com.bot.models.Scroll;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.List;

public class CommandParsingUtils {

    public static List<Pair<Scroll, Integer>> parseScrollUpdates(String input) throws IllegalArgumentException {
        var scrolls = input.split(",");

        // Scroll pair denotes a pair of scroll/number
        List<Pair<Scroll, Integer>> scrollPairs = new ArrayList<>();

        for (String scrollPair : scrolls) {
            var parts = scrollPair.split(" ");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Error found near `" +
                        scrollPair + "` More than 2 parts found.");
            }
            // Find the scroll name and the quantity
            // TODO: Custom exceptions
            int quantity;
            Scroll scroll;
            if (StringUtils.isNumeric(parts[0])) {
                quantity = Integer.parseInt(parts[0]);
                scroll = Scroll.getScrollForName(parts[1]);
            } else if (StringUtils.isNumeric(parts[1])) {
                quantity = Integer.parseInt(parts[1]);
                scroll = Scroll.getScrollForName(parts[0]);
            } else {
                throw new IllegalArgumentException("Error found near `" +
                        scrollPair + "` No quantity found.");
            }
            if (scroll == null) {
                throw new IllegalArgumentException("Error found near `" +
                        scrollPair + "` No valid scroll name found.");
            }
            scrollPairs.add(new Pair<>(scroll, quantity));
        }
        return scrollPairs;
    }
}
