package net.glowstone.constants;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.bukkit.Statistic.ANIMALS_BRED;
import static org.bukkit.Statistic.ARMOR_CLEANED;
import static org.bukkit.Statistic.AVIATE_ONE_CM;
import static org.bukkit.Statistic.BANNER_CLEANED;
import static org.bukkit.Statistic.BEACON_INTERACTION;
import static org.bukkit.Statistic.BOAT_ONE_CM;
import static org.bukkit.Statistic.BREWINGSTAND_INTERACTION;
import static org.bukkit.Statistic.CAKE_SLICES_EATEN;
import static org.bukkit.Statistic.CAULDRON_FILLED;
import static org.bukkit.Statistic.CAULDRON_USED;
import static org.bukkit.Statistic.CHEST_OPENED;
import static org.bukkit.Statistic.CLIMB_ONE_CM;
import static org.bukkit.Statistic.CRAFTING_TABLE_INTERACTION;
import static org.bukkit.Statistic.CROUCH_ONE_CM;
import static org.bukkit.Statistic.DAMAGE_DEALT;
import static org.bukkit.Statistic.DAMAGE_TAKEN;
import static org.bukkit.Statistic.DEATHS;
import static org.bukkit.Statistic.DISPENSER_INSPECTED;
import static org.bukkit.Statistic.DIVE_ONE_CM;
import static org.bukkit.Statistic.DROP;
import static org.bukkit.Statistic.DROPPER_INSPECTED;
import static org.bukkit.Statistic.ENDERCHEST_OPENED;
import static org.bukkit.Statistic.FALL_ONE_CM;
import static org.bukkit.Statistic.FISH_CAUGHT;
import static org.bukkit.Statistic.FLOWER_POTTED;
import static org.bukkit.Statistic.FLY_ONE_CM;
import static org.bukkit.Statistic.FURNACE_INTERACTION;
import static org.bukkit.Statistic.HOPPER_INSPECTED;
import static org.bukkit.Statistic.HORSE_ONE_CM;
import static org.bukkit.Statistic.ITEM_ENCHANTED;
import static org.bukkit.Statistic.JUMP;
import static org.bukkit.Statistic.LEAVE_GAME;
import static org.bukkit.Statistic.MINECART_ONE_CM;
import static org.bukkit.Statistic.MOB_KILLS;
import static org.bukkit.Statistic.NOTEBLOCK_PLAYED;
import static org.bukkit.Statistic.NOTEBLOCK_TUNED;
import static org.bukkit.Statistic.PIG_ONE_CM;
import static org.bukkit.Statistic.PLAYER_KILLS;
import static org.bukkit.Statistic.PLAY_ONE_TICK;
import static org.bukkit.Statistic.RECORD_PLAYED;
import static org.bukkit.Statistic.SHULKER_BOX_OPENED;
import static org.bukkit.Statistic.SLEEP_IN_BED;
import static org.bukkit.Statistic.SNEAK_TIME;
import static org.bukkit.Statistic.SPRINT_ONE_CM;
import static org.bukkit.Statistic.SWIM_ONE_CM;
import static org.bukkit.Statistic.TALKED_TO_VILLAGER;
import static org.bukkit.Statistic.TIME_SINCE_DEATH;
import static org.bukkit.Statistic.TRADED_WITH_VILLAGER;
import static org.bukkit.Statistic.TRAPPED_CHEST_TRIGGERED;
import static org.bukkit.Statistic.WALK_ONE_CM;
import static org.bukkit.Statistic.values;

import org.bukkit.Statistic;
import org.jetbrains.annotations.NonNls;

/**
 * Name mappings for statistics.
 */
public final class GlowStatistic {

    private static final String[] names = new String[values().length];

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
        set(SHULKER_BOX_OPENED, "shulkerBoxOpened");

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

    private static void set(Statistic stat, @NonNls String key) {
        names[stat.ordinal()] = "stat." + key;
    }

}
