package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

/**
 * A redstone lamp.
 * @author Sam
 */
public class BlockLamp extends BlockType {

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
        GlowBlockState oldState) {
        updatePhysics(block);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
        Material oldType, byte oldData, Material newType, byte newData) {
        updatePhysics(block);
    }

    @Override
    public void updatePhysics(GlowBlock me) {
        boolean powered = me.isBlockPowered() || me.isBlockIndirectlyPowered();

        if (powered != (me.getType() == Material.REDSTONE_LAMP_ON)) {
            me.setType(powered ? Material.REDSTONE_LAMP_ON : Material.REDSTONE_LAMP_OFF);
        }
    }
}
