package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class BlockLever extends BlockAttachable {

    public BlockLever() {
        setDrops(new ItemStack(Material.LEVER));
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
        Vector clickedLoc) {
        GlowBlockState state = block.getState();
        MaterialData data = state.getData();

        if (!(data instanceof Lever)) {
            warnMaterialData(Lever.class, data);
            return false;
        }

        Lever lever = (Lever) data;
        lever.setPowered(!lever.isPowered());
        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_LEVER_CLICK, 0.3F,
            lever.isPowered() ? 0.6F : 0.5F);
        state.update();
        extraUpdate(block);
        return true;
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();

        if (!(data instanceof Lever)) {
            warnMaterialData(Lever.class, data);
            return;
        }

        Lever lever = (Lever) data;
        setAttachedFace(state, face.getOppositeFace());
        lever.setFacingDirection(
            face == BlockFace.UP || face == BlockFace.DOWN ? player.getCardinalFacing() : face);

    }

    private void extraUpdate(GlowBlock block) {
        Lever lever = (Lever) block.getState().getData();
        ItemTable itemTable = ItemTable.instance();
        GlowBlock target = block.getRelative(lever.getAttachedFace());
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
}
