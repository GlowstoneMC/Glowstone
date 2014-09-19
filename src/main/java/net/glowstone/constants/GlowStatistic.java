package net.glowstone.constants;

import org.apache.commons.lang.Validate;
import org.bukkit.Statistic;

import static org.bukkit.Statistic.*;

/**
 * Name mappings for statistics.
 */
public final class GlowStatistic {

    private GlowStatistic() {}

    private static final String[] names = new String[Statistic.values().length];

    /**
     * Get the statistic name for a specified Statistic.
     * @param stat the Statistic.
     * @return the statistic name.
     */
    public static String getName(Statistic stat) {
        Validate.notNull(stat, "Achievement cannot be null");
        return names[stat.ordinal()];
    }

    private static void set(Statistic stat, String key) {
        names[stat.ordinal()] = "stat." + key;
    }

    static {
        set(LEAVE_GAME, "leaveGame");
        set(PLAY_ONE_TICK, "playOneMinute"); // this is correct
        set(WALK_ONE_CM, "walkOneCm");
        set(SWIM_ONE_CM, "swimOneCm");
        set(FALL_ONE_CM, "fallOneCm");
        set(CLIMB_ONE_CM, "climbOneCm");
        set(FLY_ONE_CM, "flyOneCm");
        set(DIVE_ONE_CM, "diveOneCm");
        set(MINECART_ONE_CM, "minecartOneCm");
        set(BOAT_ONE_CM, "boatOneCm");
        set(PIG_ONE_CM, "pigOneCm");
        set(HORSE_ONE_CM, "horseOneCm");
        set(JUMP, "jump");
        set(DROP, "drop");
        set(DAMAGE_DEALT, "damageDealt");
        set(DAMAGE_TAKEN, "damageTaken");
        set(DEATHS, "deaths");
        set(MOB_KILLS, "mobKills");
        set(ANIMALS_BRED, "animalsBred");
        set(PLAYER_KILLS, "playerKills");
        set(FISH_CAUGHT, "fishCaught");
        set(JUNK_FISHED, "junkFished");
        set(TREASURE_FISHED, "treasureFished");

        // todo: statistics with substatistics
    }

}
