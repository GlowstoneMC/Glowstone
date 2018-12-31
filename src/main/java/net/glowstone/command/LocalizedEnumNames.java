package net.glowstone.command;

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
import java.util.function.IntFunction;
import lombok.Getter;
import net.glowstone.command.minecraft.GlowVanillaCommand;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is used to map an enum or multiton type to and from the localized names of its instances,
 * which have their own properties file. These properties files are unusual in that the localized
 * names are keys, not values; the values are integers that are mapped to instances of T using
 * {@code integerResolver}.
 *
 * @param <T> the type being mapped to and from strings.
 */
public class LocalizedEnumNames<T> {

    private static final Locale ALSO_ACCEPT_LOCALE = Locale.ENGLISH;
    private final LoadingCache<Locale, Entry> cache;
    private final IntFunction<? extends T> integerResolver;
    private final String unknownKey;
    private final String commaSeparatedNamesKey;
    private final String baseName;

    /**
     * Creates an instance.
     *
     * @param integerResolver used to map integers in the resource bundle to instances of T
     * @param unknownKey a key in strings.properties that provides a name for unknown future values
     * @param commaSeparatedNamesKey a key in strings.properties that provides canonical names for
     *                               auto-complete, separated by commas
     * @param baseName the base name of the resource bundle
     */
    public LocalizedEnumNames(IntFunction<? extends T> integerResolver, String unknownKey,
            String commaSeparatedNamesKey, String baseName) {
        this.integerResolver = integerResolver;
        this.unknownKey = unknownKey;
        this.commaSeparatedNamesKey = commaSeparatedNamesKey;
        this.baseName = baseName;
        cache = CacheBuilder.newBuilder()
                .maximumSize(GlowVanillaCommand.CACHE_SIZE)
                .build(CacheLoader.from(Entry::new));
    }

    private static <T> ImmutableSortedMap<String, T> resourceBundleToMap(Locale locale,
            String baseName, IntFunction<? extends T> integerResolver) {
        Collator caseInsensitive = Collator.getInstance(locale);
        caseInsensitive.setStrength(Collator.PRIMARY);
        ImmutableSortedMap.Builder<String, T> nameToModeBuilder
                = new ImmutableSortedMap.Builder<String, T>(caseInsensitive);
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
        for (String key : bundle.keySet()) {
            nameToModeBuilder.put(key,
                    integerResolver.apply(Integer.decode(bundle.getString(key))));
        }
        return nameToModeBuilder.build();
    }

    /**
     * Retrieves autocomplete suggestions that are values of type T.
     *
     * @param locale the input locale
     * @param arg the incomplete argument to finish
     * @return a list of autocomplete suggestions
     */
    @NotNull
    public List<String> getAutoCompleteSuggestions(Locale locale, String arg) {
        ImmutableList<String> result;
        try {
            result = cache.get(locale).modeAutoCompleteList;
        } catch (ExecutionException e) {
            ConsoleMessages.Error.I18n.COMMAND.log(e, locale);
            return Collections.emptyList();
        }
        final List<String> candidates = result;
        return StringUtil.copyPartialMatches(arg, candidates,
            new ArrayList<>(candidates.size()));
    }

    /**
     * Gets a value by its localized name. Both the specified locale and {@link #ALSO_ACCEPT_LOCALE}
     * are accepted, and matching is case- and accent-insensitive (per {@link Collator#PRIMARY}).
     *
     * @param locale the locale the user is assumed to be using
     * @param name the name to look up
     * @return the matching value, or null if none matches
     */
    @Nullable
    public T nameToValue(Locale locale, String name) {
        T value = null;
        try {
            value = cache.get(locale).nameToValue(name);
        } catch (ExecutionException e) {
            ConsoleMessages.Error.I18n.COMMAND.log(e, locale);
        }
        if (value == null) {
            try {
                value = cache.get(ALSO_ACCEPT_LOCALE).nameToValue(name);
            } catch (ExecutionException e) {
                ConsoleMessages.Error.I18n.COMMAND.log(e, ALSO_ACCEPT_LOCALE);
            }
        }
        return value;
    }

    /**
     * Gets the localized name for an instance of T.
     *
     * @param locale the output locale
     * @param value the value to look up the name for
     * @return the localized name
     */
    public String valueToName(Locale locale, T value) {
        try {
            return cache.get(locale).valueToName(value);
        } catch (ExecutionException e) {
            ConsoleMessages.Error.I18n.COMMAND.log(e, locale);
            return "Unknown"; // NON-NLS: exception implies we can't use the localized "Unknown"
        }
    }

    private final class Entry {

        private final ImmutableSortedMap<String, T> nameToModeMap;
        private final ImmutableMap<T, String> modeToNameMap;
        private final String unknown;
        @Getter
        private final ImmutableList<String> modeAutoCompleteList;

        public T nameToValue(String name) {
            return nameToModeMap.get(name);
        }

        public String valueToName(T gameMode) {
            return modeToNameMap.getOrDefault(gameMode, unknown);
        }

        public Entry(Locale locale) {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            ResourceBundle strings = ResourceBundle.getBundle("strings", locale);
            unknown = new LocalizedStringImpl(unknownKey, strings).get();
            nameToModeMap = resourceBundleToMap(locale, baseName, integerResolver);
            ImmutableMap.Builder<T, String> modeToNameBuilder = ImmutableMap.builder();
            ImmutableList.Builder<String> modeAutocompleteListBuilder = ImmutableList.builder();
            for (String name : new LocalizedStringImpl(commaSeparatedNamesKey, strings)
                    .get().split(",")) {
                T mode = nameToModeMap.get(name);
                modeToNameBuilder.put(mode, name);
                modeAutocompleteListBuilder.add(name.toLowerCase(locale));
            }
            if (!ALSO_ACCEPT_LOCALE.equals(locale)) {
                try {
                    modeAutocompleteListBuilder.addAll(
                            cache.get(ALSO_ACCEPT_LOCALE).getModeAutoCompleteList());
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
}
