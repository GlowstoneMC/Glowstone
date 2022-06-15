package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.MaterialUtil;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Diode;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class BlockRedstoneRepeater extends BlockNeedsAttached {

    public BlockRedstoneRepeater() {
        setDrops(new ItemStack(Material.REPEATER));
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
                                 Vector clickedLoc) {
        Diode diode = (Diode) block.getState().getData();
        diode.setDelay(diode.getDelay() == 4 ? 1 : diode.getDelay() + 1);
        block.setData(diode.getData());
        return true;
    }

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
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
                           ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        MaterialData data = state.getData();
        if (data instanceof Diode) {
            ((Diode) data).setFacingDirection(player.getCardinalFacing());
            state.setData(data);
        } else {
            warnMaterialData(Diode.class, data);
        }
        state.getWorld().requestPulse(state.getBlock());
    }

    @Override
    public void updatePhysicsAfterEvent(GlowBlock me) {
        super.updatePhysicsAfterEvent(me);

        Diode diode = (Diode) me.getState().getData();
        GlowBlock target = me.getRelative(diode.getFacing().getOppositeFace());

        // TODO: 1.13 redstone ON data
        boolean powered = target.getType() == Material.REDSTONE_TORCH || target.isBlockPowered()
            || target.getType() == Material.REDSTONE_WIRE
            && target.getData() > 0 && BlockRedstone.calculateConnections(target)
            .contains(diode.getFacing())
            || target.getType() == Material.REPEATER
            && ((Diode) target.getState().getData()).getFacing() == diode.getFacing();

        if (powered != (me.getType() == Material.REPEATER)) {
            me.getWorld().requestPulse(me);
        }
    }

    private void extraUpdate(GlowBlock block) {
        Diode diode = (Diode) block.getState().getData();
        ItemTable itemTable = ItemTable.instance();
        GlowBlock target = block.getRelative(diode.getFacing());
        if (target.getType().isSolid()) {
            for (BlockFace face2 : ADJACENT) {
                GlowBlock target2 = target.getRelative(face2);
                BlockType notifyType = itemTable.getBlock(target2.getType());
                if (notifyType != null) {
                    if (target2.getFace(block) == null) {
                        notifyType
                            .onNearBlockChanged(target2, BlockFace.SELF, block, block.getType(),
                                block.getData(), block.getType(), block.getData());
                    }
                    notifyType.onRedstoneUpdate(target2);
                }
            }
        }
    }

    @Override
    public void receivePulse(GlowBlock block) {
        Diode diode = (Diode) block.getState().getData();
        GlowBlock target = block.getRelative(diode.getFacing().getOppositeFace());

        // TODO: redstone ON data
        boolean powered = target.getType() == Material.REDSTONE_TORCH || target.isBlockPowered()
            || target.getType() == Material.REDSTONE_WIRE
            && target.getData() > 0 && BlockRedstone.calculateConnections(target)
            .contains(diode.getFacing())
            || target.getType() == Material.REPEATER
            && ((Diode) target.getState().getData()).getFacing() == diode.getFacing();

        if (!powered && block.getType() == Material.REPEATER) {
            block.setTypeIdAndData(MaterialUtil.getId(Material.REPEATER), block.getData(),
                true); // TODO: repeater off data
            extraUpdate(block);
        } else if (powered && block.getType() == Material.REPEATER) { // TODO: repeater off data
            block.setTypeIdAndData(MaterialUtil.getId(Material.REPEATER), block.getData(),
                true); // TODO: repeater on data
            extraUpdate(block);
        }
    }

    @Override
    public boolean isPulseOnce(GlowBlock block) {
        return true;
    }

    @Override
    public int getPulseTickSpeed(GlowBlock block) {
        Diode diode = (Diode) block.getState().getData();
        return diode.getDelay() << 1;
    }
}
