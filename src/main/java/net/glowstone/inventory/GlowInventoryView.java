package net.glowstone.inventory;

import net.glowstone.entity.GlowHumanEntity;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

/**
 * Standard implementation of InventoryView for most inventories.
 */
public class GlowInventoryView extends InventoryView {

    private final GlowHumanEntity player;
    private final InventoryType type;
    private final Inventory top, bottom;

    /**
     * Create the default inventory view for this player.
     * @param player The player.
     */
    public GlowInventoryView(GlowHumanEntity player) {
        this(player, player.getGameMode() == GameMode.CREATIVE ? InventoryType.CREATIVE : InventoryType.CRAFTING,
                player.getInventory().getCraftingInventory(), player.getInventory());
    }

    /**
     * Create an inventory view for a player.
     * @param player The player.
     * @param type The inventory type.
     * @param top The top inventory.
     */
    public GlowInventoryView(GlowHumanEntity player, InventoryType type, Inventory top, Inventory bottom) {
        this.player = player;
        this.type = type;
        this.top = top;
        this.bottom = bottom;
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
