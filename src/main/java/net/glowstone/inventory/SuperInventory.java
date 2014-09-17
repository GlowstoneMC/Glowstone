package net.glowstone.inventory;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public class SuperInventory extends GlowInventory {
    private List<Inventory> inventories;

    protected SuperInventory() { }

    protected void initialize(InventoryHolder owner, InventoryType type, List<Inventory> inventories, String title) {
        List<GlowInventorySlot> slots = new ArrayList<>();

        for (Inventory inventory : inventories) {
            slots.addAll(((GlowInventory) inventory).getSlots());
        }

        super.initialize(owner, type, slots, title);
    }

    public Inventory getSubInventory(int num) {
        return inventories.get(num);
    }
}
