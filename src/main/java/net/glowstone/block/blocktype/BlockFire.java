package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public class BlockFire extends BlockDropless {

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        return true;
    }
}
