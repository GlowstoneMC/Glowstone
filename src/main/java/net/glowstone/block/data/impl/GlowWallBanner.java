package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GlowWallBanner extends AbstractBlockData implements Directional {

    private BlockFace facing;

    public GlowWallBanner(Material material, BlockFace face) {
        super(material);
        this.setFacing(face);
    }

    @Override
    public @NotNull BlockFace getFacing() {
        return this.facing;
    }

    @Override
    public void setFacing(@NotNull BlockFace blockFace) {
        if(!getFaces().stream().anyMatch(f -> f.equals(blockFace))){
            throw new UnsupportedOperationException("Direction of " + blockFace.name().toLowerCase() + " is not supported for minecraft:" + this.getMaterial().name().toLowerCase());
        }
        this.facing = blockFace;
    }

    @Override
    public @NotNull Set<BlockFace> getFaces() {
        return new HashSet<>(Arrays.asList(BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST));
    }

    @Override
    public @NotNull String getAsString() {
        return "minecraft:" + this.getMaterial().name().toLowerCase() + "[facing:" + this.facing.name().toLowerCase() + "]";
    }

    @Override
    public @NotNull BlockData merge(@NotNull BlockData blockData) {
        return null;
    }

    @Override
    public @NotNull BlockData clone() {
        return new GlowWallBanner(this.getMaterial(), this.facing);
    }
}
