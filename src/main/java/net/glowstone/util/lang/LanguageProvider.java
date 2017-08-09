package net.glowstone.util.lang;

import net.glowstone.entity.GlowPlayer;

public interface LanguageProvider {

    /**
     * Returns the locale that should be used for commands and messages according to the server settings.
     */
    String getEffectiveLocale(GlowPlayer p);
 
    /**
     * Returns the internationalized message according to the effective locale of the player.
     */
    String getString(GlowPlayer p, String key, Object ... args);

    /**
    * Returns the internationalized message according to the default locale of the server. Used when a GlowPlayer object is unavailable.
    */
    String getString(String key, Object ... args);
}
