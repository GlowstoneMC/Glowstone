package net.glowstone.util.lang;

import java.util.Locale;
import java.util.ResourceBundle;

import com.google.common.base.Preconditions;

import java.text.MessageFormat;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;

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
        public String getString(String key, GlowPlayer p, String ... args) {
            String locale = this.getEffectiveLocale(p);
            ResourceBundle rb = ResourceBundle.getBundle(baseName, Locale.forLanguageTag(locale));
            return new MessageFormat(rb.getString(key)).format(args);
        }

        @Override
        public String getString(String key, String ... args) {
            return new MessageFormat(defaultBundle.getString(key)).format(args);
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

    public String getString(String key, GlowPlayer p, String ... args) {
        return provider.getString(key, p, args);
    }

    public String getString(String key, String ... args) {
        return provider.getString(key, args);
    }
}
