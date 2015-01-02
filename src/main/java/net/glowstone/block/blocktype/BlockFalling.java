package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a block that falls down, when there's no block below it.
 */
public class BlockFalling extends BlockType {
    private final Material drop;

    public BlockFalling(Material drop) {
        this.drop = drop;
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding) {
        updatePhysics(block);
    }

    @Override
    public void onNearBlockChanged(GlowBlock me, BlockFace face, GlowBlock other, Material oldType, byte oldData, Material newType, byte newData) {
        if (face == BlockFace.DOWN) {
            updatePhysics(me);
        }
    }

    @Override
    public void updatePhysics(GlowBlock me) {
        Block below = me.getRelative(BlockFace.DOWN);
        if (!supportingBlock(below.getType())) {
            transformToFallingEntity(me);
        }
    }

    protected void transformToFallingEntity(final GlowBlock me) {
        final Material oldType = me.getType();
        final byte data = me.getData();
        me.setTypeId(0, false);
        // todo: replace with me.getWorld().spawnFallingBlock(me.getLocation(), drop, me.getData());
        // on a delay to prevent the block not being visible because its new location was just dug
        me.getWorld().getServer().getScheduler().runTask(null, new Runnable() {
            @Override
            public void run() {
                int x = me.getX(), y = me.getY(), z = me.getZ();
                for (; y > 0; --y) {
                    Material check = me.getWorld().getBlockAt(x, y - 1, z).getType();
                    if (supportingBlock(check)) {
                        me.getWorld().getBlockAt(x, y, z).setTypeIdAndData(drop.getId(), data, true);
                        break;
                    }
                }
                me.applyPhysics(oldType, 0, data, (byte) 0);
            }
        });
    }

    private boolean supportingBlock(Material material) {
        switch (material) {
            case AIR:
            case FIRE:
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
                return false;
        }
        return true;
    }
}
