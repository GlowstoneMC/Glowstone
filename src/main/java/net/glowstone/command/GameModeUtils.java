package net.glowstone.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import net.glowstone.i18n.GlowstoneMessages;
import net.glowstone.i18n.InternationalizationUtil;
import org.bukkit.GameMode;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class to create GameMode.
 */
public class GameModeUtils {
    private static final ImmutableSortedMap<String, GameMode> NAME_TO_MODE;
    private static final ImmutableMap<GameMode, String> MODE_TO_NAME;
    public static final ImmutableList<String> MODE_AUTOCOMPLETE_LIST;

    static {
        ImmutableSortedMap.Builder<String, GameMode> nameToModeBuilder
                = new ImmutableSortedMap.Builder<>(InternationalizationUtil.CASE_INSENSITIVE);
        ResourceBundle bundle = ResourceBundle.getBundle("maps/gamemode");
        for (String key : bundle.keySet()) {
            nameToModeBuilder.put(key, GameMode.getByValue(Integer.decode(bundle.getString(key))));
        }
        NAME_TO_MODE = nameToModeBuilder.build();
        ImmutableMap.Builder<GameMode, String> modeToNameBuilder = ImmutableMap.builder();
        ImmutableList.Builder<String> modeAutocompleteListBuilder = ImmutableList.builder();
        for (String name : GlowstoneMessages.GameMode.NAMES.get().split(",")) {
            GameMode mode = NAME_TO_MODE.get(name);
            modeToNameBuilder.put(mode, name);
            modeAutocompleteListBuilder.add(name.toLowerCase(Locale.getDefault()));
        }
        MODE_TO_NAME = modeToNameBuilder.build();
        MODE_AUTOCOMPLETE_LIST = modeAutocompleteListBuilder.build();
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
        if (mode == null) {
            return null;
        }
        return NAME_TO_MODE.getOrDefault(mode.toLowerCase(), null);
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
        }
        return MODE_TO_NAME.getOrDefault(gameMode, GlowstoneMessages.GameMode.UNKNOWN.get());
    }

    @NotNull
    public static List<String> partialMatchingGameModes(String arg) {
        return StringUtil.copyPartialMatches(arg, MODE_AUTOCOMPLETE_LIST,
            new ArrayList<>(MODE_AUTOCOMPLETE_LIST.size()));
    }
}
