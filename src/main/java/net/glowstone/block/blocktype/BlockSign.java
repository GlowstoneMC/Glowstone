package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TESign;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockSign extends BlockType {

    public BlockSign() {
        setDrops(new ItemStack(Material.SIGN));
    }

    public boolean isWallSign() {
        return getMaterial() == Material.WALL_SIGN;
    }

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TESign(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        state.setType(getMaterial());
        byte data;
        if (isWallSign()) {
            // attach to appropriate side of wall
            data = getFacing(face);
        } else {
            // calculate the facing of the sign based on the angle between the player and the post
            Location loc = player.getLocation();
            // 22.5 = 360 / 16
            long facing = Math.round(loc.getYaw() / 22.5) + 8;
            data = (byte) (((facing % 16) + 16) % 16);
        }
        state.setRawData(data);
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding) {
        player.openSignEditor(block.getLocation());
    }

    private byte getFacing(BlockFace face) {
        switch (face) {
            case NORTH:
                return 2;
            case SOUTH:
                return 3;
            case WEST:
                return 4;
            case EAST:
                return 5;
        }
        return 0;
    }
}
