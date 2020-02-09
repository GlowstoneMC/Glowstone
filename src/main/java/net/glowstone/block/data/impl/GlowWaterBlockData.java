package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.generator.BooleanStateGenerator;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowWaterBlockData extends AbstractBlockData implements GlowWaterlogged {

    public GlowWaterBlockData(Material material, BooleanStateGenerator generator) {
        super(material, generator);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowWaterBlockData logged = new GlowWaterBlockData(this.getMaterial(), this.getWaterLoggedValue().getGenerator());
        logged.setWaterlogged(this.isWaterlogged());
        return logged;
    }
}
