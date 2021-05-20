package com.bot.models

enum class WarNode(val id: Int, val displayName: String, val aliases: List<String>,
                   val cap: Int, val tier: NodeWarTier, val WarDay: WarDay) {
    ALEJANDRO_FARM(1, "Alejandro Farm", listOf("alejandro", "farm"), 25,
            NodeWarTier.TIER1MEDIUM, WarDay.SUNDAY),
    COASTAL_CAVE(2, "Coastal Cave", listOf("coastal", "cave"), 55,
            NodeWarTier.TIER1MEDIUM, WarDay.SUNDAY),
    NORTH_ABANDONED_QUARRY(3, "North Abandoned Quarry", listOf("abandoned quarry", "north abandoned", "quarry"),
            25, NodeWarTier.TIER1HARD, WarDay.SUNDAY),
    PILGRIMS_FAST(4, "Pilgrim's Sanctum: Fast", listOf("fast", "pilgrims fast"), 40, NodeWarTier.TIER1HARD,
            WarDay.SUNDAY),
    TREANT_FOREST(5, "Treant Forest", listOf("treant"), 40, NodeWarTier.TIER1EASY, WarDay.SUNDAY),
    TUNGRAD_FOREST(6, "Tungrad Forest", listOf("tungrad"), 40, NodeWarTier.TIER1HARD, WarDay.SUNDAY),
    WOLF_HILLS(7, "Wolf hills", listOf("wolf"), 25, NodeWarTier.TIER1MEDIUM, WarDay.SUNDAY),
    BEHR_RIVERHEAD(8, "Behr Riverhead", listOf("riverhead", "behr"), 55, NodeWarTier.TIER1MEDIUM,
        WarDay.MONDAY),
    ELDERS_BRIDGE(9, "Elder's Bridge", listOf("elders", "elder", "elder's", "bridge"), 25,
            NodeWarTier.TIER1MEDIUM, WarDay.MONDAY),
    IVORY_WASTELAND(10, "Ivory Wasteland", listOf("ivory", "wasteland"), 40,
            NodeWarTier.TIER1MEDIUM, WarDay.MONDAY),
    KEPLAN_VICINITY(11, "Keplan Vicinity", listOf("keplan", "vicinity"), 40,
            NodeWarTier.TIER1MEDIUM, WarDay.MONDAY),
    KHURUTO_CAVE(12, "Khuruto Cave", listOf("cave", "khuruto"), 25, NodeWarTier.TIER1HARD, WarDay.MONDAY),
    MARNIS_2ND_LAB(13, "Marni's 2nd lab", listOf("lab", "marni", "marni's", "marni"),
            25, NodeWarTier.TIER1HARD, WarDay.MONDAY),
    BEHR_DOWNSTREAM(14, "Behr Downstream", listOf("downstream", "behr"), 40, NodeWarTier.TIER1MEDIUM,
            WarDay.TUESDAY),
    BRADIE_FORTRESS(15, "Bradie Fort", listOf("bradie", "fort", "fortress"), 25,
            NodeWarTier.TIER1HARD, WarDay.TUESDAY),
    CRON_CASTLE_SITE(16, "Cron Castle Site", listOf("cron", "castle", "cron castle"), 20,
            NodeWarTier.TIER1EASY, WarDay.TUESDAY),
    MARNIS_LAB(17, "Marni's Lab", listOf("marnis", "marni", "marnis lab"), 25,
            NodeWarTier.TIER1MEDIUM, WarDay.TUESDAY),
    OZE_PASS(18, "Oze Pass", listOf("oze", "pass"), 55, NodeWarTier.TIER1MEDIUM, WarDay.TUESDAY),
    ALTAS_FARMLAND(19, "Altas Farmland", listOf("altas", "farm", "farmland"), 25,
            NodeWarTier.TIER1EASY, WarDay.WEDNESDAY),
    HELMS_POST(20, "Helm's Post", listOf("helms", "helm's"), 40,
            NodeWarTier.TIER1MEDIUM, WarDay.WEDNESDAY),
    IVARO_CLIFF(21, "Ivaro Cliff", listOf("ivaro", "cliff"), 55,
            NodeWarTier.TIER1MEDIUM, WarDay.WEDNESDAY),
    LONGLEAF_TREE_FOREST(22, "Longleaf Tree Forest", listOf("longleaf", "forest"), 40,
            NodeWarTier.TIER1HARD, WarDay.WEDNESDAY),
    NORTHERN_GAURD_CAMP(23, "Northern Gaurd Camp", listOf("northern", "camp", "gaurd camp"),
            40, NodeWarTier.TIER1MEDIUM, WarDay.WEDNESDAY),
    SOUTHERN_NEUTRAL_ZONE(24, "Southern Neutral Zone", listOf("southern", "neutral zone", "zone"),
            25, NodeWarTier.TIER1HARD, WarDay.WEDNESDAY),
    TOSCANI_FARM(25, "Toscani Farm", listOf("toscani", "farm"), 25,
            NodeWarTier.TIER1HARD, WarDay.WEDNESDAY),
    CONTAMINATED_FARM(26, "Contaminated farm", listOf("contaminated"), 55,
            NodeWarTier.TIER1HARD, WarDay.THURSDAY),
    FOHLAM_FARM(27, "Fohlam Farm", listOf("fohlam"), 40, NodeWarTier.TIER1MEDIUM, WarDay.THURSDAY),
    RAKSHAN_OBSERVATORY(28, "Rakshan Observatory", listOf("observatory", "rakshan"), 25,
            NodeWarTier.TIER1MEDIUM, WarDay.THURSDAY),
    SOUTHERN_CIGERNA(29, "Southern Cigerna", listOf("southern", "cigerna"), 40,
            NodeWarTier.TIER1MEDIUM, WarDay.THURSDAY),
    WALE_FARM(30, "Wale Farm", listOf("wale"), 55, NodeWarTier.TIER1MEDIUM, WarDay.THURSDAY),
    BASHIM_BASE(31, "Bashim Base", listOf("bashim", "base"), 55,
            NodeWarTier.TIER1HARD, WarDay.FRIDAY),
    BLOODY_MONASTERY(32, "Bloody Monastery", listOf("bloody", "monastary"), 40,
            NodeWarTier.TIER1MEDIUM, WarDay.FRIDAY),
    EHWAZ_HILL(33, "Ehwaz Hill", listOf("hill", "ehwaz"), 55,
            NodeWarTier.TIER1HARD, WarDay.FRIDAY),
    MANES_HIDEOUT(34, "Mane's hideout", listOf("manes", "mane's"), 25,
            NodeWarTier.TIER1MEDIUM, WarDay.FRIDAY),
    WATCHTOWER(35, "Watchtower", listOf(), 55, NodeWarTier.TIER1HARD, WarDay.FRIDAY);

    companion object {
        fun getNodeFromName(name: String) : WarNode? {
            val cleanedName = name.trim().toLowerCase()
            for (c in values()) {
                if (c.aliases.contains(cleanedName) || c.displayName.toLowerCase() == cleanedName) {
                    return c
                }
            }
            return null
        }

        fun getNodeFromName(name: String, day: WarDay) : WarNode? {
            val cleanedName = name.trim().toLowerCase()
            for (c in values()) {
                if (day != c.WarDay) continue
                if (c.aliases.contains(cleanedName) || c.displayName.toLowerCase() == cleanedName) {
                    return c
                }
            }
            return null
        }

        fun getNodeFromId(id: Int?) : WarNode? {
            for (c in values()) {
                if (c.id == id) return c
            }
            return null
        }
    }
}