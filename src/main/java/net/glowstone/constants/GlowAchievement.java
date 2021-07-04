package net.glowstone.constants;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Achievement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.bukkit.Achievement.ACQUIRE_IRON;
import static org.bukkit.Achievement.BAKE_CAKE;
import static org.bukkit.Achievement.BOOKCASE;
import static org.bukkit.Achievement.BREED_COW;
import static org.bukkit.Achievement.BREW_POTION;
import static org.bukkit.Achievement.BUILD_BETTER_PICKAXE;
import static org.bukkit.Achievement.BUILD_FURNACE;
import static org.bukkit.Achievement.BUILD_HOE;
import static org.bukkit.Achievement.BUILD_PICKAXE;
import static org.bukkit.Achievement.BUILD_SWORD;
import static org.bukkit.Achievement.BUILD_WORKBENCH;
import static org.bukkit.Achievement.COOK_FISH;
import static org.bukkit.Achievement.DIAMONDS_TO_YOU;
import static org.bukkit.Achievement.ENCHANTMENTS;
import static org.bukkit.Achievement.END_PORTAL;
import static org.bukkit.Achievement.EXPLORE_ALL_BIOMES;
import static org.bukkit.Achievement.FLY_PIG;
import static org.bukkit.Achievement.FULL_BEACON;
import static org.bukkit.Achievement.GET_BLAZE_ROD;
import static org.bukkit.Achievement.GET_DIAMONDS;
import static org.bukkit.Achievement.GHAST_RETURN;
import static org.bukkit.Achievement.KILL_COW;
import static org.bukkit.Achievement.KILL_ENEMY;
import static org.bukkit.Achievement.KILL_WITHER;
import static org.bukkit.Achievement.MAKE_BREAD;
import static org.bukkit.Achievement.MINE_WOOD;
import static org.bukkit.Achievement.NETHER_PORTAL;
import static org.bukkit.Achievement.ON_A_RAIL;
import static org.bukkit.Achievement.OPEN_INVENTORY;
import static org.bukkit.Achievement.OVERKILL;
import static org.bukkit.Achievement.OVERPOWERED;
import static org.bukkit.Achievement.SNIPE_SKELETON;
import static org.bukkit.Achievement.SPAWN_WITHER;
import static org.bukkit.Achievement.THE_END;
import static org.bukkit.Achievement.values;

/**
 * Name mappings for Bukkit Achievements.
 */
public final class GlowAchievement {

    private static final String[] names = new String[values().length];
    private static ImmutableMap.Builder<String, Achievement> achievementMapBuilder;
    private static final ImmutableMap<String, Achievement> ACHIEVEMENT_MAP;

    static {
        achievementMapBuilder = new ImmutableMap.Builder<>();
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
        set(NETHER_PORTAL, "portal");
        set(GHAST_RETURN, "ghast");
        set(GET_BLAZE_ROD, "blazeRod");
        set(BREW_POTION, "potion");
        set(END_PORTAL, "theEnd");
        set(THE_END, "theEnd2");
        set(ENCHANTMENTS, "enchantments");
        set(OVERKILL, "overkill");
        set(BOOKCASE, "bookcase");
        set(EXPLORE_ALL_BIOMES, "exploreAllBiomes");
        set(SPAWN_WITHER, "spawnWither");
        set(KILL_WITHER, "killWither");
        set(FULL_BEACON, "fullBeacon");
        set(BREED_COW, "breedCow");
        set(DIAMONDS_TO_YOU, "diamondsToYou");
        set(OVERPOWERED, "overpowered");
        ACHIEVEMENT_MAP = achievementMapBuilder.build();
        achievementMapBuilder = null;
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
        checkNotNull(achievement, "Achievement cannot be null"); // NON-NLS
        return names[achievement.ordinal()];
    }

    /**
     * Get the Achievement with a given name.
     *
     * @param name the achievement name
     * @return the achievement, or null if none match
     */
    @Nullable
    public static Achievement forName(@NonNls String name) {
        return ACHIEVEMENT_MAP.get(name);
    }

    private static void set(Achievement achievement, @NonNls String key) {
        names[achievement.ordinal()] = "achievement." + key;
        achievementMapBuilder.put(key, achievement);
    }

}
