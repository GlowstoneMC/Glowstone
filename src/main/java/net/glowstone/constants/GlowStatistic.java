package net.glowstone.constants;

import org.bukkit.Statistic;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.bukkit.Statistic.*;

/**
 * Name mappings for statistics.
 */
public final class GlowStatistic {

    private static final String[] names = new String[Statistic.values().length];

    static {
        set(LEAVE_GAME, "leaveGame");
        set(PLAY_ONE_TICK, "playOneMinute"); // this is correct
        set(WALK_ONE_CM, "walkOneCm");
        set(SWIM_ONE_CM, "swimOneCm");
        set(FALL_ONE_CM, "fallOneCm");
        set(SNEAK_TIME, "sneakTime");
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
        set(SPRINT_ONE_CM, "sprintOneCm");
        set(CROUCH_ONE_CM, "crouchOneCm");
        set(AVIATE_ONE_CM, "aviateOneCm");
        set(TIME_SINCE_DEATH, "timeSinceDeath");
        set(TALKED_TO_VILLAGER, "talkedToVillager");
        set(TRADED_WITH_VILLAGER, "tradedWithVillager");
        set(CAKE_SLICES_EATEN, "cakeSlices_eaten");
        set(CAULDRON_FILLED, "cauldronFilled");
        set(CAULDRON_USED, "cauldronUsed");
        set(ARMOR_CLEANED, "armorCleaned");
        set(BANNER_CLEANED, "bannerCleaned");
        set(BREWINGSTAND_INTERACTION, "brewingstandInteraction");
        set(BEACON_INTERACTION, "beaconInteraction");
        set(DROPPER_INSPECTED, "dropperInspected");
        set(HOPPER_INSPECTED, "hopperInspected");
        set(DISPENSER_INSPECTED, "dispenserInspected");
        set(NOTEBLOCK_PLAYED, "noteblockPlayed");
        set(NOTEBLOCK_TUNED, "noteblockTuned");
        set(FLOWER_POTTED, "flowerPotted");
        set(TRAPPED_CHEST_TRIGGERED, "trappedChestTriggered");
        set(ENDERCHEST_OPENED, "enderchestOpened");
        set(ITEM_ENCHANTED, "itemEnchanted");
        set(RECORD_PLAYED, "recordPlayed");
        set(FURNACE_INTERACTION, "furnaceInteraction");
        set(CRAFTING_TABLE_INTERACTION, "craftingTableInteraction");
        set(CHEST_OPENED, "chestOpened");
        set(SLEEP_IN_BED, "sleepInBed");

        // todo: statistics with substatistics
    }

    private GlowStatistic() {
    }

    /**
     * Get the statistic name for a specified Statistic.
     *
     * @param stat the Statistic.
     * @return the statistic name.
     */
    public static String getName(Statistic stat) {
        checkNotNull(stat, "Achievement cannot be null");
        return names[stat.ordinal()];
    }

    private static void set(Statistic stat, String key) {
        names[stat.ordinal()] = "stat." + key;
    }

}
