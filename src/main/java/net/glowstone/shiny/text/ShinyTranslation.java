package net.glowstone.shiny.text;

import org.spongepowered.api.text.translation.Translation;

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
    public String get() {
        return id;
    }

    @Override
    public String get(Object... args) {
        return id;
    }

    @Override
    public String toString() {
        return "Translation(" + id + ")";
    }
}
