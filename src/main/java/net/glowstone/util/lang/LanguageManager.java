package net.glowstone.util.lang;

import java.util.Locale;
import java.util.ResourceBundle;
import java.text.MessageFormat;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;


public class LanguageManager {
    private String baseName = "lang/messages";
    private ResourceBundle defaultBundle = ResourceBundle.getBundle(baseName, Locale.forLanguageTag(GlowServer.defaultLocale));

    /*
     * Returns the locale that should be used for commands and messages according to the server settings.
     */
    public String getEffectiveLocale(GlowPlayer p) {
        if (GlowServer.lockLocale) { //If the locale is locked, return the default locale, otherwise return the player's locale
            return GlowServer.defaultLocale;
        }
        return p.getLocale();
    }

    /*
     * Returns the internationalized message according to the effective locale of the player.
     */
    public String getString(String key, GlowPlayer p, String ... args) {
        String locale = this.getEffectiveLocale(p);
        ResourceBundle rb = ResourceBundle.getBundle(baseName, Locale.forLanguageTag(locale));
        return new MessageFormat(rb.getString(key)).format(args);
    }

    /**
    * Returns the internationalized message according to the default locale of the server. Used when a GlowPlayer object is unavailable.
    */
    public String getString(String key, String ... args) {
        return new MessageFormat(defaultBundle.getString(key)).format(args);
    }
}
