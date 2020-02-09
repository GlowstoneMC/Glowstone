package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowMultipleFaces;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Fence;
import org.jetbrains.annotations.NotNull;

public class GlowWallBlockData extends AbstractBlockData implements Fence, GlowWaterlogged, GlowMultipleFaces {

    public GlowWallBlockData(Material material) {
        super(material, StateGenerator.BOOLEAN_EAST, StateGenerator.BOOLEAN_NORTH, StateGenerator.BOOLEAN_SOUTH, StateGenerator.BOOLEAN_WEST, StateGenerator.BOOLEAN_UP_INVERTED, StateGenerator.WATER_LOGGED);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowWallBlockData faceData = new GlowWallBlockData(this.getMaterial());
        for(BlockFace face : this.getFaces()){
            faceData.setFace(face, true);
        }
        faceData.setWaterlogged(this.isWaterlogged());
        return faceData;
    }
}
