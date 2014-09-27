package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEChest;
import net.glowstone.inventory.GlowDoubleChestInventory;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

public class GlowChest extends GlowBlockState implements Chest {

    private static final BlockFace[] ADJACENT_CHEST_FACES = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

    public GlowChest(GlowBlock block) {
        super(block);
    }

    private TEChest getTileEntity() {
        return (TEChest) getBlock().getTileEntity();
    }

    @Override
    public Inventory getBlockInventory() {
        return getTileEntity().getInventory();
    }

    @Override
    public Inventory getInventory() {
        for (BlockFace face : ADJACENT_CHEST_FACES) {
            Block nearbyBlock = getBlock().getRelative(face);
            if (nearbyBlock.getType() == Material.CHEST) {
                GlowChest nearbyChest = (GlowChest) nearbyBlock.getState();
                return new GlowDoubleChestInventory(this, nearbyChest);
            }
        }

        // todo: handle double chests
        return getBlockInventory();
    }
}
