package net.glowstone.inventory;

import net.glowstone.block.state.GlowChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;

/**
 * A class which represents an chest inventory.
 */
public class GlowChestInventory extends GlowInventory {

    private GlowChest chest;

    public GlowChestInventory(GlowChest chest) {
        super(chest, InventoryType.CHEST);
        this.chest = chest;
    }

    @Override
    public void addViewer(HumanEntity viewer) {
        super.addViewer(viewer);
        chest.getTileEntity().addViewer();
    }

    @Override
    public void removeViewer(HumanEntity viewer) {
        super.removeViewer(viewer);
        chest.getTileEntity().removeViewer();
    }
}
