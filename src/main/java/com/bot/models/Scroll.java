package com.bot.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Scroll {
    // TODO: AWK Scrolls stream to make aliases
    BHEG(Collections.singletonList("bheg"), "Bheg"),
    AWK_BHEG(Arrays.asList("abheg", "awkbheg"), "Awakened Bheg"),
    RED_NOSE(Arrays.asList("rednose", "rn"), "Red Nose"),
    AWK_RED_NOSE(Arrays.asList("arn", "awkrednose", "awkrn", "arednose"), "Awakened Red Nose"),
    GIATH(Collections.singletonList("giath"), "Giath"),
    AWK_GIATH(Arrays.asList("agiath", "awkgiath"), "Awakened Giath"),
    MOGHULIS(Arrays.asList("moghulis", "mog"), "Moghulis"),
    AGRAKHAN(Arrays.asList("agrakhan", "agra"), "Agrakhan"),
    NARC(Collections.singletonList("narc"), "Narc"),
    AWK_NARC(Arrays.asList("anarc", "awknarc"), "Awakened Narc"),
    RONIN(Collections.singletonList("ronin"), "Ronin"),
    AWK_RONIN(Arrays.asList("aronin", "awkronin"), "Awakened Ronin"),
    DIM_TREE(Arrays.asList("dimtree", "tree", "dim"), "Dim Tree"),
    AWK_DIM_TREE(Arrays.asList("adimtree", "awkdimtree", "adim", "awkdim", "atree", "awktree"), "Awakened Dim Tree"),
    MUSKAN(Arrays.asList("muskan", "musk", "muski"), "Muskan"),
    AWK_MUSKAN(null, "Awakened Muskan"),
    HEXE(Arrays.asList("hexe", "witch", "hexemarie"), "Hexe"),
    AWK_HEXE(null, "Awakened Hexe"),
    AHIB(Arrays.asList("ahib", "griffon"), "Ahib"),
    AWK_AHIB(null, "Awakened Ahib"),
    URUGON(Arrays.asList("urugon", "uru"), "Urugon"),
    AWK_URUGON(null, "Awakened Urugon"),
    PUTURUM(Arrays.asList("rednose", "rn"), "Puturum"), // TODO: NAME
    TITIUM(Arrays.asList("titium", "fogan"), "Titium"),
    AWK_TITIUM(null, "Awakened Titium"),
    ARC(Arrays.asList("arc", "ancientrelic", "ancientreliccrystal", "reliccrystal", "disco"), "Arc"),
    CARTIAN(Arrays.asList("cartian", "cartianspell"), "Cartian Spell"),
    PILA_FE(Arrays.asList("pila", "pilafe", "groundbeef", "meatman"), "Pila Fe"),
    VOODOO(Collections.singletonList("voodoo"), "Voodoo");

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
}
