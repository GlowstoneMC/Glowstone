package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockSnow extends BlockType {

    @Override
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        // can absorb only snow layers if not already full
        return holding.getType() == Material.SNOW && block.getData() < 7;
    }

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        return holding.getType() == Material.SNOW;
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        // note: does not emulate certain weird broken Vanilla behaviors,
        // such as placing snow an extra block away from where it should

        if (state.getType() == Material.SNOW) {
            // add another snow layer if possible
            byte data = state.getRawData();
            if (data < 7) {
                state.setRawData((byte) (data + 1));
            }
        } else {
            // place first snow layer
            state.setType(Material.SNOW);
        }
    }
}
