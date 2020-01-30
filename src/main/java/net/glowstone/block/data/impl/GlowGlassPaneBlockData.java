package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowMultipleFaces;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.GlassPane;
import org.jetbrains.annotations.NotNull;

public class GlowGlassPaneBlockData extends AbstractBlockData implements GlowMultipleFaces, GlowWaterlogged, GlassPane {

    public GlowGlassPaneBlockData(Material material) {
        super(material, StateGenerator.BOOLEAN_WEST, StateGenerator.BOOLEAN_UP, StateGenerator.BOOLEAN_DOWN, StateGenerator.BOOLEAN_EAST, StateGenerator.BOOLEAN_NORTH, StateGenerator.BOOLEAN_SOUTH, StateGenerator.WATER_LOGGED);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowGlassPaneBlockData glassPane = new GlowGlassPaneBlockData(this.getMaterial());
        for(BlockFace face : this.getFaces()){
            glassPane.setFace(face, true);
        }
        glassPane.setWaterlogged(this.isWaterlogged());
        return glassPane;
    }
}
