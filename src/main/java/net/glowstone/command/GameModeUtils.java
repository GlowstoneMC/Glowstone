package net.glowstone.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;
import net.glowstone.ServerProvider;
import org.bukkit.GameMode;

/**
 * Utility class to create GameMode.
 */
public class GameModeUtils {
    private static final ImmutableMap<String, GameMode> MODE_MAP;

    static {
        Collator caseInsensitive = Collator.getInstance(Locale.getDefault());
        caseInsensitive.setStrength(Collator.PRIMARY);
        ImmutableSortedMap.Builder<String, GameMode> out
                = new ImmutableSortedMap.Builder<>(caseInsensitive);
        ResourceBundle bundle = ResourceBundle.getBundle("maps/gamemode");
        for (String key : bundle.keySet()) {
            out.put(key, GameMode.values()[Integer.decode(bundle.getString(key))]);
        }
        MODE_MAP = out.build();
    }

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
        return MODE_MAP.getOrDefault(mode.toLowerCase(), null);
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
