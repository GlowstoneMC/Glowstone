package net.glowstone.util.lang;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * I18n class used for translation.
 */
public final class I {
    private I() {}// Prevent instantiation of this class

    /**
     * A full list of all available locales in Minecraft.
     * Updated as of 11 August 2017.
     */
    public static final List<String> LOCALES = Arrays.asList("af-ZA", "ar-SA", "ast-ES", "az-AZ", "be-BY", "bg-BG", "br-FR", "ca-val-ES", "ca-ES", "cs-CZ", "cy-GB", "da-DK", "de-AT", "de-DE", "el-GR", "en-7S", "en-AU", "en-CA", "en-GB", "en-NZ", "en-UD", "en-US", "eo-UY", "es-AR", "es-ES", "es-MX", "es-UY", "es-VE", "et-EE", "eu-ES", "fa-IR", "fi-FI", "fil-PH", "fo-FO", "fr-CA", "fr-FR", "fy-NL", "ga-IE", "gd-GB", "gl-ES", "gv-IM", "haw", "he-IL", "hi-IN", "hr-HR", "hu-HU", "hy-AM", "id-ID", "io", "is-IS", "it-IT", "ja-JP", "jbo-EN", "ka-GE", "ko-KR", "ksh-DE", "kw-GB", "la-VA", "lb-LU", "li-LI", "lol-US", "lt-LT", "lv-LV", "mi-NZ", "mk-MK", "mn-MN", "ms-MY", "mt-MT", "nb-NO", "nds-DE", "nl-NL", "nn-NO", "no-NO", "oc-FR", "pl-PL", "pt-BR", "pt-PT", "qya-AA", "ro-RO", "ru-RU", "sk-SK", "sl-SI", "sme", "so-SO", "sq-AL", "sr-SP", "sv-SE", "swg-DE", "th-TH", "tl-PH", "tlh-AA", "tr-TR", "tzl-TZL", "uk-UA", "vi-VN", "zh-CN", "zh-TW");
    /**
     * Path basename used when looking up language resources.
     */
    public static final String BASE_NAME = "lang/messages";
    /**
     * Fallback locale used in case the active locale is missing a specific string.
     */
    public static final String FALLBACK_LOCALE = "en-US";
    /**
     * The default locale to be used in the console and with the locked locale.
     */
    @Getter
    private static String defaultLocale = FALLBACK_LOCALE;
    /**
     * Toggles the use of player-specific translations. If this is enabled then players
     * will get translations depending on the Locale set in their settings. If this is
     * disabled then the default locale will be used instead.
     */
    @Getter
    @Setter
    private static boolean localeLocked = false;

    /**
     * Set the new default locale to be used during server-wide translation lookups.
     * The Locale should be in the following format: <language>-<COUNTRY>
     * 
     * For example:
     *   - en-US for English (US)
     *   - fr-FR for French
     *   - es-ES for Spanish
     *
     * A full list of Minecraft supported language codes can be found here:
     * https://minecraft.gamepedia.com/Language#Available_languages *
     * 
     *   * Remember to replace the underscore `_` with a hyphen `-`, so en_US becomes en-US.
     *
     * @param locale New locale to be set as default locale.
     * @throws MissingResourceException Thrown if the specified locale does not exist.
     */
    public static void setDefaultLocale(@NonNull String locale) {
        try {
            ResourceBundle.getBundle(BASE_NAME, Locale.forLanguageTag(locale));
        } catch (MissingResourceException ex) {
            throw new MissingResourceException("Missing locale '" + locale + "'. class=" + ex.getClassName(), ex.getClassName(), ex.getKey());
        }
        defaultLocale = locale;
    }

    /**
     * Returns the internationalized message according to the effective locale of the player.
     *
     * @param sender CommandSender used when looking up locale.
     * @param key Language translation key mapping to a specific string in the translations.
     * @param args Arguments used by the formatter to replace placeholder variables. If there are more arguments than format specifiers, the extra arguments are ignored.
     */
    public static String tr(CommandSender sender, String key, Object ... args) {
        return trl(getEffectiveLocale(sender), key, args);
    }

    /**
    * Returns the internationalized message according to the default locale of the server.
     *
     * @param key Language translation key mapping to a specific string in the translations.
     * @param args Arguments used by the formatter to replace placeholder variables. If there are more arguments than format specifiers, the extra arguments are ignored.
    */
    public static String tr(String key, Object ... args) {
        return trl(defaultLocale, key, args);
    }

    /**
    * Returns the internationalized message according to the specified locale.
     *
     * @param locale Locale to use when looking up translation.
     * @param key Language translation key mapping to a specific string in the translations.
     * @param args Arguments used by the formatter to replace placeholder variables. If there are more arguments than format specifiers, the extra arguments are ignored.
     * @throws MissingResourceException Thrown if the provided specified does not exist.
    */
    public static String trl(@NonNull String locale, @NonNull String key, @NonNull Object ... args) {
        ResourceBundle rb;
        try {
            rb = ResourceBundle.getBundle(BASE_NAME, Locale.forLanguageTag(locale));
        } catch (MissingResourceException ex) {
            throw new MissingResourceException("Missing locale '" + locale + "'. class=" + ex.getClassName(), ex.getClassName(), ex.getKey());
        }

        String result;
        if (rb.containsKey(key)) {
            result = rb.getString(key);
        } else {
            ResourceBundle fallback = ResourceBundle.getBundle(BASE_NAME, Locale.forLanguageTag(FALLBACK_LOCALE));
            if (fallback.containsKey(key)) {
                result = fallback.getString(key);
            } else {
                throw new MissingTranslationException("Could not find translation for key '" + key + "' in fallback locale", locale, key);
            }
        }
        result = ChatColor.translateAlternateColorCodes('&', result);
        for (int i = 0; i != args.length; i++) {
            result = result.replace("{" + i + "}", args[i].toString());
        }
        return result;
    }

    /**
     * Returns the locale that should be used for commands and messages according to the server settings.
     *
     * @param player Player used when looking up locale.
     * @return The players locale code. For example `en-US` for English (US).
     */
    public static String getEffectiveLocale(@NonNull CommandSender sender) {
        // Only return the players locale if locale isn't locked.
        if (sender instanceof Player && !localeLocked) {
            String locale = ((Player) sender).getLocale();
            return locale == null ? defaultLocale : locale.replace('_', '-');
        } else {
            return defaultLocale;
        }
    }

    /**
     * Check if the specified locale exists or not.
     *
     * @throws MissingResourceException Thrown if the specified locale does not exist.
     */
    public static boolean doesLocaleExist(@NonNull String locale) {
        try {
            ResourceBundle.getBundle(BASE_NAME, Locale.forLanguageTag(locale));
            return true;
        } catch (MissingResourceException ex) {
            return false;
        } 
    }

    /**
     * Returns a Set of translation keys exising in the specified locale.
     *
     * @throws MissingResourceException Thrown if the specified locale does not exist.
     */
    public static Set<String> getTranslationKeys(@NonNull String locale) {
        ResourceBundle rb;
        try {
            rb = ResourceBundle.getBundle(BASE_NAME, Locale.forLanguageTag(locale));
        } catch (MissingResourceException ex) {
            throw new MissingResourceException("Missing locale '" + locale + "'. class=" + ex.getClassName(), ex.getClassName(), ex.getKey());
        }

        return rb.keySet();
    }

}
