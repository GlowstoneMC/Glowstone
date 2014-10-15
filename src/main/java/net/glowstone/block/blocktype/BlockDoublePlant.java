package net.glowstone.block.blocktype;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;

public class BlockDoublePlant extends BlockType implements IBlockGrowable {

    @Override
    public boolean isFertilizable(GlowBlock block) {
        int data = block.getData();
        if (data == 8) { // above part
            data = block.getRelative(BlockFace.DOWN).getState().getRawData();
        }
        if (data != 2 && // double tall grass
            data != 3) { // large fern
            return true;
        }
        return false;
    }

    @Override
    public boolean canGrowWithChance(GlowBlock block) {
        return true;
    }

    @Override
    public void grow(GlowPlayer player, GlowBlock block) {
        int data = block.getData();
        if (data == 8) { // above part
            data = block.getRelative(BlockFace.DOWN).getState().getRawData();
        }
        if (data == 0 ||     // sunflower
                data == 1 || // lilac
                data == 4 || // rose
                data == 5) { // peony
            block.getWorld().dropItemNaturally(block.getLocation(),
                    new ItemStack(Material.DOUBLE_PLANT, 1, (short) data));
        }
    }
}
