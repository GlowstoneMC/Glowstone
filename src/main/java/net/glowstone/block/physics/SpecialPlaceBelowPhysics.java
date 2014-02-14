package net.glowstone.block.physics;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class SpecialPlaceBelowPhysics extends DefaultBlockPhysics {
    private final Set<Material> allowedGround = EnumSet.noneOf(Material.class);

    public SpecialPlaceBelowPhysics(Material... belowTypes) {
        Collections.addAll(allowedGround, belowTypes);
    }

    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        return allowedGround.contains(block.getRelative(BlockFace.DOWN).getType());
    }
}
