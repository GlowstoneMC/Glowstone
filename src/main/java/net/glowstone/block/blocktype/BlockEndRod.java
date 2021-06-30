package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockEndRod extends BlockDirectDrops {

    private static final byte FACING_DOWN = 0x0;
    private static final byte FACING_UP = 0x1;
    private static final byte FACING_NORTH = 0x2;
    private static final byte FACING_SOUTH = 0x3;
    private static final byte FACING_WEST = 0x4;
    private static final byte FACING_EAST = 0x5;

    public BlockEndRod() {
        super(Material.END_ROD);
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
                           ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        byte data;
        switch (face) {
            case DOWN:
                data = FACING_DOWN;
                break;
            case UP:
                data = FACING_UP;
                break;
            case NORTH:
                data = FACING_NORTH;
                break;
            case SOUTH:
                data = FACING_SOUTH;
                break;
            case WEST:
                data = FACING_WEST;
                break;
            case EAST:
                data = FACING_EAST;
                break;
            default:
                // TODO: Should this raise a warning?
                data = 0;
        }
        state.setRawData(data);
    }
}
