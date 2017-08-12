package net.glowstone.command;

import net.glowstone.util.lang.I;
import com.google.common.collect.ImmutableList;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Utility class to create GameMode
 */
public class GameModeUtils {

    public static final List<String> GAMEMODE_NAMES = ImmutableList.of("adventure", "creative", "survival", "spectator");

    private GameModeUtils() { }

    /**
     * Create a GameMode from a string
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
     * Pretty print the given GameMode, taking the specified sender's locale consideration.
     *
     * @param sender The sender to get locale from
     * @param gameMode The mode to print
     * @return A string containing the pretty name of the mode, 'Unknown' if the mode is not known and null if the given mode is null.
     */
    public static String prettyPrint(final CommandSender sender, final GameMode gameMode) {
        if (gameMode == null) {
            return null;
        } else {
            switch (gameMode) {
                case CREATIVE:
                    return sender != null ? I.tr(sender, "command.gamemode.creative") : I.tr("command.gamemode.creative");
                case SURVIVAL:
                    return sender != null ? I.tr(sender, "command.gamemode.survival") : I.tr("command.gamemode.survival");
                case ADVENTURE:
                    return sender != null ? I.tr(sender, "command.gamemode.adventure") : I.tr("command.gamemode.adventure");
                case SPECTATOR:
                    return sender != null ? I.tr(sender, "command.gamemode.spectator") : I.tr("command.gamemode.spectator");
                default:
                    return sender != null ? I.tr(sender, "command.gamemode.unknown") : I.tr("command.gamemode.unknown");
            }
        }
    }

    /**
     * Pretty print the given GameMode
     *
     * @param gameMode The mode to print
     * @return A string containing the pretty name of the mode, 'Unknown' if the mode is not known and null if the given mode is null.
     */
    public static String prettyPrint(final GameMode gameMode) {
        return prettyPrint(null, gameMode);
    }
}
