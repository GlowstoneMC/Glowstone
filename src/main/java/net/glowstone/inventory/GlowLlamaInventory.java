package net.glowstone.inventory;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LlamaInventory;

/**
 * A llama inventory. A llama can wear a carpet, and if it's carrying a chest, that chest can have 3
 * to 15 slots.
 */
public class GlowLlamaInventory extends GlowInventory implements LlamaInventory {
    /** The carpet this llama is wearing. */
    @Getter
    @Setter
    protected ItemStack decor;
    @Getter
    @Setter
    protected ItemStack saddle;

    public GlowLlamaInventory(InventoryHolder holder, int slots) {
        super(holder, InventoryType.CHEST, slots);
    }
}
