package net.glowstone.inventory;

import org.bukkit.block.BrewingStand;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

public class GlowBrewerInventory extends GlowInventory implements BrewerInventory {

    private static final int INGREDIENT_SLOT = 0;

    /**
     * Creates the inventory for the given brewing stand.
     *
     * @param holder the brewing stand
     */
    public GlowBrewerInventory(BrewingStand holder) {
        super(holder, InventoryType.BREWING);

        getSlot(INGREDIENT_SLOT).setType(SlotType.FUEL);
        for (int slot = 1; slot < 4; slot++) {
            getSlot(slot).setType(SlotType.CRAFTING);
        }
    }

    @Override
    public ItemStack getIngredient() {
        return getItem(INGREDIENT_SLOT);
    }

    @Override
    public void setIngredient(ItemStack ingredient) {
        setItem(INGREDIENT_SLOT, ingredient);
    }

    @Override
    public ItemStack getFuel() {
        return null;
    }

    @Override
    public void setFuel(ItemStack itemStack) {

    }

    @Override
    public BrewingStand getHolder() {
        return (BrewingStand) super.getHolder();
    }
}
