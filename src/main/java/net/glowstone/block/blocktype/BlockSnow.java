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
        // can absorb snow layers if non-full, or all blocks if single layer
        return (holding.getType() == Material.SNOW && block.getData() < 7) || block.getData() == 0;
    }

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        // can always be overridden by more snow or any other block
        return true;
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
