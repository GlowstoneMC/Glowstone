package net.glowstone.util.lang;

import net.glowstone.entity.GlowPlayer;

public interface LanguageManager {
    String getEffectiveLocale(GlowPlayer p);
    String getString(String key, GlowPlayer p, String ... args);
    String getString(String key, String ... args);
}
