package net.glowstone.shiny.item;

import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.text.translation.Translations;

/**
 * Todo: Javadoc for ShinyItemType.
 */
public class ShinyItemType implements ItemType {

    private final String id;

    public ShinyItemType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getMaxStackQuantity() {
        return 64;
    }

    @Override
    public Translation getTranslation() {
        return Translations.of("item." + id + ".name").get();
    }

    @Override
    public String toString() {
        return getTranslation().getId();
    }
}
