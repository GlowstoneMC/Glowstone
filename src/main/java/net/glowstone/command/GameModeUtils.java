package net.glowstone.command;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.bukkit.GameMode;

/**
 * Utility class to create GameMode.
 */
public class GameModeUtils {

    public static final List<String> GAMEMODE_NAMES = ImmutableList
        .of("adventure", "creative", "survival", "spectator");

    private GameModeUtils() {
    }

    /**
     * Create a GameMode from a string.
     *
     * @param mode The mode to convert
     * @return The matching mode if any, null otherwise.
     */
    public static GameMode build(final String mode) {
        if (mode == null) {
            return null;
        } else {
            switch (mode.toLowerCase()) {
                case "s":
                case "0":
                case "survival":
                    return GameMode.SURVIVAL;
                case "c":
                case "1":
                case "creative":
                    return GameMode.CREATIVE;
                case "a":
                case "2":
                case "adventure":
                    return GameMode.ADVENTURE;
                case "sp":
                case "3":
                case "spectator":
                    return GameMode.SPECTATOR;
                default:
                    return null;
            }
        }
    }

    /**
     * Pretty print the given GameMode.
     *
     * @param gameMode The mode to print
     * @return A string containing the pretty name of the mode, 'Unknown' if the mode is not known,
     *     or null if the given mode is null.
     */
    public static String prettyPrint(final GameMode gameMode) {
        if (gameMode == null) {
            return null;
        } else {
            switch (gameMode) {
                case CREATIVE:
                    return "Creative";
                case SURVIVAL:
                    return "Survival";
                case ADVENTURE:
                    return "Adventure";
                case SPECTATOR:
                    return "Spectator";
                default:
                    return "Unknown";
            }
        }
    }
}
