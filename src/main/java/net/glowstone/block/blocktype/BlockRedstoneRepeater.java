package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Diode;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BlockRedstoneRepeater extends BlockNeedsAttached {

    public BlockRedstoneRepeater() {
        setDrops(new ItemStack(Material.DIODE));
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        Diode diode = (Diode) block.getState().getData();
        diode.setDelay(diode.getDelay() == 4 ? 1 : diode.getDelay() + 1);
        block.setData(diode.getData());
        return true;
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
        if (data instanceof Diode) {
            ((Diode) data).setFacingDirection(player.getDirection());
            state.setData(data);
        } else {
            warnMaterialData(Diode.class, data);
        }
    }

    @Override
    public void updatePhysics(final GlowBlock me) {
        super.updatePhysics(me);

        Diode diode = (Diode) me.getState().getData();
        GlowBlock target = me.getRelative(diode.getFacing().getOppositeFace());

        final boolean powered = target.getType() == Material.REDSTONE_TORCH_ON || target.isBlockPowered() || (target.getType() == Material.REDSTONE_WIRE
                && target.getData() > 0 && BlockRedstone.calculateConnections(target).contains(diode.getFacing()))
                || (target.getType() == Material.DIODE_BLOCK_ON && ((Diode) target.getState().getData()).getFacing() == diode.getFacing());

        if (powered != (me.getType() == Material.DIODE_BLOCK_ON)) {
            (new BukkitRunnable() {
                @Override
                public void run() {
                    if (!powered && me.getType() == Material.DIODE_BLOCK_ON) {
                        me.setTypeIdAndData(Material.DIODE_BLOCK_OFF.getId(), me.getData(), true);
                    } else if (powered && me.getType() == Material.DIODE_BLOCK_OFF) {
                        me.setTypeIdAndData(Material.DIODE_BLOCK_ON.getId(), me.getData(), true);
                    }
                }
            }).runTaskLater(null, diode.getDelay() * 2);
        }
    }
}
