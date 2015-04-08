package net.glowstone.inventory;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class GlowAnvilInventory extends GlowInventory implements AnvilInventory {

    private static final int FIRST_ITEM_SLOT = 0;
    private static final int SECOND_ITEM_SLOT = 1;
    private static final int RESULT_SLOT = 2;

    public GlowAnvilInventory(InventoryHolder holder) {
        super(holder, InventoryType.ANVIL);

        getSlot(FIRST_ITEM_SLOT).setType(InventoryType.SlotType.CRAFTING);
        getSlot(SECOND_ITEM_SLOT).setType(InventoryType.SlotType.CRAFTING);
        getSlot(RESULT_SLOT).setType(InventoryType.SlotType.RESULT);
    }

    @Override
    public int getRawSlots() {
        return 0;
    }

    @Override
    public void handleShiftClick(GlowPlayer player, InventoryView view, int clickedSlot, ItemStack clickedItem) {
        clickedItem = player.getInventory().tryToFillSlots(clickedItem, 9, 36, 0, 9);
        view.setItem(clickedSlot, clickedItem);
    }
}
