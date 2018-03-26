package net.glowstone.inventory;

import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Horse;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;

/**
 * A class which represents an inventory and the items it contains.
 */
public class GlowHorseInventory extends GlowInventory implements HorseInventory {

    private ItemStack saddle;
    private ItemStack armor;

    public GlowHorseInventory(ChestedHorse owner) {
        this(owner, owner.isCarryingChest() ? 16 : 2);
    }

    public GlowHorseInventory(Horse owner) {
        this(owner, 2);
    }

    public GlowHorseInventory(AbstractHorse owner, int size) {
        this(owner, size, "EntityHorse");
    }

    public GlowHorseInventory(AbstractHorse owner, int size, String title) {
        super(owner, InventoryType.CHEST, size, title); //TODO fix this.
    }

    @Override
    public ItemStack getSaddle() {
        return saddle != null ? saddle.clone() : null;
    }

    @Override
    public void setSaddle(ItemStack itemStack) {
        if (itemStack != null) {
            saddle = new ItemStack(itemStack);
        } else {
            armor = null;
        }
    }

    @Override
    public ItemStack getArmor() {
        return armor != null ? armor.clone() : null;
    }

    @Override
    public void setArmor(ItemStack itemStack) {
        if (itemStack != null) {
            armor = new ItemStack(itemStack);
        } else {
            armor = null;
        }
    }
}
