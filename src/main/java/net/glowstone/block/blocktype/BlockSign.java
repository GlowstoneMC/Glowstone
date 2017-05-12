package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.SignEntity;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;
import org.bukkit.util.Vector;

public class BlockSign extends BlockType {

    public BlockSign() {
        setDrops(new ItemStack(Material.SIGN));
    }

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new SignEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        state.setType(getMaterial());
        if (!(state.getData() instanceof Sign)) {
            warnMaterialData(Sign.class, state.getData());
            return;
        }
        Sign sign = (Sign) state.getData();
        sign.setFacingDirection(sign.isWallSign() ? face : player.getFacing().getOppositeFace());
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType, byte oldData, Material newType, byte newData) {
        super.onNearBlockChanged(block, face, changedBlock, oldType, oldData, newType, newData);
        GlowBlockState state = block.getState();
        if (!(state.getData() instanceof Sign)) {
            warnMaterialData(Sign.class, state.getData());
            return;
        }
        Sign sign = (Sign) state.getData();
        if (!newType.isSolid() && block.getRelative(sign.getAttachedFace()).equals(changedBlock)) {
            destroy(block);
        }
    }

    private void destroy(GlowBlock me) {
        me.setType(Material.AIR);
        me.getWorld().dropItemNaturally(me.getLocation(), new ItemStack(Material.SIGN));
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding, GlowBlockState oldState) {
        player.openSignEditor(block.getLocation());
    }
}
