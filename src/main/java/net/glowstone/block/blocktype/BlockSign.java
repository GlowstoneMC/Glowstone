package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TESign;
import net.glowstone.block.entity.TileEntity;
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
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TESign(chunk.getBlock(cx, cy, cz));
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
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding) {
        player.openSignEditor(block.getLocation());
    }
}
