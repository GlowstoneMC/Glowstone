package net.glowstone.inventory;

import com.google.common.collect.ImmutableList;
import net.glowstone.block.state.GlowChest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.DoubleChestInventory;

import java.util.Arrays;

public class GlowDoubleChestInventory extends SuperInventory implements DoubleChestInventory {
    public GlowDoubleChestInventory(GlowChest left, GlowChest right) {
        super.initialize(
            new DoubleChest(this), // Holder
            InventoryType.CHEST, // Type
            ImmutableList.of( // Inventories
                left.getBlockInventory(),
                right.getBlockInventory()
            ),
            InventoryType.CHEST.getDefaultTitle() // Title
        );
    }

    @Override
    public Inventory getLeftSide() {
        return getSubInventory(0);
    }

    @Override
    public Inventory getRightSide() {
        return getSubInventory(1);
    }

    @Override
    public DoubleChest getHolder() {
        return (DoubleChest) super.getHolder();
    }
}
