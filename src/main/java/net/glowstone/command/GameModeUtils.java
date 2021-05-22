package net.glowstone.command;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Locale;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class to create GameMode.
 */
public class GameModeUtils {

    private static final LocalizedEnumNames<GameMode> MAP = new LocalizedEnumNames<>(
        GameMode::getByValue, "glowstone.gamemode.unknown", "glowstone.gamemode.names",
        "maps/gamemode", false);

    private GameModeUtils() {
    }

    private static Locale localeFromNullable(@Nullable Locale in) {
        return in == null ? Locale.getDefault() : in;
    }

    /**
     * Create a GameMode from a string.
     *
     * @param mode   The mode to convert
     * @param locale The input locale
     * @return The matching mode if any, null otherwise.
     */
    @Nullable
    public static GameMode build(@Nullable final String mode, @Nullable final Locale locale) {
        return MAP.nameToValue(localeFromNullable(locale), mode);
    }

    /**
     * Pretty print the given GameMode.
     *
     * @param gameMode The mode to print
     * @param locale   The output locale
     * @return the pretty name of the mode or 'Unknown' if the mode is not known
     */
    public static String prettyPrint(GameMode gameMode, @Nullable Locale locale) {
        Preconditions.checkNotNull(gameMode, "gameMode cannot be null"); // NON-NLS
        return MAP.valueToName(localeFromNullable(locale), gameMode);
    }

    /**
     * Returns autocomplete suggestions that are game-mode names.
     *
     * @param arg    The partial input
     * @param locale The input locale
     * @return A list of autocomplete suggestions
     */
    @NotNull
    public static List<String> partialMatchingGameModes(String arg, @Nullable Locale locale) {
        return MAP.getAutoCompleteSuggestions(localeFromNullable(locale), arg);
    }

}
