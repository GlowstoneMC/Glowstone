package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowMultipleFaces;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Fence;
import org.jetbrains.annotations.NotNull;

public class GlowFenceBlockData extends AbstractBlockData implements Fence, GlowWaterlogged, GlowMultipleFaces {

    public GlowFenceBlockData(Material material) {
        super(material, StateGenerator.BOOLEAN_EAST, StateGenerator.BOOLEAN_NORTH, StateGenerator.BOOLEAN_SOUTH, StateGenerator.BOOLEAN_WEST, StateGenerator.WATER_LOGGED);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowMultipleFacesBlockData faceData = new GlowMultipleFacesBlockData(this.getMaterial());
        this.getStateValues().forEach(v -> faceData.getStateValues().add(v.clone()));
        return faceData;
    }
}
