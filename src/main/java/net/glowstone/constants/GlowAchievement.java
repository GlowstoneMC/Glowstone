package net.glowstone.constants;

import org.bukkit.Achievement;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.bukkit.Achievement.*;

/**
 * Name mappings for Bukkit Achievements.
 */
public final class GlowAchievement {

    private static final String[] names = new String[values().length];
    private static final String[] fancyNames = new String[values().length];

    static {
        set(OPEN_INVENTORY, "openInventory", "Taking Inventory");
        set(MINE_WOOD, "mineWood", "Getting Wood");
        set(BUILD_WORKBENCH, "buildWorkBench", "Benchmarking");
        set(BUILD_PICKAXE, "buildPickaxe", "Time to Mine!");
        set(BUILD_FURNACE, "buildFurnace", "Hot Topic");
        set(ACQUIRE_IRON, "acquireIron", "Acquire Hardware");
        set(BUILD_HOE, "buildHoe", "Time to Farm!");
        set(MAKE_BREAD, "makeBread", "Bake Bread");
        set(BAKE_CAKE, "bakeCake", "The Lie");
        set(BUILD_BETTER_PICKAXE, "buildBetterPickaxe", "Getting an Upgrade");
        set(COOK_FISH, "cookFish", "Delicious Fish");
        set(ON_A_RAIL, "onARail", "On a Rail");
        set(BUILD_SWORD, "buildSword", "Time to Strike!");
        set(KILL_ENEMY, "killEnemy", "Monster Hunter");
        set(KILL_COW, "killCow", "Cow Tipper");
        set(FLY_PIG, "flyPig", "When Pigs Fly");
        set(SNIPE_SKELETON, "snipeSkeleton", "Sniper Duel");
        set(GET_DIAMONDS, "diamonds", "DIAMONDS!");
        set(NETHER_PORTAL, "portal", "We Need to Go Deeper");
        set(GHAST_RETURN, "ghast", "Return to Sender");
        set(GET_BLAZE_ROD, "blazeRod", "Into Fire");
        set(BREW_POTION, "potion", "Local Brewery");
        set(END_PORTAL, "theEnd", "The End?");
        set(THE_END, "theEnd2", "The End.");
        set(ENCHANTMENTS, "enchantments", "Enchanter");
        set(OVERKILL, "overkill", "Overkill");
        set(BOOKCASE, "bookcase", "Librarian");
        set(EXPLORE_ALL_BIOMES, "exploreAllBiomes", "Adventuring Time");
        set(SPAWN_WITHER, "spawnWither", "The Beginning?");
        set(KILL_WITHER, "killWither", "The Beginning.");
        set(FULL_BEACON, "fullBeacon", "Beaconator");
        set(BREED_COW, "breedCow", "Repopulation");
        set(DIAMONDS_TO_YOU, "diamondsToYou", "Diamonds to you!");
        set(OVERPOWERED, "overpowered", "Overpowered");
    }

    private GlowAchievement() {
    }

    /**
     * Get the achievement name for a specified Achievement.
     *
     * @param achievement the Achievement.
     * @return the achievement name.
     */
    public static String getName(Achievement achievement) {
        checkNotNull(achievement, "Achievement cannot be null");
        return names[achievement.ordinal()];
    }

    public static String getFancyName(Achievement achievement) {
        checkNotNull(achievement, "Achievement cannot be null");
        return fancyNames[achievement.ordinal()];
    }

    private static void set(Achievement achievement, String key, String fancyName) {
        names[achievement.ordinal()] = "achievement." + key;
        fancyNames[achievement.ordinal()] = fancyName;
    }

}
