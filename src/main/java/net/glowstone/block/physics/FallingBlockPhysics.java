package net.glowstone.block.physics;

import net.glowstone.block.GlowBlock;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingSand;

public class FallingBlockPhysics extends DefaultBlockPhysics {
    private final int id;
    public FallingBlockPhysics(int id) {
        this.id = id;
    }

    @Override
    public boolean doPhysics(GlowBlock block) {
        return checkBelowFree(block);
    }

    public boolean postUpdateNeighbor(GlowBlock block, BlockFace against) {
        return checkBelowFree(block);
    }

    public boolean checkBelowFree(GlowBlock block) {
        if (block.getRelative(BlockFace.DOWN).isEmpty()) {
            block.setTypeId(0);
            block.getWorld().spawn(block.getLocation(), FallingSand.class);
            return true;
        }
        return false;
    }
}
