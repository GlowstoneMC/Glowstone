package net.glowstone.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Container for the game rule map for worlds.
 */
public final class GameRuleManager {

    private final Map<String, String> gameRules = new HashMap<>();

    public GameRuleManager() {
        setValue("commandBlockOutput", true);
        setValue("doDaylightCycle", true); // implemented
        setValue("doEntityDrops", true);
        setValue("doFireTick", true);
        setValue("doMobLoot", true);
        setValue("doMobSpawning", true);
        setValue("doTileDrops", true); // implemented
        setValue("keepInventory", false);
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
     * Gets all of the game rules defined
     *
     * @return the game rules defined, may be empty
     */
    public String[] getKeys() {
        return gameRules.keySet().toArray(new String[gameRules.size()]);
    }

    /**
     * Sets the value of a game rule. The supplied value cannot be null. If the
     * value is not a string, the string representation of the object will be
     * used instead, which must also not return null. If the value is null, or
     * is converted to null through toString(), then this will return false.
     * <p>
     * The actual object value is never stored, only the string value. The
     * helper methods provided in this class may be used to retrieve the value,
     * such as {@link #getBoolean(String)}.
     *
     * @param rule  the rule to set, cannot be null
     * @param value the value to set, cannot be null or be represented as null
     * @return true if set, false otherwise
     */
    public boolean setValue(String rule, Object value) {
        if (rule != null && value != null && value.toString() != null) {
            gameRules.put(rule, value.toString());
            return true;
        }
        return false;
    }

    /**
     * Gets whether or not the supplied rule is defined
     *
     * @param rule the rule to lookup
     * @return true if defined, false otherwise
     */
    public boolean isGameRule(String rule) {
        return rule != null && gameRules.containsKey(rule);
    }

    /**
     * Gets the game rule value as a string. If the value does not exist, then
     * this will return null.
     *
     * @param rule the rule to look up
     * @return the string value, or null if not defined
     */
    public String getString(String rule) {
        if (rule != null && isGameRule(rule)) {
            return gameRules.get(rule);
        }
        return null;
    }

    /**
     * Gets the game rule value as a boolean. If the value cannot be parsed or
     * does not exist, then this will return false.
     *
     * @param rule the rule to look up
     * @return the boolean value, or false
     */
    public boolean getBoolean(String rule) {
        if (isGameRule(rule)) {
            String value = getString(rule);
            if (value != null) {
                return Boolean.parseBoolean(value); // defaults to false
            }
        }
        return false;
    }

    /**
     * Gets the game rule value as an integer. If the value cannot be parsed or
     * does not exist then the default will be returned
     *
     * @param rule the rule to look up
     * @param def  the default value
     * @return the integer value of the rule, or the default
     */
    public int getInteger(String rule, int def) {
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
