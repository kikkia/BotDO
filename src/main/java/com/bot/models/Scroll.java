package com.bot.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Scroll {
    BHEG(Collections.singletonList("bheg"), "Bheg"),
    AWK_BHEG(getAwkAliases(BHEG), "Awakened Bheg"),
    RED_NOSE(Arrays.asList("rednose", "rn"), "Red Nose"),
    AWK_RED_NOSE(getAwkAliases(RED_NOSE), "Awakened Red Nose"),
    GIATH(Collections.singletonList("giath"), "Giath"),
    AWK_GIATH(getAwkAliases(GIATH), "Awakened Giath"),
    MOGHULIS(Arrays.asList("moghulis", "mog"), "Moghulis"),
    AGRAKHAN(Arrays.asList("agrakhan", "agra"), "Agrakhan"),
    NARC(Collections.singletonList("narc"), "Narc"),
    AWK_NARC(getAwkAliases(NARC), "Awakened Narc"),
    RONIN(Collections.singletonList("ronin"), "Ronin"),
    AWK_RONIN(getAwkAliases(RONIN), "Awakened Ronin"),
    DIM_TREE(Arrays.asList("dimtree", "tree", "dim"), "Dim Tree"),
    AWK_DIM_TREE(getAwkAliases(DIM_TREE), "Awakened Dim Tree"),
    MUSKAN(Arrays.asList("muskan", "musk", "muski"), "Muskan"),
    AWK_MUSKAN(getAwkAliases(MUSKAN), "Awakened Muskan"),
    HEXE(Arrays.asList("hexe", "witch", "hexemarie"), "Hexe"),
    AWK_HEXE(getAwkAliases(HEXE), "Awakened Hexe"),
    AHIB(Arrays.asList("ahib", "griffon"), "Ahib"),
    AWK_AHIB(getAwkAliases(AHIB), "Awakened Ahib"),
    URUGON(Arrays.asList("urugon", "uru"), "Urugon"),
    AWK_URUGON(getAwkAliases(URUGON), "Awakened Urugon"),
    PUTURUM(Arrays.asList("puturum", "putrum"), "Puturum"),
    TITIUM(Arrays.asList("titium", "fogan"), "Titium"),
    AWK_TITIUM(getAwkAliases(TITIUM), "Awakened Titium"),
    ARC(Arrays.asList("arc", "ancientrelic", "ancientreliccrystal", "reliccrystal", "disco"), "Arc"),
    CARTIAN(Arrays.asList("cartian", "cartianspell"), "Cartian Spell"),
    PILA_FE(Arrays.asList("pila", "pilafe", "groundbeef", "meatman"), "Pila Fe"),
    VOODOO(Collections.singletonList("voodoo"), "Voodoo"),
    LEEBUR(Collections.singletonList("leebur"), "Leebur"),
    RIFT_ECHO(Arrays.asList("rift_echo", "re", "rift", "rifts", "echo"), "Rift Echo");

    private List<String> aliases;
    private String displayName;

    private Scroll(List<String> aliases, String displayName) {
        this.aliases = aliases;
        this.displayName = displayName;
    }

    public static Scroll getScrollForName(String name) {
        var cleanedName = name.trim().toLowerCase().replace(" ", "");
        for (Scroll s : Scroll.values()) {
            if (s.aliases.contains(cleanedName) ||
                    s.displayName.equals(name)) {
                return s;
            }
        }
        return null;
    }

    private static List<String> getAwkAliases(Scroll base) {
        return base.aliases.stream()
                .flatMap(e -> Stream.of("a_" + e,
                        "a" + e,
                        "awk" + e,
                        "awk_" + e))
                .collect(Collectors.toList());
    }

    public String getDisplayName() {
        return displayName;
    }
}
