package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowRotatable;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sign;
import org.jetbrains.annotations.NotNull;

public class GlowFreeStandingSignData extends AbstractBlockData implements Sign, GlowRotatable, GlowWaterlogged {

    public GlowFreeStandingSignData(Material material) {
        super(material, StateGenerator.SIXTEEN_ROTATION, StateGenerator.WATER_LOGGED);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowFreeStandingSignData sign = new GlowFreeStandingSignData(this.getMaterial());
        sign.setRotation(this.getRotation());
        sign.setWaterlogged(this.isWaterlogged());
        return sign;
    }
}
