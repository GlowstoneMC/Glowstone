package net.glowstone.util;

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
        setValue("commandBlockOutput", true);
        setValue("doDaylightCycle", true); // implemented
        setValue("doEntityDrops", true);
        setValue("doFireTick", true); // implemented
        setValue("doMobLoot", true); // implemented
        setValue("doMobSpawning", true); // implemented (partial)
        setValue("doTileDrops", true); // implemented
        setValue("keepInventory", false); // implemented
        setValue("logAdminCommands", true);
        setValue("mobGriefing", true);
        setValue("naturalRegeneration", true);
        setValue("randomTickSpeed", 3);
        setValue("reducedDebugInfo", false); // implemented
        setValue("sendCommandFeedback", true);
        setValue("showDeathMessages", true);
        setValue("announceAdvancements", true);
        setValue("doLimitedCrafting", false);
        setValue("gameLoopFunction", "");
        setValue("maxCommandChainLength", 65536);
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
