package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.RedstoneTorch;
import org.bukkit.util.Vector;

public class BlockRedstoneTorch extends BlockNeedsAttached {

    public BlockRedstoneTorch() {
        setDrops(new ItemStack(Material.REDSTONE_TORCH_ON));
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding, GlowBlockState oldState) {
        updatePhysics(block);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType, byte oldData, Material newType, byte newData) {
        updatePhysics(block);
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        final MaterialData data = state.getData();
        if (data instanceof RedstoneTorch) {
            ((RedstoneTorch) data).setFacingDirection(face);
        } else {
            warnMaterialData(RedstoneTorch.class, data);
        }
    }

    @Override
    protected BlockFace getAttachedFace(GlowBlock me) {
        return ((RedstoneTorch) me.getState().getData()).getAttachedFace();
    }
    
    public static BlockFace getAttachedBlockFace(GlowBlock block) {
        return ((RedstoneTorch) block.getState().getData()).getAttachedFace();
    }

    @Override
    public void updatePhysics(GlowBlock me) {
        me.getWorld().requestPulse(me, 2);
    }

    @Override
    public void receivePulse(GlowBlock me) {
        boolean poweredBelow = me.getRelative(getAttachedFace(me)).isBlockPowered();
        if (poweredBelow != (me.getType() == Material.REDSTONE_TORCH_OFF)) {
            me.setTypeIdAndData((poweredBelow ? Material.REDSTONE_TORCH_OFF : Material.REDSTONE_TORCH_ON).getId(), me.getData(), true);
        }
    }
}
