package net.glowstone.command;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import net.glowstone.command.minecraft.GlowVanillaCommand;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.GameMode;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class to create GameMode.
 */
public class GameModeUtils {

    private static final class GameModeMaps {

        private static final String BASE_NAME = "maps/gamemode";
        private final ImmutableSortedMap<String, GameMode> nameToModeMap;
        private final ImmutableMap<GameMode, String> modeToNameMap;
        private final String unknown;
        private final ImmutableList<String> modeAutoCompleteList;

        public GameMode nameToMode(String name) {
            return nameToModeMap.get(name);
        }

        public String modeToName(GameMode gameMode) {
            return modeToNameMap.getOrDefault(gameMode, unknown);
        }

        public GameModeMaps(Locale locale) {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            ResourceBundle strings = ResourceBundle.getBundle("strings", locale);
            unknown = new LocalizedStringImpl("glowstone.gamemode.unknown", strings).get();
            Collator caseInsensitive = Collator.getInstance(locale);
            caseInsensitive.setStrength(Collator.PRIMARY);
            ImmutableSortedMap.Builder<String, GameMode> nameToModeBuilder
                    = new ImmutableSortedMap.Builder<>(caseInsensitive);
            ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
            for (String key : bundle.keySet()) {
                nameToModeBuilder.put(key,
                        GameMode.getByValue(Integer.decode(bundle.getString(key))));
            }
            nameToModeMap = nameToModeBuilder.build();
            ImmutableMap.Builder<GameMode, String> modeToNameBuilder = ImmutableMap.builder();
            ImmutableList.Builder<String> modeAutocompleteListBuilder = ImmutableList.builder();
            for (String name : new LocalizedStringImpl("glowstone.gamemode.names", strings)
                    .get().split(",")) {
                GameMode mode = nameToModeMap.get(name);
                modeToNameBuilder.put(mode, name);
                modeAutocompleteListBuilder.add(name.toLowerCase(locale));
            }
            if (!ALSO_ACCEPT_LOCALE.equals(locale)) {
                try {
                    modeAutocompleteListBuilder.addAll(
                            MAPS_CACHE.get(ALSO_ACCEPT_LOCALE).modeAutoCompleteList);
                } catch (ExecutionException e) {
                    ConsoleMessages.Error.I18n.GAME_MODE.log(e, ALSO_ACCEPT_LOCALE);
                }
                // We can't merge nameToModeMap this way because the two locales may have different
                // case-folding rules (e.g. Turkish dotted/dotless i) and each Map can only use one
                // locale's rules
            }
            modeToNameMap = modeToNameBuilder.build();
            modeAutoCompleteList = modeAutocompleteListBuilder.build();
        }
    }

    private static final Locale ALSO_ACCEPT_LOCALE = Locale.ENGLISH;
    private static final LoadingCache<Locale, GameModeMaps> MAPS_CACHE = CacheBuilder.newBuilder()
            .maximumSize(GlowVanillaCommand.CACHE_SIZE)
            .build(CacheLoader.from(GameModeMaps::new));

    private GameModeUtils() {
    }

    private static Locale localeFromNullable(@Nullable Locale in) {
        return in == null ? Locale.getDefault() : in;
    }

    /**
     * Create a GameMode from a string.
     *
     * @param mode The mode to convert
     * @param locale The input locale
     * @return The matching mode if any, null otherwise.
     */
    @Nullable
    public static GameMode build(@Nullable final String mode, @Nullable final Locale locale) {
        try {
            Locale targetLocale = localeFromNullable(locale);
            GameMode out = MAPS_CACHE.get(targetLocale).nameToMode(mode);
            if (out == null && !ALSO_ACCEPT_LOCALE.equals(targetLocale)) {
                out = MAPS_CACHE.get(ALSO_ACCEPT_LOCALE).nameToMode(mode);
            }
            return out;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Pretty print the given GameMode.
     *
     * @param gameMode The mode to print
     * @param locale The output locale
     * @return the pretty name of the mode or 'Unknown' if the mode is not known
     */
    public static String prettyPrint(GameMode gameMode, @Nullable Locale locale) {
        Preconditions.checkNotNull(gameMode, "gameMode cannot be null"); // NON-NLS
        try {
            return MAPS_CACHE.get(localeFromNullable(locale)).modeToName(gameMode);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns autocomplete suggestions that are game-mode names.
     *
     * @param arg The partial input
     * @param locale The input locale
     * @return A list of autocomplete suggestions
     */
    @NotNull
    public static List<String> partialMatchingGameModes(String arg, @Nullable Locale locale) {
        final List<String> candidates;
        try {
            candidates = MAPS_CACHE.get(localeFromNullable(locale)).modeAutoCompleteList;
        } catch (ExecutionException e) {
            ConsoleMessages.Error.I18n.GAME_MODE.log(e, locale);
            return Collections.emptyList();
        }
        return StringUtil.copyPartialMatches(arg, candidates,
            new ArrayList<>(candidates.size()));
    }
}
