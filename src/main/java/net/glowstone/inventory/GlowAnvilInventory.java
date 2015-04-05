package net.glowstone.inventory;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryHolder;

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
}
