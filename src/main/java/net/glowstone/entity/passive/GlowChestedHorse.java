package net.glowstone.entity.passive;

import lombok.Getter;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.AbstractHorseInventory;

/**
 * A quadruped whose inventory may include a chest.
 *
 * @param <InventoryType> the inventory class this entity uses
 */
public abstract class GlowChestedHorse<InventoryType extends AbstractHorseInventory>
        extends GlowAbstractHorse implements ChestedHorse {

    /**
     * Null when not carrying a chest; otherwise, a 15-slot container.
     */
    @Getter
    protected InventoryType inventory;

    public GlowChestedHorse(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
        createNewInventory();
    }

    @Override
    public boolean isCarryingChest() {
        return metadata.getBoolean(MetadataIndex.CHESTED_HORSE_HAS_CHEST);
    }

    @Override
    public void setCarryingChest(boolean carryingChest) {
        if (carryingChest != isCarryingChest()) {
            metadata.set(MetadataIndex.CHESTED_HORSE_HAS_CHEST, carryingChest);
            createNewInventory();
        }
    }

    /**
     * Creates and sets a new inventory, and copies equipment over from the existing inventory.
     */
    protected abstract void createNewInventory();
}
