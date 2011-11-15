package net.glowstone.block.physics;

import gnu.trove.set.hash.TIntHashSet;
import net.glowstone.block.GlowBlock;
import org.bukkit.block.BlockFace;

public class SpecialPlaceBelowPhysics extends DefaultBlockPhysics {
    private final int type;
    private final TIntHashSet allowedGround;
    public SpecialPlaceBelowPhysics(int type, int ... belowTypes) {
        this.type = type;
        this.allowedGround = new TIntHashSet(belowTypes);
    }

    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        int below = block.getWorld().getBlockTypeIdAt(block.getX(), block.getY() - 1, block.getZ());
        return allowedGround.contains(below);
    }
}
