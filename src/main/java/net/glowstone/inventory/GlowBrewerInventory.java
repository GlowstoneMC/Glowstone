package net.glowstone.inventory;

import org.bukkit.block.BrewingStand;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

public class GlowBrewerInventory extends GlowInventory implements BrewerInventory {

    private static final int INGREDIENT_SLOT = 0;

    public GlowBrewerInventory(BrewingStand holder) {
        super(holder, InventoryType.BREWING);

        slotTypes[INGREDIENT_SLOT] = InventoryType.SlotType.FUEL;
        for (int slot = 1; slot < 4; slot++) {
            slotTypes[slot] = InventoryType.SlotType.CRAFTING;
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
    public BrewingStand getHolder() {
        return (BrewingStand) super.getHolder();
    }
}
