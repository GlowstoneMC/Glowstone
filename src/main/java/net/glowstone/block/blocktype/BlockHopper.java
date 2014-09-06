package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEHopper;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockHopper extends BlockContainer {

    public BlockHopper() {
        setDrops(new ItemStack(Material.HOPPER));
    }

    public void setFacingDirection(final BlockState bs, final BlockFace face) {
        byte data;
        switch (face) {
            case DOWN:
                data = 0;
                break;
            case UP:
                data = 1;
                break;
            case NORTH:
                data = 2;
                break;
            case SOUTH:
                data = 3;
                break;
            case WEST:
                data = 4;
                break;
            case EAST:
            default:
                data = 5;
                break;
        }
        bs.setRawData(data);
    }

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TEHopper(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        setFacingDirection(state, face.getOppositeFace());
    }

}
