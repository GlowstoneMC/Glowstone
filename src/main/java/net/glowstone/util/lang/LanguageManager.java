package net.glowstone.util.lang;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import com.google.common.base.Preconditions;

import java.text.MessageFormat;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;

import org.bukkit.command.CommandSender;

public final class LanguageManager {
    private static final DefaultLanguageProvider DEFAULT_LANGUAGE_PROVIDER = new DefaultLanguageProvider();
    private LanguageProvider provider = DEFAULT_LANGUAGE_PROVIDER;

    private static final class DefaultLanguageProvider implements LanguageProvider {
        private String baseName = "lang/messages";
        private ResourceBundle defaultBundle = ResourceBundle.getBundle(baseName, Locale.forLanguageTag(GlowServer.defaultLocale));

        private DefaultLanguageProvider() {
        }

        @Override
        public String getEffectiveLocale(GlowPlayer p) {
            if (GlowServer.lockLocale) { //If the locale is locked, return the default locale, otherwise return the player's locale
                return GlowServer.defaultLocale;
            }
            return p.getLocale();
        }

        @Override
        public String getString(GlowPlayer p, String key, Object ... args) {
            try {
                String locale = this.getEffectiveLocale(p);
                ResourceBundle rb = ResourceBundle.getBundle(baseName, Locale.forLanguageTag(locale));
                return new MessageFormat(rb.getString(key)).format(args);
            } catch (MissingResourceException ex) {
                // Fallback to English so we don't have to wait for
                // complete translations implementing a new feature.
                ResourceBundle rb = ResourceBundle.getBundle(baseName, Locale.forLanguageTag("en_US"));
                return new MessageFormat(rb.getString(key)).format(args);
            }
        }

        @Override
        public String getString(String key, Object ... args) {
            try {
                return new MessageFormat(defaultBundle.getString(key)).format(args);
            } catch (MissingResourceException ex) {
                // Fallback to English so we don't have to wait for
                // complete translations implementing a new feature.
                ResourceBundle rb = ResourceBundle.getBundle(baseName, Locale.forLanguageTag("en_US"));
                return new MessageFormat(rb.getString(key)).format(args);
            }
        }
    }

    public LanguageProvider getProvider() {
        return provider;
    }

    public void setProvider(LanguageProvider provider) {
        Preconditions.checkNotNull(provider, "provider");
        this.provider = provider;
    }

    public LanguageProvider getDefaultProvider() {
        return DEFAULT_LANGUAGE_PROVIDER;
    }

    public String getEffectiveLocale(GlowPlayer p) {
        return provider.getEffectiveLocale(p);
    }

    public String getString(GlowPlayer p, String key, Object ... args) {
        return provider.getString(key, p, args);
    }

    public String getString(CommandSender sender, String key, Object ... args) {
        if (sender instanceof GlowPlayer) {
            return provider.getString(key, (GlowPlayer) sender, args);
        } else {
            return provider.getString(key, args);
        }
    }

    public String getString(String key, Object ... args) {
        return provider.getString(key, args);
    }
}
