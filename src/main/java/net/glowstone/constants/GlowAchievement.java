package net.glowstone.constants;

import org.apache.commons.lang.Validate;
import org.bukkit.Achievement;

import static org.bukkit.Achievement.*;

/**
 * Name mappings for Bukkit Achievements.
 */
public final class GlowAchievement {

    private GlowAchievement() {}

    private static final String[] names = new String[Achievement.values().length];

    /**
     * Get the achievement name for a specified Achievement.
     * @param achievement the Achievement.
     * @return the achievement name.
     */
    public static String getName(Achievement achievement) {
        Validate.notNull(achievement, "Achievement cannot be null");
        return names[achievement.ordinal()];
    }

    private static void set(Achievement achievement, String key) {
        names[achievement.ordinal()] = "achievement." + key;
    }

    static {
        set(OPEN_INVENTORY, "openInventory");
        set(MINE_WOOD, "mineWood");
        set(BUILD_WORKBENCH, "buildWorkBench");
        set(BUILD_PICKAXE, "buildPickaxe");
        set(BUILD_FURNACE, "buildFurnace");
        set(ACQUIRE_IRON, "acquireIron");
        set(BUILD_HOE, "buildHoe");
        set(MAKE_BREAD, "makeBread");
        set(BAKE_CAKE, "bakeCake");
        set(BUILD_BETTER_PICKAXE, "buildBetterPickaxe");
        set(COOK_FISH, "cookFish");
        set(ON_A_RAIL, "onARail");
        set(BUILD_SWORD, "buildSword");
        set(KILL_ENEMY, "killEnemy");
        set(KILL_COW, "killCow");
        set(FLY_PIG, "flyPig");
        set(SNIPE_SKELETON, "snipeSkeleton");
        set(GET_DIAMONDS, "diamonds");
        set(DIAMONDS_TO_YOU, "diamondsToYou");
        set(NETHER_PORTAL, "portal");
        set(GHAST_RETURN, "ghast");
        set(GET_BLAZE_ROD, "blazeRod");
        set(BREW_POTION, "potion");
        set(END_PORTAL, "theEnd");
        set(THE_END, "theEnd2");
        set(ENCHANTMENTS, "enchantments");
        set(OVERKILL, "overkill");
        set(BOOKCASE, "bookcase");
        set(BREED_COW, "breedCow");
        set(SPAWN_WITHER, "spawnWither");
        set(KILL_WITHER, "killWither");
        set(FULL_BEACON, "fullBeacon");
        set(EXPLORE_ALL_BIOMES, "exploreAllBiomes");
    }

}
