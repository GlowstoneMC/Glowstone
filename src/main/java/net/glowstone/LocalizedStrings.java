package net.glowstone;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.annotation.PropertyKey;

/**
 * Static helper methods used with localized strings.
 */
public class LocalizedStrings {

    /**
     * Utility class; do not instantiate.
     */
    private LocalizedStrings() {}

    /**
     * Localized strings.
     */
    private static final ResourceBundle STRINGS = ResourceBundle.getBundle("strings");

    /**
     * Returns the given arguments formatted by {@link MessageFormat#format(String, Object...)} with
     * a localized format string.
     *
     * @param key a property key for a format string
     * @param args the values to format
     * @return the localized, formatted values
     */
    public static String format(@PropertyKey String key, Object... args) {
        return MessageFormat.format(STRINGS.getString(key), (Object[]) args);
    }

    /**
     * Looks up the given property key, uses it to format the given arguments with
     * {@link MessageFormat#format(String, Object...)}, and logs the formatted, localized string
     * by calling {@link java.util.logging.Logger#info(String)} on {@link GlowServer#logger}.
     *
     * @param key a property key for a format string
     * @param args the values to format
     */
    public static void logInfo(@PropertyKey String key, Object... args) {
        GlowServer.logger.info(format(key, (Object[]) args));
    }

    /**
     * Looks up the given property key, uses it to format the given arguments with
     * {@link MessageFormat#format(String, Object...)}, and logs the formatted, localized string
     * by calling {@link java.util.logging.Logger#warning(String)} on {@link GlowServer#logger}.
     *
     * @param key a property key for a format string
     * @param args the values to format
     */
    public static void logWarning(@PropertyKey String key, Object... args) {
        GlowServer.logger.warning(format(key, (Object[]) args));
    }

    /**
     * Looks up the given property key, uses it to format the given arguments with
     * {@link MessageFormat#format(String, Object...)}, and logs the formatted, localized string
     * by calling {@link java.util.logging.Logger#severe(String)} on {@link GlowServer#logger}.
     *
     * @param key a property key for a format string
     * @param args the values to format
     */
    public static void logError(@PropertyKey String key, Object... args) {
        GlowServer.logger.severe(format(key, (Object[]) args));
    }
}
