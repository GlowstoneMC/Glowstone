package io.github.deathcap.bukkit2sponge.text;

import org.spongepowered.api.text.translation.Translation;

import java.util.Locale;

/**
 * Todo: Javadoc for ShinyTranslation.
 */
public class ShinyTranslation implements Translation {
    private final String id;

    public ShinyTranslation(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String get(Locale locale) {
        return id;
    }

    @Override
    public String get(Locale locale, Object... args) {
        return id;
    }

    @Override
    public String toString() {
        return "Translation(" + id + ")";
    }
}
