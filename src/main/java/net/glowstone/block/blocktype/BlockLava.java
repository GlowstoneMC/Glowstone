package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlockState;
import org.bukkit.Material;

public class BlockLava extends BlockLiquid {

    public BlockLava() {
        super(Material.LAVA_BUCKET);
    }

    @Override
    public boolean isCollectible(GlowBlockState target) {
        return (target.getType() == Material.LAVA || target.getType() == Material.STATIONARY_LAVA) &&
                (target.getRawData() == 0 || target.getRawData() == 8); // 8 for backwards compatibility
    }

}
