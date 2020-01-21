package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GlowBed extends AbstractBlockData implements Bed, Directional {

    private BlockFace facing;
    private Bed.Part part;

    public GlowBed(Material material, BlockFace face){
        this(material, face, Bed.Part.HEAD);
    }

    public GlowBed(Material material, BlockFace facing, Bed.Part part) {
        super(material);
        this.facing = facing;
        this.part = part;
    }

    @Override
    public @NotNull Bed.Part getPart() {
        return this.part;
    }

    @Override
    public void setPart(@NotNull Bed.Part part) {
        this.part = part;
    }

    @Override
    public boolean isOccupied() {
        //TODO - how should this implemented? Store Entity here for the Entity in the bed? or Store isLyingDown and getBed in Entity?
        return false;
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
        return new GlowBed(this.getMaterial(), this.facing, this.part);
    }
}
