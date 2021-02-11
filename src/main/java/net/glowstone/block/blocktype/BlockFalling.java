package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
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
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
        GlowBlockState oldState) {
        updatePhysics(block);
    }

    @Override
    public void onNearBlockChanged(GlowBlock me, BlockFace face, GlowBlock other, Material oldType,
        byte oldData, Material newType, byte newData) {
        if (face == BlockFace.DOWN) {
            updatePhysics(me);
        }
    }

    @Override
    public void updatePhysicsAfterEvent(GlowBlock me) {
        super.updatePhysicsAfterEvent(me);
        Block below = me.getRelative(BlockFace.DOWN);
        if (!supportingBlock(below.getType())) {
            //Simulates real Minecraft delay on block fall
            //If possible should be changed to 2.5 ticks
            me.getWorld().getServer().getScheduler()
                .runTaskLater(null, () -> transformToFallingEntity(me), 2);
        }
    }

    protected void transformToFallingEntity(GlowBlock me) {
        //Force block to update otherwise it can sometimes duplicate
        me = me.getWorld().getBlockAt(me.getX(), me.getY(), me.getZ());

        if (!me.isEmpty()) {
            byte data = me.getData();
            me.setType(Material.AIR);
            me.getWorld().spawnFallingBlock(me.getLocation().add(0.50, 0.00, 0.50), drop, data);
        }
    }

    /**
     * @param material A Material
     * @return true if material will support this block and prevent it from falling or breaking
     */
    private boolean supportingBlock(Material material) {
        return material.isSolid();
    }
}
