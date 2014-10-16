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

        slotTypes[FIRST_ITEM_SLOT] = InventoryType.SlotType.CRAFTING;
        slotTypes[SECOND_ITEM_SLOT] = InventoryType.SlotType.CRAFTING;
        slotTypes[RESULT_SLOT] = InventoryType.SlotType.RESULT;
    }

    @Override
    public int getRawSlots() {
        return 0;
    }
}
