package net.glowstone.command;

import com.google.common.collect.ImmutableSortedMap;
import java.util.ResourceBundle;
import net.glowstone.i18n.GlowstoneMessages;
import net.glowstone.i18n.InternationalizationUtil;
import org.bukkit.GameMode;

/**
 * Utility class to create GameMode.
 */
public class GameModeUtils {
    private static final ImmutableSortedMap<String, GameMode> MODE_MAP;

    static {
        ImmutableSortedMap.Builder<String, GameMode> out
                = new ImmutableSortedMap.Builder<>(InternationalizationUtil.CASE_INSENSITIVE);
        ResourceBundle bundle = ResourceBundle.getBundle("maps/gamemode");
        for (String key : bundle.keySet()) {
            out.put(key, GameMode.getByValue(Integer.decode(bundle.getString(key))));
        }
        MODE_MAP = out.build();
    }

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
            int ordinal = gameMode.ordinal();
            if (GlowstoneMessages.GameMode.NAMES.size() > ordinal) {
                return GlowstoneMessages.GameMode.NAMES.get(ordinal);
            }
        }
        return GlowstoneMessages.GameMode.UNKNOWN.get();
    }
}
