package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowWaterBlockData extends AbstractBlockData implements GlowWaterlogged {

    public GlowWaterBlockData(Material material) {
        super(material, StateGenerator.WATER_LOGGED);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowWaterBlockData logged = new GlowWaterBlockData(this.getMaterial());
        logged.setWaterlogged(this.isWaterlogged());
        return logged;
    }
}
