package net.glowstone.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.glowstone.entity.GlowHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * Standard implementation of InventoryView for most inventories.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class GlowInventoryView extends InventoryView {

    /**
     * The player.
     */
    private final HumanEntity player;

    /**
     * The inventory type.
     */
    // NB: by spec, getter ought to return CREATIVE instead of CRAFTING if player is in creative
    // mode
    // but this messes up the calculations in InventoryView which expect CRAFTING but also
    // apply to CREATIVE.
    private final InventoryType type;
    /**
     * The inventory in the top half of the window.
     */
    private final Inventory topInventory;
    /**
     * The inventory in the bottom half of the window.
     */
    private final Inventory bottomInventory;

    /**
     * Create the default inventory view for this player.
     *
     * @param player The player.
     */
    public GlowInventoryView(GlowHumanEntity player) {
        this(player, player.getInventory().getCraftingInventory());
    }

    /**
     * Create an inventory view for this player looking at a given top inventory.
     *
     * @param player The player.
     * @param topInventory The top inventory.
     */
    public GlowInventoryView(HumanEntity player, Inventory topInventory) {
        this(player, topInventory.getType(), topInventory, player.getInventory());
    }

    /**
     * Check if an inventory view is the player's default inventory view.
     *
     * @param view The view to check.
     * @return Whether it is a player's default inventory view.
     */
    public static boolean isDefault(InventoryView view) {
        return view.getBottomInventory() instanceof GlowPlayerInventory
                && view.getTopInventory() == ((GlowPlayerInventory) view.getBottomInventory())
                .getCraftingInventory();
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        checkSlot(slot);
        super.setItem(slot, item);
    }

    @Override
    public ItemStack getItem(int slot) {
        checkSlot(slot);
        return super.getItem(slot);
    }

    /**
     * Verify that the given slot is within the bounds of this inventory view.
     *
     * @param slot The slot to check.
     */
    private void checkSlot(int slot) {
        if (slot == OUTSIDE) {
            return;
        }

        int size = countSlots();
        if (slot < 0 || slot >= size) {
            throw new IllegalArgumentException("Slot out of range [0," + size + "): " + slot);
        }
    }
}
