package net.glowstone.inventory;

import org.bukkit.block.Furnace;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

public class GlowFurnaceInventory extends GlowInventory implements FurnaceInventory {

    private static final int INPUT_SLOT = 0;
    private static final int FUEL_SLOT = 1;
    private static final int RESULT_SLOT = 2;

    public GlowFurnaceInventory(Furnace owner) {
        super(owner, InventoryType.FURNACE);

        slotTypes[INPUT_SLOT] = InventoryType.SlotType.CRAFTING;
        slotTypes[FUEL_SLOT] = InventoryType.SlotType.FUEL;
        slotTypes[RESULT_SLOT] = InventoryType.SlotType.RESULT;
    }

    @Override
    public ItemStack getResult() {
        return getItem(RESULT_SLOT);
    }

    @Override
    public ItemStack getFuel() {
        return getItem(FUEL_SLOT);
    }

    @Override
    public ItemStack getSmelting() {
        return getItem(INPUT_SLOT);
    }

    @Override
    public void setFuel(ItemStack stack) {
        setItem(FUEL_SLOT, stack);
    }

    @Override
    public void setResult(ItemStack stack) {
        setItem(RESULT_SLOT, stack);
    }

    @Override
    public void setSmelting(ItemStack stack) {
        setItem(INPUT_SLOT, stack);
    }

    @Override
    public Furnace getHolder() {
        return (Furnace) super.getHolder();
    }
}
