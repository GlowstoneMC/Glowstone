package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowRotatable;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sign;
import org.jetbrains.annotations.NotNull;

public class GlowFreeStandingSign extends AbstractBlockData implements Sign, GlowRotatable, GlowWaterlogged {

    public GlowFreeStandingSign(Material material) {
        super(material, StateGenerator.SIXTEEN_ROTATION, StateGenerator.WATER_LOGGED);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowFreeStandingSign sign = new GlowFreeStandingSign(this.getMaterial());
        sign.setRotation(this.getRotation());
        sign.setWaterlogged(this.isWaterlogged());
        return sign;
    }
}
