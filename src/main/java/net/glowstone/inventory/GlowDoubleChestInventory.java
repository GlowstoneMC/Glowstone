package net.glowstone.inventory;

import com.google.common.collect.ImmutableList;
import net.glowstone.block.state.GlowChest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

public class GlowDoubleChestInventory extends GlowSuperInventory implements DoubleChestInventory {

    public GlowDoubleChestInventory(GlowChest first, GlowChest second) {
        super.initialize(
                new DoubleChest(this), // Holder
                InventoryType.CHEST, // Type
                ImmutableList.of( // Inventories
                        (GlowBaseInventory) first.getBlockInventory(),
                        (GlowBaseInventory) second.getBlockInventory()
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
