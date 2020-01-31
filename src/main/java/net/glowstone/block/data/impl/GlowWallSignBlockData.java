package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.jetbrains.annotations.NotNull;

public class GlowWallSignBlockData extends AbstractBlockData implements GlowDirectional, GlowWaterlogged, WallSign {

    public GlowWallSignBlockData(Material material) {
        super(material, StateGenerator.FOUR_FACING, StateGenerator.WATER_LOGGED);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowWallSignBlockData data = new GlowWallSignBlockData(this.getMaterial());
        data.setFacing(this.getFacing());
        data.setWaterlogged(this.isWaterlogged());
        return data;
    }
}
