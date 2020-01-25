package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowChestBlockData extends AbstractBlockData implements GlowDirectional, GlowWaterlogged {

    public GlowChestBlockData(Material material) {
        super(material, StateGenerator.FOUR_FACING, StateGenerator.WATER_LOGGED);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowChestBlockData data = new GlowChestBlockData(this.getMaterial());
        data.setFacing(this.getFacing());
        data.setWaterlogged(this.isWaterlogged());
        return data;
    }
}
