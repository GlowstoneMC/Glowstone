package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CoralWallFan;
import org.jetbrains.annotations.NotNull;

public class GlowCoralWallBlockDara extends AbstractBlockData implements GlowDirectional, GlowWaterlogged, CoralWallFan {

    public GlowCoralWallBlockDara(Material material) {
        super(material, StateGenerator.FOUR_FACING, StateGenerator.WATER_LOGGED);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowCoralWallBlockDara wall = new GlowCoralWallBlockDara(this.getMaterial());
        wall.setFacing(this.getFacing());
        wall.setWaterlogged(this.isWaterlogged());
        return wall;
    }
}
