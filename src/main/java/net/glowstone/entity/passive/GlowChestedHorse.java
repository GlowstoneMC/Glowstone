package net.glowstone.entity.passive;

import lombok.Getter;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * A horse or similar mount (donkey, mule, llama...) whose inventory may include a chest.
 *
 * @param <InventoryT> the inventory class this entity uses
 */
public abstract class GlowChestedHorse<InventoryT extends AbstractHorseInventory>
        extends GlowAbstractHorse implements ChestedHorse {

    /**
     * Null when not carrying a chest; otherwise, a 15-slot container.
     */
    @Getter
    protected InventoryT inventory;

    public GlowChestedHorse(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
        inventory = createNewInventory();
    }

    @Override
    public boolean isCarryingChest() {
        return metadata.getBoolean(MetadataIndex.CHESTED_HORSE_HAS_CHEST);
    }

    @Override
    public void setCarryingChest(boolean carryingChest) {
        if (carryingChest != isCarryingChest()) {
            metadata.set(MetadataIndex.CHESTED_HORSE_HAS_CHEST, carryingChest);
            inventory = createNewInventory();
        }
    }

    /**
     * Move all items from one inventory's chest to another, and drop those that don't fit.
     *
     * @param from the inventory to transfer from
     * @param to the inventory to transfer to
     */
    protected void moveChestContents(InventoryT from, InventoryT to) {
        for (ItemStack remaining : to.addItem(from.getContents()).values()) {
            world.spawn(location, Item.class).setItemStack(remaining);
        }
    }

    /**
     * Creates and sets a new inventory, and copies equipment over from the existing inventory.
     */
    protected abstract InventoryT createNewInventory();
}
