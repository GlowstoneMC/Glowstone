package net.glowstone.block.physics;

import net.glowstone.EventFactory;
import net.glowstone.block.BlockProperties;
import net.glowstone.block.GlowBlock;
import org.bukkit.block.BlockFace;

public class BlockPhysicsEngine {
    public static void doPhysics(GlowBlock block) {
        if (!EventFactory.onBlockPhysics(block).isCancelled()) {
            int original = block.getTypeId();
            BlockPhysicsHandler handler = BlockProperties.get(block.getTypeId()).getPhysics();
            if (handler.doPhysics(block)) {
                for (BlockFace face : BlockFace.values()) {
            if (face != BlockFace.SELF) {
                neighborPhysics(block.getRelative(face), face, original);
            }
                }
            }
        }
    }

    public static void updateAllNeighbors(GlowBlock block) {
        for (BlockFace face : BlockFace.values()) {
            if (face != BlockFace.SELF) {
                neighborPhysics(block.getRelative(face), face, block.getTypeId());
            }
        }
    }

    private static void neighborPhysics(GlowBlock block, BlockFace against, int original) {
        if (!EventFactory.onBlockPhysics(block, original).isCancelled()) {
            BlockPhysicsHandler handler = BlockProperties.get(block.getTypeId()).getPhysics();
            if (handler.postUpdateNeighbor(block, against)) {
                original = block.getTypeId();
                for (BlockFace face : BlockFace.values()) {
                    if (face != BlockFace.SELF && face != against) {
                        neighborPhysics(block.getRelative(face), face, original);
                    }
                }
            }
        }
    }
}
