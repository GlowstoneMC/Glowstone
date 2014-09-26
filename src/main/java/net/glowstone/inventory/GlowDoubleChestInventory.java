package net.glowstone.inventory;

import com.google.common.collect.ImmutableList;
import net.glowstone.block.state.GlowChest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.DoubleChestInventory;

public class GlowDoubleChestInventory extends GlowSuperInventory implements DoubleChestInventory {
    public GlowDoubleChestInventory(GlowChest left, GlowChest right) {
        super.initialize(
            new DoubleChest(this), // Holder
            InventoryType.CHEST, // Type
            ImmutableList.of( // Inventories
                (GlowBaseInventory) left.getBlockInventory(),
                (GlowBaseInventory) right.getBlockInventory()
            )
        );
    }

    @Override
    public Inventory getLeftSide() {
        return getParents().get(0);
    }

    @Override
    public Inventory getRightSide() {
        return getParents().get(1);
    }

    @Override
    public DoubleChest getHolder() {
        return (DoubleChest) super.getHolder();
    }
}
