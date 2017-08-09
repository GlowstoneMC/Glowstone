package net.glowstone.util.lang;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.command.CommandSender;

public interface LanguageProvider {

    /**
     * Returns the locale that should be used for commands and messages according to the server settings.
     *
     * @param player Player used when looking up locale.
     * @return The players locale code. For example en_US for English (US).
     */
    String getEffectiveLocale(GlowPlayer player);

    /**
     * Returns the internationalized message according to the effective locale of the player.
     *
     * @param player Player used when looking up locale.
     * @param key Language translation key mapping to a specific string in the translations.
     * @param args Arguments used by the formatter to replace placeholder variables. If there are more arguments than format specifiers, the extra arguments are ignored.
     */
    String getString(GlowPlayer player, String key, Object ... args);

    /**
     * Returns the internationalized message according to the effective locale of the player.
     *
     * @param sender CommandSender used when looking up locale.
     * @param key Language translation key mapping to a specific string in the translations.
     * @param args Arguments used by the formatter to replace placeholder variables. If there are more arguments than format specifiers, the extra argu
ments are ignored.
     */
    String getString(CommandSender sender, String key, Object ... args);

    /**
    * Returns the internationalized message according to the default locale of the server. Used when a GlowPlayer object is unavailable.
     *
     * @param key Language translation key mapping to a specific string in the translations.
     * @param args Arguments used by the formatter to replace placeholder variables. If there are more arguments than format specifiers, the extra argu
ments are ignored.
    */
    String getString(String key, Object ... args);
}
