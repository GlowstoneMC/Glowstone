package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;

public class BlockLog2 extends BlockType {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        // No Tree2 MaterialData
        MaterialData data = state.getData();

        data.setData(setTree(face, (byte) holding.getDurability()));

        state.setData(data);
    }

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
                data += 12;
                break;
        }
        return data;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Arrays.asList(new ItemStack(Material.LOG_2, 1, (short) (block.getData() % 4)));
    }
}
