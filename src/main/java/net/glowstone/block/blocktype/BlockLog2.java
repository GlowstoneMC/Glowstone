package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class BlockLog2 extends BlockType {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        // No Tree2 MaterialData
        MaterialData data = state.getData();

        data.setData(setTree(face, (byte) holding.getDurability()));

        state.setData(data);
    }

    /**
     * Returns data updated to face the given direction.
     * @param dir the direction to face
     * @param data a data value that specifies species but not direction
     * @return the data value with facing direction specified
     */
    public byte setTree(BlockFace dir, byte data) {
        switch (dir) {
            case UP:
            case DOWN:
                data += 0;
                break;
            case WEST:
            case EAST:
                data += 4;
                break;
            case NORTH:
            case SOUTH:
                data += 8;
                break;
            case SELF:
            default:
                data += 12;
                break;
        }
        return data;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Arrays.asList(new ItemStack(Material.LOG_2, 1, (short) (block.getData() & 0x03)));
    }

    @Override
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        // vanilla set leaf decay check in a 9x9x9 neighboring when a log block is removed
        GlowWorld world = block.getWorld();
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 9; z++) {
                for (int y = 0; y < 9; y++) {
                    GlowBlock b = world.getBlockAt(block.getLocation().add(x - 4, y - 4, z - 4));
                    if (b.getType() == Material.LEAVES || b.getType() == Material.LEAVES_2) {
                        GlowBlockState state = b.getState();
                        if ((state.getRawData() & 0x08) == 0 && (state.getRawData() & 0x04)
                            == 0) { // check decay is off and decay is on
                            // set decay check on for this leaves block
                            state.setRawData((byte) (state.getRawData() | 0x08));
                            state.update(true);
                        }
                    }
                }
            }
        }
    }
}
