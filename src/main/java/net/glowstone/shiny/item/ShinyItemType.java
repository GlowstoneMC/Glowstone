package net.glowstone.shiny.item;

import com.google.common.base.Optional;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.properties.ItemProperty;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.text.Texts;

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
    public <T extends ItemProperty<?, ?>> Optional<T> getDefaultProperty(Class<T> propertyClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Translation getTranslation() {
        return null; //Texts.of("item." + id + ".name").get(); // TODO
    }

    @Override
    public String toString() {
        return getTranslation().getId();
    }
}
