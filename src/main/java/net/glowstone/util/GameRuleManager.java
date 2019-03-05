package net.glowstone.util;

import static net.glowstone.constants.GameRules.ANNOUNCE_ADVANCEMENTS;
import static net.glowstone.constants.GameRules.COMMAND_BLOCK_OUTPUT;
import static net.glowstone.constants.GameRules.DO_DAYLIGHT_CYCLE;
import static net.glowstone.constants.GameRules.DO_ENTITY_DROPS;
import static net.glowstone.constants.GameRules.DO_FIRE_TICK;
import static net.glowstone.constants.GameRules.DO_LIMITED_CRAFTING;
import static net.glowstone.constants.GameRules.DO_MOB_LOOT;
import static net.glowstone.constants.GameRules.DO_MOB_SPAWNING;
import static net.glowstone.constants.GameRules.DO_TILE_DROPS;
import static net.glowstone.constants.GameRules.GAME_LOOP_FUNCTION;
import static net.glowstone.constants.GameRules.KEEP_INVENTORY;
import static net.glowstone.constants.GameRules.LOG_ADMIN_COMMANDS;
import static net.glowstone.constants.GameRules.MAX_COMMAND_CHAIN_LENGTH;
import static net.glowstone.constants.GameRules.MOB_GRIEFING;
import static net.glowstone.constants.GameRules.NATURAL_REGENERATION;
import static net.glowstone.constants.GameRules.RANDOM_TICK_SPEED;
import static net.glowstone.constants.GameRules.REDUCED_DEBUG_INFO;
import static net.glowstone.constants.GameRules.SEND_COMMAND_FEEDBACK;
import static net.glowstone.constants.GameRules.SHOW_DEATH_MESSAGES;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NonNls;

/**
 * Container for the game rule map for worlds.
 */
public final class GameRuleManager implements DynamicallyTypedMap<String> {

    private final Map<String, String> gameRules = new HashMap<>();

    /**
     * Creates an instance with the vanilla game rules.
     */
    public GameRuleManager() {
        setValue(COMMAND_BLOCK_OUTPUT, true);
        setValue(DO_DAYLIGHT_CYCLE, true); // implemented
        setValue(DO_ENTITY_DROPS, true);
        setValue(DO_FIRE_TICK, true); // implemented
        setValue(DO_MOB_LOOT, true); // implemented
        setValue(DO_MOB_SPAWNING, true); // implemented (partial)
        setValue(DO_TILE_DROPS, true); // implemented
        setValue(KEEP_INVENTORY, false); // implemented
        setValue(LOG_ADMIN_COMMANDS, true);
        setValue(MOB_GRIEFING, true);
        setValue(NATURAL_REGENERATION, true);
        setValue(RANDOM_TICK_SPEED, 3);
        setValue(REDUCED_DEBUG_INFO, false); // implemented
        setValue(SEND_COMMAND_FEEDBACK, true);
        setValue(SHOW_DEATH_MESSAGES, true);
        setValue(ANNOUNCE_ADVANCEMENTS, true);
        setValue(DO_LIMITED_CRAFTING, false);
        setValue(GAME_LOOP_FUNCTION, "");
        setValue(MAX_COMMAND_CHAIN_LENGTH, 65536);
    }

    /**
     * Gets all of the game rules defined.
     *
     * @return the game rules defined, may be empty
     */
    public String[] getKeys() {
        return gameRules.keySet().toArray(new String[0]);
    }

    /**
     * <p>Sets the value of a game rule.</p>
     *
     * <p>The actual object value is never stored, only the string value.</p>
     *
     * <p>The helper methods provided in this class may be used to retrieve the value, such as
     * {@link #getBoolean(String)}.</p>
     *
     * @param rule the rule to set, cannot be null
     * @param value the value to set, cannot be null or be represented as null
     * @return true if set, false otherwise
     */
    public boolean setValue(@NonNls String rule, Object value) {
        if (rule != null && value != null && value.toString() != null) {
            gameRules.put(rule, value.toString());
            return true;
        }
        return false;
    }

    /**
     * Gets whether or not the supplied rule is defined.
     *
     * @param rule the rule to lookup
     * @return true if defined, false otherwise
     */
    public boolean isGameRule(@NonNls String rule) {
        return rule != null && gameRules.containsKey(rule);
    }

    /**
     * Gets the game rule value as a string. If the value does not exist, then this will return
     * null.
     *
     * @param rule the rule to look up
     * @return the string value, or null if not defined
     */
    public String getString(String rule) {
        if (isGameRule(rule)) {
            return gameRules.get(rule);
        }
        return null;
    }

    /**
     * Gets the game rule value as a boolean. If the value cannot be parsed or does not exist, then
     * this will return false.
     *
     * @param rule the rule to look up
     * @return the boolean value, or false
     */
    public boolean getBoolean(@NonNls String rule) {
        if (isGameRule(rule)) {
            String value = getString(rule);
            if (value != null) {
                return Boolean.parseBoolean(value); // defaults to false
            }
        }
        return false;
    }

    @Override
    public int getInt(@NonNls String key) {
        return getInt(key, 0);
    }

    /**
     * Gets the game rule value as an integer. If the value cannot be parsed or does not exist then
     * the default will be returned.
     *
     * @param rule the rule to look up
     * @param def the default value
     * @return the integer value of the rule, or the default
     */
    public int getInt(@NonNls String rule, int def) {
        if (isGameRule(rule)) {
            String value = getString(rule);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
                // fall through to end
            }
        }
        return def;
    }
}
