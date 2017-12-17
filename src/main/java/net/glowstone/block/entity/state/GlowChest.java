package net.glowstone.block.entity.state;

import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockChest;
import net.glowstone.block.entity.ChestEntity;
import net.glowstone.inventory.GlowDoubleChestInventory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GlowChest extends GlowContainer implements Chest {

    public GlowChest(GlowBlock block) {
        super(block);
    }

    public ChestEntity getBlockEntity() {
        return (ChestEntity) getBlock().getBlockEntity();
    }

    @Override
    public Inventory getBlockInventory() {
        return getBlockEntity().getInventory();
    }

    @Override
    public Inventory getInventory() {
        GlowBlock me = getBlock();
        BlockChest blockChest = (BlockChest) ItemTable.instance().getBlock(me.getType());
        BlockFace attachedChest = blockChest.getAttachedChest(me);

        if (attachedChest != null) {
            Block nearbyBlock = me.getRelative(attachedChest);
            GlowChest nearbyChest = (GlowChest) nearbyBlock.getState();

            switch (attachedChest) {
                case SOUTH:
                case EAST:
                    return new GlowDoubleChestInventory(this, nearbyChest);
                case WEST:
                case NORTH:
                    return new GlowDoubleChestInventory(nearbyChest, this);

                default:
                    GlowServer.logger.warning(
                        "GlowChest#getInventory() can only handle N/O/S/W BlockFaces, got "
                            + attachedChest);
                    return getBlockInventory();
            }
        }

        return getBlockInventory();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        ItemStack[] contents = getBlockInventory().getContents();

        boolean result = super.update(force, applyPhysics);

        if (result) {
            getBlockEntity().setContents(contents);
            getBlockEntity().updateInRange();
        }

        return result;
    }
}
