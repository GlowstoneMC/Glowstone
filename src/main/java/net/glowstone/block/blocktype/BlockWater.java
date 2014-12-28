package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlockState;
import org.bukkit.Material;

public class BlockWater extends BlockLiquid {

    public BlockWater() {
        super(Material.WATER_BUCKET);
    }

    @Override
    public boolean isCollectible(GlowBlockState target) {
        return target.getType() == Material.STATIONARY_WATER || (target.getType() == Material.WATER && target.getRawData() == 8);
    }

}
