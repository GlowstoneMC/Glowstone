package net.glowstone.inventory;

import net.glowstone.entity.GlowHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * Standard implementation of InventoryView for most inventories.
 */
public class GlowInventoryView extends InventoryView {

    private final HumanEntity player;
    private final InventoryType type;
    private final Inventory top, bottom;

    /**
     * Create the default inventory view for this player.
     * @param player The player.
     */
    public GlowInventoryView(GlowHumanEntity player) {
        this(player, player.getInventory().getCraftingInventory());
    }

    /**
     * Create an inventory view for this player looking at a given top inventory.
     * @param player The player.
     * @param top    The top inventory.
     */
    public GlowInventoryView(HumanEntity player, Inventory top) {
        this(player, top.getType(), top, player.getInventory());
    }

    /**
     * Create an inventory view for a player.
     * @param player The player.
     * @param type   The inventory type.
     * @param top    The top inventory.
     * @param bottom The bottom inventory.
     */
    public GlowInventoryView(HumanEntity player, InventoryType type, Inventory top, Inventory bottom) {
        this.player = player;
        this.type = type;
        this.top = top;
        this.bottom = bottom;
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
     * @param slot The slot to check.
     */
    private void checkSlot(int slot) {
        if (slot == OUTSIDE) return;

        int size = countSlots();
        if (isDefault(this)) size += 4; // armor slots
        if (slot < 0 || slot >= size) {
            throw new IllegalArgumentException("Slot out of range [0," + size + "): " + slot);
        }
    }

    @Override
    public Inventory getTopInventory() {
        return top;
    }

    @Override
    public Inventory getBottomInventory() {
        return bottom;
    }

    @Override
    public HumanEntity getPlayer() {
        return player;
    }

    @Override
    public InventoryType getType() {
        // NB: by spec, ought to return CREATIVE instead of CRAFTING if player is in creative mode
        // but this messes up the calculations in InventoryView which expect CRAFTING but also
        // apply to CREATIVE.
        return type;
    }

    /**
     * Check if an inventory view is the player's default inventory view.
     * @param view The view to check.
     * @return Whether it is a player's default inventory view.
     */
    public static boolean isDefault(InventoryView view) {
        return view.getBottomInventory() instanceof GlowPlayerInventory && view.getTopInventory() == ((GlowPlayerInventory) view.getBottomInventory()).getCraftingInventory();
    }

}
