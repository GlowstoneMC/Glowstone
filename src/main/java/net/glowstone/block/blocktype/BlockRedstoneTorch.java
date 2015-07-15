package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
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
        super.updatePhysics(me);
        me.getWorld().requestPulse(me, 2);
    }

    @Override
    public void receivePulse(GlowBlock me) {
        boolean powered = me.getRelative(getAttachedFace(me)).isBlockPowered();

        // If below is powered, are we turned off?
        if (powered != (me.getType() == Material.REDSTONE_TORCH_OFF)) {

            // Are we burnt out?
            if (!powered && me.getCounter() > 8) {
                powered = true;
                if (me.getCounter() == 9) {
                    me.getWorld().playSound(me.getLocation(), Sound.FIZZ, 1, 1);
                    me.getWorld().playEffect(me.getLocation().add(0.5, 0.75, 0.5), Effect.SMOKE, BlockFace.UP);
                }
            }

            // If below is powered or burnt out, are we turned off?
            if (powered != (me.getType() == Material.REDSTONE_TORCH_OFF)) {
                if (!powered) {
                    me.count(60);
                }

                me.setTypeIdAndData((powered ? Material.REDSTONE_TORCH_OFF : Material.REDSTONE_TORCH_ON).getId(), me.getData(), true);
                extraUpdate(me);
                return;
            }
        }

        me.getWorld().cancelPulse(me);
    }
    
    private static final BlockFace[] ADJACENT = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};

    private void extraUpdate(GlowBlock block) {
        ItemTable itemTable = ItemTable.instance();
        GlowBlock target = block.getRelative(BlockFace.UP);
        if (target.getType().isSolid()) {
            for (BlockFace face2 : ADJACENT) {
                GlowBlock target2 = target.getRelative(face2);
                BlockType notifyType = itemTable.getBlock(target2.getTypeId());
                if (notifyType != null) {
                    if (target2.getFace(block) == null) {
                        notifyType.onNearBlockChanged(target2, BlockFace.SELF, block, block.getType(), block.getData(), block.getType(), block.getData());
                    }
                    notifyType.onRedstoneUpdate(target2);
                }
            }
        }
    }
}
