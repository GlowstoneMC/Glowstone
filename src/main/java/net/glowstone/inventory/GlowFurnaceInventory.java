package net.glowstone.inventory;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.Furnace;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class GlowFurnaceInventory extends GlowInventory implements FurnaceInventory {

    private static final int INPUT_SLOT = 0;
    private static final int FUEL_SLOT = 1;
    private static final int RESULT_SLOT = 2;

    public GlowFurnaceInventory(Furnace owner) {
        super(owner, InventoryType.FURNACE);

        getSlot(INPUT_SLOT).setType(InventoryType.SlotType.CRAFTING);
        getSlot(FUEL_SLOT).setType(InventoryType.SlotType.FUEL);
        getSlot(RESULT_SLOT).setType(InventoryType.SlotType.RESULT);
    }

    @Override
    public ItemStack getResult() {
        return getItem(RESULT_SLOT);
    }

    @Override
    public void setResult(ItemStack stack) {
        setItem(RESULT_SLOT, stack);
    }

    @Override
    public ItemStack getFuel() {
        return getItem(FUEL_SLOT);
    }

    @Override
    public void setFuel(ItemStack stack) {
        setItem(FUEL_SLOT, stack);
    }

    @Override
    public ItemStack getSmelting() {
        return getItem(INPUT_SLOT);
    }

    @Override
    public void setSmelting(ItemStack stack) {
        setItem(INPUT_SLOT, stack);
    }

    @Override
    public Furnace getHolder() {
        return (Furnace) super.getHolder();
    }

    @Override
    public void handleShiftClick(GlowPlayer player, InventoryView view, int clickedSlot, ItemStack clickedItem) {
        clickedItem = player.getInventory().tryToFillSlots(clickedItem, 9, 36, 0, 9);
        view.setItem(clickedSlot, clickedItem);
    }
}
